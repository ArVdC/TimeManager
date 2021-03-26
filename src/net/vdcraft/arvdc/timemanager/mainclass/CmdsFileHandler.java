package net.vdcraft.arvdc.timemanager.mainclass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import net.vdcraft.arvdc.timemanager.MainTM;

public class CmdsFileHandler extends MainTM {

	/**
	 * Activate or reload the commands file
	 */
	public static void loadCmds(String firstOrRe) {

		// #1. When it is the server startup
		if (firstOrRe.equalsIgnoreCase("first")) {
			// Creation of cmds.yml file if doesn't exist
			if (!(MainTM.getInstance().cmdsFileYaml.exists())) {
				MsgHandler.infoMsg(cmdsFileCreaMsg); // Console log msg
				// Copy the file from src in .jar
				CopyFilesHandler.copy(MainTM.getInstance().getResource(CMDSFILENAME), MainTM.getInstance().cmdsFileYaml);
				// Actualize values
				MainTM.getInstance().cmdsConf = YamlConfiguration.loadConfiguration(MainTM.getInstance().cmdsFileYaml);
			} else {		
				MsgHandler.infoMsg(lgFileExistMsg); // Console log msg
			}
		}

		// #2. When using the admin command /tm reload
		if (firstOrRe.equalsIgnoreCase("re")) {
			if (MainTM.getInstance().cmdsFileYaml.exists()) {
				// Notification
				MsgHandler.infoMsg(cmdsFileTryReloadMsg);
				// Reload values from cmds.yml file
				MainTM.getInstance().cmdsConf = YamlConfiguration.loadConfiguration(MainTM.getInstance().cmdsFileYaml);
			} else
				loadCmds("first");
		}

		// #3. In both case

		// #3.A. Is useCmds enable ? Set to false if doesn't exist or if invalid boolean
		if (MainTM.getInstance().cmdsConf.getKeys(false).contains(CF_USECOMMANDS)) {
			if (MainTM.getInstance().cmdsConf.getString(CF_USECOMMANDS).equalsIgnoreCase("true")) {
				MsgHandler.infoMsg(cmdsIsOnMsg);
			} else {
				MainTM.getInstance().cmdsConf.set(CF_USECOMMANDS, "false");
				MsgHandler.infoMsg(cmdsIsOffMsg);
			}
		} else {
			MainTM.getInstance().cmdsConf.set(CF_USEMULTILANG, "false");
			MsgHandler.infoMsg(cmdsIsOffMsg);
		}

		// #3.B. If the date is 'today', get the date and convert it into the cmds.yml file
		for (String key : MainTM.getInstance().cmdsConf.getConfigurationSection(CF_COMMANDSLIST).getKeys(false)) {
			String eDate = MainTM.getInstance().cmdsConf.getString(CF_COMMANDSLIST + "." + key + "." + CF_DATE);
			String refTimeSrc = MainTM.getInstance().cmdsConf.getString(CF_COMMANDSLIST + "." + key + "." + CF_REFTIME);
			Integer year = 1;
			Integer month = 1;
			Integer day = 1;
			if (eDate.equalsIgnoreCase("today")) {
				// If the reference time is a MC world, get the world time
				if (!refTimeSrc.contains("UTC") && !refTimeSrc.equalsIgnoreCase("")) {
					Long ft = Bukkit.getWorld(refTimeSrc).getFullTime();
					Long ed = ValuesConverter.elapsedDaysFromTick(ft);
					year = Integer.parseInt(ValuesConverter.dateFromElapsedDays(ed, "yyyy"));
					month = Integer.parseInt(ValuesConverter.dateFromElapsedDays(ed, "mm"));
					day = Integer.parseInt(ValuesConverter.dateFromElapsedDays(ed, "dd"));
				} else { // Else, the reference time is UTC, get local time
					year = Calendar.getInstance().get(Calendar.YEAR);
					month = Calendar.getInstance().get(Calendar.MONTH) + 1;
					day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				}
				MainTM.getInstance().cmdsConf.set(CF_COMMANDSLIST + "." + key + "." + CF_DATE, String.format("%04d", year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", day));
			}
		}
		
		// #3.C. Adapt the repeatFreq key
		for (String key : MainTM.getInstance().cmdsConf.getConfigurationSection(CF_COMMANDSLIST).getKeys(false)) {
			String repeatFreq = MainTM.getInstance().cmdsConf.getString(CF_COMMANDSLIST + "." + key + "." + CF_REPEATFREQ);
			if (repeatFreq.contains("no") || repeatFreq.equalsIgnoreCase("false") || repeatFreq.equalsIgnoreCase(" ") || repeatFreq.equalsIgnoreCase("")) {
				MainTM.getInstance().cmdsConf.set(CF_COMMANDSLIST + "." + key + "." + CF_REPEATFREQ, "none");
			}
		}
		
		// #3.D. Restore the version value
		MainTM.getInstance().cmdsConf.set(CF_VERSION, versionTM());

		// #3.E. Save the cmds.yml file
		SaveCmdsYml();
		
		// #3.F. Launch the scheduler if necessary
		if (!commandsSchedulerIsActive.contains("active") && MainTM.getInstance().cmdsConf.getString(CF_USECOMMANDS).equalsIgnoreCase("true")) {
			CmdsScheduler.commandsScheduler();
		}
		
		// 3.G. Notifications
		if (firstOrRe.equalsIgnoreCase("first")) {
			MsgHandler.infoMsg(cmdsVersionMsg + MainTM.getInstance().cmdsConf.getString("version") + ".");
		}

	}

	/**
	 * Save the cmds.yml
	 */
	public static void SaveCmdsYml() {
		try {
			MainTM.getInstance().cmdsConf.save(MainTM.getInstance().cmdsFileYaml);
		} catch (IOException e) {
			MsgHandler.errorMsg(prefixTM + " " + couldNotSaveCmds);
			e.printStackTrace();
		}
	}

	/**
	 * Return an array list from everything listed in a specific key from the cmds.yml file
	 */
	public static List<String> setAnyListFromCmds(String inWichYamlKey) {
		List<String> listedElementsList = new ArrayList<>();
		for (String listedElement : MainTM.getInstance().cmdsConf.getConfigurationSection(inWichYamlKey).getKeys(false)) {
			listedElementsList.add(listedElement);
		}
		return listedElementsList;
	}

};