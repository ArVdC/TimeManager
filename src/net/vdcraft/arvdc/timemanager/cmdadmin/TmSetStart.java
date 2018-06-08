package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSyncHandler;

public class TmSetStart extends MainTM {

	/**
	 * CMD /tm set start [tick|daypart] [world]
	 */ 
	public static void cmdSetStart(CommandSender sender, Long tickToSet, String worldToSet) {
		// If using a world name in several parts
		if(sender instanceof Player) worldToSet = ValuesConverter.restoreSpacesInString(worldToSet);
		
		// Modify all worlds
		if(worldToSet.equalsIgnoreCase("all")) {
			// Relaunch this for each world
			for(String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
				cmdSetStart(sender, tickToSet, listedWorld);	
			}
		}
    	// Else, if the string argument is a listed world, modify a single world
        else if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(worldToSet)) {
			// Adapt wrong values
			Double currentSpeed = MainTM.getInstance().getConfig().getDouble("worldsList."+worldToSet+".speed");
			if(currentSpeed == 24.00) {
				tickToSet = ValuesConverter.returnCorrectUTC(tickToSet) * 1000;
			} else {
				tickToSet = tickToSet % 24000;
			}
			// Modify and save the start tick in the config.yml
		    MainTM.getInstance().getConfig().set("worldsList."+worldToSet+".start", tickToSet);
			MainTM.getInstance().saveConfig();
			// Resync this world
		    WorldSyncHandler.WorldSyncRe(worldToSet);
			// Notifications
	        Bukkit.getLogger().info(prefixTM + " " + worldStartChgMsg1 + " " + worldToSet + " " + worldStartChgMsg2); // Console final msg (always)
	        if(sender instanceof Player) {
	        	sender.sendMessage(prefixTMColor + " " + worldStartChgMsg1 + " §e" + worldToSet + "§r " + worldStartChgMsg2); // Player final msg (in case)
	        }
        }
		// Else, return an error and help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "set start");
		}
	}
	
}