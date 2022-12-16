package net.vdcraft.arvdc.timemanager.mainclass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import net.vdcraft.arvdc.timemanager.MainTM;

public class LgFileHandler extends MainTM {
	
	/**
	 * Default sentences in the lang.yml
	 */
	private static String defaultPrefix = "&8&l[&6&lTimeManager&8&l]";
	private static String defaultMsg = "Please ask an admin to properly define the default language in the lang.yml file then reload this plugin.";
	private static String defaultNoMsg = "There is no day-night cycle in the Nether and the End dimensions.";
	private static String defaultDay = "begin at 7.00 am or tick #" + dayStart;
	private static String defaultDusk = "begin at 6.00 pm or tick #" + duskStart;
	private static String defaultNight = "begin at 7.00 pm or tick #" + nightStart;
	private static String defaultDawn = "begin at 6.00 am or tick #" + dawnStart;
	private static String defaultM01 = "begin at #0 elapsedDays";
	private static String defaultM02 = "begin at #31 elapsedDays";
	private static String defaultM03 = "begin at #59 elapsedDays";
	private static String defaultM04 = "begin at #90 elapsedDays";
	private static String defaultM05 = "begin at #120 elapsedDays";
	private static String defaultM06 = "begin at #151 elapsedDays";
	private static String defaultM07 = "begin at #181 elapsedDays";
	private static String defaultM08 = "begin at #212 elapsedDays";
	private static String defaultM09 = "begin at #243 elapsedDays";
	private static String defaultM10 = "begin at #273 elapsedDays";
	private static String defaultM11 = "begin at #304 elapsedDays";
	private static String defaultM12 = "begin at #334 elapsedDays";

