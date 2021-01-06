package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class TmSetFullTime extends MainTM {

	/**
	 * CMD /tm set day [number] [world]
	 */
	public static void cmdSetDay(CommandSender sender, Long elapsedDays, String world) {
		// If using a world name in several parts
		if (sender instanceof Player)
			world = ValuesConverter.restoreSpacesInString(world);
		// Adapt wrong values in the arg
		if (elapsedDays < 0 ) elapsedDays = 0L;

		// Modify all worlds
		if (world.equalsIgnoreCase("all")) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
				cmdSetDay(sender, elapsedDays, listedWorld);
			}
		}
		// Else, if the string argument is a listed world, modify a single world
		else if (MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(world)) {
			// Modify targeted world's timer
			World w = Bukkit.getWorld(world);
			Long t = w.getTime();
			Long tickToSet = (elapsedDays * 24000) + t;
			w.setFullTime(tickToSet);

			// Notifications
			String date = ValuesConverter.returnDateFromDays(elapsedDays, "yyyy") + "-" + ValuesConverter.returnDateFromDays(elapsedDays, "mm") + "-" + ValuesConverter.returnDateFromDays(elapsedDays, "dd");
			Bukkit.getLogger().info(prefixTM + " " + worldFullTimeChgMsg + " " + world + " " + worldTimeChgMsg2 + " #" + elapsedDays + " (" + date + ")."); // Console final msg (always)
			if (sender instanceof Player) {
				sender.sendMessage(prefixTMColor + " " + worldFullTimeChgMsg + " §e" + world + "§r " + worldTimeChgMsg2 + " §e#" + elapsedDays + " §r(§e" + date + "§r)."); // Player final msg (in case)
			}
		}
		// Else, return an error and help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "set day");
		}
	}

};