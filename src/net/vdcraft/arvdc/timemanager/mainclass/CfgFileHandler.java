package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.ArrayList;
import java.util.List;

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
				MsgHandler.infoMsg(cfgFileCreateMsg); // Console missing file msg
			} else {
				MsgHandler.infoMsg(cfgFileExistMsg); // Console existing file msg
			}

			// #1.b. Assure to recreate missing key in config.yml file
			MainTM.getInstance().getConfig().options().copyDefaults(true);

			// #1.c. Actualize or create the config.yml file
			MainTM.getInstance().saveDefaultConfig();
		}

		// # 2. Get the previous initial tick value (before the reload)
		long oldTick = MainTM.getInstance().getConfig().getLong(CF_INITIALTICK + "." + CF_INITIALTICKNB);

		// #3. Only when using the admin command /tm reload:
		if (firstOrRe.equalsIgnoreCase("re")) {
			if (MainTM.getInstance().configFileYaml.exists()) {
				// #A. Notification
				MsgHandler.infoMsg(cfgFileTryReloadMsg);
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

		// #8. Set the default value if missing or corrupt for the wakeUpTick key
		if (MainTM.getInstance().getConfig().getKeys(false).contains(CF_WAKEUPTICK)) {
			ValuesConverter.restrainWakeUpTick();
		} else {
			MainTM.getInstance().getConfig().set(CF_WAKEUPTICK, 0L);
		}

		// #9. Set the default value if missing or corrupt for the newDayAt key
		if (MainTM.getInstance().getConfig().getKeys(false).contains(CF_NEWDAYAT)) { // #9.A. Check if the value already exists
			if (MainTM.getInstance().getConfig().getString(CF_NEWDAYAT).contains("18000") // If the value already exists, check if it is 00:00
					|| MainTM.getInstance().getConfig().getString(CF_NEWDAYAT).equalsIgnoreCase("midnight")
					|| MainTM.getInstance().getConfig().getString(CF_NEWDAYAT).contains("0:0")) {
				MainTM.getInstance().getConfig().set(CF_NEWDAYAT, CF_NEWDAYAT_0H00);
			} else {
				MainTM.getInstance().getConfig().set(CF_NEWDAYAT, CF_NEWDAYAT_6H00); // If not, set the default value
			}
		} else { // #9.B. If not, set the default value
			MainTM.getInstance().getConfig().set(CF_NEWDAYAT, CF_NEWDAYAT_6H00);
		}

		// #10. Set the default value if missing or corrupt for the placeholder keys
		if (!MainTM.getInstance().getConfig().getKeys(false).contains(CF_PLACEHOLDER)
				|| !MainTM.getInstance().getConfig().getString(CF_PLACEHOLDER + "." + CF_PLACEHOLDER_PAPI).equalsIgnoreCase("true")) {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDER + "." + CF_PLACEHOLDER_PAPI, "false");
		} else {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDER + "." + CF_PLACEHOLDER_PAPI, "true");
		}
		if (!MainTM.getInstance().getConfig().getKeys(false).contains(CF_PLACEHOLDER)
				|| !MainTM.getInstance().getConfig().getString(CF_PLACEHOLDER + "." + CF_PLACEHOLDER_MVDWPAPI).equalsIgnoreCase("true")) {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDER + "." + CF_PLACEHOLDER_MVDWPAPI, "false");
		} else {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDER + "." + CF_PLACEHOLDER_MVDWPAPI, "true");
		}

		// #11. Only when using the admin command /tm reload: Update the initialTickNb value
		if (firstOrRe.equalsIgnoreCase("re")) {
			WorldSyncHandler.updateInitialTickAndTime(oldTick);
		}

		// #12. Refresh the initialTickNb every (x) minutes - only if a MySQL database is used and the scheduleSyncDelayedTask is off
		if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equals("true") && !mySqlRefreshIsAlreadyOn) {
			mySqlRefreshIsAlreadyOn = true;
			WorldSyncHandler.refreshInitialTickMySql();
			MsgHandler.infoMsg(sqlInitialTickAutoUpdateMsg); // Notify the console
		}

		// #13. Check and complete list of available worlds
		MsgHandler.debugMsg(cfgOptionsCheckDebugMsg); // Console debug msg
		WorldListHandler.listLoadedWorlds();

		// #14. For each world
		for (String w : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {

			// #14.a. Restrain the start times
			ValuesConverter.restrainStart(w);

			// #14.b. Restrain the speed modifiers
			ValuesConverter.restrainSpeed(w);

			// #14.c. Restrain the sync value
			ValuesConverter.restrainSync(w, 0.1);

			// #14.d. Restrain the sleep value
			ValuesConverter.restrainSleep(w);
		}

		// #15. Restore the version value
		MainTM.getInstance().getConfig().set(CF_VERSION, versionTM());

		// #16. Save the changes
		MainTM.getInstance().saveConfig();

		// #17. Notifications
		if (firstOrRe.equalsIgnoreCase("first")) {
			MsgHandler.infoMsg(cfgVersionMsg + versionTM() + "."); // Notify the console
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