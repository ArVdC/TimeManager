package net.vdcraft.arvdc.timemanager.mainclass;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.DecimalFormat;
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
		if (firstOrRe.equalsIgnoreCase(ARG_FIRST)) {
			// #1.A Creation of cmds.yml file if doesn't exist
			if (!(MainTM.getInstance().cmdsFileYaml.exists())) {
				MsgHandler.infoMsg(cmdsFileCreaMsg); // Console log msg
				// #1.A.a. Copy the file from src in .jar
				CopyFilesHandler.copy(MainTM.getInstance().getResource(CMDSFILENAME), MainTM.getInstance().cmdsFileYaml);
				// #1.A.b. Actualize values
				MainTM.getInstance().cmdsConf = YamlConfiguration.loadConfiguration(MainTM.getInstance().cmdsFileYaml);
			} else {		
				MsgHandler.infoMsg(lgFileExistMsg); // Console log msg
			}
			// #1.B. Load the header from the .txt file
			// #1.B.a. Extract the file from the .jar
			CopyFilesHandler.copyAnyFile(CMDSHEADERFILENAME, MainTM.getInstance().cmdsHeaderFileTxt);
			// #1.B.b. Try to get the documentation text
			List<String> header = new ArrayList<String>();
			try {
				header.addAll(Files.readAllLines(MainTM.getInstance().cmdsHeaderFileTxt.toPath(), Charset.defaultCharset()));
			} catch (IOException e) {
				header.add(CMDSHEADERFILENAME + " could not be loaded. Find it inside the .jar file to get the " + CMDSFILENAME + " documentation.");
			}
			MsgHandler.devMsg("The §eheader§9 of " + CMDSFILENAME + " file contents : §e" + header); // Console dev msg
			// #1.B.c. Delete the txt file
			MainTM.getInstance().cmdsHeaderFileTxt.delete();
			// #1.B.d. Set the header into the yml file
			MainTM.getInstance().cmdsConf.options().setHeader(header);
		}

		// #2. When using the admin command /tm reload
		if (firstOrRe.equalsIgnoreCase(ARG_RE)) {
			if (MainTM.getInstance().cmdsFileYaml.exists()) {
				// Notification
				MsgHandler.infoMsg(cmdsFileTryReloadMsg);
				// Reload values from cmds.yml file
				MainTM.getInstance().cmdsConf = YamlConfiguration.loadConfiguration(MainTM.getInstance().cmdsFileYaml);
			} else
				loadCmds(ARG_FIRST);
		}

		// #3. In both case
		
		// #3.A. Restore the version value
		MainTM.getInstance().cmdsConf.set(CF_VERSION, versionTM());
		
		// #3.B. Is useCmds enable ? Set to false if doesn't exist or if invalid boolean
		if (MainTM.getInstance().cmdsConf.getKeys(false).contains(CMDS_USECOMMANDS)) {
			if (MainTM.getInstance().cmdsConf.getString(CMDS_USECOMMANDS).equalsIgnoreCase(ARG_TRUE)) {
				MsgHandler.infoMsg(cmdsIsOnMsg);
			} else {
				MainTM.getInstance().cmdsConf.set(CMDS_USECOMMANDS, ARG_FALSE);
				MsgHandler.infoMsg(cmdsIsOffMsg);
			}
		} else {
			MainTM.getInstance().cmdsConf.set(LG_USEMULTILANG, ARG_FALSE);
			MsgHandler.infoMsg(cmdsIsOffMsg);
		}
		
		// #3.C. Detect wrong worlds names
		List<String> worlds = CfgFileHandler.setAnyListFromConfig(CF_WORLDSLIST);
		for (String key : MainTM.getInstance().cmdsConf.getConfigurationSection(CMDS_COMMANDSLIST).getKeys(false)) {
			String phWorld = MainTM.getInstance().cmdsConf.getString(CMDS_COMMANDSLIST + "." + key + "." + CMDS_PHREFWOLRD);
			String refTimeSrc = MainTM.getInstance().cmdsConf.getString(CMDS_COMMANDSLIST + "." + key + "." + CMDS_REFTIME);
			String defWorld = Bukkit.getServer().getWorlds().get(0).getName();
			if (!worlds.contains(phWorld)) {
				MainTM.getInstance().cmdsConf.set(CMDS_COMMANDSLIST + "." + key + "." + CMDS_PHREFWOLRD, defWorld);
				MsgHandler.debugMsg("cmd.yml: World §e" + phWorld + "§b " + cmdsWrongPHWorldDebugMsg + " §e" + defWorld + "§b.");
			}
			if (!worlds.contains(refTimeSrc)) {
				if (!refTimeSrc.contains("UTC") || refTimeSrc.equalsIgnoreCase("")) {
					MainTM.getInstance().cmdsConf.set(CMDS_COMMANDSLIST + "." + key + "." + CMDS_REFTIME, defWorld);
					MsgHandler.debugMsg("cmd.yml: §e" + refTimeSrc + "§b " + cmdsWrongTimeSrcDebugMsg + " §e" + defWorld + "§b.");
				}
			}
		}
		// #3.D. If the date is 'today', get the date and convert it into the cmds.yml file
		for (String key : MainTM.getInstance().cmdsConf.getConfigurationSection(CMDS_COMMANDSLIST).getKeys(false)) {
			String eDate = MainTM.getInstance().cmdsConf.getString(CMDS_COMMANDSLIST + "." + key + "." + CMDS_DATE);
			String refTimeSrc = MainTM.getInstance().cmdsConf.getString(CMDS_COMMANDSLIST + "." + key + "." + CMDS_REFTIME);
			Integer year = 1;
			Integer month = 1;
			Integer day = 1;
			if (eDate.equalsIgnoreCase(ARG_TODAY)) {
				// If the reference time is a MC world, get the world time
				if (!refTimeSrc.contains("UTC") && !refTimeSrc.equalsIgnoreCase("")) {
					Long ft = Bukkit.getWorld(refTimeSrc).getFullTime();
					Long ed = ValuesConverter.daysFromTick(ft);
					year = Integer.parseInt(ValuesConverter.dateFromElapsedDays(ed, PH_YYYY));
					month = Integer.parseInt(ValuesConverter.dateFromElapsedDays(ed, PH_MM));
					day = Integer.parseInt(ValuesConverter.dateFromElapsedDays(ed, PH_DD));
				} else { // Else, the reference time is UTC, get local time
					year = Calendar.getInstance().get(Calendar.YEAR);
					month = Calendar.getInstance().get(Calendar.MONTH) + 1;
					day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				}
				MainTM.getInstance().cmdsConf.set(CMDS_COMMANDSLIST + "." + key + "." + CMDS_DATE, String.format("%04d", year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", day));
			}
		}
		// #3.E. Ensure to save the time value as a string (Some wrong values ​​cannot be avoided because all yaml nodes are read before this code)
		for (String key : MainTM.getInstance().cmdsConf.getConfigurationSection(CMDS_COMMANDSLIST).getKeys(false)) {
			String time = MainTM.getInstance().cmdsConf.getString(CMDS_COMMANDSLIST + "." + key + "." + CMDS_TIME);
			if (!time.contains(":")) {
				time = time + ":00";
			}
			String[] timeParts = time.split(":");
			Integer hours = 0;
			Integer mins = 0;
			String tPart1 = timeParts[0];
			String tPart2 = timeParts[1];
			try { // The time is supposed to be in correct format (hh:mm)
				hours = Integer.parseInt(timeParts[0]);
				mins = Integer.parseInt(timeParts[1]);
				hours = hours % 24;
				mins = mins % 60;
				DecimalFormat formater = new DecimalFormat("00"); // Converts the 2 integers to formatted strings (00)
				tPart1 = "" + hours;
				tPart2 = formater.format(mins);
			} catch (NumberFormatException nfe) {
				MsgHandler.errorMsg(hourFormatMsg); // Console error msg
			}
			time = tPart1 + ":" + tPart2;
			MainTM.getInstance().cmdsConf.set(CMDS_COMMANDSLIST + "." + key + "." + CMDS_TIME, time);
		}

		// #3.F. Adapt the repeatFreq key
		for (String key : MainTM.getInstance().cmdsConf.getConfigurationSection(CMDS_COMMANDSLIST).getKeys(false)) {
			String repeatFreq = MainTM.getInstance().cmdsConf.getString(CMDS_COMMANDSLIST + "." + key + "." + CMDS_REPEATFREQ);
			if (!repeatFreq.equalsIgnoreCase(ARG_HOUR) && !repeatFreq.equalsIgnoreCase(ARG_DAY) && !repeatFreq.equalsIgnoreCase(ARG_WEEK)  && !repeatFreq.equalsIgnoreCase(ARG_MONTH) && !repeatFreq.equalsIgnoreCase(ARG_YEAR)) {
				MainTM.getInstance().cmdsConf.set(CMDS_COMMANDSLIST + "." + key + "." + CMDS_REPEATFREQ, ARG_NONE);
			}
		}

		// #3.G. Save the cmds.yml file
		SaveCmdsYml();
		
		// #3.H. Launch the scheduler if necessary
		if (!commandsSchedulerIsActive.contains(ARG_ACTIVE) && MainTM.getInstance().cmdsConf.getString(CMDS_USECOMMANDS).equalsIgnoreCase(ARG_TRUE)) {
			CmdsScheduler.commandsScheduler();
		}
		
		// 3.I. Notifications
		if (firstOrRe.equalsIgnoreCase(ARG_FIRST)) {
			MsgHandler.infoMsg(cmdsVersionMsg + MainTM.getInstance().cmdsConf.getString(CF_VERSION) + ".");
		}
	}

	/**
	 * Save the cmds.yml
	 */
	public static void SaveCmdsYml() {
		try {
			MainTM.getInstance().cmdsConf.save(MainTM.getInstance().cmdsFileYaml);
		} catch (IOException e) {
			MsgHandler.errorMsg(MainTM.prefixTM + " " + couldNotSaveCmds);
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