package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.WorldDoDaylightCycleHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSpeedHandler;

public class TmSetSpeed extends MainTM {

	/**
	 * CMD /tm set speed [multiplier] [world]
	 * CMD /tm set speedDay [multiplier] [world]
	 * CMD /tm set speedNight [multiplier] [world]
	 */
	public static void cmdSetSpeed(CommandSender sender, double speed, String when, String world) {
		// If using a world name in several parts
		if (sender instanceof Player)
			world = ValuesConverter.restoreSpacesInString(world);
		// Adapt wrong values in the arg
		speed = ValuesConverter.returnCorrectSpeed(speed);

		// Modify all worlds
		if (world.equalsIgnoreCase("all")) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdSetSpeed(sender, speed, when, listedWorld);
			}
		}
		// Else, if the string argument is a listed world, modify a single world
		else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			// Get the old speed value of this world to correctly resynchronize 24h worlds
			long t = Bukkit.getWorld(world).getTime();
			Double oldSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + WorldSpeedHandler.wichSpeedParam(t));
			// Modify the speed in the config    
			if (when.equalsIgnoreCase(CMD_SET_SPEED)) {
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED, speed);
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED, speed);
			} else if (when.equalsIgnoreCase(CMD_SET_D_SPEED)) {
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED, speed);	
			} else if (when.equalsIgnoreCase(CMD_SET_N_SPEED)) {
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED, speed);	
			}
			// Restrain the sync value
			ValuesConverter.restrainSync(world, oldSpeed);
			// Restrain the sleep value
			ValuesConverter.restrainSleep(world);
			// Check if daylightCycle effects is needed
			WorldDoDaylightCycleHandler.doDaylightSet(world);
			// Save the config
			MainTM.getInstance().saveConfig();
			if (debugMode == true)
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + launchSchedulerDebugMsg); // Console debug msg
			// Launch the good scheduler if is inactive
			if (speed == realtimeSpeed) {
				if (realScheduleIsOn == false) {
					WorldSpeedHandler.WorldRealSpeed();
				}
			} else if (speed >= 1.0 && speed <= speedMax) {
				if (increaseScheduleIsOn == false) {
					WorldSpeedHandler.WorldIncreaseSpeed();
				}
			} else if (speed > 0.0 && speed < 1.0) {
				if (decreaseScheduleIsOn == false) {
					WorldSpeedHandler.WorldDecreaseSpeed();
				}
			}
			// Notifications
			if (speed == realtimeSpeed) { // Display realtime message (speed = 24.00)
				Bukkit.getLogger().info(prefixTM + " " + worldSpeedChgIntro + " " + world + " " + worldRealSpeedChgMsg); // Console final msg (always)
				if (sender instanceof Player) {
					sender.sendMessage(prefixTMColor + " " + worldSpeedChgIntro + " §e" + world + " §r" + worldRealSpeedChgMsg); // Player final msg (in case)
				}
			} else { // Display usual message (any speed but 24.00)
				if (when.equalsIgnoreCase(CMD_SET_SPEED)) {
					Bukkit.getLogger().info(prefixTM + " " + worldSpeedChgIntro + " " + world + " " + worldSpeedChgMsg + " " + speed + "."); // Console final msg (always)
					if (sender instanceof Player) {
						sender.sendMessage(prefixTMColor + " " + worldSpeedChgIntro + " §e" + world + " §r" + worldSpeedChgMsg + " §e" + speed + "§r."); // Player final msg (in case)
					}
				} else if (when.equalsIgnoreCase(CMD_SET_D_SPEED)) {
					Bukkit.getLogger().info(prefixTM + " " + worldDaySpeedChgIntro + " " + world + " " + worldSpeedChgMsg + " " + speed + "."); // Console final msg (always)
					if (sender instanceof Player) {
						sender.sendMessage(prefixTMColor + " " + worldDaySpeedChgIntro + " §e" + world + " §r" + worldSpeedChgMsg + " §e" + speed + "§r."); // Player final msg (in case)
					}
				} else if (when.equalsIgnoreCase(CMD_SET_N_SPEED)) {
					Bukkit.getLogger().info(prefixTM + " " + worldNightSpeedChgIntro + " " + world + " " + worldSpeedChgMsg + " " + speed + "."); // Console final msg (always)
					if (sender instanceof Player) {
						sender.sendMessage(prefixTMColor + " " + worldNightSpeedChgIntro + " §e" + world + " §r" + worldSpeedChgMsg + " §e" + speed + "§r."); // Player final msg (in case)
					}
				}
			}
		}
		// Else, return an error and help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "set speed");
		}
	}


	/**
	 * CMD /tm set speed [multiplier] [world]
	 */
	public static void cmdSetSpeed(CommandSender sender, double speed, String world) {
		cmdSetSpeed(sender, speed, CMD_SET_SPEED, world);
	}
};