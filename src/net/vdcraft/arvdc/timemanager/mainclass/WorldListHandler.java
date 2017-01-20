package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.World;

import net.vdcraft.arvdc.timemanager.MainTM;

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
			}
		}
		// #4. Remove 'Example' + 'nether' + 'ender' worlds if they are present in the config list		
		for(String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {	
			if(listedWorld.equals("Example") || listedWorld.contains("_the_end") || listedWorld.contains("_nether")) {			
				MainTM.getInstance().getConfig().getConfigurationSection("worldsList").set(listedWorld, null);
			}
		}
		// #5. Save the file
		MainTM.getInstance().saveConfig();
		// #6. Notification
	    Bukkit.getLogger().info(prefixTM + " " + worldsCheckMsg); // Final console msg
	};

}