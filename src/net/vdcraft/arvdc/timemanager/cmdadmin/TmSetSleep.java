package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class TmSetSleep extends MainTM {

	/**
	 * CMD /tm set sleep [true|false|linked] [world]
	 */
	public static void cmdSetSleep(CommandSender sender, String sleepValue, String world) {

		// Modify all worlds
		if (world.equalsIgnoreCase(ARG_ALL)) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdSetSleep(sender, sleepValue, listedWorld);
			}
		}
		// Else, if the string argument is a listed world, modify a single world
		else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			// Avoid impossible values
			if (!sleepValue.equalsIgnoreCase(ARG_TRUE) && !sleepValue.equalsIgnoreCase(ARG_LINKED)) sleepValue = ARG_FALSE;
			World w = Bukkit.getWorld(world);
			long t = w.getTime();
			String currentSpeed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t));
			if ((sleepValue.equalsIgnoreCase(ARG_TRUE) || (sleepValue.equalsIgnoreCase(ARG_LINKED)) && (currentSpeed.equals("24.0")))) {
				// Notifications
				MsgHandler.infoMsg(worldSleepNoChgMsg + " " + world + "."); // Console final msg (always)
				MsgHandler.playerAdminMsg(sender, worldSleepNoChgMsg + " §e" + world + "§r."); // Player final msg (in case)
			} else {
				// Modify the value
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SLEEP, sleepValue);
				// Avoid to synchronize worlds where players can sleep
				if ((sleepValue.equalsIgnoreCase(ARG_TRUE) || sleepValue.equalsIgnoreCase(ARG_LINKED)) && MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equals(ARG_TRUE)) {
					MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, ARG_FALSE);
					MsgHandler.infoMsg("The world " + world + " " + sleepWorldSyncChgMsg); // Console warn msg (always)
					MsgHandler.playerAdminMsg(sender, "The world §e" + world + "§r " + sleepWorldSyncChgMsg); // Player warn msg (in case)
				}
				// Save the value(s) in the config.yml
				MainTM.getInstance().saveConfig();
				// Notifications
				if (sleepValue.equalsIgnoreCase(ARG_TRUE)) {
					MsgHandler.infoMsg(worldSleepTrueChgMsg + " " + world + "."); // Console final msg (always)
					MsgHandler.playerAdminMsg(sender, worldSleepTrueChgMsg + " §e" + world + "§r."); // Player final msg (in case)
				} else if (sleepValue.equalsIgnoreCase(ARG_LINKED)) {
					MsgHandler.infoMsg(worldSleepTrueChgMsg + " " + world + ". " + worldSleepLinkedChgMsg); // Console final msg (always)
					MsgHandler.playerAdminMsg(sender, worldSleepTrueChgMsg + " §e" + world + "§r. " + worldSleepLinkedChgMsg); // Player final msg (in case)
				} else if (sleepValue.equalsIgnoreCase(ARG_FALSE)) {
					MsgHandler.infoMsg(worldSleepFalseChgMsg + " " + world + "."); // Console final msg (always)
					MsgHandler.playerAdminMsg(sender, worldSleepFalseChgMsg + " §e" + world + "§r."); // Player final msg (in case)
				}
			}
		}
		// Else, return an error and help message
		else {
			MsgHandler.cmdErrorMsg(sender, MainTM.wrongWorldMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_SLEEP);
		}
	}

};