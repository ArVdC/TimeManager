package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.PlayerLangHandler;
import net.vdcraft.arvdc.timemanager.placeholders.PlaceholdersHandler;

/**
 * Optional ActionBar HUD that broadcasts a configurable time/day string to
 * online players. Disabled by default — admins enable via
 * {@code hud.actionbar.enabled: true} in config.yml.
 *
 * Config keys:
 * <pre>
 * hud:
 *   actionbar:
 *     enabled: false
 *     refresh-rate: 20        # ticks between updates
 *     format: "&7%tm_time24% &8| &eDay %tm_serverday%"
 *     worlds: []              # empty list = all worlds; otherwise list world names
 * </pre>
 *
 * Players can opt out individually with {@code /tm hud off} (in-memory only —
 * preference resets on restart, kept intentionally simple).
 */
public class ActionBarHandler {

	private static final String CF_HUD_ENABLED = "hud.actionbar.enabled";
	private static final String CF_HUD_REFRESH = "hud.actionbar.refresh-rate";
	private static final String CF_HUD_FORMAT  = "hud.actionbar.format";
	private static final String CF_HUD_WORLDS  = "hud.actionbar.worlds";

	/** Players who explicitly opted out via /tm hud off. */
	private static final Set<UUID> optedOut = new HashSet<>();
	private static int taskId = -1;

	/**
	 * Ensure the HUD-related config keys exist with defaults. Called from
	 * {@link CfgFileHandler}.
	 */
	public static void ensureDefaults() {
		if (!MainTM.getInstance().getConfig().contains(CF_HUD_ENABLED)) {
			MainTM.getInstance().getConfig().set(CF_HUD_ENABLED, false);
		}
		if (!MainTM.getInstance().getConfig().contains(CF_HUD_REFRESH)) {
			MainTM.getInstance().getConfig().set(CF_HUD_REFRESH, 20);
		}
		if (!MainTM.getInstance().getConfig().contains(CF_HUD_FORMAT)) {
			MainTM.getInstance().getConfig().set(CF_HUD_FORMAT,
					"&7{tm_time24} &8| &eDay {tm_serverday}");
		}
		if (!MainTM.getInstance().getConfig().contains(CF_HUD_WORLDS)) {
			MainTM.getInstance().getConfig().set(CF_HUD_WORLDS, new java.util.ArrayList<String>());
		}
	}

	/** Start or restart the broadcasting task based on current config. */
	public static void startOrRestart() {
		// Cancel previous task if any
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
		if (!MainTM.getInstance().getConfig().getBoolean(CF_HUD_ENABLED, false)) {
			return;
		}
		int refresh = Math.max(5, MainTM.getInstance().getConfig().getInt(CF_HUD_REFRESH, 20));
		taskId = new BukkitRunnable() {
			@Override
			public void run() {
				try {
					broadcast();
				} catch (Throwable t) {
					// BukkitRunnable swallows exceptions thrown by a repeating
					// task on some Paper builds — catch + log explicitly so
					// configuration / placeholder bugs don't fail silently.
					// Cancel ourselves to avoid hammering the log forever.
					MainTM.getInstance().getLogger().warning(
							"ActionBar HUD broadcast failed: "
									+ t.getClass().getSimpleName() + ": " + t.getMessage());
					cancel();
					taskId = -1;
				}
			}
		}.runTaskTimer(MainTM.getInstance(), refresh, refresh).getTaskId();
	}

	/** Send one round of action bars to all eligible players. */
	private static void broadcast() {
		String format = MainTM.getInstance().getConfig().getString(CF_HUD_FORMAT,
				"&7{tm_time24} &8| &eDay {tm_serverday}");
		List<String> worldsFilter = MainTM.getInstance().getConfig().getStringList(CF_HUD_WORLDS);

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (optedOut.contains(p.getUniqueId())) continue;
			String world = p.getWorld().getName();
			if (!worldsFilter.isEmpty() && !worldsFilter.contains(world)) continue;

			// Resolve plugin placeholders (no PAPI dependency) — supports
			// curly-brace {tm_X} form.
			String lang = PlayerLangHandler.setLangToUse(p);
			String resolved = format;
			// Substitute every known placeholder. Done by calling the central
			// PlaceholdersHandler once per occurrence — cheap, format strings
			// are short.
			resolved = resolveAll(resolved, world, lang, p);
			// Legacy color codes (&7 etc.)
			resolved = ChatColor.translateAlternateColorCodes('&', resolved);

			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(resolved));
		}
	}

	private static String resolveAll(String input, String world, String lang, Player p) {
		// Quick scan for {tm_*} occurrences and replace each via
		// PlaceholdersHandler. Avoids per-tick regex compile cost by using
		// indexOf / substring.
		int pos = 0;
		StringBuilder out = new StringBuilder(input.length());
		while (true) {
			int open = input.indexOf("{" + MainTM.PH_PREFIX, pos);
			if (open < 0) {
				out.append(input, pos, input.length());
				break;
			}
			int close = input.indexOf('}', open + 1);
			if (close < 0) { // unterminated — bail
				out.append(input, pos, input.length());
				break;
			}
			out.append(input, pos, open);
			String token = input.substring(open, close + 1);
			String resolved = PlaceholdersHandler.replacePlaceholder(token, world, lang, p);
			if (resolved == null) resolved = token; // unknown — leave literal
			out.append(resolved);
			pos = close + 1;
		}
		return out.toString();
	}

	public static void setOptOut(UUID uuid, boolean off) {
		if (off) optedOut.add(uuid); else optedOut.remove(uuid);
	}

	public static boolean isOptedOut(UUID uuid) {
		return optedOut.contains(uuid);
	}
}
