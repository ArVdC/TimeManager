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
	if (MainTM.getInstance().langConf.getString(CF_USEMULTILANG).equalsIgnoreCase("true")) {
	    multiLangOnOff = "enabled";
	}
	String multiLangMsg = "Multilanguage is " + multiLangOnOff + ".";
	String colMultiLangMsg = "Multilanguage is §e" + multiLangOnOff + "§r.";

	// Default language
	String defLangMsg = "Default language is " + MainTM.getInstance().langConf.getString(CF_DEFAULTLANG) + ".";
	String colDefLangMsg = "Default language is §e" + MainTM.getInstance().langConf.getString(CF_DEFAULTLANG) + "§r.";

	// Available languages
	List<String> availableLg = LgFileHandler.setAnyListFromLang(CF_lANGUAGES);
	String availableLangMsg = "Available languages are : " + availableLg + ".";
	String colAvailableLangMsg = "Available languages are : §e" + availableLg + "§r.";

	// Default units
	String defUnitsMsg = "Default time units are " + MainTM.getInstance().getConfig().getString(CF_DEFTIMEUNITS) + ".";
	String colDefUnitsMsg = "Default time units are §e" + MainTM.getInstance().getConfig().getString(CF_DEFTIMEUNITS) + "§r.";

	// Refresh rate
	String refRateMsg = refreshRateMsg + " " + MainTM.getInstance().getConfig().getString(CF_REFRESHRATE) + " ticks.";
	String colRefRateMsg = refreshRateMsg + " §e" + MainTM.getInstance().getConfig().getString(CF_REFRESHRATE) + " ticks§r.";
	
	// Wake up tick
	String wakeUpTickMsg = "Players will wake up at #" + MainTM.getInstance().getConfig().getString(CF_WAKEUPTICK) + ".";
	String colwakeUpTickMsg = "Players will wake up at §e#" + MainTM.getInstance().getConfig().getString(CF_WAKEUPTICK) + "§r.";	

	// List of the worlds
	List<String> activeWorlds = CfgFileHandler.setAnyListFromConfig(CF_WORLDSLIST);
	String worldsMsg = "Active world(s) are : " + activeWorlds + ".";
	String colWorldsMsg = "Active world(s) are : §e" + activeWorlds + "§r.";

	// Initial tick reset status
	String resetOnOff = " not ";
	if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_RESETONSTARTUP).equalsIgnoreCase("true")) {
	    resetOnOff = " ";
	}
	String resetMsg = "The initial tick will" + resetOnOff + "reset on the next startup.";
	String colResetMsg = "The initial tick §ewill" + resetOnOff + "reset§r on the next startup.";

	// MySql
	String sqlOnOff = "the config.yml file";
	if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equalsIgnoreCase("true")) {
	    sqlOnOff = "a MySql database";
	}
	String sqlMsg = "The initial tick is saved in " + sqlOnOff + ".";
	String colSqlMsg = "The initial tick is saved in §e" + sqlOnOff + "§r.";

	// Update
	String updateMsg = "The update message source can not be checked.";
	String colUpdateMsg = "The update message source §ecan not be checked§r.";
	if (!MainTM.getInstance().getConfig().getString(CF_UPDATEMSGSRC).equals("")) {
	    String updateSrc = "s" + MainTM.getInstance().getConfig().getString(CF_UPDATEMSGSRC);
	    updateMsg = "The update message will check on " + updateSrc + " server to find a new version.";
	    colUpdateMsg = "The update message will check on §e" + updateSrc + "§r server to find a new version.";
	} else {
	    updateMsg = "The update message is disabled.";
	    colUpdateMsg = "The update message is §edisabled§r.";
	}

	// Debug
	String debugOnOff = "disabled";
	if (MainTM.getInstance().getConfig().getString(CF_DEBUGMODE).equalsIgnoreCase("true")) {
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
	    // Wake up tick
	    Bukkit.getLogger().info(prefixTM + " " + colwakeUpTickMsg);
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
	    // Display the update source status
	    sender.sendMessage(prefixTMColor + " " + colUpdateMsg);
	    waitTime(1000);
	    // Display the debug mode status
	    sender.sendMessage(prefixTMColor + " " + colDebugMsg);
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
	    // Wake up tick
	    Bukkit.getLogger().info(prefixTM + " " + wakeUpTickMsg);
	    // Display the world's list
	    Bukkit.getLogger().info(prefixTM + " " + worldsMsg);
	    // Display the initial tick reset status
	    Bukkit.getLogger().info(prefixTM + " " + resetMsg);
	    // Display the MySql status
	    Bukkit.getLogger().info(prefixTM + " " + sqlMsg);
	    // Display the update source status
	    Bukkit.getLogger().info(prefixTM + " " + updateMsg);
	    // Display the debug mode status
	    Bukkit.getLogger().info(prefixTM + " " + debugMsg);
	}

    }

};