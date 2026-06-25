package net.vdcraft.arvdc.timemanager.cmdadmin;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;

public class TmCheckConfig extends MainTM {

	/**
	 * CMD /tm checkConfig
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
		if (MainTM.getInstance().langConf.getString(LG_USEMULTILANG).equalsIgnoreCase(ARG_TRUE)) {
			multiLangOnOff = "enabled";
		}
		String multiLangMsg = "Multilanguage is " + multiLangOnOff + ".";
		String colMultiLangMsg = "Multilanguage is §e" + multiLangOnOff + "§r.";

		// Default language
		String defLangMsg = "Default language is " + MainTM.getInstance().langConf.getString(LG_DEFAULTLANG) + ".";
		String colDefLangMsg = "Default language is §e" + MainTM.getInstance().langConf.getString(LG_DEFAULTLANG) + "§r.";

		// Available languages
		List<String> availableLg = LgFileHandler.setAnyListFromLang(LG_LANGUAGES);
		String availableLangMsg = "Available languages are : " + availableLg + ".";
		String colAvailableLangMsg = "Available languages are : §e" + availableLg + "§r.";

		// Refresh rate
		String refRateMsg = refreshRateMsg + " " + MainTM.getInstance().getConfig().getString(CF_REFRESHRATE) + " ticks.";
		String colRefRateMsg = refreshRateMsg + " §e" + MainTM.getInstance().getConfig().getString(CF_REFRESHRATE) + " ticks§r.";

		// Wake up tick
		String wakeUpTickMsg = "Players will wake up at #" + MainTM.getInstance().getConfig().getString(CF_WAKEUPTICK) + ".";
		String colwakeUpTickMsg = "Players will wake up at §e#" + MainTM.getInstance().getConfig().getString(CF_WAKEUPTICK) + "§r.";	

		// New day will start at
		String tick = "#18000";
		if (MainTM.getInstance().getConfig().getString(CF_NEWDAYAT).equalsIgnoreCase(newDayStartsAt_6h00)) tick = "#0";
		String newDayAtMsg = "In the calendar, new day starts at " + MainTM.getInstance().getConfig().getString(CF_NEWDAYAT) + ". (" + tick + ")";
		String colNewDayAtMsg = "In the calendar, new day starts at §e" + MainTM.getInstance().getConfig().getString(CF_NEWDAYAT) + "§r. (§e" + tick + "§r)";	

		// List of the worlds
		List<String> activeWorlds = CfgFileHandler.setAnyListFromConfig(CF_WORLDSLIST);
		String worldsMsg = "Active world(s) are : " + activeWorlds + ".";
		String colWorldsMsg = "Active world(s) are : §e" + activeWorlds + "§r.";

		// Initial tick reset status
		String resetOnOff = " not ";
		if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_RESETONSTARTUP).equalsIgnoreCase(ARG_TRUE)) {
			resetOnOff = " ";
		}
		String resetMsg = "The initial tick will" + resetOnOff + "reset on the next startup.";
		String colResetMsg = "The initial tick §ewill" + resetOnOff + "reset§r on the next startup.";

		// MySql
		String sqlOnOff = "the config.yml file";
		if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equalsIgnoreCase(ARG_TRUE)) {
			sqlOnOff = "a MySql database";
		}
		String sqlMsg = "The initial tick is saved in " + sqlOnOff + ".";
		String colSqlMsg = "The initial tick is saved in §e" + sqlOnOff + "§r.";

		// Update
		String updateMsg = "The update message source can not be checked.";
		String colUpdateMsg = "The update message source §ecan not be checked§r.";
		if (!MainTM.getInstance().getConfig().getString(CF_UPDATEMSGSRC).equals(defUpdateMsgSrc)) {
			String updateSrc = "s" + MainTM.getInstance().getConfig().getString(CF_UPDATEMSGSRC);
			updateMsg = "The update message will check on " + updateSrc + " server to find a new version.";
			colUpdateMsg = "The update message will check on §e" + updateSrc + "§r server to find a new version.";
		} else {
			updateMsg = "The update message is disabled.";
			colUpdateMsg = "The update message is §edisabled§r.";
		}

		// Debug
		String debugOnOff = "disabled";
		if (MainTM.getInstance().getConfig().getString(CF_DEBUGMODE).equalsIgnoreCase(ARG_TRUE)) {
			debugOnOff = "enabled";
		}
		String debugMsg = "The debug mode is " + debugOnOff + ".";
		String colDebugMsg = "The debug mode is §e" + debugOnOff + "§r.";

		// Placeholders
		String PAPIOnOff = "aren't";
		if (MainTM.getInstance().getConfig().getString(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_PAPI).equalsIgnoreCase(ARG_TRUE)) {
			PAPIOnOff = "are";
		}
		String MvDWPAPIOnOff = "aren't";
		if (MainTM.getInstance().getConfig().getString(CF_PLACEHOLDERS + "." + CF_PLACEHOLDER_MVDWPAPI).equalsIgnoreCase(ARG_TRUE)) {
			MvDWPAPIOnOff = "are";
		}
		String placeholdersMsg = "Your placeholders " + PAPIOnOff + " registred in " + CF_PLACEHOLDER_PAPI + " and/but they " + MvDWPAPIOnOff + " registred in " + CF_PLACEHOLDER_MVDWPAPI + ".";
		String colPlaceholdersMsg = "Your placeholders " + PAPIOnOff + " registred in §e" + CF_PLACEHOLDER_PAPI + "§r and/but they " + MvDWPAPIOnOff + " registred in §e" + CF_PLACEHOLDER_MVDWPAPI + "§r.";

		/******************
		 ***** EVENT ******
		 ******************/

