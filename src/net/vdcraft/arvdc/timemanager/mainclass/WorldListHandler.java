package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

import net.vdcraft.arvdc.timemanager.MainTM;

public class WorldListHandler extends MainTM {

	/**
	 * Manage available worlds list in the config.yml file
	 */
	public static void listLoadedWorlds() {
		
		// #1. Avoid missing 'worldsList' key
		if (!(MainTM.getInstance().getConfig().getKeys(false).contains(CF_WORLDSLIST))) {
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + ".Example.start", "0");
		}
		
		// #2. Avoid void 'worldsList' key
		if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST).equals("")) {
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + ".Example.start", "example");
		}
		
		// #3. Get the complete list of loaded worlds and add it to config.yml
		for (World w : Bukkit.getServer().getWorlds()) {
			String world = w.getName();
			// Check if it already figures in existing list and if it is not 'nether' neither 'ender'
			if (!(MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) && !(world.contains(ARG_NETHER)) && !(world.contains(ARG_THEEND))) {
				// If not, add it in the list with default parameters
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_START, defStart);
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED, defSpeed);
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED, defSpeed);
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SLEEP, defSleep);
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, defSync);
			} // If a world already exists, check its 'start', 'daySpeed', 'nightSpeed', 'sleep' and 'sync' keys
			else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
				// Check if 'start' exists
				if (!(MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST + "." + world).getKeys(false).contains(CF_START))) { // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_START, defStart);
				}
				// Check if 'DaySpeed' exists
				if (!(MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST + "." + world).getKeys(false).contains(CF_D_SPEED))) { // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED, defSpeed);
				}
				// Check if 'nightSpeed' exists
				if (!(MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST + "." + world).getKeys(false).contains(CF_N_SPEED))) { // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED, defSpeed);
				}
				// Check if 'sleep' exists
				if (!(MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST + "." + world).getKeys(false).contains(CF_SLEEP))) { // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SLEEP, defSleep);
				}
				// Check if 'sync' exists
				if (!(MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST + "." + world).getKeys(false).contains(CF_SYNC))) { // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, defSync);
				}
			}
		}
		
		// #4. Remove 'Example' + 'nether' + 'ender' + inexistent worlds if they are present in the config list
		List<World> loadedWorlds = Bukkit.getServer().getWorlds();
		MsgHandler.debugMsg(refrehWorldsListDebugMsg); // Console debug msg
		MsgHandler.debugMsg(worldsRawListDebugMsg + " §e" + loadedWorlds); // Console debug msg
		String loadedWorldsNames = "" + loadedWorlds;
		loadedWorldsNames = loadedWorldsNames.replace("},", ",").replace("CraftWorld{name=", "");
		loadedWorldsNames = loadedWorldsNames.substring(0, loadedWorldsNames.length() - 2);
		loadedWorldsNames = loadedWorldsNames.substring(1, loadedWorldsNames.length());
		MsgHandler.debugMsg(worldsFormatListDebugMsg + " [" + loadedWorldsNames + "]"); // Console debug msg
		MsgHandler.debugMsg(worldsCfgListDebugMsg + " " + CfgFileHandler.setAnyListFromConfig(CF_WORLDSLIST)); // Console debug msg
		for (String w : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
			Boolean eraseWorld = false;
			if (w.equals("Example") || w.contains(ARG_NETHER) || w.contains(ARG_THEEND)) {
				eraseWorld = true;
			} else {
				if (!(loadedWorldsNames.contains(w))) {
					eraseWorld = true;
				}
			}
			MainTM.waitTime(500);
			if (eraseWorld == true) {
				MsgHandler.debugMsg("The world §e" + eraseWorld + "§b " + delWorldDebugMsg); // Console debug msg
				MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).set(w, null);
			}
		}
		
		// #5. Save the file
		MainTM.getInstance().saveConfig();
		
		// #6. Notification
		MsgHandler.infoMsg(worldsCheckMsg); // Final console msg
	}

};