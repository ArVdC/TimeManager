package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.DoDaylightCycleHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
import net.vdcraft.arvdc.timemanager.mainclass.SpeedHandler;

public class TmSetSpeed extends MainTM {

	/**
	 * CMD /tm set speed [multiplier] [world]
	 * CMD /tm set speedDay [multiplier] [world]
	 * CMD /tm set speedNight [multiplier] [world]
	 */
	public static void cmdSetSpeed(CommandSender sender, double speed, String when, String world) {

		// Adapt wrong values in the arg
		speed = ValuesConverter.correctSpeed(speed);

		// Modify all worlds
		if (world.equalsIgnoreCase(ARG_ALL)) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdSetSpeed(sender, speed, when, listedWorld);
			}
		}
		// Else, if the string argument is a listed world, modify a single world
		else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			// Get the old speed value of this world to correctly resynchronize 24h worlds
			long t = Bukkit.getWorld(world).getTime();
			Double oldSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t));
			// Modify the speed in the config    
			if (when.equalsIgnoreCase(CMD_SET_SPEED) || speed == 24.0) {
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED, speed);
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED, speed);
			} else if (when.equalsIgnoreCase(CMD_SET_D_SPEED)) {
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED, speed);	
			} else if (when.equalsIgnoreCase(CMD_SET_N_SPEED)) {
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED, speed);	
			}
			// Restrain the sleep value
			ValuesConverter.restrainSleep(world);
			// Restrain the sync value
			ValuesConverter.restrainSync(world, oldSpeed);
			// Do daylightCycle change if needed
			DoDaylightCycleHandler.adjustDaylightCycle(world);
			// Save the config
			MainTM.getInstance().saveConfig();
			// Detect if this world needs to change its speed value
			MsgHandler.debugMsg(launchSchedulerDebugMsg); // Console debug msg
			SpeedHandler.speedScheduler(world);

			// Notifications
			if (speed == realtimeSpeed) { // Display realtime message (speed = 24.00)
				MsgHandler.infoMsg(worldSpeedChgIntro + " " + world + " " + worldRealSpeedChgMsg); // Console final msg (always)
				MsgHandler.playerAdminMsg(sender, worldSpeedChgIntro + " §e" + world + " §r" + worldRealSpeedChgMsg); // Player final msg (in case)

			} else { // Display usual message (any speed but 24.00)
				if (when.equalsIgnoreCase(CMD_SET_SPEED)) {
					MsgHandler.infoMsg(worldSpeedChgIntro + " " + world + " " + worldSpeedChgMsg + " " + speed + "."); // Console final msg (always)
					MsgHandler.playerAdminMsg(sender, worldSpeedChgIntro + " §e" + world + " §r" + worldSpeedChgMsg + " §e" + speed + "§r."); // Player final msg (in case)

				} else if (when.equalsIgnoreCase(CMD_SET_D_SPEED)) {
					MsgHandler.infoMsg(worldDaySpeedChgIntro + " " + world + " " + worldSpeedChgMsg + " " + speed + "."); // Console final msg (always)
					MsgHandler.playerAdminMsg(sender, worldDaySpeedChgIntro + " §e" + world + " §r" + worldSpeedChgMsg + " §e" + speed + "§r."); // Player final msg (in case)

				} else if (when.equalsIgnoreCase(CMD_SET_N_SPEED)) {
					MsgHandler.infoMsg(worldNightSpeedChgIntro + " " + world + " " + worldSpeedChgMsg + " " + speed + "."); // Console final msg (always)
					MsgHandler.playerAdminMsg(sender, worldNightSpeedChgIntro + " §e" + world + " §r" + worldSpeedChgMsg + " §e" + speed + "§r."); // Player final msg (in case)

				}
			}
		}
		// Else, return an error and help message
		else {
			MsgHandler.cmdErrorMsg(sender, MainTM.wrongWorldMsg, MainTM.CMD_SET + " " + CMD_SET_SPEED);
		}
	}


	/**
	 * CMD /tm set speed [multiplier] [world]
	 */
	public static void cmdSetSpeed(CommandSender sender, double speed, String world) {
		cmdSetSpeed(sender, speed, CMD_SET_SPEED, world);
	}
};