package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSyncHandler;

public class TmResync extends MainTM {

	/**
	 * CMD /tm resync [all|world]
	 */
	public static void cmdResync(CommandSender sender, String worldToSet) {
		// If using a world name in several parts
		if(sender instanceof Player) worldToSet = ValuesConverter.restoreSpacesInString(worldToSet);
		
		// Re-synchronize all worlds
		if(worldToSet.equalsIgnoreCase("all")) {
			// Relaunch this for each world
			for(String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
				cmdResync(sender, listedWorld);			
			}
		}
    	// Else, if the string argument is a listed world, re-synchronize a single world
        else if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(worldToSet)) {
        	if(MainTM.getInstance().getConfig().getString("worldsList."+worldToSet+".sync").equalsIgnoreCase("true")) {
				// Warning notifications, no synchronization needed
		        Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + " " + worldSyncNoManualSyncChgMsg); // Console warn msg (always)
		        if(sender instanceof Player) {
		        	sender.sendMessage(prefixTMColor + " The world §e" + worldToSet + "§r " + worldSyncNoManualSyncChgMsg); // Player warn msg (in case)
		        }
        	} else {
				// Do the synchronization
				WorldSyncHandler.WorldSyncRe(worldToSet);
		        // Notifications
		        Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + resyncDoneOneMsg); // Console final msg (always)
				if(sender instanceof Player) {
					sender.sendMessage(prefixTMColor + " The world §e" + worldToSet + "§r" + resyncDoneOneMsg); // Player final msg (in case)
				}
	        }   
        }
		// Else, return an error and help message
		else {	
        	TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "resync");
		}
	}
	
}