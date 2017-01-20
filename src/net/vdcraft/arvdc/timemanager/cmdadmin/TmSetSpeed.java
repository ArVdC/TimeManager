package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.DaylightCycleHandler;
import net.vdcraft.arvdc.timemanager.mainclass.RestrainValuesHandler;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSpeedHandler;

public class TmSetSpeed extends MainTM {

	/**
	 * CMD /tm set speed [tick] [world]
	 */
	public static void cmdSetSpeed(CommandSender sender, double speedToSet, String worldToSet) {
		
		// Adapt wrong values
		speedToSet = RestrainValuesHandler.returnCorrectSpeed(speedToSet);
		
		// Modify all worlds
		if(worldToSet.equals("all")) {
			// Modify and save the config
			for(String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
			    MainTM.getInstance().getConfig().set("worldsList."+listedWorld+".speed", speedToSet);				
			}
			MainTM.getInstance().saveConfig();
			// Check if daylightCycle effects is needed
			DaylightCycleHandler.doDaylightCheck("all");
			// Launch scheduler if is inactive
		    if(ScheduleIsOn == false) {
	    		WorldSpeedHandler.WorldSpeedModify();
	    	}
			// Notifications
	        Bukkit.getLogger().info(prefixTM + " " + allSpeedChgMsg + " " + speedToSet + "."); // Console final msg (always)
	        if(sender instanceof Player) {
	        	sender.sendMessage(prefixTMColor + " " + allSpeedChgMsg + " §e" + speedToSet + "§r."); // Player final msg (in case)
			}
		}
    	// Else, if the string argument is a listed world, modify a single world
        else if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(worldToSet)) {		
			// Modify and save the config
		    MainTM.getInstance().getConfig().set("worldsList."+worldToSet+".speed", speedToSet);	
			MainTM.getInstance().saveConfig();
			// Check if daylightCycle effects is needed
			DaylightCycleHandler.doDaylightCheck(worldToSet);
			// Launch scheduler if is inactive
	    	if(ScheduleIsOn == false) {
	    		WorldSpeedHandler.WorldSpeedModify();
	    	}
			// Notifications
	        Bukkit.getLogger().info(prefixTM + " World " + worldToSet + " " + worldSpeedChgMsg + " " + speedToSet + "."); // Console final msg (always)
	        if(sender instanceof Player) {
	        	sender.sendMessage(prefixTMColor + " World " + worldToSet  + " " + worldSpeedChgMsg  + " §e" + speedToSet + "§r."); // Player final msg (in case)
	        }
        }
		// Else, return an error and help message
		else {	
        	TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "set speed");
		}
	};

}