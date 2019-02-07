package net.vdcraft.arvdc.timemanager.mainclass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import net.vdcraft.arvdc.timemanager.MainTM;

public class LgFileHandler extends MainTM {

    /**
     * Default sentences in the lang.yml
     */
    private static String defaultPrefix = "&8&l[&6&lTime Manager&8&l]";
    private static String defaultMsg = "Please ask an admin to properly define the default language in the lang.yml file then reload this plugin.";
    private static String defaultNoMsg = "There is no day-night cycle in the Nether and the End dimensions.";
    private static String defaultDay = "begin at 6.00 am or tick #" + dayStart;
    private static String defaultDusk = "begin at 5.30 pm or tick #" + duskStart;
    private static String defaultNight = "begin at 7.00 pm or tick #" + nightStart;
    private static String defaultDawn = "begin at 4.30 am or tick #" + dawnStart;

    /**
     * Activate or reload the language file
     */
    public static void loadLang(String firstOrRe) {

	// #1. When it is the server startup
	if (firstOrRe.equalsIgnoreCase("first")) {
	    // Creation of lang.yml file if doesn't exist
	    if (!(MainTM.getInstance().langFileYaml.exists())) {
		Bukkit.getLogger().info(prefixTM + " " + lgFileCreaMsg); // Console log msg
		// Copy the file from src in .jar
		CopyFilesHandler.copy(MainTM.getInstance().getResource(lANGFILENAME), MainTM.getInstance().langFileYaml);
		// Actualize values
		MainTM.getInstance().langConf = YamlConfiguration.loadConfiguration(MainTM.getInstance().langFileYaml);
	    } else {
		Bukkit.getLogger().info(prefixTM + " " + lgFileExistMsg); // Console log msg
	    }
	}

	// #2. When using the admin command /tm reload
	if (firstOrRe.equalsIgnoreCase("re")) {
	    if (MainTM.getInstance().langFileYaml.exists()) {
		// Notification
		Bukkit.getLogger().info(prefixTM + " " + lgFileTryReloadMsg);
		// Reload values from lang.yml file
		MainTM.getInstance().langConf = YamlConfiguration.loadConfiguration(MainTM.getInstance().langFileYaml);
	    } else
		loadLang("first");
	}

	// #3. In both case

	// #A. Restore fixed values
	MainTM.getInstance().langConf.set(CF_VERSION, MainTM.versionTM());
	MainTM.getInstance().langConf.set(CF_lANGUAGES + "." + CF_DEFAULT + "." + CF_PREFIX, defaultPrefix);
	MainTM.getInstance().langConf.set(CF_lANGUAGES + "." + CF_DEFAULT + "." + CF_MSG, defaultMsg);
	MainTM.getInstance().langConf.set(CF_lANGUAGES + "." + CF_DEFAULT + "." + CF_NOMSG, defaultNoMsg);
	MainTM.getInstance().langConf.set(CF_lANGUAGES + "." + CF_DEFAULT + "." + CF_DAYPARTS + "." + CF_DAY, defaultDay);
	MainTM.getInstance().langConf.set(CF_lANGUAGES + "." + CF_DEFAULT + "." + CF_DAYPARTS + "." + CF_DUSK, defaultDusk);
	MainTM.getInstance().langConf.set(CF_lANGUAGES + "." + CF_DEFAULT + "." + CF_DAYPARTS + "." + CF_NIGHT, defaultNight);
	MainTM.getInstance().langConf.set(CF_lANGUAGES + "." + CF_DEFAULT + "." + CF_DAYPARTS + "." + CF_DAWN, defaultDawn);

	// #B. Check 'defLang' integrity
	checkDefLang();

	// #C. Is multilanguage enable ? Set to false if doesn't exist or if invalid boolean
	if (MainTM.getInstance().langConf.getKeys(false).contains(CF_USEMULTILANG)) {
	    if (MainTM.getInstance().langConf.getString(CF_USEMULTILANG).equalsIgnoreCase("true")) {
		Bukkit.getLogger().info(prefixTM + " " + multiLangIsOnMsg);
	    } else {
		MainTM.getInstance().langConf.set(CF_USEMULTILANG, "false");
		Bukkit.getLogger().info(prefixTM + " " + multiLangIsOffMsg);
	    }
	} else {
	    MainTM.getInstance().langConf.set(CF_USEMULTILANG, "false");
	    Bukkit.getLogger().info(prefixTM + " " + multiLangIsOffMsg);
	}

	// #D. Save the lang.yml file
	SaveLangYml();

	// E. Notifications
	if (debugMode == true)
	    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + availableTranslationsDebugMsg + " §e" + setAnyListFromLang("languages")); // Console debug msg
	if (firstOrRe.equalsIgnoreCase("first")) {
	    Bukkit.getLogger().info(prefixTM + " " + lgVersionMsg + MainTM.getInstance().langConf.getString("version") + ".");
	}

    }

    /**
     * Check 'defaultLang' integrity in lang.yml
     */
    // Check if 'defaultLang' key exists in yaml, if not create it and set it to
    // default
    private static void checkDefLang() {
	if (!MainTM.getInstance().langConf.getKeys(false).contains(CF_DEFAULTLANG)) {
	    restoreDefLang();
	} else { // Else, if 'defaultLang' key exists but is void set it to default
	    if (MainTM.getInstance().langConf.getString(CF_DEFAULTLANG).equals("")) {
		MainTM.getInstance().langConf.set(CF_DEFAULTLANG, CF_DEFAULT);
	    }
	    // Then actualize 'defaultLang' from lang.yml file for checking
	    serverLang = new String(MainTM.getInstance().langConf.getString(CF_DEFAULTLANG));
	    Bukkit.getServer().getConsoleSender().sendMessage(prefixTM + " " + defLangCheckMsg + " §e" + serverLang + "§r."); // Console log msg
	    // Check if key 'defaultLang' correspond to an existing language who contains
	    // every needed keys
	    if (!MainTM.getInstance().langConf.getConfigurationSection(CF_lANGUAGES).getKeys(false).contains(serverLang)) {
		restoreDefLang();
	    } else {
		Set<String> langKeys = MainTM.getInstance().langConf.getConfigurationSection(CF_lANGUAGES + "." + serverLang).getKeys(true);
		if (langKeys.contains(CF_PREFIX)
			&& langKeys.contains(CF_MSG)
			&& langKeys.contains(CF_NOMSG) && langKeys.contains(CF_DAYPARTS)
			&& langKeys.contains(CF_DAY) && langKeys.contains(CF_DUSK)
			&& langKeys.contains(CF_NIGHT) && langKeys.contains(CF_DAWN)) {
		    // If every key exists, keep actual 'defLang'
		    Bukkit.getServer().getConsoleSender().sendMessage(prefixTM + " §e" + serverLang + "§r " + defLangOkMsg); // Console log msg
		} else {
		    restoreDefLang();
		}
	    }
	}
    }

    /**
     * Restore the 'default' translation in lang.yml
     */
    private static void restoreDefLang() {
	if (McVersionHandler.KeepTypeOfServer().equalsIgnoreCase("bukkit") && decimalOfMcVersion < 12.0) {
	    Bukkit.getServer().getConsoleSender().sendMessage(prefixTM + " §e" + serverLang + "§r " + defLangResetMsg); // Console log msg
	}
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
	    Bukkit.getLogger().severe(prefixTM + " " + couldNotSaveLang);
	    e.printStackTrace();
	}
    }

    /**
     * Return an array list from everything listed in a specific key from the
     * lang.yml
     */
    public static List<String> setAnyListFromLang(String inWichYamlKey) {
	List<String> listedElementsList = new ArrayList<>();
	for (String listedElement : MainTM.getInstance().langConf.getConfigurationSection(inWichYamlKey).getKeys(false)) {
	    listedElementsList.add(listedElement);
	}
	return listedElementsList;
    }

};