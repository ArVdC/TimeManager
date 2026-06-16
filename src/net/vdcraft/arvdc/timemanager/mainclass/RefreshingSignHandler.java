package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.sign.SignSide;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.placeholders.PlaceholdersHandler;
import net.vdcraft.arvdc.timemanager.ymlfilesmanagement.SignsFileHandler;

/**
 * Refreshing signs — players place a sign whose first line is the marker
 * {@code [tm]} (case-insensitive) and the remaining 3 lines may contain any
 * combination of plain text and {curly-brace} placeholders such as
 * {@code {tm_time24}}, {@code {tm_serverday}}. The plugin then rewrites the
 * sign's text on a configurable interval so it acts as a live clock / day
 * counter in-world.
 *
 * Config keys (under root):
 * <pre>
 * signs:
 *   enabled: true
 *   refresh-rate: 40    # ticks
 *   marker: "[tm]"      # first-line marker that turns a sign into a TM sign
 * </pre>
 *
 * Persistence: registered sign coordinates are written to {@code signs.yml}
 * inside the plugin folder so they survive restart.
 */
public class RefreshingSignHandler implements Listener {

	/**
	 * In-memory cache of tracked signs, keyed by "<world>:<x>,<y>,<z>:<side>".
	 */
	private static final Set<String> tracked = new HashSet<>();
	
	/** 
	 * Original line2..4 text (with placeholders) per sign key.
	 * */
	private static final java.util.Map<String, String[]> templates = new java.util.HashMap<>();
	private static int taskId = -1;

	public static void startOrRestart() {
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
		if (!MainTM.getInstance().getConfig().getString(MainTM.CF_SIGNS + "." + MainTM.CF_SIGNS_USESIGNS).equalsIgnoreCase(MainTM.ARG_TRUE)) return;

		int refresh = Math.max(MainTM.signsRefreshMin, MainTM.getInstance().getConfig().getInt(MainTM.CF_SIGNS + "." + MainTM.CF_SIGNS_REFRESH, MainTM.signsRefreshMax));
		taskId = new BukkitRunnable() {
			@Override
			public void run() { refreshAll(); }
		}.runTaskTimer(MainTM.getInstance(), refresh, refresh).getTaskId();
	}

	/* ─────────────── Persistence ─────────────── */

	/**
	 * Load  from the signs.yml file
	 */
	public static void loadFromDisk() {
		tracked.clear();
		templates.clear();
		if (MainTM.getInstance().signsConf == null) return;
		ConfigurationSection root = MainTM.getInstance().signsConf.getConfigurationSection(MainTM.SIGNS_SIGNSLIST);
		if (root == null) return;
		for (String id : root.getKeys(false)) {
			ConfigurationSection s = root.getConfigurationSection(id);
			if (s == null) continue;
			String world = s.getString(MainTM.SIGNS_WORLD);
			String pos = s.getString(MainTM.SIGNS_POSITION);
			String side = s.getString(MainTM.SIGNS_SIDE);
			if (world == null || pos == null || pos.isEmpty()) continue;
			String[] xyz = pos.replace(" ", "").split(",");
			if (xyz.length != 3) continue;
			String key = world + ":" + xyz[0] + "," + xyz[1] + "," + xyz[2] + ":" + side;
			tracked.add(key);
			ConfigurationSection content = s.getConfigurationSection(MainTM.SIGNS_CONTENT);
			if (content != null) {
				String[] tpl = new String[3];
				tpl[0] = content.getString("line2", "");
				tpl[1] = content.getString("line3", "");
				tpl[2] = content.getString("line4", "");
				templates.put(key, tpl);
			}
		}
	}

