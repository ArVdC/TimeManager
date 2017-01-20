package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;

public class WorldSpeedHandler extends MainTM {
	
	/** 
	 * Modify worlds speed with an auto cancel/repeat capable scheduler
	 */	   
    public static void WorldSpeedModify() {
    	ScheduleIsOn = true;
    	refreshRateLong = MainTM.getInstance().getConfig().getLong("refreshRate");
    	
        BukkitScheduler speedSheduler = MainTM.getInstance().getServer().getScheduler();
        speedSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
            @Override
            public void run() {
            	boolean loopMore = false;
            	for(String w : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
            		long actualWorldTime = Bukkit.getWorld(w).getTime(); // Get the current time of the world
                	double speedModifier = MainTM.getInstance().getConfig().getDouble("worldsList."+w+".speed");
                	if(speedModifier > 0 && speedModifier != 1 && speedModifier <= speedMax) { // Don't treat worlds with frozen or normal timers
                		loopMore = true;
                    	long modifTime = (long) Math.ceil(refreshRateInt * speedModifier);
                		//Bukkit.getLogger().info(prefixTM + " " + w + " modifier: " + refreshRateLong + " * " + speedModifier + "."); // Console debug msg
                    	
                		long newTime = actualWorldTime + modifTime - refreshRateLong;
                		//Bukkit.getLogger().info(prefixTM + " " + w + " time: " + actualWorldTime + " + " + modifTime + " - " + refreshRateLong + "."); // debug msg
                    	// Restrain too big and too small values
                    	newTime = RestrainValuesHandler.returnCorrectTicks(newTime);
                    	// Change world's timer
                    	Bukkit.getWorld(w).setTime(newTime);
                		//Bukkit.getLogger().info(prefixTM + " " + w+ "'s time is now tick #" + newTime + "."); // Console debug msg	
            		}                
                }
            	if(loopMore == true) {
            		WorldSpeedModify();
            	} else {
                	ScheduleIsOn = false;
            	}
            }
        }, refreshRateLong);
    };

}