		if (sender instanceof Player) {
			// Display the version of the plugin
			MsgHandler.playerAdminMsg(sender, colVersionMsg);
			// Display the multilanguage status
			MsgHandler.playerAdminMsg(sender, colMultiLangMsg);
			// Display the default language
			MsgHandler.playerAdminMsg(sender, colDefLangMsg);
			// Display the available languages
			MsgHandler.playerAdminMsg(sender, colAvailableLangMsg);
			// Display the refresh rate
			MsgHandler.playerAdminMsg(sender, colRefRateMsg);
			// Display the wake up tick
			MsgHandler.playerAdminMsg(sender, colwakeUpTickMsg);
			// Display the new day at dawn/midnight
			MsgHandler.playerAdminMsg(sender, colNewDayAtMsg);
			// Display the world's list
			MsgHandler.playerAdminMsg(sender, colWorldsMsg);
			// Display the initial tick reset status
			MsgHandler.playerAdminMsg(sender, colResetMsg);
			// Display the MySql status
			MsgHandler.playerAdminMsg(sender, colSqlMsg);
			// Display the update source status
			MsgHandler.playerAdminMsg(sender, colUpdateMsg);
			// Display the debug mode status
			MsgHandler.playerAdminMsg(sender, colDebugMsg);
			// Display the placeholder API status
			MsgHandler.playerAdminMsg(sender, colPlaceholdersMsg);
		} else {
			// Display the version of the plugin
			MsgHandler.infoMsg(versionMsg);
			// Display the multilanguage status
			MsgHandler.infoMsg(multiLangMsg);
			// Display the default language
			MsgHandler.infoMsg(defLangMsg);
			// Display the available languages
			MsgHandler.infoMsg(availableLangMsg);
			// Display the refresh rate
			MsgHandler.infoMsg(refRateMsg);
			// Display the wake up tick
			MsgHandler.infoMsg(wakeUpTickMsg);
			// Display the new day at dawn/midnight
			MsgHandler.infoMsg(newDayAtMsg);
			// Display the world's list
			MsgHandler.infoMsg(worldsMsg);
			// Display the initial tick reset status
			MsgHandler.infoMsg(resetMsg);
			// Display the MySql status
			MsgHandler.infoMsg(sqlMsg);
			// Display the update source status
			MsgHandler.infoMsg(updateMsg);
			// Display the debug mode status
			MsgHandler.infoMsg(debugMsg);
			// Display the placeholder API status
			MsgHandler.infoMsg(placeholdersMsg);
		}
	}

};