package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;

/**
 * /tm animation [on|off|toggle] <world|all>
 *
 * Shortcut for flipping {@code worldsList.<world>.nightSkipMode} between
 * {@code animation} and {@code default}. When {@code animation} is on, the
 * sleep particle show (DUST_COLOR_TRANSITION + the enhanced END_ROD / GLOW /
 * FIREWORK layers) plays during the night-skip sequence; otherwise the
 * vanilla instant skip is used.
 *
 * No argument or {@code toggle} flips the current value. {@code on} forces
 * animation; {@code off} forces default.
 */
public class TmSetSleepAnimation extends MainTM {

	public static void cmdSetSleepAnimation(CommandSender sender, String onOffArg, String world) {

		// #1. Modify all worlds
		if (world.equalsIgnoreCase(ARG_ALL)) {
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdSetSleepAnimation(sender, onOffArg, listedWorld);
			}
			return;
		}

		// #2. Validate world name
		else if (Bukkit.getWorld(world) == null) {
			sender.sendMessage(ChatColor.RED + "Unknown world: " + ChatColor.YELLOW + world);
			return;
		} else if (!MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			sender.sendMessage(ChatColor.RED + "World " + ChatColor.YELLOW + world + ChatColor.RED + " isn't tracked by TimeManager.");
			return;
		}

		String key = CF_WORLDSLIST + "." + world + "." + CF_NIGHTSKIP_MODE;
		String current = MainTM.getInstance().getConfig().getString(key, ARG_DEFAULT);
		boolean isOn = current.equalsIgnoreCase(ARG_ANIMATION);

		boolean enable;
		if (onOffArg == null || onOffArg.isEmpty() || onOffArg.equalsIgnoreCase("toggle")) {
			enable = !isOn;
		} else if (onOffArg.equalsIgnoreCase("on") || onOffArg.equalsIgnoreCase("true")
				|| onOffArg.equalsIgnoreCase(ARG_ANIMATION)) {
			enable = true;
		} else if (onOffArg.equalsIgnoreCase("off") || onOffArg.equalsIgnoreCase("false")
				|| onOffArg.equalsIgnoreCase(ARG_DEFAULT)) {
			enable = false;
		} else if (onOffArg.equalsIgnoreCase(ARG_INSTANT)) {
			// Pass through explicit 'instant' option from the original
			// nightSkipMode enum.
			MainTM.getInstance().getConfig().set(key, ARG_INSTANT);
			MainTM.getInstance().saveConfig();
			sender.sendMessage(ChatColor.GOLD + "nightSkipMode for " + ChatColor.YELLOW + world
					+ ChatColor.GOLD + " is now " + ChatColor.YELLOW + ARG_INSTANT);
			return;
		} else {
			sender.sendMessage(ChatColor.RED + "Usage: /tm animation [on|off|toggle|instant] [all|world]");
			return;
		}

		String newValue = enable ? ARG_ANIMATION : ARG_DEFAULT;
		MainTM.getInstance().getConfig().set(key, newValue);
		MainTM.getInstance().saveConfig();

		sender.sendMessage(ChatColor.GOLD + "Sleep animation on " + ChatColor.YELLOW + world
				+ ChatColor.GOLD + " is now "
				+ (enable ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF")
				+ ChatColor.GRAY + " (nightSkipMode = " + newValue + ").");
	}
}