	/**
	 * Save signs list to the signs.yml file
	 */
	public static void saveToDisk() {
		if (MainTM.getInstance().signsConf == null) return;
		int i = 1;
		for (String key : tracked) {		
			String[] keys = key.split(":");
			String world = keys[0];
			String pos =  keys[1];
			String side =  keys[2];			
			String keyName = String.format("%02d", i++);		
			World w = Bukkit.getWorld(world);
			String[] xyz = pos.split(",");
			double x = Double.parseDouble(xyz[0]);
			double y = Double.parseDouble(xyz[1]);
			double z = Double.parseDouble(xyz[2]);
			Location loc = new Location(w, x, y, z);
			Block b = w.getBlockAt(loc);
			String mat = b.getType().name();			
			BlockData data = b.getBlockData();
			BlockFace f = null;
			if (data instanceof Directional directional) {
				f = directional.getFacing();
			} else if (data instanceof org.bukkit.block.data.type.Sign s) {
				f = s.getRotation();
			}
			String face = f.name();
			String[] tpl = templates.get(key);
			if (tpl != null) {
				// The saved .yml file will be more complete than the list used to refresh the signs
				MainTM.getInstance().signsConf.set(MainTM.SIGNS_SIGNSLIST + "." + keyName + "." + MainTM.SIGNS_WORLD, world);
				MainTM.getInstance().signsConf.set(MainTM.SIGNS_SIGNSLIST + "." + keyName + "." + MainTM.SIGNS_POSITION, pos);
				MainTM.getInstance().signsConf.set(MainTM.SIGNS_SIGNSLIST + "." + keyName + "." + MainTM.SIGNS_SIDE, side);
				MainTM.getInstance().signsConf.set(MainTM.SIGNS_SIGNSLIST + "." + keyName + "." + MainTM.SIGNS_MATERIAL, mat);
				MainTM.getInstance().signsConf.set(MainTM.SIGNS_SIGNSLIST + "." + keyName + "." + MainTM.SIGNS_FACE, face);
				MainTM.getInstance().signsConf.set(MainTM.SIGNS_SIGNSLIST + "." + keyName + "." + MainTM.SIGNS_CONTENT + "." + MainTM.SIGNS_LINE1, MainTM.getInstance().getConfig().getString(MainTM.CF_SIGNS + "." + MainTM.CF_SIGNS_MARKER));
				MainTM.getInstance().signsConf.set(MainTM.SIGNS_SIGNSLIST + "." + keyName + "." + MainTM.SIGNS_CONTENT + "." + MainTM.SIGNS_LINE2, tpl[0]);
				MainTM.getInstance().signsConf.set(MainTM.SIGNS_SIGNSLIST + "." + keyName + "." + MainTM.SIGNS_CONTENT + "." + MainTM.SIGNS_LINE3, tpl[1]);
				MainTM.getInstance().signsConf.set(MainTM.SIGNS_SIGNSLIST + "." + keyName + "." + MainTM.SIGNS_CONTENT + "." + MainTM.SIGNS_LINE4, tpl[2]);
			}
		}
		SignsFileHandler.SaveSignsYml();
	}

	/* ─────────────── Periodic refresh ─────────────── */

	/**
	 * Replacing placeholders in the text
	 */
	private static void refreshAll() {
		if (tracked.isEmpty()) return;
		List<String> stale = new ArrayList<>();
		for (String key : tracked) {
			String[] keys = key.split(":");
			String world = keys[0];
			String pos =  keys[1];
			String[] xyz = pos.split(",");						
			World w = Bukkit.getWorld(world);
			if (w == null) continue;			
			int x, y, z;
			try {
				x = Integer.parseInt(xyz[0]);
				y = Integer.parseInt(xyz[1]);
				z = Integer.parseInt(xyz[2]);
			} catch (NumberFormatException nfe) {
				stale.add(key); continue;
			}
			Block b = w.getBlockAt(x, y, z);
			if (!(b.getState() instanceof Sign)) {
				stale.add(key); continue; // sign broken or replaced
			}
			String whichSide = keys[2];
			Sign sign = (Sign) b.getState();
			String[] tpl = templates.get(key);
			if (tpl == null) continue;
			SignSide side = sign.getSide(Side.FRONT);
			if (whichSide.contains("BACK")) {
				side = sign.getSide(Side.BACK);
			}
			side.setLine(0, ChatColor.translateAlternateColorCodes('&', MainTM.getInstance().getConfig().getString(MainTM.CF_SIGNS + "." + MainTM.CF_SIGNS_MARKER, MainTM.getInstance().getConfig().getString(MainTM.CF_SIGNS + "." + MainTM.CF_SIGNS_MARKER))));
			side.setLine(1, resolvePlaceholder(tpl[0], world));
			side.setLine(2, resolvePlaceholder(tpl[1], world));
			side.setLine(3, resolvePlaceholder(tpl[2], world));
			sign.update(false, false);
		}
		if (!stale.isEmpty()) {
			tracked.removeAll(stale);
			for (String s : stale) templates.remove(s);
			saveToDisk();
		}
	}
	
