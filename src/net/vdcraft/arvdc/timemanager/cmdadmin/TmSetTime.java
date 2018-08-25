package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class TmSetTime extends MainTM {

	/** 
	 * CMD /tm set start [tick|daypart] [world]
	 */
	public static void cmdSetTime(CommandSender sender, Long tickToSet, String worldToSet) {
		// If using a world name in several parts
		if(sender instanceof Player) worldToSet = ValuesConverter.restoreSpacesInString(worldToSet);		
		// Adapt wrong values in the arg
		tickToSet = ValuesConverter.returnCorrectTicks(tickToSet);

		// Modify all worlds
		if(worldToSet.equalsIgnoreCase("all")) {
			// Relaunch this for each world
			for(String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
				cmdSetTime(sender, tickToSet, listedWorld);			
			}
		}
		// Else, if the string argument is a listed world, modify a single world
        else if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(worldToSet)) {
			// Modify targeted world's timer
			World w = Bukkit.getWorld(worldToSet);
			// Check if sync is activated for this world if true, calculate an equivalent "start" value
			if(MainTM.getInstance().getConfig().getString("worldsList."+worldToSet+".sync").equalsIgnoreCase("true")) {
				Long oldStartNb = (MainTM.getInstance().getConfig().getLong("worldsList."+worldToSet+".start"));
				Long currentTime = w.getTime();
				Long startTickToSet = (Long) (ValuesConverter.returnCorrectTicks(oldStartNb + tickToSet - currentTime));
				if(debugMode == true) {
					Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " SetTime >>> SetStart: Calculation of " + worldStartAtVar + " for world §e" + worldToSet + "§b:");
	        		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + worldStartAtCalculation + " = §3" + oldStartNb + "§b + §8" + tickToSet + "§b - §c" + currentTime + "§b = §e" + (oldStartNb + tickToSet - currentTime) + "§b restrained to one day = §etick #" + startTickToSet);
				}
				// Warning notifications, config.yml will be changed
				if(MainTM.getInstance().getConfig().getString("worldsList."+worldToSet+".speed").contains("24")) {
			        Bukkit.getLogger().info(prefixTM + " The time of the world " + worldToSet + " " + worldRealSyncTimeChgMsg); // Console warn msg (always)
			        if(sender instanceof Player) { 
			        	sender.sendMessage(prefixTMColor + " The time of the world " + worldToSet + " " + worldRealSyncTimeChgMsg); // Player warn msg (in case)
			        }
				} else {
			        Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + " " + worldSyncTimeChgMsg); // Console warn msg (always)
			        if(sender instanceof Player) { 
			        	sender.sendMessage(prefixTMColor + " The world " + worldToSet + " " + worldSyncTimeChgMsg); // Player warn msg (in case)
			        }
				}
				// Change the 'start' value and do resync
				TmSetStart.cmdSetStart(sender, startTickToSet, worldToSet);
			} else { // If false, do the usual time change
				w.setTime(tickToSet);
			}
			// Notifications
			if(MainTM.getInstance().getConfig().getString("worldsList."+worldToSet+".sync").equalsIgnoreCase("false")) {
				String timeToSet = ValuesConverter.returnTicksAsTime(tickToSet);
		        Bukkit.getLogger().info(prefixTM + " " + worldTimeChgMsg1 + " " + worldToSet + " " + worldTimeChgMsg2 + " tick #" + tickToSet + " (" + timeToSet + ")."); // Console final msg (always)
		        if(sender instanceof Player) {
		        	sender.sendMessage(prefixTMColor + " " + worldTimeChgMsg1 + " §e" + worldToSet + "§r " + worldTimeChgMsg2 + " §etick #" + tickToSet + " §r(§e" + timeToSet + "§r)."); // Player final msg (in case)
		        }
        	}
		}
		// Else, return an error and help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "set time");
		}
	}

};