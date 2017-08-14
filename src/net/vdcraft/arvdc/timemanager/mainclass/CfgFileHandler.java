package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import net.vdcraft.arvdc.timemanager.MainTM;

public class CfgFileHandler extends MainTM {

	/**
	 * Activate or reload the configuration file
	 */
	public static void loadConfig(String firstOrRe) {
		
    	// #1. When it is the server startup
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
			// #D. Toggle debugMode on/off
			DebugModeHandler.debugModeOnOff();
		    // #E. Actualize SQL related values
			SqlHandler.initSqlDatas();
    	}
    	
    	// #2. When using the admin command /tm reload
    	if(firstOrRe.equalsIgnoreCase("re")) {
		    if(MainTM.getInstance().configFileYaml.exists()) {
	    		// #A. Notification
	            Bukkit.getLogger().info(prefixTM + " " + cfgFileTryReloadMsg);
				// #B. Reload values from config.yml file
				MainTM.getInstance().reloadConfig();
				// #C. Toggle debugMode on/off
				DebugModeHandler.debugModeOnOff();
			    // #D. Check if SQL is needed, in case open connection
				SqlHandler.initSqlDatas();
			    // #E. Check for ref tick. If SQL is needed, open the connection, else use config.yml
				WorldSyncHandler.refreshRefTickAndTime();
		    } else loadConfig("first");
    	}
    	
    	// #3. In both case:
    	
		// #A. Set some default values if missing or corrupt
    	if(MainTM.getInstance().getConfig().getKeys(false).contains("defTimeUnits")) {
    		if(MainTM.getInstance().getConfig().getString("defTimeUnits").equals("")) {
    			MainTM.getInstance().getConfig().set("defTimeUnits", defTimeUnits);
    		}
	    }
    	if(MainTM.getInstance().getConfig().getConfigurationSection("initialTick").getKeys(false).contains("resetOnStartup")) {
		    if(!(MainTM.getInstance().getConfig().getString("initialTick.resetOnStartup").equals("false"))) {
		    	MainTM.getInstance().getConfig().set("initialTick.resetOnStartup", "true");
		    }
    	}
		
	    // #B. Check and complete list of available worlds
		WorldListHandler.listLoadedWorlds();
		
    	// #C. Restrain the refresh rate
    	ValuesConverter.restrainRate();
    	
    	// #D. Restrain the initialTickNb value
    	ValuesConverter.restrainInitTick();

		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + cfgOptionsCheckDebugMsg); // Console debug msg
		
		for(String w : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
			
			// #E. Restrain the speed modifiers
	    	ValuesConverter.restrainSpeed(w);
	    	
			// #F. Restrain the start times
	    	ValuesConverter.restrainStart(w);
	    	
			// #G. Restrain the sync value
	    	ValuesConverter.restrainSync(w, 0.1);
	    	
			// #H. Restrain the sleep value
	    	ValuesConverter.restrainSleep(w);
		}
    	
    	// #I. Restore and display version value
	    MainTM.getInstance().getConfig().set("version", versionTM);
		Bukkit.getLogger().info(prefixTM + " " + cfgVersionMsg + MainTM.getInstance().getConfig().getString("version") + "."); // Console version msg
		
		// #J. Save the changes
		MainTM.getInstance().saveConfig();
	};

	/** 
	 * Return an array list from anything listed in a specific key from the config.yml
	 */
	public static List<String> setAnyListFromConfig(String inWichYamlKey) {
		List<String> listedElementsList = new ArrayList<>();
		for(String listedElement : MainTM.getInstance().getConfig().getConfigurationSection(inWichYamlKey).getKeys(false)) {
			listedElementsList.add(listedElement);		
		}
		return listedElementsList;
	};

}