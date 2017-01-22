package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;

public class WorldSpeedHandler extends MainTM {
	
	/** 
	 * Increase worlds speed to a custom rate with an auto cancel/repeat capable scheduler
	 */	   
    public static void WorldIncreaseSpeed() {
    	increaseScheduleIsOn = true;
    	refreshRateLong = MainTM.getInstance().getConfig().getLong("refreshRate");
    	
        BukkitScheduler speedSheduler = MainTM.getInstance().getServer().getScheduler();
        speedSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
            @Override
            public void run() {
            	boolean loopMore = false;
            	for(String w : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
            		long actualWorldTime = Bukkit.getWorld(w).getTime(); // Get the current time of the world
                	double speedModifier = MainTM.getInstance().getConfig().getDouble("worldsList."+w+".speed");
                	String isSpeedRealTime = MainTM.getInstance().getConfig().getString("worldsList."+w+".speed");
                	if(speedModifier > 1.0 && speedModifier <= speedMax && !(isSpeedRealTime.equals("realTime"))) { // Only treat worlds with increased timers
                		loopMore = true;
                    	long modifTime = (long) Math.ceil(refreshRateInt * speedModifier);
                		long newTime = actualWorldTime + modifTime - refreshRateLong;
                		// Restrain too big and too small values
                    	newTime = ValuesConverter.returnCorrectTicks(newTime);
                    	// Change world's timer
                    	Bukkit.getWorld(w).setTime(newTime);
            		}                
                }
            	if(loopMore == true) {
            		WorldIncreaseSpeed();
            	} else {
            		increaseScheduleIsOn = false;
            	}
            }
        }, refreshRateLong);
    };
	
	/** 
	 * Decrease worlds speed to a custom rate with an auto cancel/repeat capable scheduler
	 */	   
    public static void WorldDecreaseSpeed() {
    	decreaseScheduleIsOn = true;
    	refreshRateLong = MainTM.getInstance().getConfig().getLong("refreshRate");
    	
        BukkitScheduler speedSheduler = MainTM.getInstance().getServer().getScheduler();
        speedSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
            @Override
            public void run() {
            	boolean loopMore = false;
            	for(String w : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
            		long actualWorldTime = Bukkit.getWorld(w).getTime(); // Get the current time of the world
                	double speedModifier = MainTM.getInstance().getConfig().getDouble("worldsList."+w+".speed");
                	String isSpeedRealTime = MainTM.getInstance().getConfig().getString("worldsList."+w+".speed");
                	if(speedModifier > 0.0 && speedModifier < 1.0 && !(isSpeedRealTime.equals("realTime"))) { // Only treat worlds with increased timers
                		loopMore = true;
                    	long modifTime = (long) Math.floor((refreshRateInt * speedModifier));                   	
                		long newTime = actualWorldTime + modifTime;
                    	// Restrain too big and too small values
                    	newTime = ValuesConverter.returnCorrectTicks(newTime);
                    	// Change world's timer
                    	Bukkit.getWorld(w).setTime(newTime);
            		}                
                }
            	if(loopMore == true) {
            		WorldDecreaseSpeed();
            	} else {
                	decreaseScheduleIsOn = false;
            	}
            }
        }, refreshRateLong);
    };
	
	/** 
	 * Modify worlds speed to real time speed with an auto cancel/repeat capable scheduler
	 */	   
    public static void WorldRealSpeed() {
    	realScheduleIsOn = true;
    	
        BukkitScheduler realSpeedSheduler = MainTM.getInstance().getServer().getScheduler();
        realSpeedSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
            @Override
            public void run() {
            	boolean loopMore = false;
            	for(String w : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
                	String isSpeedRealTime = MainTM.getInstance().getConfig().getString("worldsList."+w+".speed");
                	long worldStartAt = MainTM.getInstance().getConfig().getLong("worldsList."+w+".start");
                	if(isSpeedRealTime.contains("24")) { // Only treat worlds with a '24.0' timers
                		loopMore = true;
                    	// Get the current server tick
                		long currentServerTick = ValuesConverter.returnServerTick();
                		long newTime = (currentServerTick / 72L) + (worldStartAt - 6000L); // -6000 cause a mc's day start at 6:00
                    	// Restrain too big and too small values
                    	newTime = ValuesConverter.returnCorrectTicks(newTime);
                    	// Change world's timer
                    	Bukkit.getWorld(w).setTime(newTime);
            		}                
                }
            	if(loopMore == true) {
            		WorldRealSpeed();
            	} else {
            		realScheduleIsOn = false;
            	}
            }
        }, 72L);
    };
 
}
