package net.vdcraft.arvdc.timemanager.cmdplayer;

import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class PlayerLangHandler extends MainTM {

	/**
	 * Define the language to use, in regards to the locale and parameters
	 * (returns a String)
	 */
	public static String setLangToUse(CommandSender sender) {
		Player p = (Player) sender;
		// If option is disable, use default language
		if (MainTM.getInstance().langConf.getString(LG_USEMULTILANG).equalsIgnoreCase(ARG_FALSE)) {
			return serverLang;
		}
		// Get player locale and format it
		String lowerCaseLocale;
		// MC 1.12-
		if (serverMcVersion < reqMcVToGetLocale) {
			Locale computerLocale = Locale.getDefault();
			lowerCaseLocale = computerLocale.toString();
			MsgHandler.devMsg(pcLocaleDebugMsg); // Console dev msg
		// MC 1.12+
		} else {
			lowerCaseLocale = p.getLocale();
			MsgHandler.devMsg(mcLocaleDebugMsg); // Console dev msg
		}
		// Restore the correct case format (lg_LG)
		String playerLocale = ValuesConverter.getCorrectLocaleCase(lowerCaseLocale);

		MsgHandler.devMsg(foundLocaleDebugMsg + " §e" + sender.getName() + "§9 is §e" + playerLocale + "§b."); // Console dev msg

		// If locale is unavailable in the yaml keys, try to use the first part to reach the nearest existing language
		if (!(MainTM.getInstance().langConf.getConfigurationSection(LG_LANGUAGES).getKeys(false).contains(playerLocale))) {
			playerLocale = ValuesConverter.findNearestLang(playerLocale);
		}

		MsgHandler.debugMsg(useLocaleDebugMsg + " §e" + sender.getName() + "§b is §e" + playerLocale + "§b."); // Console debug msg

		return playerLocale;
	}
	
	/**
	 * Call the method with a 'Player' arg instead of 'CommandSender'
	 * (returns a String)
	 */	
	public static String setLangToUse(Player p) {
		CommandSender sender = (CommandSender) p;
		return setLangToUse(sender);
	}

};