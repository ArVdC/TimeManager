package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import net.vdcraft.arvdc.timemanager.MainTM;

public class WorldListHandler implements Listener {

	/**
	 * Manage available worlds list in the config.yml file
	 */
	public static void listLoadedWorlds() {
		
		String example = "Example";
		
		// #1. Avoid missing or void 'worldsList' key
		if (!(MainTM.getInstance().getConfig().getKeys(false).contains(MainTM.CF_WORLDSLIST))) {
			MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + example + "." + MainTM.CF_START, MainTM.defStart);
		}
		
		// #2. Avoid void 'worldsList' key
		if (MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST).equals("")) {
			MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + example + "." + MainTM.CF_START, MainTM.defStart);
		}
		
		// #3. Get the complete list of loaded worlds and add it to config.yml
		for (World w : Bukkit.getServer().getWorlds()) {
			String world = w.getName();
			// Check if it already figures in existing list
			if (!(MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST).getKeys(false).contains(world))) {
				// If not, add it in the list with default parameters
				MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_START, MainTM.defStart);
				MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_D_SPEED, MainTM.defSpeed);
				MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_N_SPEED, MainTM.defSpeed);
				MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP, MainTM.defSleep);
				MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SYNC, MainTM.defSync);
				MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_FIRSTSTARTTIME, MainTM.defFirstStartTime);
				MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_NIGHTSKIP_MODE, MainTM.defNightSkipMode);
				MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_NIGHTSKIP_REQUIREDPLAYERS, MainTM.defNightSkipNbPlayers);
			} // If a world already exists, check its 'start', 'daySpeed', 'nightSpeed', 'sleep' and 'sync' keys
			else if (MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST).getKeys(false).contains(world)) {
				// Check if 'start' exists
				if (!(MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST + "." + world).getKeys(false).contains(MainTM.CF_START))) { // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_START, MainTM.defStart);
				}
				// Check if 'DaySpeed' exists
				if (!(MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST + "." + world).getKeys(false).contains(MainTM.CF_D_SPEED))) { // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_D_SPEED, MainTM.defSpeed);
				}
				// Check if 'nightSpeed' exists
				if (!(MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST + "." + world).getKeys(false).contains(MainTM.CF_N_SPEED))) { // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_N_SPEED, MainTM.defSpeed);
				}
				// Check if 'sleep' exists
				if (!(MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST + "." + world).getKeys(false).contains(MainTM.CF_SLEEP))) { // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP, MainTM.defSleep);
				}
				// Check if 'sync' exists
				if (!(MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST + "." + world).getKeys(false).contains(MainTM.CF_SYNC))) { // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SYNC, MainTM.defSync);
				}
				// Check if 'firstStartTime' exists
				if (!(MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST + "." + world).getKeys(false).contains(MainTM.CF_FIRSTSTARTTIME))) { // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_FIRSTSTARTTIME, MainTM.defFirstStartTime);
				}
				// Check if 'nightSkipSpeed' exists
				if (!(MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST + "." + world).getKeys(false).contains(MainTM.CF_NIGHTSKIP_MODE))) { // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_NIGHTSKIP_MODE, MainTM.defNightSkipMode);
				}
				// Check if 'nightSkipRequiredPlayers' exists
				if (!(MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST + "." + world).getKeys(false).contains(MainTM.CF_NIGHTSKIP_REQUIREDPLAYERS))) { // If not, add it in the list with default parameters
					MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_NIGHTSKIP_REQUIREDPLAYERS, MainTM.defNightSkipNbPlayers);
				}
				
			}
		}
		
		// #4. Remove 'Example' and inexistent worlds if they are present in the config list
		List<World> loadedWorlds = Bukkit.getServer().getWorlds();
		MsgHandler.debugMsg(MainTM.refrehWorldsListDebugMsg); // Console debug msg
		MsgHandler.debugMsg(MainTM.worldsRawListDebugMsg + " §e" + loadedWorlds); // Console debug msg
		String loadedWorldsNames = "" + loadedWorlds;
		loadedWorldsNames = loadedWorldsNames.replace("},", ",").replace("CraftWorld{name=", "");
		loadedWorldsNames = loadedWorldsNames.substring(0, loadedWorldsNames.length() - 2);
		loadedWorldsNames = loadedWorldsNames.substring(1, loadedWorldsNames.length());
		MsgHandler.debugMsg(MainTM.worldsFormatListDebugMsg + " [" + loadedWorldsNames + "]"); // Console debug msg
		MsgHandler.debugMsg(MainTM.worldsCfgListDebugMsg + " " + CfgFileHandler.setAnyListFromConfig(MainTM.CF_WORLDSLIST)); // Console debug msg
		for (String w : MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST).getKeys(false)) {
			Boolean eraseWorld = false;
			if (w.equalsIgnoreCase(example) || !loadedWorldsNames.contains(w)) {
				eraseWorld = true;
			}
			MainTM.waitTime(200);
			if (eraseWorld == true) {
				MsgHandler.debugMsg("The world §e" + eraseWorld + "§b " + MainTM.delWorldDebugMsg); // Console debug msg
				MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST).set(w, null);
			}
		}
		
		MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST).set(example, null);
		
		// #6. Notification
		MsgHandler.infoMsg(MainTM.worldsCheckMsg); // Final console msg
	}

	/**
	 * If a new world is created after server was loaded, add it to the worlds list
	 */
	@EventHandler
	private void addAnyLoadedWorldToConfig(WorldLoadEvent e) {
		String world = e.getWorld().getName();
		if (!(MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST).getKeys(false).contains(world))) {
			MsgHandler.debugMsg("World §e" + world + MainTM.addNewWorldDebugMsg); // Console debug msg
			WorldListHandler.listLoadedWorlds();
			MainTM.getInstance().saveConfig();
		}
	}

	/**
	 * If a world is deleted after server was loaded, remove it from the worlds list
	 */
	@EventHandler
	private void removeAnyUnloadedWorldToConfig(WorldUnloadEvent e) {
		String world = e.getWorld().getName();
		if (MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST).getKeys(false).contains(world)) {
			MsgHandler.debugMsg("World §e" + world + MainTM.deleteUnknowWorldDebugMsg); // Console debug msg
			MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST).set(world, null);
			MainTM.getInstance().saveConfig();
		}
	}
	
	/**
	 * Detects if a world exists in the config (returns a boolean)
	 */
	public static boolean worldExistsInConfig(String world) {		
		return MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST).getKeys(false).contains(world);
	}	
	
	/**
	 * Detects if a world exists on the server (returns a boolean)
	 */
	public static boolean worldExistsOnServer(String world) {
		for (World w : Bukkit.getServer().getWorlds()) {
			String wName = w.getName();
			if (wName.equalsIgnoreCase(world)) {
				return true;
			}
		}
		return false;
	}
	
};