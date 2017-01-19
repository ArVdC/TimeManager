package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.NowFormatTime;
import net.vdcraft.arvdc.timemanager.mainclass.RestrainValuesHandler;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSyncHandler;

public class TmSetStart extends MainTM {

	/** 
	 * CMD /tm set start [tick] [world]
	 */ 
	public static void cmdSetStart(CommandSender sender, Long tickToSet, String worldToSet) {
		
		// Adapt wrong values
		tickToSet = RestrainValuesHandler.returnCorrectTicks(tickToSet);

		// Modify all worlds
		if(worldToSet.equals("all")) {
			for(String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
				// Modify and save the start tick in the config.yml
			    MainTM.getInstance().getConfig().set("worldsList."+listedWorld+".start", tickToSet);
			}
			MainTM.getInstance().saveConfig();
			// Resync all worlds
		    WorldSyncHandler.WorldSyncRe("all");
			// Notifications
    		String timeToSet = NowFormatTime.ticksAsTime(tickToSet);
	        Bukkit.getLogger().info(prefixTM + " " + allStartChgMsg + tickToSet + " tick #" +tickToSet + " (" + timeToSet + ")."); // Console final msg (always)
	        if(sender instanceof Player) {
	        	sender.sendMessage(prefixTMColor + " " + allStartChgMsg + " §etick #" + tickToSet + " §r(§e" + timeToSet + "§r)."); // Player final msg (in case)
	        }
		}
    	// Else, if the string argument is a listed world, modify a single world
        else if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(worldToSet)) {
			// Modify and save the start tick in the config.yml
		    MainTM.getInstance().getConfig().set("worldsList."+worldToSet+".start", tickToSet);
			MainTM.getInstance().saveConfig();
			// Resync this world
		    WorldSyncHandler.WorldSyncRe(worldToSet);
			// Notifications
    		String timeToSet = NowFormatTime.ticksAsTime(tickToSet);
	        Bukkit.getLogger().info(prefixTM + " World " + worldToSet + " " + worldStartChgMsg + " tick #" + tickToSet + " (" + timeToSet + ")."); // Console final msg (always)
	        if(sender instanceof Player) {
	        	sender.sendMessage(prefixTMColor + " World " + worldToSet + " " + worldStartChgMsg + " §etick #" + tickToSet + " §r(§e" + timeToSet + "§r)."); // Player final msg (in case)
	        }
        }
		// Else, return an error and help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "set start");
		}
	};

}