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

		// #1. Only at the server startup:
		if (firstOrRe.equalsIgnoreCase("first")) {

			// #1.a. Create congig.yml file if missing, force actual version
			if (!(MainTM.getInstance().configFileYaml.exists())) {
				Bukkit.getLogger().info(prefixTM + " " + cfgFileCreaMsg); // Console missing file msg
			} else {
				Bukkit.getLogger().info(prefixTM + " " + cfgFileExistMsg); // Console existing file msg
			}

			// #1.b. Assure to recreate missing key in config.yml file
			MainTM.getInstance().getConfig().options().copyDefaults(true);

			// #1.c. Actualize or create the config.yml file
			MainTM.getInstance().saveDefaultConfig();
		}

		// # 2. Get the previous initial tick value (before the reload)
		Long oldTick = MainTM.getInstance().getConfig().getLong(CF_INITIALTICK + "." + CF_INITIALTICKNB);

		// #3. Only when using the admin command /tm reload:
		if (firstOrRe.equalsIgnoreCase("re")) {
			if (MainTM.getInstance().configFileYaml.exists()) {
				// #A. Notification
				Bukkit.getLogger().info(prefixTM + " " + cfgFileTryReloadMsg);
				// #B. Reload values from config.yml file
				MainTM.getInstance().reloadConfig();
			} else
				loadConfig("first");
		}

		// #4. Toggle debugMode on/off
		DebugModeHandler.debugModeOnOff();

		// #5. Set some default values if missing or corrupt in the initialTick node

		// #5.a. defTimeUnits value
		if (MainTM.getInstance().getConfig().getKeys(false).contains(CF_DEFTIMEUNITS)) {
			if (MainTM.getInstance().getConfig().getString(CF_DEFTIMEUNITS).equals("")) {
				MainTM.getInstance().getConfig().set(CF_DEFTIMEUNITS, defTimeUnits);
			}
		} else {
			MainTM.getInstance().getConfig().set(CF_DEFTIMEUNITS, defTimeUnits);
		}

		// #5.b. resetOnStartup value
		if (MainTM.getInstance().getConfig().getConfigurationSection(CF_INITIALTICK).getKeys(false).contains(CF_RESETONSTARTUP)) {
			if (!(MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_RESETONSTARTUP).equals("false"))) {
				MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_RESETONSTARTUP, "true");
			}
		} else {
			MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_RESETONSTARTUP, "true");
		}

		// #5.c. useMySql value
		if (MainTM.getInstance().getConfig().getConfigurationSection(CF_INITIALTICK).getKeys(false).contains(CF_USEMYSQL)) {
			if (!(MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equals("false"))) {
				MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_USEMYSQL, "true");
			}
		} else {
			MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_USEMYSQL, "false");
		}

		// #6. Set some default values if missing or corrupt in the mySQL node
		SqlHandler.initSqlDatas();

		// #7. Restrain the refresh rate
		if (MainTM.getInstance().getConfig().getKeys(false).contains(CF_REFRESHRATE)) {
			ValuesConverter.restrainRate();
		} else {
			MainTM.getInstance().getConfig().set(CF_REFRESHRATE, defRefresh);
		}

		// #8. Set the default value if missing or corrupt for the wakeUpTick key //TODO
		if (MainTM.getInstance().getConfig().getKeys(false).contains(CF_WAKEUPTICK)) {
			ValuesConverter.restrainWakeUpTick();
		} else {
			MainTM.getInstance().getConfig().set(CF_WAKEUPTICK, 0L);
		}

		// #9. Only when using the admin command /tm reload: Update the initialTickNb value
		if (firstOrRe.equalsIgnoreCase("re")) {
			WorldSyncHandler.updateInitialTickAndTime(oldTick);
		}

		// #10. Refresh the initialTickNb every (x) minutes - only if a MySQL database is used and the scheduleSyncDelayedTask is off
		if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equals("true") && !mySqlRefreshIsAlreadyOn) {
			mySqlRefreshIsAlreadyOn = true;
			WorldSyncHandler.refreshInitialTickMySql();
			Bukkit.getLogger().info(prefixTM + " " + sqlInitialTickAutoUpdateMsg); // Notify the console
		}

		// #11. Check and complete list of available worlds
		if (debugMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + cfgOptionsCheckDebugMsg); // Console debug msg
		WorldListHandler.listLoadedWorlds();

		// #12. For each world
		for (String w : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {

			// #12.a. Restrain the start times
			ValuesConverter.restrainStart(w);

			// #12.b. Restrain the speed modifiers
			ValuesConverter.restrainSpeed(w);

			// #12.c. Restrain the sync value
			ValuesConverter.restrainSync(w, 0.1);

			// #12.d. Restrain the sleep value
			ValuesConverter.restrainSleep(w);
		}

		// #13. Restore the version value
		MainTM.getInstance().getConfig().set(CF_VERSION, versionTM());

		// #14. Save the changes
		MainTM.getInstance().saveConfig();

		// #15. Notifications
		if (firstOrRe.equalsIgnoreCase("first")) {
			Bukkit.getLogger().info(prefixTM + " " + cfgVersionMsg + versionTM() + "."); // Notify the console
		}
	}

	/**
	 * Return an array list from anything listed in a specific key from the config.yml
	 */
	public static List<String> setAnyListFromConfig(String inWichYamlKey) {
		List<String> listedElementsList = new ArrayList<>();
		for (String listedElement : MainTM.getInstance().getConfig().getConfigurationSection(inWichYamlKey).getKeys(false)) {
			listedElementsList.add(listedElement);
		}
		return listedElementsList;
	}

};