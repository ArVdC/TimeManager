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
		if (firstOrRe.equalsIgnoreCase(ARG_FIRST)) {

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
		if (firstOrRe.equalsIgnoreCase(ARG_RE)) {
			if (MainTM.getInstance().configFileYaml.exists()) {
				// #A. Notification
				MsgHandler.infoMsg(cfgFileTryReloadMsg);
				// #B. Reload values from config.yml file
				MainTM.getInstance().reloadConfig();
			} else
				loadConfig(ARG_FIRST);
		}

		// #4. Toggle debugMode on/off
		DebugModeHandler.debugModeOnOff();

		// #5. Set some default values if missing or corrupt in the initialTick node

		// #5.a. resetOnStartup value
		if (MainTM.getInstance().getConfig().getConfigurationSection(CF_INITIALTICK).getKeys(false).contains(CF_RESETONSTARTUP)) {
			if (!(MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_RESETONSTARTUP).equals(ARG_FALSE))) {
				MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_RESETONSTARTUP, ARG_TRUE);
			}
		} else {
			MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_RESETONSTARTUP, ARG_TRUE);
		}

		// #5.b. useMySql value
		if (MainTM.getInstance().getConfig().getConfigurationSection(CF_INITIALTICK).getKeys(false).contains(CF_USEMYSQL)) {
			if (!(MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equals(ARG_FALSE))) {
				MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_USEMYSQL, ARG_TRUE);
			}
		} else {
			MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_USEMYSQL, ARG_FALSE);
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
				MainTM.getInstance().getConfig().set(CF_NEWDAYAT, newDayStartsAt_0h00);
			} else {
				MainTM.getInstance().getConfig().set(CF_NEWDAYAT, newDayStartsAt_6h00); // If not, set the default value
			}
		} else { // #9.B. If not, set the default value
			MainTM.getInstance().getConfig().set(CF_NEWDAYAT, newDayStartsAt_6h00);
		}

		// #10. Set the default value if missing or corrupt for the updateMsgSrc key
		if (!MainTM.getInstance().getConfig().getKeys(false).contains(CF_UPDATEMSGSRC)
				|| MainTM.getInstance().getConfig().getString(CF_UPDATEMSGSRC).equals("")) {
			MainTM.getInstance().getConfig().set(CF_UPDATEMSGSRC, defUpdateMsgSrc);
		}
		// #11. Set the default value if missing or corrupt for the placeholder keys
		if (!MainTM.getInstance().getConfig().getKeys(false).contains(CF_PLACEHOLDER)
				|| !MainTM.getInstance().getConfig().getString(CF_PLACEHOLDER + "." + CF_PLACEHOLDER_PAPI).equalsIgnoreCase(ARG_TRUE)) {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDER + "." + CF_PLACEHOLDER_PAPI, ARG_FALSE);
		} else {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDER + "." + CF_PLACEHOLDER_PAPI, ARG_TRUE);
		}
		if (!MainTM.getInstance().getConfig().getKeys(false).contains(CF_PLACEHOLDER)
				|| !MainTM.getInstance().getConfig().getString(CF_PLACEHOLDER + "." + CF_PLACEHOLDER_MVDWPAPI).equalsIgnoreCase(ARG_TRUE)) {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDER + "." + CF_PLACEHOLDER_MVDWPAPI, ARG_FALSE);
		} else {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDER + "." + CF_PLACEHOLDER_MVDWPAPI, ARG_TRUE);
		}

		// #12. Only when using the admin command /tm reload: Update the initialTickNb value
		if (firstOrRe.equalsIgnoreCase(ARG_RE)) {
			SyncHandler.updateInitialTickAndTime(oldTick);
		}

		// #13. Refresh the initialTickNb every (x) minutes - only if a MySQL database is used and the scheduleSyncDelayedTask is off
		if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equals(ARG_TRUE) && !mySqlRefreshIsAlreadyOn) {
			mySqlRefreshIsAlreadyOn = true;
			SyncHandler.refreshInitialTickMySql();
			MsgHandler.infoMsg(sqlInitialTickAutoUpdateMsg); // Notify the console
		}

		// #14. Check and complete list of available worlds
		MsgHandler.debugMsg(cfgOptionsCheckDebugMsg); // Console debug msg
		WorldListHandler.listLoadedWorlds();

		// #15. For each world
		for (String w : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {

			// #15.A. Restrain the start times
			ValuesConverter.restrainStart(w);

			// #15.B. Restrain the speed modifiers
			ValuesConverter.restrainSpeed(w);

			// #15.C. Restrain the sync value
			ValuesConverter.restrainSync(w, 0.1);

			// #15.D. Restrain the sleep value
			ValuesConverter.restrainSleep(w);
		}

		// #16. Restore the version value
		MainTM.getInstance().getConfig().set(CF_VERSION, versionTM());

		// #17. Save the changes
		MainTM.getInstance().saveConfig();

		// #18. Notifications
		if (firstOrRe.equalsIgnoreCase(ARG_FIRST)) {
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