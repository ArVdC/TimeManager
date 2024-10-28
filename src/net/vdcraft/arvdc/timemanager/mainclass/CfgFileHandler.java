package net.vdcraft.arvdc.timemanager.mainclass;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import net.vdcraft.arvdc.timemanager.MainTM;

public class CfgFileHandler extends MainTM {

	/**
	 * Activate or reload the configuration file
	 */
	@SuppressWarnings("deprecation")
	public static void loadConfig(String firstOrRe) {
		
		if (serverMcVersion >= reqMcVForConfigFile) MainTM.getInstance().getConfig().options().parseComments(true); // Check if MC version is at least 1.19.0
		
		// #1. Only at the server startup:
		if (firstOrRe.equalsIgnoreCase(ARG_FIRST)) {

			// #1.A. Create congig.yml file if missing, force actual version
			if (!(MainTM.getInstance().configFileYaml.exists())) {
				MsgHandler.infoMsg(cfgFileCreateMsg); // Console missing file msg
			} else {
				MsgHandler.infoMsg(cfgFileExistMsg); // Console existing file msg
			}

			// #1.B. Assure to recreate missing key in config.yml file
			MainTM.getInstance().getConfig().options().copyDefaults(true);

			// #1.C. Actualize or create the config.yml file
			MainTM.getInstance().saveDefaultConfig();
			
			// #1.D. Load the header from the .txt file
			// #1.D.a. Extract the file from the .jar
			CopyFilesHandler.copyAnyFile(CONFIGHEADERFILENAME, MainTM.getInstance().configHeaderFileTxt);
			// #1.D.b. Try to get the documentation text
			List<String> header = new ArrayList<String>();
			try {
				header.addAll(Files.readAllLines(MainTM.getInstance().configHeaderFileTxt.toPath(), Charset.defaultCharset()));
			} catch (IOException e) {
				header.add(CONFIGHEADERFILENAME + " could not be loaded. Find it inside the .jar file to get the " + CONFIGFILENAME + " documentation.");
			}
			MsgHandler.devMsg("The §eheader§9 of " + CONFIGFILENAME + " file contents : §e" + header); // Console dev msg
			// #1.D.c. Delete the txt file
			MainTM.getInstance().configHeaderFileTxt.delete();
			// #1.D.d. Set the header into the yml file
			if (serverMcVersion < reqMcVForConfigFile) { // Check if MC version is at least 1.19.0
				String concatHeader = "";
				for (String s : header) {
					concatHeader = concatHeader + s + "\n";
				}
				MainTM.getInstance().getConfig().options().header(concatHeader);
			} else MainTM.getInstance().getConfig().options().setHeader(header);
			
		}
		
		// # 2. Get the previous initial tick value (before the reload)
		long oldTick = MainTM.getInstance().getConfig().getLong(CF_INITIALTICK + "." + CF_INITIALTICKNB);

		// #3. Only when using the admin command /tm reload:
		if (firstOrRe.equalsIgnoreCase(ARG_RE)) {
			if (MainTM.getInstance().configFileYaml.exists()) {
				// #3.A. Notification
				MsgHandler.infoMsg(cfgFileTryReloadMsg);
				// #3.B. Reload values from config.yml file
				MainTM.getInstance().reloadConfig();
			} else
				loadConfig(ARG_FIRST);
		}

		// #4. Manage and maybe activate the debug mode
		DebugModeHandler.debugModeOnOff();
		
		// #5. Restore the version value
		MainTM.getInstance().getConfig().set(CF_VERSION, versionTM());

		// #6. Restrain the refresh rate
		if (MainTM.getInstance().getConfig().getKeys(false).contains(CF_REFRESHRATE)) {
			ValuesConverter.restrainRate();
		} else {
			MainTM.getInstance().getConfig().set(CF_REFRESHRATE, defRefresh);
		}
		
		// #7. Set the default value if missing or corrupt for the wakeUpTick key
		if (MainTM.getInstance().getConfig().getKeys(false).contains(CF_WAKEUPTICK)) {
			ValuesConverter.restrainWakeUpTick();
		} else {
			MainTM.getInstance().getConfig().set(CF_WAKEUPTICK, 0L);
		}

		// #8. Set the default value if missing or corrupt for the newDayAt key
		if (MainTM.getInstance().getConfig().getKeys(false).contains(CF_NEWDAYAT)) {
			// #8.A. Check if the value already exists and if it is 00:00
			if (MainTM.getInstance().getConfig().getString(CF_NEWDAYAT).contains("18000")
					|| MainTM.getInstance().getConfig().getString(CF_NEWDAYAT).equalsIgnoreCase("midnight")
					|| MainTM.getInstance().getConfig().getString(CF_NEWDAYAT).contains("0:0")) {
				MainTM.getInstance().getConfig().set(CF_NEWDAYAT, newDayStartsAt_0h00);
			// #8.B. If not, set the default value
			} else {
				MainTM.getInstance().getConfig().set(CF_NEWDAYAT, newDayStartsAt_6h00);
			}
		} else {
			MainTM.getInstance().getConfig().set(CF_NEWDAYAT, newDayStartsAt_6h00);
		}
		
		// #9. Manage the worlds list
		// #9.A. Check and complete list of available worlds
		MsgHandler.debugMsg(cfgOptionsCheckDebugMsg); // Console debug msg
		WorldListHandler.listLoadedWorlds();
		// #9.B. For each world
		for (String world : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
			// #9.A. Restrain the start times
			ValuesConverter.restrainStart(world);
			// #9.B. Restrain the speed modifiers
			ValuesConverter.restrainSpeed(world);
			// #9.C. Restrain the sleep value
			ValuesConverter.restrainSleep(world);
			// #9.D. Restrain the sync value
			ValuesConverter.restrainSync(world, 0.1);
			// #9.E. Restrain the firstStartTime value
			ValuesConverter.restrainFirstStartTime(world);
			// #9.F. Restrain the nightSkipMode value
			ValuesConverter.restrainNightSkipMode(world);
			// #9.G. Restrain the nightSkipRequiredPlayers value
			ValuesConverter.restrainNightSkipRequiredPlayers(world);
			SleepHandler.setSleepingPlayersNeeded(world);
		}		

		// #10. Manage initial tick
		// #10.A Set some default values if missing or corrupt in the initialTick node
		// #10.A.a. resetOnStartup value
		if (MainTM.getInstance().getConfig().getConfigurationSection(CF_INITIALTICK).getKeys(false).contains(CF_RESETONSTARTUP)) {
			if (!(MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_RESETONSTARTUP).equals(ARG_FALSE))) {
				MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_RESETONSTARTUP, ARG_TRUE);
			}
		} else {
			MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_RESETONSTARTUP, ARG_TRUE);
		}

		// #10.A.b. useMySql value
		if (MainTM.getInstance().getConfig().getConfigurationSection(CF_INITIALTICK).getKeys(false).contains(CF_USEMYSQL)) {
			if (!(MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equals(ARG_FALSE))) {
				MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_USEMYSQL, ARG_TRUE);
			}
		} else {
			MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_USEMYSQL, ARG_FALSE);
		}
		// #10.B. Only when using the admin command /tm reload: Update the initialTickNb value
		if (firstOrRe.equalsIgnoreCase(ARG_RE)) {
			SyncHandler.updateInitialTickAndTime(oldTick);
		}
		// #10.C. Refresh the initialTickNb every (x) minutes - only if a MySQL database is used and the scheduleSyncDelayedTask is off
		if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equals(ARG_TRUE) && !mySqlRefreshIsAlreadyOn) {
			mySqlRefreshIsAlreadyOn = true;
			SyncHandler.refreshInitialTickMySql();
			MsgHandler.infoMsg(sqlInitialTickAutoUpdateMsg); // Notify the console
		}

		// #11. Set some default values if missing or corrupt in the mySQL node
		SqlHandler.initSqlDatas();
		
		// #12. Set the default value if missing or corrupt for the updateMsgSrc key
		if (!MainTM.getInstance().getConfig().getKeys(false).contains(CF_UPDATEMSGSRC)
				|| MainTM.getInstance().getConfig().getString(CF_UPDATEMSGSRC).equals("")) {
			MainTM.getInstance().getConfig().set(CF_UPDATEMSGSRC, defUpdateMsgSrc);
		}
		
		// #13. Set the default value if missing or corrupt for the placeholder keys
		if (!MainTM.getInstance().getConfig().getKeys(false).contains(CF_PLACEHOLDERS)
				|| !MainTM.getInstance().getConfig().getString(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_PAPI).equalsIgnoreCase(ARG_TRUE)) {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_PAPI, ARG_FALSE);
		} else {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_PAPI, ARG_TRUE);
		}
		if (!MainTM.getInstance().getConfig().getKeys(false).contains(CF_PLACEHOLDERS)
				|| !MainTM.getInstance().getConfig().getString(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_MVDWPAPI).equalsIgnoreCase(ARG_TRUE)) {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_MVDWPAPI, ARG_FALSE);
		} else {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_MVDWPAPI, ARG_TRUE);
		}
		if (!MainTM.getInstance().getConfig().getKeys(false).contains(CF_PLACEHOLDERS)
				|| !MainTM.getInstance().getConfig().getString(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_CHAT).equalsIgnoreCase(ARG_FALSE)) {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_CHAT, ARG_TRUE);
		} else {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_CHAT, ARG_FALSE);
		}
		if (!MainTM.getInstance().getConfig().getKeys(false).contains(CF_PLACEHOLDERS)
				|| !MainTM.getInstance().getConfig().getString(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_CMDS).equalsIgnoreCase(ARG_FALSE)) {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_CMDS, ARG_TRUE);
		} else {
			MainTM.getInstance().getConfig().set(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_CMDS, ARG_FALSE);
		}

		// #14. Restore debugMode node location
		DebugModeHandler.debugModeNodeRelocate();
		
		// #15. Save the changes
		MainTM.getInstance().saveConfig();

		// #16. Notifications
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