package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;

/**
 * /tm set sleepAnimation [on|off|toggle|instant] [all|world]
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

	public static void cmdSetSleepAnimation(CommandSender sender, String onOff, String world) {

		// Modify all worlds
		if (world.equalsIgnoreCase(ARG_ALL)) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdSetSleepAnimation(sender, onOff, listedWorld);
			}
		}
		
		// #1. Validate
		if (Bukkit.getWorld(world) == null) {
			sender.sendMessage(ChatColor.RED + "Unknown world: " + ChatColor.YELLOW + world);
			return;
		}
		if (!MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			sender.sendMessage(ChatColor.RED + "World " + ChatColor.YELLOW + world
					+ ChatColor.RED + " isn't tracked by TimeManager.");
			return;
		}

		String key = CF_WORLDSLIST + "." + world + "." + CF_NIGHTSKIP_MODE;
		String current = MainTM.getInstance().getConfig().getString(key, ARG_DEFAULT);
		boolean isOn = current.equalsIgnoreCase(ARG_ANIMATION);

		boolean enable;
		if (onOff == null || onOff.isEmpty() || onOff.equalsIgnoreCase(ARG_TOGGLE)) {
			enable = !isOn;
		} else if (onOff.equalsIgnoreCase(ARG_ON) || onOff.equalsIgnoreCase(ARG_TRUE)
				|| onOff.equalsIgnoreCase(ARG_ANIMATION)) {
			enable = true;
		} else if (onOff.equalsIgnoreCase(ARG_OFF) || onOff.equalsIgnoreCase(ARG_FALSE)
				|| onOff.equalsIgnoreCase(ARG_DEFAULT)) {
			enable = false;
		} else if (onOff.equalsIgnoreCase(ARG_INSTANT)) {
			// Pass through explicit 'instant' option from the original nightSkipMode enum.
			MainTM.getInstance().getConfig().set(key, ARG_INSTANT);
			MainTM.getInstance().saveConfig();
			sender.sendMessage(ChatColor.GOLD + "nightSkipMode for " + ChatColor.YELLOW + world
					+ ChatColor.GOLD + " is now " + ChatColor.YELLOW + ARG_INSTANT);
			return;
		} else {
			sender.sendMessage(ChatColor.RED + "Usage: /tm animation <world> [on|off|toggle|instant]");
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
	
};