package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.NowFormatTime;
import net.vdcraft.arvdc.timemanager.mainclass.RestrainValuesHandler;

public class TmSetTime extends MainTM {

	/** 
	 * CMD /tm set start [tick] [world]
	 */ 
	public static void cmdSetTime(CommandSender sender, Long tickToSet, String worldToSet) {

		// Adapt wrong values
		tickToSet = RestrainValuesHandler.returnCorrectTicks(tickToSet);

		// Modify all worlds
		if(worldToSet.equals("all")) {
			for(String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
				Bukkit.getWorld(listedWorld).setTime(tickToSet);
			}
			// Notifications
    		String timeToSet = NowFormatTime.ticksAsTime(tickToSet);
	        Bukkit.getLogger().info(prefixTM + " " + allTimeChgMsg + tickToSet + " tick #" +tickToSet + " (" + timeToSet + ")."); // Console final msg (always)
	        if(sender instanceof Player) {
	        	sender.sendMessage(prefixTMColor + " " + allTimeChgMsg + " §etick #" + tickToSet + " §r(§e" + timeToSet + "§r)."); // Player final msg (in case)
			}
		}
		// Else, if the string argument is a listed world, modify a single world
        else if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(worldToSet)) {
			// Modify targeted world's timer
			World w = Bukkit.getWorld(worldToSet);
			w.setTime(tickToSet);
			// Notifications
    		String timeToSet = NowFormatTime.ticksAsTime(tickToSet);
	        Bukkit.getLogger().info(prefixTM + " World " + worldToSet + " " + worldTimeChgMsg + " tick #" + tickToSet + " (" + timeToSet + ")."); // Console final msg (always)
	        if(sender instanceof Player) {
	        	sender.sendMessage(prefixTMColor + " World " + worldToSet + " " + worldTimeChgMsg + " §etick #" + tickToSet + " §r(§e" + timeToSet + "§r)."); // Player final msg (in case)
	        }
		}
		// Else, return an error and help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "set time");
		}
	};

}