	/**
	 * Replacing placeholders in the text
	 */
	private static String resolvePlaceholder(String line, String world) {
		if (line == null || line.isEmpty()) return "";
		String lang = MainTM.getInstance().langConf.getString(MainTM.LG_DEFAULTLANG);
		// World-context only — no player here.
		int pos = 0;
		StringBuilder out = new StringBuilder(line.length());
		while (true) {
			int open = line.indexOf("{" + MainTM.PH_PREFIX, pos);
			if (open < 0) {
				out.append(line, pos, line.length());
				break;
			}
			int close = line.indexOf('}', open + 1);
			if (close < 0) {
				out.append(line, pos, line.length());
				break;
			}
			out.append(line, pos, open);
			String token = line.substring(open, close + 1);
			String resolved;
			try {
				resolved = PlaceholdersHandler.replacePlaceholder(token, world, lang, null, false);
			} catch (Throwable t) {
				resolved = token;
			}
			out.append(resolved == null ? token : resolved);
			pos = close + 1;
		}
		return ChatColor.translateAlternateColorCodes('&', out.toString());
	}

	/* ─────────────── Listeners ─────────────── */

	/**
	 * Save the text of a sign
	 */
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		String marker = MainTM.getInstance().getConfig().getString(MainTM.CF_SIGNS + "." + MainTM.CF_SIGNS_MARKER);
		String line0 = ChatColor.stripColor(e.getLine(0) == null ? "" : e.getLine(0)).trim();
		if (!line0.equalsIgnoreCase(marker)) return;
		Player p = e.getPlayer();
		if (!p.hasPermission(MainTM.PERM_SIGNS_CREATE)) {
			p.sendMessage(ChatColor.RED + MainTM.signsCreationDeniedPlayerMsg); // Unauthorized Player msg
			e.setLine(0, "");
			return;
		}		
		Block b = e.getBlock();		
		Side whichSide = getSignSide(b, p);		
		String key = b.getWorld().getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ() + ":" + whichSide.name();
		tracked.add(key);
		templates.put(key, new String[] {
				e.getLine(1) == null ? "" : e.getLine(1),
				e.getLine(2) == null ? "" : e.getLine(2),
				e.getLine(3) == null ? "" : e.getLine(3),
		});
		saveToDisk();
		MsgHandler.playerAdminMsg(p, ChatColor.GOLD + MainTM.signsRegistredPlayerMsg); // Authorized Player msg
	}

	/**
	 * Keep the original placeholders when reopening a refreshed sign
	 */
	@EventHandler
	public void onSignOpen(PlayerInteractEvent e) {
		if (e.getClickedBlock() == null) return;
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) return;
		// Retrieve the block used and check if it is a sign
		Block b = e.getClickedBlock();
		Material m = e.getClickedBlock().getType();
		String material = m.name();	
		if (!material.endsWith("_SIGN")) return;		
		if (b.getState() instanceof Sign sign) {
			// Check that the panel starts with the correct marker [tm]
			Player p = e.getPlayer();	
			SignSide side = sign.getTargetSide(p);
			Side whichSide = getSignSide(b, p);
			String line1 = side.getLine(0);
			MsgHandler.debugMsg("Player §e" + p + " §b" + MainTM.signsOpenPart1DebugMsg + whichSide.name() + MainTM.signsOpenPart2DebugMsg + " §e" + line1); // Console debug msg
			String marker = MainTM.getInstance().getConfig().getString(MainTM.CF_SIGNS + "." + MainTM.CF_SIGNS_MARKER);			
			if (!line1.equalsIgnoreCase(marker)) return;
			// It's important to cancel the opening of the sign in order to replace it later.
			e.setCancelled(true);			
			// Find the correct key and the corresponding lines of text in the existing list
			String line2;
			String line3;
			String line4;
			World w = b.getWorld();
			for (String ymlKey : MainTM.getInstance().signsConf.getConfigurationSection(MainTM.SIGNS_SIGNSLIST).getKeys(false)) {
				if (MainTM.getInstance().signsConf.getString(MainTM.SIGNS_SIGNSLIST + "." + ymlKey + "." + MainTM.SIGNS_WORLD).equals(w.getName())
						&& MainTM.getInstance().signsConf.getString(MainTM.SIGNS_SIGNSLIST + "." + ymlKey + "." + MainTM.SIGNS_POSITION).equals(b.getX() + "," + b.getY() + "," + b.getZ())
						&& MainTM.getInstance().signsConf.getString(MainTM.SIGNS_SIGNSLIST + "." + ymlKey + "." + MainTM.SIGNS_SIDE).equals(whichSide.name())) {
					MsgHandler.errorMsg("[onSignOpen] Le panneau ouvert est le n°" + ymlKey);
					MsgHandler.errorMsg("[onSignOpen] Evenement annulé");
					line2 = MainTM.getInstance().signsConf.getString(MainTM.SIGNS_SIGNSLIST + "." + ymlKey + "." + MainTM.SIGNS_CONTENT + "." + MainTM.SIGNS_LINE2);
					line3 = MainTM.getInstance().signsConf.getString(MainTM.SIGNS_SIGNSLIST + "." + ymlKey + "." + MainTM.SIGNS_CONTENT + "." + MainTM.SIGNS_LINE3);
					line4 = MainTM.getInstance().signsConf.getString(MainTM.SIGNS_SIGNSLIST + "." + ymlKey + "." + MainTM.SIGNS_CONTENT + "." + MainTM.SIGNS_LINE4);
					// Replace the lines of text in the sign that will be read/modified and update it
					side.setLine(1, line2);
					side.setLine(2, line3);
					side.setLine(3, line4);
					MsgHandler.debugMsg(MainTM.signsRestoredLinesDebugMsg);
					MsgHandler.debugMsg("[1] §e" + line1);
					MsgHandler.debugMsg("[2] §e" + line2);
					MsgHandler.debugMsg("[3] §e" + line3);
					MsgHandler.debugMsg("[4] §e" + line4);
					sign.update();								
					// Open the updated sign with a slight delay
					Bukkit.getScheduler().runTaskLater(MainTM.getInstance(), () -> {
						p.openSign(sign, whichSide);
					}, 2L);					
				}
			}
		}
	}
	
	/**
	 * Remove the destroyed signs from the list
	 */
	@EventHandler // In case the sign is destroyed by a player
	public void onBlockBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		World w = b.getWorld();
		String key = w.getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ() + ":" + Side.FRONT.name();
		if (tracked.contains(key)) templates.remove(key);	
		key = w.getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ() + ":" + Side.BACK.name();
		if (tracked.contains(key)) templates.remove(key);		
		for (String ymlKey : MainTM.getInstance().signsConf.getConfigurationSection(MainTM.SIGNS_SIGNSLIST).getKeys(false)) {
			if (MainTM.getInstance().signsConf.getString(MainTM.SIGNS_SIGNSLIST + "." + ymlKey + "." + MainTM.SIGNS_WORLD).equals(w.getName())
					&& MainTM.getInstance().signsConf.getString(MainTM.SIGNS_SIGNSLIST + "." + ymlKey + "." + MainTM.SIGNS_POSITION).equals(b.getX() + "," + b.getY() + "," + b.getZ())) {
				MainTM.getInstance().signsConf.set(MainTM.SIGNS_SIGNSLIST + "." + ymlKey, null);
				MsgHandler.debugMsg("Sign N° §e"+ ymlKey + "§b " + MainTM.signsDestroyedPart1DebugMsg + " player §e" + e.getPlayer().getName() + "§b " + MainTM.signsDestroyedPart2DebugMsg); // Console debug msg
		}
			SignsFileHandler.SaveSignsYml();
		}
	}
	@EventHandler // In case the sign is destroyed by an entity (Creeper, TNT, ...)
	public void onEntityExplode(EntityExplodeEvent e) {
	    for (Block b : e.blockList()) {
	    	if (b.getState() instanceof Sign) {
				World w = b.getWorld();
				String key = w.getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ() + ":" + Side.FRONT.name();
				if (tracked.contains(key)) templates.remove(key);	
				key = w.getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ() + ":" + Side.BACK.name();
				if (tracked.contains(key)) templates.remove(key);		
				for (String ymlKey : MainTM.getInstance().signsConf.getConfigurationSection(MainTM.SIGNS_SIGNSLIST).getKeys(false)) {
					if (MainTM.getInstance().signsConf.getString(MainTM.SIGNS_SIGNSLIST + "." + ymlKey + "." + MainTM.SIGNS_WORLD).equals(w.getName())
							&& MainTM.getInstance().signsConf.getString(MainTM.SIGNS_SIGNSLIST + "." + ymlKey + "." + MainTM.SIGNS_POSITION).equals(b.getX() + "," + b.getY() + "," + b.getZ())) {
						MainTM.getInstance().signsConf.set(MainTM.SIGNS_SIGNSLIST + "." + ymlKey, null);
						MsgHandler.debugMsg("Sign N° §e"+ ymlKey + "§b " + MainTM.signsDestroyedPart1DebugMsg + " an entity explosion (§e"+ e.getEntity().getName() +"§b) " + MainTM.signsDestroyedPart2DebugMsg); // Console debug msg
				}
			}
		}
			SignsFileHandler.SaveSignsYml();
		}
	}
	@EventHandler // In case the sign is destroyed by a block explosion
	public void onBlockExplode(BlockExplodeEvent e) {
	    for (Block b : e.blockList()) {
	    	if (b.getState() instanceof Sign) {
				World w = b.getWorld();
				String key = w.getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ() + ":" + Side.FRONT.name();
				if (tracked.contains(key)) templates.remove(key);	
				key = w.getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ() + ":" + Side.BACK.name();
				if (tracked.contains(key)) templates.remove(key);		
				for (String ymlKey : MainTM.getInstance().signsConf.getConfigurationSection(MainTM.SIGNS_SIGNSLIST).getKeys(false)) {
					if (MainTM.getInstance().signsConf.getString(MainTM.SIGNS_SIGNSLIST + "." + ymlKey + "." + MainTM.SIGNS_WORLD).equals(w.getName())
							&& MainTM.getInstance().signsConf.getString(MainTM.SIGNS_SIGNSLIST + "." + ymlKey + "." + MainTM.SIGNS_POSITION).equals(b.getX() + "," + b.getY() + "," + b.getZ())) {
						MainTM.getInstance().signsConf.set(MainTM.SIGNS_SIGNSLIST + "." + ymlKey, null);
						MsgHandler.debugMsg("Sign N° §e"+ ymlKey + "§b " + MainTM.signsDestroyedPart1DebugMsg + " a block explosion " + MainTM.signsDestroyedPart2DebugMsg); // Console debug msg
				}
			}
		}
			SignsFileHandler.SaveSignsYml();
		}
	}
	@EventHandler
	public void onPhysics(BlockPhysicsEvent e) {
	    Block b = e.getBlock();
    	if (b.getState() instanceof Sign) {
			World w = b.getWorld();
			String key = w.getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ() + ":" + Side.FRONT.name();
			if (tracked.contains(key)) templates.remove(key);	
			key = w.getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ() + ":" + Side.BACK.name();
			if (tracked.contains(key)) templates.remove(key);		
			for (String ymlKey : MainTM.getInstance().signsConf.getConfigurationSection(MainTM.SIGNS_SIGNSLIST).getKeys(false)) {
				if (MainTM.getInstance().signsConf.getString(MainTM.SIGNS_SIGNSLIST + "." + ymlKey + "." + MainTM.SIGNS_WORLD).equals(w.getName())
						&& MainTM.getInstance().signsConf.getString(MainTM.SIGNS_SIGNSLIST + "." + ymlKey + "." + MainTM.SIGNS_POSITION).equals(b.getX() + "," + b.getY() + "," + b.getZ())) {
					MainTM.getInstance().signsConf.set(MainTM.SIGNS_SIGNSLIST + "." + ymlKey, null);
					MsgHandler.debugMsg("Sign N° §e"+ ymlKey + "§b " + MainTM.signsDestroyedPart1DebugMsg + " physics " + MainTM.signsDestroyedPart2DebugMsg); // Console debug msg
			}
		}
	}
		SignsFileHandler.SaveSignsYml();
	}
	
	/**
	 * Retrieve the orientation of the written face of the panel (useful for human-readable YAML files)
	 */
	public String getSignFace(Block b) {
		BlockData data = b.getBlockData();
		BlockFace f = null;
		if (data instanceof Directional directional) {
			f = directional.getFacing();
		} else if (data instanceof org.bukkit.block.data.type.Sign s) {
			f = s.getRotation();
		}
		return f.name();
	}

	/**
	 * Retrieve the side of the sign face that is used (necessary to distinguish the two faces in different entries of the list)
	 */
	public Side getSignSide(Block b, Player p) {
		Side whichSide = Side.FRONT;
		if (b.getState() instanceof Sign sign) {
			SignSide side = sign.getTargetSide(p);
			if (side == sign.getSide(Side.BACK)) whichSide = Side.BACK;
		}
		return whichSide;
	}

};