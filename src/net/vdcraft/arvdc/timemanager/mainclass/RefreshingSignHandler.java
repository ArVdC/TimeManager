package net.vdcraft.arvdc.timemanager.mainclass;

import java.io.File;
import java.io.IOException;
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
import org.bukkit.block.Sign;
import org.bukkit.block.sign.SignSide;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.PlayerLangHandler;
import net.vdcraft.arvdc.timemanager.placeholders.PlaceholdersHandler;

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

	private static final String CF_SIGNS_ENABLED = "signs.enabled";
	private static final String CF_SIGNS_REFRESH = "signs.refresh-rate";
	private static final String CF_SIGNS_MARKER  = "signs.marker";

	/** In-memory cache of tracked signs, keyed by "<world>:<x>,<y>,<z>". */
	private static final Set<String> tracked = new HashSet<>();
	/** Original line2..4 text (with placeholders) per sign key. */
	private static final java.util.Map<String, String[]> templates = new java.util.HashMap<>();
	private static int taskId = -1;
	private static File signsFile;

	/** Wire-up: ensure config defaults, load persisted signs, start task. */
	public static void init() {
		ensureDefaults();
		signsFile = new File(MainTM.getInstance().getDataFolder(), "signs.yml");
		loadFromDisk();
		startOrRestart();
	}

	private static void ensureDefaults() {
		if (!MainTM.getInstance().getConfig().contains(CF_SIGNS_ENABLED)) {
			MainTM.getInstance().getConfig().set(CF_SIGNS_ENABLED, true);
		}
		if (!MainTM.getInstance().getConfig().contains(CF_SIGNS_REFRESH)) {
			MainTM.getInstance().getConfig().set(CF_SIGNS_REFRESH, 40);
		}
		if (!MainTM.getInstance().getConfig().contains(CF_SIGNS_MARKER)) {
			MainTM.getInstance().getConfig().set(CF_SIGNS_MARKER, "[tm]");
		}
	}

	public static void startOrRestart() {
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
		if (!MainTM.getInstance().getConfig().getBoolean(CF_SIGNS_ENABLED, true)) return;

		int refresh = Math.max(5, MainTM.getInstance().getConfig().getInt(CF_SIGNS_REFRESH, 40));
		taskId = new BukkitRunnable() {
			@Override
			public void run() { refreshAll(); }
		}.runTaskTimer(MainTM.getInstance(), refresh, refresh).getTaskId();
	}

	/* ─────────────── Persistence ─────────────── */

	private static void loadFromDisk() {
		tracked.clear();
		templates.clear();
		if (signsFile == null || !signsFile.exists()) return;
		YamlConfiguration y = YamlConfiguration.loadConfiguration(signsFile);
		ConfigurationSection root = y.getConfigurationSection("signsList");
		if (root == null) return;
		for (String id : root.getKeys(false)) {
			ConfigurationSection s = root.getConfigurationSection(id);
			if (s == null) continue;
			String world = s.getString("world");
			String pos = s.getString("position");
			if (world == null || pos == null || pos.isEmpty()) continue;
			String[] xyz = pos.replace(" ", "").split(",");
			if (xyz.length != 3) continue;
			String key = world + ":" + xyz[0] + "," + xyz[1] + "," + xyz[2];
			tracked.add(key);
			ConfigurationSection content = s.getConfigurationSection("content");
			if (content != null) {
				String[] tpl = new String[3];
				tpl[0] = content.getString("line2", "");
				tpl[1] = content.getString("line3", "");
				tpl[2] = content.getString("line4", "");
				templates.put(key, tpl);
			}
		}
		MsgHandler.infoMsg("Loaded " + tracked.size() + " refreshing sign(s) from disk.");
	}

	private static void saveToDisk() {
		if (signsFile == null) return;
		YamlConfiguration y = new YamlConfiguration();
		int i = 1;
		for (String key : tracked) {
			int colon = key.indexOf(':');
			String world = key.substring(0, colon);
			String pos = key.substring(colon + 1);
			String base = "signsList." + String.format("%02d", i++);
			y.set(base + ".world", world);
			y.set(base + ".position", pos);
			String[] tpl = templates.get(key);
			if (tpl != null) {
				y.set(base + ".content.line1", "[tm]");
				y.set(base + ".content.line2", tpl[0]);
				y.set(base + ".content.line3", tpl[1]);
				y.set(base + ".content.line4", tpl[2]);
			}
		}
		try {
			y.save(signsFile);
		} catch (IOException e) {
			MsgHandler.errorMsg("Failed to save signs.yml: " + e.getMessage());
		}
	}

	/* ─────────────── Periodic refresh ─────────────── */

	private static void refreshAll() {
		if (tracked.isEmpty()) return;
		List<String> stale = new ArrayList<>();
		for (String key : tracked) {
			int colon = key.indexOf(':');
			String worldName = key.substring(0, colon);
			String[] xyz = key.substring(colon + 1).split(",");
			World w = Bukkit.getWorld(worldName);
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
			Sign sign = (Sign) b.getState();
			String[] tpl = templates.get(key);
			if (tpl == null) continue;
			SignSide side = sign.getSide(Side.FRONT);
			side.setLine(0, ChatColor.translateAlternateColorCodes('&',
					MainTM.getInstance().getConfig().getString(CF_SIGNS_MARKER, "[tm]")));
			side.setLine(1, resolve(tpl[0], worldName));
			side.setLine(2, resolve(tpl[1], worldName));
			side.setLine(3, resolve(tpl[2], worldName));
			sign.update(false, false);
		}
		if (!stale.isEmpty()) {
			tracked.removeAll(stale);
			for (String s : stale) templates.remove(s);
			saveToDisk();
		}
	}

	private static String resolve(String line, String world) {
		if (line == null || line.isEmpty()) return "";
		String lang = "en";
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
				resolved = PlaceholdersHandler.replacePlaceholder(token, world, lang, null);
			} catch (Throwable t) {
				resolved = token;
			}
			out.append(resolved == null ? token : resolved);
			pos = close + 1;
		}
		return ChatColor.translateAlternateColorCodes('&', out.toString());
	}

	/* ─────────────── Listeners ─────────────── */

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		String marker = MainTM.getInstance().getConfig().getString(CF_SIGNS_MARKER, "[tm]");
		String line0 = ChatColor.stripColor(e.getLine(0) == null ? "" : e.getLine(0)).trim();
		if (!line0.equalsIgnoreCase(marker)) return;

		Player p = e.getPlayer();
		if (!p.hasPermission("timemanager.signs.create")) {
			p.sendMessage(ChatColor.RED + "You don't have permission to create TimeManager signs.");
			e.setLine(0, "");
			return;
		}

		Block b = e.getBlock();
		String key = b.getWorld().getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ();
		tracked.add(key);
		templates.put(key, new String[] {
				e.getLine(1) == null ? "" : e.getLine(1),
				e.getLine(2) == null ? "" : e.getLine(2),
				e.getLine(3) == null ? "" : e.getLine(3),
		});
		saveToDisk();
		p.sendMessage(ChatColor.GOLD + "TimeManager sign registered. Placeholders will refresh automatically.");
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		Material m = b.getType();
		if (!m.name().endsWith("_SIGN") && !m.name().endsWith("_WALL_SIGN")) return;
		String key = b.getWorld().getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ();
		if (tracked.remove(key)) {
			templates.remove(key);
			saveToDisk();
		}
	}
}
