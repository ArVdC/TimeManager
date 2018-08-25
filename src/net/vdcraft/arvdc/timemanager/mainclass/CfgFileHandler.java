package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import net.vdcraft.arvdc.timemanager.MainTM;

public class CfgFileHandler extends MainTM {

	/**
	 * Activate or reload the configuration file
	 */
	/**
	 * @param firstOrRe
	 */
	public static void loadConfig(String firstOrRe) {		
		
    	// #1. Only at the server startup:
    	if(firstOrRe.equalsIgnoreCase("first")) {
			// #A. Create congig.yml file if missing, force actual version
		    if(!(MainTM.getInstance().configFileYaml.exists())) {
		    	Bukkit.getLogger().info(prefixTM + " " + cfgFileCreaMsg); // Console missing file msg
		    }	
		    else {
		    	Bukkit.getLogger().info(prefixTM + " " + cfgFileExistMsg); // Console existing file msg
		    }
		    // #B. Assure to recreate missing key in config.yml file
		    MainTM.getInstance().getConfig().options().copyDefaults(true);
		    // #C. Actualize or create the config.yml file
		    MainTM.getInstance().saveDefaultConfig();
    	}

		// # 2. Get the previous initial tick value (before the reload)
        Long oldTick = MainTM.getInstance().getConfig().getLong("initialTick.initialTickNb");
        
    	// #3. Only when using the admin command /tm reload:
    	if(firstOrRe.equalsIgnoreCase("re")) {
		    if(MainTM.getInstance().configFileYaml.exists()) {
	    		// #A. Notification
	            Bukkit.getLogger().info(prefixTM + " " + cfgFileTryReloadMsg);
				// #C. Reload values from config.yml file
				MainTM.getInstance().reloadConfig();
		    } else loadConfig("first");
    	}
    	    	
		// #4. Toggle debugMode on/off
		DebugModeHandler.debugModeOnOff();
    	
		// #5. Set some default values if missing or corrupt in the initialTick node
    	// #A. defTimeUnits value
    	if(MainTM.getInstance().getConfig().getKeys(false).contains("defTimeUnits")) {
    		if(MainTM.getInstance().getConfig().getString("defTimeUnits").equals("")) {
    			MainTM.getInstance().getConfig().set("defTimeUnits", defTimeUnits);
    		}
	    } else {
	    	MainTM.getInstance().getConfig().set("defTimeUnits", defTimeUnits);
	    }
    	// #B. resetOnStartup value
    	if(MainTM.getInstance().getConfig().getConfigurationSection("initialTick").getKeys(false).contains("resetOnStartup")) {
		    if(!(MainTM.getInstance().getConfig().getString("initialTick.resetOnStartup").equals("false"))) {
		    	MainTM.getInstance().getConfig().set("initialTick.resetOnStartup", "true");
		    }
    	} else {
    		MainTM.getInstance().getConfig().set("initialTick.resetOnStartup", "true");
    	}
    	// #C. useMySql value
    	if(MainTM.getInstance().getConfig().getConfigurationSection("initialTick").getKeys(false).contains("useMySql")) {
		    if(!(MainTM.getInstance().getConfig().getString("initialTick.useMySql").equals("false"))) {
		    	MainTM.getInstance().getConfig().set("initialTick.useMySql", "true");
		    }
    	} else {
    		MainTM.getInstance().getConfig().set("initialTick.useMySql", "false");
    	}    	
		
	    // #6. Set some default values if missing or corrupt in the mySQL node
		SqlHandler.initSqlDatas();		
		
    	// #7. Restrain the refresh rate
    	ValuesConverter.restrainRate();
    	
    	// #8. Only when using the admin command /tm reload: Update the initialTickNb value
    	if(firstOrRe.equalsIgnoreCase("re")) {
    		WorldSyncHandler.updateInitialTickAndTime(oldTick);
    	}
		// #9. Refresh the initialTickNb every (x) minutes - only if a MySQL database is used and the scheduleSyncDelayedTask is off
    	if(MainTM.getInstance().getConfig().getString("initialTick.useMySql").equals("true") && !mySqlRefreshIsAlreadyOn) {
    		mySqlRefreshIsAlreadyOn = true;
    		WorldSyncHandler.refreshInitialTickMySql();
    		Bukkit.getLogger().info(prefixTM + " " + sqlInitialTickAutoUpdateMsg);  // Notify the console
    	}
    	
	    // #1. Check and complete list of available worlds
		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + cfgOptionsCheckDebugMsg); // Console debug msg
		WorldListHandler.listLoadedWorlds();

		// #11. For each world		
		for(String w : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
			
			// #A. Restrain the speed modifiers
	    	ValuesConverter.restrainSpeed(w);
	    	
			// #B. Restrain the start times
	    	ValuesConverter.restrainStart(w);
	    	
			// #C. Restrain the sync value
	    	ValuesConverter.restrainSync(w, 0.1);
	    	
			// #D. Restrain the sleep value
	    	ValuesConverter.restrainSleep(w);
		}
    	
    	// #12. Restore the version value
	    MainTM.getInstance().getConfig().set("version", versionTM());
		
		// #13. Save the changes
		MainTM.getInstance().saveConfig();
	    
	    // #14. Notifications
	    if(firstOrRe.equalsIgnoreCase("first")) {
	    	Bukkit.getLogger().info(prefixTM + " " + cfgVersionMsg + MainTM.getInstance().langConf.getString("version") + "."); // Notify the console
	    }
	}

	/** 
	 * Return an array list from anything listed in a specific key from the config.yml
	 */
	public static List<String> setAnyListFromConfig(String inWichYamlKey) {
		List<String> listedElementsList = new ArrayList<>();
		for(String listedElement : MainTM.getInstance().getConfig().getConfigurationSection(inWichYamlKey).getKeys(false)) {
			listedElementsList.add(listedElement);		
		}
		return listedElementsList;
	}

};