	/**
	 * Activate or reload the language file
	 */
	public static void loadLang(String firstOrRe) {

		// #1. When it is the server startup
		if (firstOrRe.equalsIgnoreCase(ARG_FIRST)) {
			// Creation of lang.yml file if doesn't exist
			if (!(MainTM.getInstance().langFileYaml.exists())) {
				MsgHandler.infoMsg(lgFileCreaMsg); // Console log msg
				// Copy the file from src in .jar
				CopyFilesHandler.copy(MainTM.getInstance().getResource(LANGFILENAME), MainTM.getInstance().langFileYaml);
				// Actualize values
				MainTM.getInstance().langConf = YamlConfiguration.loadConfiguration(MainTM.getInstance().langFileYaml);
			} else {
				// Update the file if < 1.6.0-b
				if (ValuesConverter.tmVersionIsOk("lg", 1, 6, 0, 2, 0)) { // TODO Update this when lang file changes.
					updateLangFile();
				} else MsgHandler.infoMsg(lgFileExistMsg); // Console log msg
			}
		}

		// #2. When using the admin command /tm reload
		if (firstOrRe.equalsIgnoreCase(ARG_RE)) {
			if (MainTM.getInstance().langFileYaml.exists()) {
				// Notification
				MsgHandler.infoMsg(lgFileTryReloadMsg);
				// Reload values from lang.yml file
				MainTM.getInstance().langConf = YamlConfiguration.loadConfiguration(MainTM.getInstance().langFileYaml);
			} else
				loadLang(ARG_FIRST);
		}

		// #3. In both case

		// #3.A. Restore default values
		MainTM.getInstance().langConf.set(CF_VERSION, MainTM.versionTM());
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_PREFIX, defaultPrefix);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_MSG, defaultMsg);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_TITLE, defaultMsg);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_SUBTITLE, defaultMsg);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_ACTIONBAR, defaultMsg);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_NETHERMSG, defaultNoMsg);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_ENDMSG, defaultNoMsg);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_DAYPARTS + "." + CF_DAY, defaultDay);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_DAYPARTS + "." + CF_DUSK, defaultDusk);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_DAYPARTS + "." + CF_NIGHT, defaultNight);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_DAYPARTS + "." + CF_DAWN, defaultDawn);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_MONTHS + "." + CF_MONTH_01, defaultM01);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_MONTHS + "." + CF_MONTH_02, defaultM02);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_MONTHS + "." + CF_MONTH_03, defaultM03);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_MONTHS + "." + CF_MONTH_04, defaultM04);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_MONTHS + "." + CF_MONTH_05, defaultM05);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_MONTHS + "." + CF_MONTH_06, defaultM06);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_MONTHS + "." + CF_MONTH_07, defaultM07);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_MONTHS + "." + CF_MONTH_08, defaultM08);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_MONTHS + "." + CF_MONTH_09, defaultM09);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_MONTHS + "." + CF_MONTH_10, defaultM10);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_MONTHS + "." + CF_MONTH_11, defaultM11);
		MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_DEFAULT + "." + CF_MONTHS + "." + CF_MONTH_12, defaultM12);

		// #3.B. Check 'defLang' integrity
		checkDefLang();

		// #3.C. Is multilanguage enable ? Set to false if doesn't exist or if invalid boolean
		if (MainTM.getInstance().langConf.getKeys(false).contains(CF_USEMULTILANG)) {
			// If not existing and 'true', let it 'true'
			if (MainTM.getInstance().langConf.getString(CF_USEMULTILANG).equalsIgnoreCase(ARG_TRUE)) {
				MsgHandler.infoMsg(multiLangIsOnMsg);
			} else { // If not 'true', set default 'false'
				MainTM.getInstance().langConf.set(CF_USEMULTILANG, ARG_FALSE);
				MsgHandler.infoMsg(multiLangIsOffMsg);
			}
		} else { // If not existing, set default 'false'
			MainTM.getInstance().langConf.set(CF_USEMULTILANG, ARG_FALSE);
			MsgHandler.infoMsg(multiLangIsOffMsg);
		}

		// #3.D. Check the defaultDisplay node
		if (MainTM.getInstance().langConf.getKeys(false).contains(CF_DEFAULTDISPLAY)) {
			if (MainTM.getInstance().langConf.getString(CF_DEFAULTDISPLAY).equals("") || MainTM.getInstance().langConf.getString(CF_DEFAULTDISPLAY).equals(" ")) {
				MainTM.getInstance().langConf.set(CF_DEFAULTDISPLAY, ARG_MSG);
			}
		} else MainTM.getInstance().langConf.set(CF_DEFAULTDISPLAY, ARG_MSG);

		// #3.E. Check titles timers values
		Boolean needToCreateTitlesTimers = false;
		// If key does not exist, create it with default values
		if (!MainTM.getInstance().langConf.getKeys(false).contains(CF_TITLES)) {
			needToCreateTitlesTimers = true;
		} else { // If key does exist, loop the sub-keys
			for (String key : MainTM.getInstance().langConf.getConfigurationSection(CF_TITLES).getKeys(false)) {
				String value = MainTM.getInstance().langConf.getString(CF_TITLES + "." + key);
				// If a sub-key is empty, (re-)create it with default values
				if (value.equals("") || value.equals(" ") || value.equals(null)) {
					needToCreateTitlesTimers = true;
				} else { // If a sub-key seems usable, try to get its value as an integer
					try {
						String s = MainTM.getInstance().langConf.getString(CF_TITLES + "." + key);
						Integer i = Integer.parseInt(s);
						MainTM.getInstance().langConf.set(CF_TITLES + "." + key, i);
					} catch (NumberFormatException nfe) { // If the value is not an integer, use the default value
						MsgHandler.errorMsg(titlesTimersFormatMsg); // Console error msg
						needToCreateTitlesTimers = true;
					}
				}
			}
		}
		if (needToCreateTitlesTimers) {
			MainTM.getInstance().langConf.set(CF_TITLES + "." + CF_FADEIN, defTitleFadeIn);
			MainTM.getInstance().langConf.set(CF_TITLES + "." + CF_STAY, defTitleStay);
			MainTM.getInstance().langConf.set(CF_TITLES + "." + CF_FADEOUT, defTitleFadeOut);
		}

		// #3.F. Save the lang.yml file
		SaveLangYml();

		// 3.G. Notifications
		if (firstOrRe.equalsIgnoreCase(ARG_FIRST)) {
			MsgHandler.infoMsg(lgVersionMsg + MainTM.getInstance().langConf.getString("version") + ".");
		}
	}

	/**
	 * Check 'defaultLang' integrity in lang.yml
	 */
	// Check if 'defaultLang' key exists in yaml, if not create it and set it to default
	private static void checkDefLang() {
		if (!MainTM.getInstance().langConf.getKeys(false).contains(CF_DEFAULTLANG)) {
			restoreDefLang();
		} else { // Else, if 'defaultLang' key exists but is void set it to default
			if (MainTM.getInstance().langConf.getString(CF_DEFAULTLANG).equals("")) {
				MainTM.getInstance().langConf.set(CF_DEFAULTLANG, CF_DEFAULT);
			}
			// Then actualize the 'defaultLang' key from lang.yml file
			serverLang = new String(MainTM.getInstance().langConf.getString(CF_DEFAULTLANG));
			MsgHandler.colorMsg(defLangCheckMsg + " §e" + serverLang + "§r."); // Console log msg
			// Check if the 'defaultLang' value correspond to an existing language who contains every needed keys
			MsgHandler.debugMsg(availableTranslationsDebugMsg + " §e" + setAnyListFromLang(CF_LANGUAGES)); // Console debug msg
			MsgHandler.debugMsg("Does it contain the choosen language \"§e" + serverLang + "§b\" ?"); // Console debug msg
		}
		if (!MainTM.getInstance().langConf.getConfigurationSection(CF_LANGUAGES).getKeys(false).contains(serverLang)) {
			MsgHandler.debugMsg("No, §b\"§e" + serverLang + "§b\" wasn't found. The §edefaultLang §bvalue will be reseted."); // Console debug msg
			restoreDefLang();
		} else {
			Set<String> langKeys = MainTM.getInstance().langConf.getConfigurationSection(CF_LANGUAGES + "." + serverLang).getKeys(true);
			MsgHandler.debugMsg("Yes, \"§e" + serverLang + "§b\" was found, now let's check for the subkeys :"); // Console debug msg
			MsgHandler.debugMsg("Does §b\"§e" + langKeys + "§b\" contain every needed keys ?"); // Console debug msg
			if ((langKeys.contains(CF_PREFIX))
					&& (langKeys.contains(CF_MSG))
					&& (langKeys.contains(CF_NETHERMSG))
					&& (langKeys.contains(CF_ENDMSG))
					&& (langKeys.contains(CF_TITLE))
					&& (langKeys.contains(CF_SUBTITLE))
					&& (langKeys.contains(CF_ACTIONBAR))
					&& (langKeys.contains(CF_DAYPARTS))
					&& (langKeys.contains(CF_DAYPARTS + "." + CF_DAY))
					&& (langKeys.contains(CF_DAYPARTS + "." + CF_DUSK))
					&& (langKeys.contains(CF_DAYPARTS + "." + CF_NIGHT))
					&& (langKeys.contains(CF_DAYPARTS + "." + CF_DAWN))
					&& (langKeys.contains(CF_MONTHS))
					&& (langKeys.contains(CF_MONTHS + "." + CF_MONTH_01))
					&& (langKeys.contains(CF_MONTHS + "." + CF_MONTH_02))
					&& (langKeys.contains(CF_MONTHS + "." + CF_MONTH_03))
					&& (langKeys.contains(CF_MONTHS + "." + CF_MONTH_04))
					&& (langKeys.contains(CF_MONTHS + "." + CF_MONTH_05))
					&& (langKeys.contains(CF_MONTHS + "." + CF_MONTH_06))
					&& (langKeys.contains(CF_MONTHS + "." + CF_MONTH_07))
					&& (langKeys.contains(CF_MONTHS + "." + CF_MONTH_08))
					&& (langKeys.contains(CF_MONTHS + "." + CF_MONTH_09))
					&& (langKeys.contains(CF_MONTHS + "." + CF_MONTH_10))
					&& (langKeys.contains(CF_MONTHS + "." + CF_MONTH_11))
					&& (langKeys.contains(CF_MONTHS + "." + CF_MONTH_12))) {
				// If every key exists, keep actual 'defaultLang'
				MsgHandler.debugMsg("Yes, all the subkeys where founded."); // Console debug msg
				MsgHandler.colorMsg("§e" + serverLang + "§r " + defLangOkMsg); // Console log msg
			} else {
				MsgHandler.debugMsg("No, some subkeys are missing."); // Console debug msg
				MsgHandler.infoMsg(defLangNonOkMsg); // Console log msg
				restoreDefLang();
			}
		}
	}

	/**
	 * Restore the 'default' translation in lang.yml
	 */
	private static void restoreDefLang() {
		MsgHandler.colorMsg("§e" + serverLang + "§r " + defLangResetMsg); // Console log msg
		MainTM.getInstance().langConf.set(CF_DEFAULTLANG, CF_DEFAULT);
		serverLang = new String(MainTM.getInstance().langConf.getString(CF_DEFAULTLANG));
	}

	/**
	 * Save the lang.yml
	 */
	public static void SaveLangYml() {
		try {
			MainTM.getInstance().langConf.save(MainTM.getInstance().langFileYaml);
		} catch (IOException e) {
			MsgHandler.errorMsg(MainTM.prefixTM + " " + couldNotSaveLang);
			e.printStackTrace();
		}
	}

	/**
	 * Return an array list from everything listed in a specific key from the lang.yml file
	 */
	public static List<String> setAnyListFromLang(String inWichYamlKey) {
		List<String> listedElementsList = new ArrayList<>();
		for (String listedElement : MainTM.getInstance().langConf.getConfigurationSection(inWichYamlKey).getKeys(false)) {
			listedElementsList.add(listedElement);
		}
		return listedElementsList;
	}

	/**
	 * Create a backup, then create a new file, then copy data, so it adds months keys if missing in lang.yml
	 */
	public static void updateLangFile() {
		// Rename the old file
		String path = new File(".").getAbsolutePath();
		Path source = Paths.get(path + "/plugins/TimeManager/" + LANGFILENAME);
		try {
			// Rename the file in the same directory
			Files.move(source, source.resolveSibling(LANGBCKPFILENAME), StandardCopyOption.REPLACE_EXISTING);

			// #1. Extract the new file from .jar
			CopyFilesHandler.copy(MainTM.getInstance().getResource(LANGFILENAME), MainTM.getInstance().langFileYaml); // Copy the file from src in .jar
			MainTM.getInstance().langConf = YamlConfiguration.loadConfiguration(MainTM.getInstance().langFileYaml); // Actualize values
			// #2. Get data from backup file
			MainTM.getInstance().langBckpConf = YamlConfiguration.loadConfiguration(MainTM.getInstance().langBckpFileYaml); // Actualize values		
			// #3. Copy the data from the old file to the new one
			// #3.A. useMultiLang
			String useMultiLang = MainTM.getInstance().langBckpConf.getString(CF_USEMULTILANG);
			MainTM.getInstance().langConf.set(CF_USEMULTILANG, useMultiLang);
			// #3.B. defaultLang
			String defaultLang = MainTM.getInstance().langBckpConf.getString(CF_DEFAULTLANG);
			MainTM.getInstance().langConf.set(CF_DEFAULTLANG, defaultLang);
			// #3.C. Languages
			for (String lang : MainTM.getInstance().langBckpConf.getConfigurationSection(CF_LANGUAGES).getKeys(false)) {
				if (!lang.equalsIgnoreCase(CF_DEFAULT)) { // Ignore the default language
					// #3.C.a. prefix
					String prefix = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_PREFIX);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_PREFIX, prefix);
					// #3.C.b. msg
					String msg = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MSG);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_MSG, msg);
					// #3.C.c. nethermsg // TODO 1.6.0 Pay attention to change this on the next update ! // TODO 1.7
					//String netherMsg = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_NOMSG);
					//MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_NETHERMSG, netherMsg);
					// #3.C.d. endmsg // TODO 1.6.0 Pay attention to change this on the next update !
					//String endMsg = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_NOMSG);
					//MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_ENDMSG, endMsg);
					// #3.C.e. dayParts
					// #3.C.e.1. dawn
					String dawn = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_DAYPARTS + "." + CF_DAWN);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_DAYPARTS + "." + CF_DAWN, dawn);
					// #3.C.e.2. day
					String day = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_DAYPARTS + "." + CF_DAY);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_DAYPARTS + "." + CF_DAY, day);
					// #3.C.e.3. dusk
					String dusk = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_DAYPARTS + "." + CF_DUSK);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_DAYPARTS + "." + CF_DUSK, dusk);
					// #3.C.e.4. night
					String night = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_DAYPARTS + "." + CF_NIGHT);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_DAYPARTS + "." + CF_NIGHT, night);
					// #3.C.f. months
					// #3.C.f.1. jan
					String jan = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_01);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_01, jan);				
					// #3.C.f.2. feb
					String feb = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_02);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_02, feb);
					// #3.C.f.3. mar
					String mar = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_03);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_03, mar);
					// #3.C.f.4. apr
					String apr = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_04);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_04, apr);
					// #3.C.f.5. may
					String may = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_05);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_05, may);
					// #3.C.f.6. jun
					String jun = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_06);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_06, jun);
					// #3.C.f.7. jul
					String jul = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_07);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_07, jul);
					// #3.C.f.8. aug
					String aug = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_08);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_08, aug);
					// #3.C.f.9. sep
					String sep = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_09);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_09, sep);
					// #3.C.f.10. oct
					String oct = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_10);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_10, oct);
					// #3.C.f.11. nov
					String nov = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_11);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_11, nov);
					// #3.C.f.12. dec
					String dec = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_12);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_MONTHS + "." + CF_MONTH_12, dec);
					// #3.C.g. title, subtitle and action bar messages
					// #3.C.g.1. title
					String title = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_TITLE);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_TITLE, title);
					// #3.C.g.2. subtitle
					String subtitle = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_SUBTITLE);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_SUBTITLE, subtitle);
					// #3.C.g.3. action bar
					String actionbar = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + lang + "." + CF_ACTIONBAR);
					MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + lang + "." + CF_ACTIONBAR, actionbar);
					
				}
				// #3.D. Titles timers values
				// #3.D.a. fadeIn
				String fadein = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + CF_TITLES + "." + CF_FADEIN);
				MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_TITLES + "." + CF_FADEIN, fadein);
				// #3.D.b. stay
				String stay = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + CF_TITLES + "." + CF_STAY);
				MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_TITLES + "." + CF_STAY, stay);
				// #3.D.c. fadeOut
				String fadeout = MainTM.getInstance().langBckpConf.getString(CF_LANGUAGES + "." + CF_TITLES + "." + CF_FADEOUT);
				MainTM.getInstance().langConf.set(CF_LANGUAGES + "." + CF_TITLES + "." + CF_FADEOUT, fadeout);
			}		

			// TODO >>> Delete the backup file ???
			// MainTM.getInstance().langBckpFileYaml.delete();
			
			MsgHandler.infoMsg(langFileUpdateMsg); // Console log msg
		} catch (IOException e) {
			MsgHandler.infoMsg(LangFileNonOkMsg); // Console log msg
		}
	}

};