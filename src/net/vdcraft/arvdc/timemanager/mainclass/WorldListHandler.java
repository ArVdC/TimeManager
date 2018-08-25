package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmCheckTime;

public class WorldListHandler extends MainTM {

	/** 
	 *  Manage available worlds list
	 */
	public static void listLoadedWorlds() {
		
		// #1. Avoid missing 'worldsList' key
		if(!(MainTM.getInstance().getConfig().getKeys(false).contains("worldsList"))) {	
			MainTM.getInstance().getConfig().set("worldsList.Example.start", "0");
    	}
		// #2. Avoid void 'worldsList' key
		if(MainTM.getInstance().getConfig().getString("worldsList").equals("")) {			
			MainTM.getInstance().getConfig().set("worldsList.Example.start", "example");
    	}
		// #3. Get the complete list of loaded worlds and add it to config.yml
		for(World w : Bukkit.getServer().getWorlds()) {
			String loadedWorld = w.getName();
			// Check if the old 'sleepUntilDawn' option still exists, if yes update it to the new 'sleep'
			if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(loadedWorld)) {
				if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList."+loadedWorld).getKeys(false).contains("sleepUntilDawn")) {
					if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " sleepUntilDawn still exists for the world " + loadedWorld + ", copy it to 'sleep' and erase it.");
					String sleepParam = MainTM.getInstance().getConfig().getString("worldsList."+loadedWorld+".sleepUntilDawn");
					MainTM.getInstance().getConfig().set("worldsList."+loadedWorld+".sleep", sleepParam);
					MainTM.getInstance().getConfig().set("worldsList."+loadedWorld+".sleepUntilDawn", null);
					}
			} // Check if it already figures in existing list and if it is not 'nether' neither 'ender'
			if(!(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(loadedWorld)) && !(loadedWorld.contains("_nether")) && !(loadedWorld.contains("_the_end"))) { 
			// If not, add it in the list with default parameters
				MainTM.getInstance().getConfig().set("worldsList."+loadedWorld+".start", defStart);
				MainTM.getInstance().getConfig().set("worldsList."+loadedWorld+".speed", defSpeed);
				MainTM.getInstance().getConfig().set("worldsList."+loadedWorld+".sleep", defSleep);
				MainTM.getInstance().getConfig().set("worldsList."+loadedWorld+".sync", defSync);
			} // If a world already exists, check its 'speed' and 'start' keys
			else if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(loadedWorld)) {
				// Check if 'start' exists
				if(!(MainTM.getInstance().getConfig().getConfigurationSection("worldsList."+loadedWorld).getKeys(false).contains("start")))
				{ // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set("worldsList."+loadedWorld+".start", defStart);
				}
				// Check if 'speed' exists
				if(!(MainTM.getInstance().getConfig().getConfigurationSection("worldsList."+loadedWorld).getKeys(false).contains("speed")))
				{ // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set("worldsList."+loadedWorld+".speed", defSpeed);
				}
				// Check if 'sleep' exists
				if(!(MainTM.getInstance().getConfig().getConfigurationSection("worldsList."+loadedWorld).getKeys(false).contains("sleep")))
				{ // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set("worldsList."+loadedWorld+".sleep", defSleep);
				}
				// Check if 'sync' exists
				if(!(MainTM.getInstance().getConfig().getConfigurationSection("worldsList."+loadedWorld).getKeys(false).contains("sync")))
				{ // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set("worldsList."+loadedWorld+".sync", defSync);
				}
			}
		}
		// #4. Remove 'Example' + 'nether' + 'ender' + inexistent worlds if they are present in the config list
		List<World> loadedWorlds = Bukkit.getServer().getWorlds();
		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + refrehWorldsListDebugMsg); // Console debug msg
		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + worldsRawListDebugMsg + " §e" + loadedWorlds); // Console debug msg
		String loadedWorldsNames = "" + loadedWorlds;
		loadedWorldsNames = loadedWorldsNames.replace("},", ",").replace("CraftWorld{name=", "");
		loadedWorldsNames = loadedWorldsNames.substring(0,loadedWorldsNames.length()-2);
		loadedWorldsNames = loadedWorldsNames.substring(1,loadedWorldsNames.length());
		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + worldsFormatListDebugMsg + " [" + loadedWorldsNames + "]"); // Console debug msg
		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + worldsCfgListDebugMsg + " " + CfgFileHandler.setAnyListFromConfig("worldsList")); // Console debug msg
		for(String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
			Boolean eraseWorld = false;
			if(listedWorld.equals("Example") || listedWorld.contains("_the_end") || listedWorld.contains("_nether")) {
				eraseWorld = true;
			} else {
					if(!(loadedWorldsNames.contains(listedWorld))) {
						eraseWorld = true;
					}
			}
			TmCheckTime.waitTime(1000);
			if(eraseWorld == true) {
				if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " The world §e" + eraseWorld + "§b " + delWorldDebugMsg); // Console debug msg
				MainTM.getInstance().getConfig().getConfigurationSection("worldsList").set(listedWorld, null);
			}
		}		
		// #5. Save the file
		MainTM.getInstance().saveConfig();
		// #6. Notification
	    Bukkit.getLogger().info(prefixTM + " " + worldsCheckMsg); // Final console msg
	}

};