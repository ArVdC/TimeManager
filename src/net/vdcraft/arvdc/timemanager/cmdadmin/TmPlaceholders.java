package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;

/**
 * /tm placeholders
 *
 * Lists every placeholder the plugin exposes — both the legacy {curly-brace}
 * form used inside in-game messages (TmNow, signs, etc.) and the PlaceholderAPI
 * form {@code %tm_<name>%} for other plugins (TAB, scoreboards, holograms).
 *
 * Permission: {@link MainTM#PERM_PLACEHOLDERS} (granted to ops by default).
 */
public class TmPlaceholders extends MainTM {

	private static final String[][] ROWS = new String[][] {
		// {placeholder, description}
		{ PH_PLAYER,     "Player name" },
		{ PH_WORLD,      "Player's current world" },
		{ PH_TICK,       "Current world tick (0–23999)" },
		{ PH_TIME12,     "Time in 12h format (e.g. 02:30 PM)" },
		{ PH_TIME24,     "Time in 24h format (e.g. 14:30)" },
		{ PH_HOURS12,    "Hours in 12h format" },
		{ PH_HOURS24,    "Hours in 24h format" },
		{ PH_MINUTES,    "Minutes" },
		{ PH_SECONDS,    "Seconds" },
		{ PH_AMPM,       "AM / PM" },
		{ PH_DAYPART,    "Day phase (dawn, day, dusk, night, midnight)" },
		{ PH_C_DAY,      "Current world day number" },
		{ PH_E_DAYS,     "Total elapsed days in the world" },
		{ PH_DAYNAME,    "Day of week (Monday, Tuesday, ...)" },
		{ PH_YEARDAY,    "Day of year (1–365)" },
		{ PH_YEARWEEK,   "Week of year (1–52)" },
		{ PH_WEEK,       "Week number" },
		{ PH_MONTHNAME,  "Month name (January, February, ...)" },
		{ PH_DD,         "Day of month (01–31)" },
		{ PH_MM,         "Month (01–12)" },
		{ PH_YY,         "Year, 2-digit" },
		{ PH_YYYY,       "Year, 4-digit" },
		{ PH_SERVERDAY,  "Server-wide elapsed days (persists across restarts)" },
	};

	public static void cmdPlaceholders(CommandSender sender) {
		if (sender instanceof org.bukkit.entity.Player
				&& !sender.hasPermission(PERM_PLACEHOLDERS)) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to view placeholders.");
			return;
		}

		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "TimeManager placeholders");
		sender.sendMessage(ChatColor.GRAY + "PAPI form: " + ChatColor.YELLOW + "%" + PH_PREFIX + "<name>%"
				+ ChatColor.GRAY + "   In-message form: " + ChatColor.YELLOW + "{" + PH_PREFIX + "<name>}");
		sender.sendMessage(ChatColor.GRAY + "-------------------------------------------------------");

		for (String[] row : ROWS) {
			sender.sendMessage(
					ChatColor.YELLOW + "%" + PH_PREFIX + row[0] + "%"
					+ ChatColor.DARK_GRAY + "  —  "
					+ ChatColor.GRAY + row[1]);
		}

		sender.sendMessage(ChatColor.GRAY + "-------------------------------------------------------");
		sender.sendMessage(ChatColor.GRAY + "Total: " + ChatColor.YELLOW + ROWS.length + ChatColor.GRAY + " placeholders.");
	}
}
