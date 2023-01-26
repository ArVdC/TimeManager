package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;

public class TmSetFirstStartTime extends MainTM {

	/**
	 * CMD /tm set firstStartTime [default|previous|start] [world]
	 */
	public static void cmdSetFirstStartTime(CommandSender sender, String firstStartTimeValue, String world) {

		// Modify all worlds
		if (world.equalsIgnoreCase(ARG_ALL)) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdSetFirstStartTime(sender, firstStartTimeValue, listedWorld);
			}
		}
		// Else, if the string argument is a listed world, modify a single world
		else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			// #1. Avoid impossible values
			if (!firstStartTimeValue.equalsIgnoreCase(ARG_START) && !firstStartTimeValue.equalsIgnoreCase(ARG_PREVIOUS)) firstStartTimeValue = ARG_DEFAULT;
			
			// #2.A. Avoid to change the value if the world is sync ...
			if (!firstStartTimeValue.equalsIgnoreCase(ARG_DEFAULT) && MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equals(ARG_TRUE)) {
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_FIRSTSTARTTIME, ARG_DEFAULT);
				// Notifications
				MsgHandler.infoMsg(firstStartTimeNoChgMsg + " " + world + "."); // Console final msg (always)
				MsgHandler.playerAdminMsg(sender, firstStartTimeNoChgMsg + " §e" + world + "§r."); // Player final msg (in case)
				
			// #2.B. ... or modify the value			
			} else {				 
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_FIRSTSTARTTIME, firstStartTimeValue);
				// Save the value in the config.yml
				MainTM.getInstance().saveConfig();
				// Notifications
				if (firstStartTimeValue.equalsIgnoreCase(ARG_START)) {
					MsgHandler.infoMsg(world + " " + firstStartTimeStartChgMsg); // Console final msg (always)
					MsgHandler.playerAdminMsg(sender, "§e" + world + "§r " + firstStartTimeStartChgMsg); // Player final msg (in case)
				} else if (firstStartTimeValue.equalsIgnoreCase(ARG_PREVIOUS)) {
					MsgHandler.infoMsg(world + " " + firstStartTimePreviousChgMsg); // Console final msg (always)
					MsgHandler.playerAdminMsg(sender, "§e" + world + "§r " + firstStartTimePreviousChgMsg); // Player final msg (in case)
				} else if (firstStartTimeValue.equalsIgnoreCase(ARG_DEFAULT)) {
					MsgHandler.infoMsg(world + " " + firstStartTimeDefaultChgMsg); // Console final msg (always)
					MsgHandler.playerAdminMsg(sender, "§e" + world + "§r" + firstStartTimeDefaultChgMsg); // Player final msg (in case)
				}
			}
		}
		// Else, return an error and help message
		else {
			MsgHandler.cmdErrorMsg(sender, MainTM.wrongWorldMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_FIRSTSTARTTIME);
		}
	}

};