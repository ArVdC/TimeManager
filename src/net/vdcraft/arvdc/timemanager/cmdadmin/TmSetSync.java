package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
import net.vdcraft.arvdc.timemanager.mainclass.SpeedHandler;

public class TmSetSync extends MainTM {

	/**
	 * CMD /tm set sync [boolean] [world]
	 */
	public static void cmdSetSync(CommandSender sender, String syncOrNo, String world) {
		
		// Modify all worlds
		if (world.equalsIgnoreCase(ARG_ALL)) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdSetSync(sender, syncOrNo, listedWorld);
			}
		}
		// Else, if the string argument is a listed world, modify a single world
		else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {			
			// Avoid impossible values
			World w = Bukkit.getWorld(world);
			long t = w.getTime();
			String speed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t));
			if (speed.equals("0.0") || speed.equals("24.0")) {
				// Notifications
				MsgHandler.infoMsg(worldSyncNoChgMsg + " " + world + "."); // Console final msg (always)
				MsgHandler.playerAdminMsg(sender, worldSyncNoChgMsg + " §e" + world + "§r."); // Player final msg (in case)				
			} else {
				// Modify the value
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, syncOrNo);
				// If sync is true, make some changes
				if (syncOrNo.equals(ARG_TRUE)) {
					// Detect if this world needs to change its speed value
					SpeedHandler.speedScheduler(world);
					// Avoid players to sleep in a synchronized world
					if (!MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SLEEP).equals(ARG_FALSE)) {
						MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SLEEP, ARG_FALSE);
						MsgHandler.infoMsg("The world " + world + " " + worldSyncSleepChgMsg); // Console warn msg (always)
						MsgHandler.playerAdminMsg(sender, "The world §e" + world + "§r " + worldSyncSleepChgMsg); // Player warn msg (in case)
					}
					// Avoid wrong firstTimeStart values
					if (!MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_FIRSTSTARTTIME).equals(ARG_DEFAULT)) {
						MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_FIRSTSTARTTIME, ARG_DEFAULT);
						MsgHandler.infoMsg("The world " + world + " " + worldSyncfirstTimeStartChgMsg); // Console warn msg (always)
						MsgHandler.playerAdminMsg(sender, "The world §e" + world + "§r " + worldSyncfirstTimeStartChgMsg); // Player warn msg (in case)
					}
				}				
				// Save the value(s) in the config.yml
				MainTM.getInstance().saveConfig();				
				// Notifications
				if (syncOrNo.equals(ARG_TRUE)) {
					MsgHandler.infoMsg(worldSyncTrueChgMsg + " " + world + "."); // Console final msg (always)
					MsgHandler.playerAdminMsg(sender, worldSyncTrueChgMsg + " §e" + world + "§r."); // Player final msg (in case)
				} else if (syncOrNo.equals(ARG_FALSE)) {
					MsgHandler.infoMsg(worldSyncFalseChgMsg + " " + world + "."); // Console final msg (always)
					MsgHandler.playerAdminMsg(sender, worldSyncFalseChgMsg + " §e" + world + "§r."); // Player final msg (in case)
				}
			}
		}
		// Else, return an error and help message
		else {
			MsgHandler.cmdErrorMsg(sender, MainTM.wrongWorldMsg, MainTM.CMD_SET + " " + CMD_SET_SYNC);
		}
	}

};