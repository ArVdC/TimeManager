package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmServTime;

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
		for(World loadedWorld : Bukkit.getServer().getWorlds()) {
			// Check if it already figures in existing list and if it is not 'nether' neither 'ender'
			if(!(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(loadedWorld.getName())) && !(loadedWorld.getName().contains("_nether")) && !(loadedWorld.getName().contains("_the_end"))) { 
			// If not, create it in list with default parameters
				MainTM.getInstance().getConfig().set("worldsList."+loadedWorld.getName()+".start", defStart);
				MainTM.getInstance().getConfig().set("worldsList."+loadedWorld.getName()+".speed", defSpeed);
				MainTM.getInstance().getConfig().set("worldsList."+loadedWorld.getName()+".sleepUntilDawn", defSleepUntilDawn);
			} // If a world already exists, check its 'speed' and 'start' keys
			else if(MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(loadedWorld.getName())) {
				// Check if 'start' exist
				if(!(MainTM.getInstance().getConfig().getConfigurationSection("worldsList."+loadedWorld.getName()).getKeys(false).contains("start")))
				{ // If not, create it in list with default parameters
					MainTM.getInstance().getConfig().set("worldsList."+loadedWorld.getName()+".start", defStart);
				}
				// Check if 'speed' exist
				if(!(MainTM.getInstance().getConfig().getConfigurationSection("worldsList."+loadedWorld.getName()).getKeys(false).contains("speed")))
				{ // If not, create it in list with default parameters
					MainTM.getInstance().getConfig().set("worldsList."+loadedWorld.getName()+".speed", defSpeed);
				}
				// Check if 'sleepUntilNextDay' exist
				if(!(MainTM.getInstance().getConfig().getConfigurationSection("worldsList."+loadedWorld.getName()).getKeys(false).contains("sleepUntilDawn")))
				{ // If not, create it in list with default parameters
					MainTM.getInstance().getConfig().set("worldsList."+loadedWorld.getName()+".sleepUntilDawn", defSleepUntilDawn);
				}
			}
		}
		// #4. Remove 'Example' + 'nether' + 'ender' + inexistent worlds if they are present in the config list
		List<World> loadedWorlds = Bukkit.getServer().getWorlds();
		String loadedWorldsNames = "" + loadedWorlds;
		loadedWorldsNames = loadedWorldsNames.replace("},", ",").replace("CraftWorld{name=", "");
		loadedWorldsNames = loadedWorldsNames.substring(0,loadedWorldsNames.length()-2);
		loadedWorldsNames = loadedWorldsNames.substring(1,loadedWorldsNames.length());
		for(String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
			Boolean eraseWorld = false;
			if(listedWorld.equals("Example") || listedWorld.contains("_the_end") || listedWorld.contains("_nether")) {		
				eraseWorld = true;
			} else {
					if(!(loadedWorldsNames.contains(listedWorld))) {
						eraseWorld = true;
					}
			}
			TmServTime.waitTime(1000);
			if(eraseWorld == true) {
				MainTM.getInstance().getConfig().getConfigurationSection("worldsList").set(listedWorld, null);
			}
		}		
		// #5. Save the file
		MainTM.getInstance().saveConfig();
		// #6. Notification
	    Bukkit.getLogger().info(prefixTM + " " + worldsCheckMsg); // Final console msg
	};

}