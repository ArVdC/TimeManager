package net.vdcraft.arvdc.timemanager.cmdadmin;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;

public class TmCheckConfig extends MainTM {

    /**
     * CMD /tm checkconfig
     * 
     */

    public static void cmdCheckConfig(CommandSender sender) {

	/*****************
	 *** VARIABLES ***
	 *****************/

	// Version
	String versionMsg = "You are running the " + versionTM() + " version.";
	String colVersionMsg = "You are running the §e" + versionTM() + "§r version.";

	// Multilanguage
	String multiLangOnOff = "disabled";
	if (MainTM.getInstance().langConf.getString("useMultiLang").equalsIgnoreCase("true")) {
	    multiLangOnOff = "enabled";
	}
	String multiLangMsg = "Multilanguage is " + multiLangOnOff + ".";
	String colMultiLangMsg = "Multilanguage is §e" + multiLangOnOff + "§r.";

	// Default language
	String defLangMsg = "Default language is " + MainTM.getInstance().langConf.getString("defaultLang") + ".";
	String colDefLangMsg = "Default language is §e" + MainTM.getInstance().langConf.getString("defaultLang") + "§r.";

	// Available languages
	List<String> availableLg = LgFileHandler.setAnyListFromLang("languages");
	String availableLangMsg = "Available languages are : " + availableLg + ".";
	String colAvailableLangMsg = "Available languages are : §e" + availableLg + "§r.";

	// Default units
	String defUnitsMsg = "Default time units are " + MainTM.getInstance().getConfig().getString("defTimeUnits") + ".";
	String colDefUnitsMsg = "Default time units are §e" + MainTM.getInstance().getConfig().getString("defTimeUnits") + "§r.";

	// Refresh rate
	String refRateMsg = refreshRateMsg + " " + MainTM.getInstance().getConfig().getString("refreshRate") + " ticks.";
	String colRefRateMsg = refreshRateMsg + " §e" + MainTM.getInstance().getConfig().getString("refreshRate") + " ticks§r.";

	// List of the worlds
	List<String> activeWorlds = CfgFileHandler.setAnyListFromConfig("worldsList");
	String worldsMsg = "Active world(s) are : " + activeWorlds + ".";
	String colWorldsMsg = "Active world(s) are : §e" + activeWorlds + "§r.";

	// Initial tick reset status
	String resetOnOff = " not ";
	if (MainTM.getInstance().getConfig().getString("initialTick.resetOnStartup").equalsIgnoreCase("true")) {
	    resetOnOff = " ";
	}
	String resetMsg = "The initial tick will" + resetOnOff + "reset on the next startup.";
	String colResetMsg = "The initial tick §ewill" + resetOnOff + "reset§r on the next startup.";

	// MySql
	String sqlOnOff = "the config.yml file";
	if (MainTM.getInstance().getConfig().getString("initialTick.useMySql").equalsIgnoreCase("true")) {
	    sqlOnOff = "a MySql database";
	}
	String sqlMsg = "The initial tick is saved in " + sqlOnOff + ".";
	String colSqlMsg = "The initial tick is saved in §e" + sqlOnOff + "§r.";

	// Debug
	String debugOnOff = "disabled";
	if (MainTM.getInstance().getConfig().getString("debugMode").equalsIgnoreCase("true")) {
	    debugOnOff = "enabled";
	}
	String debugMsg = "The debug mode is " + debugOnOff + ".";
	String colDebugMsg = "The debug mode is §e" + debugOnOff + "§r.";

	/******************
	 ***** EVENT ******
	 ******************/

	if (sender instanceof Player) {
	    // Display the version of the plugin
	    sender.sendMessage(prefixTMColor + " " + colVersionMsg);
	    waitTime(1000);
	    // Display the multilanguage status
	    sender.sendMessage(prefixTMColor + " " + colMultiLangMsg);
	    waitTime(1000);
	    // Display the default language
	    sender.sendMessage(prefixTMColor + " " + colDefLangMsg);
	    waitTime(1000);
	    // Display the available languages
	    sender.sendMessage(prefixTMColor + " " + colAvailableLangMsg);
	    waitTime(1000);
	    // Display the default time units
	    sender.sendMessage(prefixTMColor + " " + colDefUnitsMsg);
	    waitTime(1000);
	    // Display the refresh rate
	    sender.sendMessage(prefixTMColor + " " + colRefRateMsg);
	    waitTime(1000);
	    // Display the world's list
	    sender.sendMessage(prefixTMColor + " " + colWorldsMsg);
	    waitTime(1000);
	    // Display the initial tick reset status
	    sender.sendMessage(prefixTMColor + " " + colResetMsg);
	    waitTime(1000);
	    // Display the MySql status
	    sender.sendMessage(prefixTMColor + " " + colSqlMsg);
	    waitTime(1000);
	    // Display the debugmode status
	    sender.sendMessage(prefixTMColor + " " + colDebugMsg);
	    waitTime(1000);
	} else {
	    // Display the version of the plugin
	    Bukkit.getLogger().info(prefixTM + " " + versionMsg);
	    // Display the multilanguage status
	    Bukkit.getLogger().info(prefixTM + " " + multiLangMsg);
	    // Display the default language
	    Bukkit.getLogger().info(prefixTM + " " + defLangMsg);
	    // Display the available languages
	    Bukkit.getLogger().info(prefixTM + " " + availableLangMsg);
	    // Display the default time units
	    Bukkit.getLogger().info(prefixTM + " " + defUnitsMsg);
	    // Display the refresh rate
	    Bukkit.getLogger().info(prefixTM + " " + refRateMsg);
	    // Display the world's list
	    Bukkit.getLogger().info(prefixTM + " " + worldsMsg);
	    // Display the initial tick reset status
	    Bukkit.getLogger().info(prefixTM + " " + resetMsg);
	    // Display the MySql status
	    Bukkit.getLogger().info(prefixTM + " " + sqlMsg);
	    // Display the debugmode status
	    Bukkit.getLogger().info(prefixTM + " " + debugMsg);
	}

    }

};