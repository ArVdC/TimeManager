package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class TmSetDuration extends MainTM { // Gérer les notifs ????

	/**
	 * CMD /tm set duration [00d-00h-00m-00s] [world]
	 * CMD /tm set durationDay [00d-00h-00m-00s] [world]
	 * CMD /tm set durationNight [00d-00h-00m-00s] [world]
	 */
	public static void cmdSetDuration(CommandSender sender, String formatedTime, String when, String world) {

		// Adapt wrong values in the arg
		formatedTime = ValuesConverter.correctDuration(formatedTime);

		// Modify all worlds
		if (world.equalsIgnoreCase(ARG_ALL)) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdSetDuration(sender, formatedTime, when, listedWorld);
			}
		}
		// Else, if the string argument is a listed world, modify a single world
		else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			// #1. Find corresponding speed
			double speed = ValuesConverter.doubleFromFormatedTime(formatedTime, when);
			// #2. Set the world speed
			switch (when) {
				default :
				case CMD_SET_DURATION :
					when = CMD_SET_SPEED;
					break;
				case CMD_SET_D_DURATION :
					when = CMD_SET_D_SPEED;
					break;
				case CMD_SET_N_DURATION :
					when = CMD_SET_N_SPEED;
					break;
			}
			TmSetSpeed.cmdSetSpeed(sender, speed, when, world);
		}
		// Else, return an error and help message
		else {
			MsgHandler.cmdErrorMsg(sender, MainTM.wrongWorldMsg, MainTM.CMD_SET + " " + CMD_SET_DURATION);
		}
	}

	/**
	 * CMD /tm set duration [00d-00h-00m-00s] [world]
	 */
	public static void cmdSetDuration(CommandSender sender, String formatedTime, String world) {
		cmdSetDuration(sender, formatedTime, CMD_SET_DURATION, world);
	}
};