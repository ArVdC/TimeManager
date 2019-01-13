package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.WorldDayCycleHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSpeedHandler;

public class TmSetSpeed extends MainTM {

    /**
     * CMD /tm set speed [multiplier] [world]
     */
    public static void cmdSetSpeed(CommandSender sender, double speedToSet, String worldToSet) {
	// If using a world name in several parts
	if (sender instanceof Player)
	    worldToSet = ValuesConverter.restoreSpacesInString(worldToSet);
	// Adapt wrong values in the arg
	speedToSet = ValuesConverter.returnCorrectSpeed(speedToSet);

	// Modify all worlds
	if (worldToSet.equalsIgnoreCase("all")) {
	    // Relaunch this for each world
	    for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
		cmdSetSpeed(sender, speedToSet, listedWorld);
	    }
	}
	// Else, if the string argument is a listed world, modify a single world
	else if (MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(worldToSet)) {
	    // Get the old speed value of this world
	    Double oldSpeed = MainTM.getInstance().getConfig().getDouble("worldsList." + worldToSet + ".speed");
	    // Modify the speed in the config
	    MainTM.getInstance().getConfig().set("worldsList." + worldToSet + ".speed", speedToSet);
	    // Restrain the sync value
	    ValuesConverter.restrainSync(worldToSet, oldSpeed);
	    // Restrain the sleep value
	    ValuesConverter.restrainSleep(worldToSet);
	    // Check if daylightCycle effects is needed
	    WorldDayCycleHandler.doDaylightCheck(worldToSet);
	    // Save the config
	    MainTM.getInstance().saveConfig();
	    if (debugMode == true)
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + launchSchedulerDebugMsg); // Console debug msg
	    // Launch the good scheduler if is inactive
	    if (speedToSet == realtimeSpeed) {
		if (realScheduleIsOn == false) {
		    WorldSpeedHandler.WorldRealSpeed();
		}
	    } else if (speedToSet >= 1.0 && speedToSet <= speedMax) {
		if (increaseScheduleIsOn == false) {
		    WorldSpeedHandler.WorldIncreaseSpeed();
		}
	    } else if (speedToSet > 0.0 && speedToSet < 1.0) {
		if (decreaseScheduleIsOn == false) {
		    WorldSpeedHandler.WorldDecreaseSpeed();
		}
	    }
	    // Notifications
	    if (speedToSet != realtimeSpeed) { // Usual message (any speed but 24.00)
		Bukkit.getLogger().info(prefixTM + " " + worldSpeedChgIntro + " " + worldToSet + " " + worldSpeedChgMsg + " " + speedToSet + "."); // Console final msg (always)
		if (sender instanceof Player) {
		    sender.sendMessage(prefixTMColor + " " + worldSpeedChgIntro + " §e" + worldToSet + " §r" + worldSpeedChgMsg + " §e" + speedToSet + "§r."); // Player final msg (in case)
		}
	    } else { // If real time message (speed = 24.00)
		Bukkit.getLogger().info(prefixTM + " " + worldSpeedChgIntro + " " + worldToSet + " " + worldRealSpeedChgMsg); // Console final msg (always)
		if (sender instanceof Player) {
		    sender.sendMessage(prefixTMColor + " " + worldSpeedChgIntro + " §e" + worldToSet + " §r" + worldRealSpeedChgMsg); // Player final msg (in case)
		}
	    }
	}
	// Else, return an error and help message
	else {
	    TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "set speed");
	}
    }

};