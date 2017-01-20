package net.vdcraft.arvdc.timemanager.cmdadmin;

import java.text.DateFormat;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.NowFormatTime;

public class TmServTime extends MainTM {
	
	/** 
	 * CMD /tm servtime
	 * 
	 */
	public static void cmdServerTime(CommandSender sender) {
		
		// Get the current server tick
		long ticksSinceEpoch = (long) (System.currentTimeMillis() / 50L); // Get the server actual time
		long currentServerTick = ticksSinceEpoch % 24000; // Convert the server actual time into ticks		
		// Get the current server time in HH:mm:ss
		DateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm:ss z");
		Calendar ServerCal = Calendar.getInstance();
		String currentServerTime = timeFormat.format(ServerCal.getTime());
			
		// Display the reference tick and HH:mm:ss
		if(sender instanceof Player) {
			sender.sendMessage(prefixTMColor + " " + serverInitTickMsg + " §e#" + initialTick + " §r(§e" + initialTime + "§r)."); // Final player msg
			waitTime(500);
		} else
		{
			Bukkit.getLogger().info(prefixTM + " " + serverInitTickMsg + " #" +  initialTick + " (" + initialTime + ")."); // Final console msg
		}
		
		// Get current tick
		if(sender instanceof Player) {
			sender.sendMessage(prefixTMColor + " " + serverCurrentTickMsg + " §e#" + currentServerTick + " §r(§e" + currentServerTime + "§r)."); // Final player msg
			waitTime(500);	
		} else
		{
			Bukkit.getLogger().info(prefixTM + " " + serverCurrentTickMsg + " #" + currentServerTick + " (" + currentServerTime + ")."); // Final console msg
		}
        
		// Get each world start tick, actual tick and speed
    	for(String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
    		Long listedWorldStartTick = MainTM.getInstance().getConfig().getLong("worldsList."+listedWorld+".start");
    		World thisWorld = Bukkit.getServer().getWorld(listedWorld);
    		Long listedWorldCurrentTick = thisWorld.getTime();
    		String listedWorldStartTime = NowFormatTime.ticksAsTime(listedWorldStartTick);
    		String listedWorldCurrentTime = NowFormatTime.ticksAsTime(listedWorldCurrentTick);
    		String listedWorldSpeed = (MainTM.getInstance().getConfig().getString("worldsList."+listedWorld+".speed"));
			if(sender instanceof Player) {
				sender.sendMessage(prefixTMColor + " World " + listedWorld + worldCurrentStartMsg + " §e#" + listedWorldStartTick + " §r(§e" + listedWorldStartTime + "§r).");  // Final player msg
				waitTime(1000);
				sender.sendMessage(prefixTMColor + " World " + listedWorld + worldCurrentTickMsg + " §e#" + listedWorldCurrentTick + " §r(§e" + listedWorldCurrentTime + "§r)."); // Final player msg
				waitTime(1000);
				sender.sendMessage(prefixTMColor + " World " + listedWorld + worldCurrentSpeedMsg + " §e" +  listedWorldSpeed + "§r."); // Final player msg
				waitTime(1000);
			} else
			{
				Bukkit.getLogger().info(prefixTM + " World " + listedWorld + worldCurrentStartMsg + " #" +  listedWorldStartTick + " (" + listedWorldStartTime + ")."); // Final console msg
				Bukkit.getLogger().info(prefixTM + " World " + listedWorld + worldCurrentTickMsg + " #" +  listedWorldCurrentTick + " (" +  listedWorldCurrentTime + ")."); // Final console msg
				Bukkit.getLogger().info(prefixTM + " World " + listedWorld + worldCurrentSpeedMsg + " " +  listedWorldSpeed + "."); // Final console msg
			}
    	}
	};

	/** 
	 * Custom wait
	 * 
	 */ 
	public static void waitTime(Integer ticksToWait) {
		try {
			Thread.sleep(ticksToWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	};	
	
}