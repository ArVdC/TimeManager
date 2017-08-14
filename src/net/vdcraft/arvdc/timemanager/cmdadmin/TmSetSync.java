package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSpeedHandler;

public class TmSetSync extends MainTM {

	/**
	 * CMD /tm set sync [boolean] [world]
	 */ 
	public static void cmdSetSync(CommandSender sender, String syncOrNo, String worldToSet) {
		// If using a world name in several parts
		if(sender instanceof Player) worldToSet = ValuesConverter.restoreSpacesInString(worldToSet);
		
		// Modify all worlds
		if(worldToSet.equalsIgnoreCase("all")) {
			// Relaunch this for each world
			for(String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
				cmdSetSync(sender, syncOrNo, listedWorld);			
			}
		}
    	// Else, if the string argument is a listed world, modify a single world
        else if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(worldToSet)) {
			// Avoid impossible values
			String currentSpeed = MainTM.getInstance().getConfig().getString("worldsList."+worldToSet+".speed");
			if((syncOrNo.equals("true") && currentSpeed.equals("0.0")) || (syncOrNo.equals("false") && currentSpeed.equals("24.0"))) {
				// Notifications
				Bukkit.getLogger().info(prefixTM + " " + worldSyncNoChgMsg + " " + worldToSet + "."); // Console final msg (always)
		        if(sender instanceof Player) {
		        	sender.sendMessage(prefixTMColor + " " + worldSyncNoChgMsg + " §e" + worldToSet + "§r."); // Player final msg (in case)
		        }
			} else {
				// Modify the value
			    MainTM.getInstance().getConfig().set("worldsList."+worldToSet+".sync", syncOrNo);
			    // If sync is true, make some changes
				if(syncOrNo.equals("true")) {
					// Start synchronize 1.0 speed worlds
				    if(currentSpeed.equals("1.0") && increaseScheduleIsOn == false) {
			    		WorldSpeedHandler.WorldIncreaseSpeed();
			    	}
					// Avoid players to sleep in a synchronized world
					if(MainTM.getInstance().getConfig().getString("worldsList."+worldToSet+".sleep").equals("true")) {
						MainTM.getInstance().getConfig().set("worldsList."+worldToSet+".sleep", "false");
						Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + " " + worldSyncSleepChgMsg); // Console warn msg (always)
						if(sender instanceof Player) {
			        	sender.sendMessage(prefixTMColor + " The world §e" + worldToSet + "§r " + worldSyncSleepChgMsg); // Player warn msg (in case)
						}
					}
				}
				// Save the value(s) in the config.yml
				MainTM.getInstance().saveConfig();
				// Notifications
				if(syncOrNo.equals("true")) {
			        Bukkit.getLogger().info(prefixTM + " " +  worldSyncTrueChgMsg + " " + worldToSet + "."); // Console final msg (always)
			        if(sender instanceof Player) {
			        	sender.sendMessage(prefixTMColor + " " + worldSyncTrueChgMsg + " §e" + worldToSet + "§r."); // Player final msg (in case)
			        }
				} else if(syncOrNo.equals("false")) {
			        Bukkit.getLogger().info(prefixTM + " " + worldSyncFalseChgMsg + " " + worldToSet + "."); // Console final msg (always)
			        if(sender instanceof Player) {
			        	sender.sendMessage(prefixTMColor + " " + worldSyncFalseChgMsg + " §e" + worldToSet + "§r."); // Player final msg (in case)
			        }				
				}
			}
		}
		// Else, return an error and help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "set sync");
		}
	};

}