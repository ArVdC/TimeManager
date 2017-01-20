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
			// Create congig.yml file if missing, force actual version
		    if(!MainTM.getInstance().configFileYaml.exists()) {
		    	Bukkit.getLogger().info(prefixTM + " " + cfgFileCreaMsg); // Console missing file msg
		    }	
		    else {
		    	Bukkit.getLogger().info(prefixTM + " " + cfgFileExistMsg); // Console existing file msg
		    }
		    // Assure to recreate missing key in config.yml file
		    MainTM.getInstance().getConfig().options().copyDefaults(true);
		    // Actualize or create the config.yml file
		    MainTM.getInstance().saveDefaultConfig();
    	}	
    	
    	// #2. When using the admin command reload
    	if(firstOrRe.equalsIgnoreCase("re")) {
    		// Notification
            Bukkit.getLogger().info(prefixTM + " " + cfgFileTryReloadMsg);
			// Reload values from config.yml file
			MainTM.getInstance().reloadConfig();
    	}
    	
    	// #3. In both case
    	
		// #A. Set some default values if missing
    	if(MainTM.getInstance().getConfig().getKeys(false).contains("defTimeUnits")) {
    		if(MainTM.getInstance().getConfig().getString("defTimeUnits").equals("")) {
    			MainTM.getInstance().getConfig().set("defTimeUnits", defTimeUnits);
    		}
	    }
    	if(MainTM.getInstance().getConfig().getKeys(false).contains("useMySql")) {
		    if(MainTM.getInstance().getConfig().getString("useMySql").equals("")) {
		    	MainTM.getInstance().getConfig().set("mySql.useMySql", "false");
		    }
    	}
    	if(MainTM.getInstance().getConfig().getKeys(false).contains("refServer")) {
		    if(MainTM.getInstance().getConfig().getString("refServer").equals("")) {
		    	MainTM.getInstance().getConfig().set("mySql.refServer", "true");
		    }
    	}
    	
	    // #B. Check and complete list of available worlds
		WorldListHandler.listLoadedWorlds();
	    
    	// #C. Restrain the refresh rate
    	RestrainValuesHandler.restrainRate();
    	
		// #D. Restrain the speed modifiers
    	RestrainValuesHandler.restrainSpeed();
    	
		// #E. Restrain the start times
    	RestrainValuesHandler.restrainStart();

    	// #F. Restore and display version value
	    MainTM.getInstance().getConfig().set("version", versionTM);
		Bukkit.getLogger().info(prefixTM + " " + cfgVersionMsg + MainTM.getInstance().getConfig().getString("version") + "."); // Console version msg
		
		// #G.Save the changes
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