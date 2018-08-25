package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class TmSetSleep extends MainTM {

	/**
	 * CMD /tm set sleep [boolean] [world]
	 */ 
	public static void cmdSetSleep(CommandSender sender, String sleepOrNo, String worldToSet) {
		// If using a world name in several parts
		if(sender instanceof Player) worldToSet = ValuesConverter.restoreSpacesInString(worldToSet);
		
		// Modify all worlds
		if(worldToSet.equalsIgnoreCase("all")) {
			// Relaunch this for each world
			for(String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
				cmdSetSleep(sender, sleepOrNo, listedWorld);			
			}
		}
    	// Else, if the string argument is a listed world, modify a single world
        else if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(worldToSet)) {
			// Avoid impossible values
			String currentSpeed = MainTM.getInstance().getConfig().getString("worldsList."+worldToSet+".speed");
			if((sleepOrNo.equals("true") && (currentSpeed.equals("0.0") || currentSpeed.equals("24.0")))) {
				// Notifications
				Bukkit.getLogger().info(prefixTM + " " + worldSleepNoChgMsg + " " + worldToSet + "."); // Console final msg (always)
		        if(sender instanceof Player) {
		        	sender.sendMessage(prefixTMColor + " " + worldSleepNoChgMsg + " §e" +  worldToSet + "§r."); // Player final msg (in case)
		        }
			} else {
				// Modify the value
			    MainTM.getInstance().getConfig().set("worldsList."+worldToSet+".sleep", sleepOrNo);
				// Avoid to synchronize worlds where players can sleep
				if(sleepOrNo.equals("true") && MainTM.getInstance().getConfig().getString("worldsList."+worldToSet+".sync").equals("true")) {
					MainTM.getInstance().getConfig().set("worldsList."+worldToSet+".sync", "false");
					Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + " " + SleepWorldSyncChgMsg); // Console warn msg (always)
			        if(sender instanceof Player) {
			        	sender.sendMessage(prefixTMColor + " The world §e" + worldToSet + "§r " + SleepWorldSyncChgMsg); // Player warn msg (in case)
			        }
				}
				// Save the value(s) in the config.yml
				MainTM.getInstance().saveConfig();
				// Notifications
				if(sleepOrNo.equals("true")) {
			        Bukkit.getLogger().info(prefixTM + " " +  worldSleepTrueChgMsg + " " + worldToSet + "."); // Console final msg (always)
			        if(sender instanceof Player) {
			        	sender.sendMessage(prefixTMColor + " " + worldSleepTrueChgMsg + " §e" + worldToSet + "§r."); // Player final msg (in case)
			        }
				} else if(sleepOrNo.equals("false")) {
			        Bukkit.getLogger().info(prefixTM + " " + worldSleepFalseChgMsg + " " + worldToSet + "."); // Console final msg (always)
			        if(sender instanceof Player) {
			        	sender.sendMessage(prefixTMColor + " " + worldSleepFalseChgMsg + " §e" + worldToSet + "§r."); // Player final msg (in case)
			        }				
				}
			}
		}
		// Else, return an error and help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "set sleep");
		}
	}

};