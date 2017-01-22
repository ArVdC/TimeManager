package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSyncHandler;

public class TmResync extends MainTM {

	/**
	 * CMD /tm resync [all|world]
	 */
	public static void cmdResync(CommandSender sender, String worldToSet) {
		// Re-synchronize all worlds
		if(worldToSet.equalsIgnoreCase("all")) {
			// Do the synchronization
			WorldSyncHandler.WorldSyncRe(worldToSet);
	        // Notifications
			if(sender instanceof Player) {
				sender.sendMessage(prefixTMColor + " " + resyncDoneAllMsg); // Player final msg (in case)
			}
	        Bukkit.getLogger().info(prefixTM + " " + resyncDoneAllMsg); // Console final msg (always)
		}
    	// Else, if the string argument is a listed world, re-synchronize a single world
        else if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(worldToSet)) {
			// Do the synchronization
			WorldSyncHandler.WorldSyncRe(worldToSet);
	        // Notifications
			if(sender instanceof Player) {
				sender.sendMessage(prefixTMColor + " World " + worldToSet + resyncDoneOneMsg); // Player final msg (in case)
			}
	        Bukkit.getLogger().info(prefixTM + " World " + worldToSet + resyncDoneOneMsg); // Console final msg (always)        	
        }
		// Else, return an error and help message
		else {	
        	TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "resync");
		}
	};

}