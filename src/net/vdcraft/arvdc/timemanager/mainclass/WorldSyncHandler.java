package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;

public class WorldSyncHandler extends MainTM {
	
	/**
	 * Delayed sync all on startup
	 */	
    public static void WorldSyncFirst() { // Run only once
        BukkitScheduler firstSyncSheduler = MainTM.getInstance().getServer().getScheduler();
        firstSyncSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
            @Override
            public void run() {
        		// #A. Get the current server time and save it as the reference tick
            	getInitialTickAndTime();
        		// #B. Synchronize the worlds, based on a server constant point
		        Bukkit.getLogger().info(prefixTM + " " + resyncIntroMsg); // Console log msg (always)
        		WorldSyncRe("all");
    			// #C. Launch the good scheduler if it is inactive
				if(increaseScheduleIsOn == false) { 
		    		WorldSpeedHandler.WorldIncreaseSpeed();
			    }
		    	if(decreaseScheduleIsOn == false) {
		    		WorldSpeedHandler.WorldDecreaseSpeed();
		    	}
			    if(realScheduleIsOn == false) {
		    		WorldSpeedHandler.WorldRealSpeed();
		    	}
            }
        }, 2L);
    };
    
	/** 
	 * Sync method <world> or <all>
	 */	
    public static void WorldSyncRe(String wichWorld) {
    	// Get the current server time
		long currentServerTick = ValuesConverter.returnServerTick();
    	// Get the current server time
		String currentServerTime = ValuesConverter.returnServerTime();
		long startAtTickNb;
		double speedModifNb;		
		// #A. Re-synchronize all worlds
		if(wichWorld.equalsIgnoreCase("all")) {
			DaylightCycleHandler.doDaylightCheck("all");
			Bukkit.getLogger().info(prefixTM + " " + serverInitTickMsg + " #" +  initialTick + " (" + initialTime + ")."); // Final console msg // Console log msg
			Bukkit.getLogger().info(prefixTM + " " + serverCurrentTickMsg + " #" + currentServerTick + " (" + currentServerTime + ")."); // Console log msg
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
			if(speedModifNb == 24) { // if realtime world
        		newTick = (long) (currentServerTick / 72L) + (startAtTickNb - 6000L);
        	} else if(speedModifNb == 0) { // if frozen world
            		newTick = (long) startAtTickNb;
        	} else {
        	   newTick = (long) ((((currentServerTick % 24000) - (initialTick % 24000)) * speedModifNb * 72) + startAtTickNb); // Elapsed time * speed modifier + start at #tick
        	   }
			newTick = ValuesConverter.returnCorrectTicks(newTick);
			//Bukkit.getLogger().info(prefixTM + " " + newTick + " = ((" + (currentServerTick % 24000) + " - " + (initialTick % 24000) + ") * " + speedModifNb + " * 72) + " + startAtTickNb);
        	
        	Bukkit.getServer().getWorld(wichWorld).setTime(newTick);
			// Notifications
    		String listedWorldCurrentTime = ValuesConverter.returnTicksAsTime(newTick);
    		String listedWorldStartTime = ValuesConverter.returnTicksAsTime(startAtTickNb);
    		String formattedUTC = ValuesConverter.formatAsUTC(startAtTickNb);
    		if(speedModifNb == 24) { // Display realtime messages
				Bukkit.getLogger().info(prefixTM + " World " + wichWorld + " " + worldCurrentStartMsg + " " + formattedUTC + " (+" + startAtTickNb + " ticks)."); // Final console msg
				Bukkit.getLogger().info(prefixTM + " World " + wichWorld + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " (#" + newTick + ")."); // Final console msg
				Bukkit.getLogger().info(prefixTM + " World " + wichWorld + worldCurrentSpeedMsg + " " + worldRealSpeedMsg); // Final console msg
			} else { // Display normal messages
				Bukkit.getLogger().info(prefixTM + " World " + wichWorld + " " + worldCurrentStartMsg + " " + listedWorldStartTime + " (+" + startAtTickNb + " ticks)."); // Final console msg
				Bukkit.getLogger().info(prefixTM + " World " + wichWorld + worldCurrentTimeMsg + " " +  listedWorldCurrentTime + " (#" +  newTick + ")."); // Final console msg
				Bukkit.getLogger().info(prefixTM + " World " + wichWorld + worldCurrentSpeedMsg + " " +  speedModifNb + "."); // Final console msg
			}
		}
    };
    
	/** 
	 * Set or get the reference tick on startup
	 */	
    private static void getInitialTickAndTime() {
    	String setOrGet = "null";
    	String ymlOrSql = "null";
    	if(MainTM.getInstance().getConfig().getString("initialTick.useMySql").equalsIgnoreCase("true")) { // If mySQL is true
			if(SqlHandler.openTheConnectionIfPossible() == true) {
				if(MainTM.getInstance().getConfig().getString("initialTick.resetOnStartup").equalsIgnoreCase("false")) { // If reset false
					 // Try to read database
					initialTick = SqlHandler.getServerTickSQL(); // Get existing reference tick from SQL database
					if(initialTick == null) { // If db is null, create the initial tick
						initialTick = ValuesConverter.returnServerTick();
						SqlHandler.setServerTickSQL(initialTick); // Save tick in SQL database
					}
					initialTime = ValuesConverter.returnTickAsHHmmss(initialTick); // Convert the initial time in HH:mm:ss UTC
					MainTM.getInstance().getConfig().set("initialTick.initialTickNb", initialTick); // Save tick in config
					setOrGet = "get from";
				} else { // If reset true
					// Define a new reference tick
					initialTick = ValuesConverter.returnServerTick(); // Create the initial tick
					initialTime = ValuesConverter.returnServerTime(); // Create the initial time in HH:mm:ss UTC
					MainTM.getInstance().getConfig().set("initialTick.initialTickNb", initialTick); // Save tick in config
					Long testInitialTickSQL = SqlHandler.getServerTickSQL(); // Get existing reference tick from SQL database
					if(testInitialTickSQL == null) {
						SqlHandler.setServerTickSQL(initialTick); // Save tick in SQL database
					} else {
						SqlHandler.updateServerTickSQL(initialTick); // Update tick in SQL database
					}
					setOrGet = "set in";
				}
				ymlOrSql = "the mySQL database";
			} else { // When a connection fails, the key 'useMySql' is set on false, so this will retry sync but using the config.yml
				getInitialTickAndTime();
			}
		} else if(MainTM.getInstance().getConfig().getString("initialTick.useMySql").equalsIgnoreCase("false")) { // When mySQL is false
			// If reset true OR initialTickNb doesn't exist
			if(MainTM.getInstance().getConfig().getString("initialTick.resetOnStartup").equalsIgnoreCase("false") && !MainTM.getInstance().getConfig().getString("initialTick.initialTickNb").equals("")) {
				// If reset false AND initialTickNb exists
				initialTick = MainTM.getInstance().getConfig().getLong("initialTick.initialTickNb"); // Get existing reference tick from config.yml
				initialTime = ValuesConverter.returnTickAsHHmmss(initialTick); // Convert the initial time in HH:mm:ss UTC
				setOrGet = "get from";
			} else { // Define a new reference tick
	     		initialTick = ValuesConverter.returnServerTick(); // Create the initial tick
				initialTime = ValuesConverter.returnTickAsHHmmss(initialTick); // Convert the initial time in HH:mm:ss UTC
				MainTM.getInstance().getConfig().set("initialTick.initialTickNb", initialTick); // Save tick in config.yml
				setOrGet = "set in";
			}
			ymlOrSql = "the config.yml";
		}		
		MainTM.getInstance().saveConfig(); // Save config.yml file
		SqlHandler.closeConnection("DB"); // Close connection
		Bukkit.getLogger().info(prefixTM + " " + "The server's initial tick was " + setOrGet + " " + ymlOrSql + "."); // Console log msg
    };
    
	/** 
	 * Get the reference tick on reload
	 */
    public static void refreshRefTickAndTime() {
    	if(MainTM.getInstance().getConfig().getString("initialTick.useMySql").equalsIgnoreCase("true")) { // If mySQL is true
			if(SqlHandler.openTheConnectionIfPossible() == true) {
				initialTick = SqlHandler.getServerTickSQL(); // Get existing reference tick from SQL database
				initialTime = ValuesConverter.returnTickAsHHmmss(initialTick); // Convert the initial time in HH:mm:ss UTC
			} else {
				initialTick = MainTM.getInstance().getConfig().getLong("initialTick.initialTickNb"); // Get existing reference tick from config.yml
				initialTime = ValuesConverter.returnTickAsHHmmss(initialTick); // Convert the initial time in HH:mm:ss UTC
			}
		} else {
				initialTick = MainTM.getInstance().getConfig().getLong("initialTick.initialTickNb"); // Get existing reference tick from config.yml
				initialTime = ValuesConverter.returnTickAsHHmmss(initialTick); // Convert the initial time in HH:mm:ss UTC
		}
		MainTM.getInstance().saveConfig(); // Save config file
		SqlHandler.closeConnection("DB");
    };
       
}