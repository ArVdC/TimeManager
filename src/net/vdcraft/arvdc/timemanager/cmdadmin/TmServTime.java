package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class TmServTime extends MainTM {
	
	/** 
	 * CMD /tm servtime
	 * 
	 */
	public static void cmdServerTime(CommandSender sender) {
		
		// Get the current server tick
		long currentServerTick = ValuesConverter.returnServerTick();	
		// Get the current server UTC time in HH:mm:ss
		String currentServerTime = ValuesConverter.returnServerTime();
			
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
    		String listedWorldStartTime = ValuesConverter.returnTicksAsTime(listedWorldStartTick);
    		String formattedUTC = ValuesConverter.formatAsUTC(listedWorldStartTick);
    		String listedWorldCurrentTime = ValuesConverter.returnTicksAsTime(listedWorldCurrentTick);
    		String listedWorldSpeed = (MainTM.getInstance().getConfig().getString("worldsList."+listedWorld+".speed"));
    		if(listedWorldSpeed.contains("24")) { // Display realtime messages
				if(sender instanceof Player) {
					sender.sendMessage(prefixTMColor + " World " + listedWorld + " " + worldCurrentStartMsg + " §e" + formattedUTC + " §r(§e+" + listedWorldStartTick + " §rticks).");  // Final player msg
					waitTime(1000);
					sender.sendMessage(prefixTMColor + " World " + listedWorld + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " §r(§e#" + listedWorldCurrentTick + "§r)."); // Final player msg
					waitTime(1000);
					sender.sendMessage(prefixTMColor + " World " + listedWorld + worldCurrentSpeedMsg + " " + worldRealSpeedMsg); // Final player msg
					waitTime(1000);
				} else
				{
					Bukkit.getLogger().info(prefixTM + " World " + listedWorld + " " + worldCurrentStartMsg + " " + formattedUTC + " (+" + listedWorldStartTick + " ticks)."); // Final console msg
					Bukkit.getLogger().info(prefixTM + " World " + listedWorld + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " (#" + listedWorldCurrentTick + ")."); // Final console msg
					Bukkit.getLogger().info(prefixTM + " World " + listedWorld + worldCurrentSpeedMsg + " " + worldRealSpeedMsg); // Final console msg
				}
    		} else { // Display normal messages
				if(sender instanceof Player) {
					sender.sendMessage(prefixTMColor + " World " + listedWorld + " " + worldCurrentStartMsg + " §e" + listedWorldStartTime + " §r(§e+" + listedWorldStartTick + " §rticks).");  // Final player msg
					waitTime(1000);
					sender.sendMessage(prefixTMColor + " World " + listedWorld + worldCurrentTimeMsg + " §e" + listedWorldCurrentTime + " §r(§e#" + listedWorldCurrentTick + "§r)."); // Final player msg
					waitTime(1000);
					sender.sendMessage(prefixTMColor + " World " + listedWorld + worldCurrentSpeedMsg + " §e" +  listedWorldSpeed + "§r."); // Final player msg
					waitTime(1000);
				} else
				{
					Bukkit.getLogger().info(prefixTM + " World " + listedWorld + " " + worldCurrentStartMsg + " " + listedWorldStartTime + " (+" + listedWorldStartTick + " ticks)."); // Final console msg
					Bukkit.getLogger().info(prefixTM + " World " + listedWorld + worldCurrentTimeMsg + " " +  listedWorldCurrentTime + " (#" +  listedWorldCurrentTick + ")."); // Final console msg
					Bukkit.getLogger().info(prefixTM + " World " + listedWorld + worldCurrentSpeedMsg + " " +  listedWorldSpeed + "."); // Final console msg
				}    			
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