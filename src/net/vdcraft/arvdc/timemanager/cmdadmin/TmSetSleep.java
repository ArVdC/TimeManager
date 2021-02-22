package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class TmSetSleep extends MainTM {

	/**
	 * CMD /tm set sleep [boolean] [world]
	 */
	public static void cmdSetSleep(CommandSender sender, String sleepOrNo, String world) {
		// If using a world name in several parts
		if (sender instanceof Player)
			world = ValuesConverter.restoreSpacesInString(world);

		// Modify all worlds
		if (world.equalsIgnoreCase("all")) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdSetSleep(sender, sleepOrNo, listedWorld);
			}
		}
		// Else, if the string argument is a listed world, modify a single world
		else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			// Avoid impossible values
			if (!sleepOrNo.equalsIgnoreCase("true")) sleepOrNo = "false";
			World w = Bukkit.getWorld(world);
			long t = w.getTime();
			String currentSpeed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t));
			if ((sleepOrNo.equalsIgnoreCase("true") && (currentSpeed.equals("0.0") || currentSpeed.equals("24.0")))) {
				// Notifications
				MsgHandler.infoMsg(worldSleepNoChgMsg + " " + world + "."); // Console final msg (always)
				MsgHandler.playerMsg(sender, worldSleepNoChgMsg + " §e" + world + "§r."); // Player final msg (in case)
			} else {
				// Modify the value
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SLEEP, sleepOrNo);
				// Avoid to synchronize worlds where players can sleep
				if (sleepOrNo.equalsIgnoreCase("true") && MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equals("true")) {
					MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, "false");
					MsgHandler.infoMsg("The world " + world + " " + sleepWorldSyncChgMsg); // Console warn msg (always)
					MsgHandler.playerMsg(sender, "The world §e" + world + "§r " + sleepWorldSyncChgMsg); // Player warn msg (in case)
				}
				// Save the value(s) in the config.yml
				MainTM.getInstance().saveConfig();
				// Notifications
				if (sleepOrNo.equals("true")) {
					MsgHandler.infoMsg(worldSleepTrueChgMsg + " " + world + "."); // Console final msg (always)
					MsgHandler.playerMsg(sender, worldSleepTrueChgMsg + " §e" + world + "§r."); // Player final msg (in case)
				} else if (sleepOrNo.equals("false")) {
					MsgHandler.infoMsg(worldSleepFalseChgMsg + " " + world + "."); // Console final msg (always)
					MsgHandler.playerMsg(sender, worldSleepFalseChgMsg + " §e" + world + "§r."); // Player final msg (in case)
				}
			}
		}
		// Else, return an error and help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_SLEEP);
		}
	}

};