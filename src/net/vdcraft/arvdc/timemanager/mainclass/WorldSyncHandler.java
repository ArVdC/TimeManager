package net.vdcraft.arvdc.timemanager.mainclass;

import java.text.DateFormat;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.NowFormatTime;

public class WorldSyncHandler extends MainTM {
	
	/** 
	 * Delayed sync on startup method
	 */	
    public static void WorldSyncFirst() { // Run only once
        BukkitScheduler firstSyncSheduler = MainTM.getInstance().getServer().getScheduler();
        firstSyncSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
            @Override
            public void run() {
        		// #A. Get the initial Server tick once only
        		long ticksSinceEpoch = (long) (System.currentTimeMillis() / 50L); // Get the server actual time
        		initialTick = ticksSinceEpoch % 24000; // Convert the server actual time into ticks
				DateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm:ss z");
				Calendar ServerCal = Calendar.getInstance();
				initialTime = timeFormat.format(ServerCal.getTime()); // Save the initial time in HH:mm:ss
        		// #B. Synchronize the worlds, based on a server constant point
		        Bukkit.getLogger().info(prefixTM + " " + resyncIntroMsg); // Console log msg (always)
        		WorldSyncRe("all");
        		// #C. Try to launch speed modify scheduler
				WorldSpeedHandler.WorldSpeedModify();
            }
        }, 2L);
    };
    
	/** 
	 * Sync method
	 */	
    public static void WorldSyncRe(String wichWorld) {    	
		long ticksSinceEpoch = (long) (System.currentTimeMillis() / 50L); // Get the server actual time
		long serverTick = ticksSinceEpoch % 24000; // Convert the server actual time into ticks
		DateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm:ss z");
		Calendar ServerCal = Calendar.getInstance();
		String currentTime = timeFormat.format(ServerCal.getTime()); // Save the initial time in HH:mm:ss
		long startAtTickNb;
		double speedModifNb;		
		// #A. Re-synchronize all worlds
		if(wichWorld.equalsIgnoreCase("all")) {
			DaylightCycleHandler.doDaylightCheck("all");
			Bukkit.getLogger().info(prefixTM + " " + serverInitTickMsg + " #" + initialTick + " (" + initialTime + ")."); // Console log msg
			Bukkit.getLogger().info(prefixTM + " " + serverCurrentTickMsg + " #" + serverTick + " (" + currentTime + ")."); // Console log msg
	        for(World w:Bukkit.getServer().getWorlds()) { 		
	        	if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(w.getName())) { // Read config.yml to check if the world's name is listed        		
	        		WorldSyncRe(w.getName());
	        	}
	        }
	    // #B. Re-synchronize a single world
		} else {
			DaylightCycleHandler.doDaylightCheck(wichWorld);
			startAtTickNb = (MainTM.getInstance().getConfig().getLong("worldsList."+wichWorld+".start")); // Read config.yml to get the world's 'start' value 
			speedModifNb = (MainTM.getInstance().getConfig().getDouble("worldsList."+wichWorld+".speed")); // Read config.yml to get the world's 'speed' value   	
			long newTick;
			if(speedModifNb == 0) {
        		newTick = (long) startAtTickNb;
        	} else {
        		newTick = (long) (((serverTick - initialTick) * speedModifNb) + startAtTickNb); // Elapsed time * speed modifier + start at #tick
        	}
			newTick = RestrainValuesHandler.returnCorrectTicks(newTick);
        	Bukkit.getServer().getWorld(wichWorld).setTime(newTick);
			// Notifications
    		String newTime = NowFormatTime.ticksAsTime(newTick);
    		Bukkit.getLogger().info(prefixTM + " " + wichWorld + worldCurrentTickMsg + " #" + newTick + " (" + newTime + ")."); // Console log msg
    		Bukkit.getLogger().info(prefixTM + " " + wichWorld + worldCurrentSpeedMsg + " " + speedModifNb + "."); // Console log msg
		}
    };
    
}