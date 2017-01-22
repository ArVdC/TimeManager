package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;

public class TmSetSleepUntilDawn extends MainTM {

	/**
	 * CMD /tm set sleepUntilDawn [boolean] [world]
	 */ 
	public static void cmdSetSleepUntilDawn(CommandSender sender, String sleepOrNo, String worldToSet) {
		
		// Modify all worlds
		if(worldToSet.equalsIgnoreCase("all")) {
			for(String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
				// Avoid impossible values
				String currentSpeed = MainTM.getInstance().getConfig().getString("worldsList."+listedWorld+".speed");
				if(!(currentSpeed.equals("0.0")) && !(currentSpeed.equals("24.0"))) {
					// Modify and save the sleep state in the config.yml
				    MainTM.getInstance().getConfig().set("worldsList."+listedWorld+".sleepUntilDawn", sleepOrNo);
				}
			}
			MainTM.getInstance().saveConfig();
			// Notifications
			if(sleepOrNo.equals("true"))  {
		        Bukkit.getLogger().info(prefixTM + " " + allSleepTrueChgMsg); // Console final msg (always)
		        if(sender instanceof Player) {
		        	sender.sendMessage(prefixTMColor + " " + allSleepTrueChgMsg); // Player final msg (in case)
		        }
			} else if(sleepOrNo.equals("false")) {
		        Bukkit.getLogger().info(prefixTM + " " + allSleepFalseChgMsg); // Console final msg (always)
		        if(sender instanceof Player) {
		        	sender.sendMessage(prefixTMColor + " " + allSleepFalseChgMsg); // Player final msg (in case)
		        }
			}
		}
    	// Else, if the string argument is a listed world, modify a single world
        else if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(worldToSet)) {
			// Avoid impossible values
			String currentSpeed = MainTM.getInstance().getConfig().getString("worldsList."+worldToSet+".speed");
			if(currentSpeed.equals("0.0") || currentSpeed.equals("24.0")) {
				// Notifications
				Bukkit.getLogger().info(prefixTM + " " + worldSleepNoChgMsg + worldToSet + "."); // Console final msg (always)
		        if(sender instanceof Player) {
		        	sender.sendMessage(prefixTMColor + " " + worldSleepNoChgMsg + worldToSet + "."); // Player final msg (in case)
		        }
			} else {
				// Modify and save the start tick in the config.yml
			    MainTM.getInstance().getConfig().set("worldsList."+worldToSet+".sleepUntilDawn", sleepOrNo);
				MainTM.getInstance().saveConfig();
				// Notifications
				if(sleepOrNo.equals("true")) {
			        Bukkit.getLogger().info(prefixTM + " " +  worldSleepTrueChgMsg + worldToSet + "."); // Console final msg (always)
			        if(sender instanceof Player) {
			        	sender.sendMessage(prefixTMColor + " " + worldSleepTrueChgMsg + worldToSet + "."); // Player final msg (in case)
			        }
				} else if(sleepOrNo.equals("false")) {
			        Bukkit.getLogger().info(prefixTM + " " + worldSleepFalseChgMsg + worldToSet + "."); // Console final msg (always)
			        if(sender instanceof Player) {
			        	sender.sendMessage(prefixTMColor + " " + worldSleepFalseChgMsg + worldToSet + "."); // Player final msg (in case)
			        }				
				}
			}
		}
		// Else, return an error and help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "set sleepUntilDawn");
		}
	};

}