package net.vdcraft.arvdc.timemanager.cmdplayer;

import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class UserDefineLang extends MainTM {
	
	/**
	 * Define the language to use, in regards to the locale and parameters
	 */
	@SuppressWarnings({ "deprecation" })
	public static String setLangToUse(CommandSender sender) {
		
		// If option is disable, use default language
		if(MainTM.getInstance().langConf.getString("useMultiLang").equalsIgnoreCase("false")) {
			return serverLang;
		}
		// Get player locale and format it
		String lowerCaseLocale;
		// Spigot/Bukkit (or other) version 1.12-
		if(ValuesConverter.KeepDecimalOfMcVersion() < 12.0) {
			// If the server is a Spigot
			if(ValuesConverter.KeepTypeOfServer().equalsIgnoreCase("spigot")) {
				lowerCaseLocale = ((Player) sender).spigot().getLocale();
			} else {
				// If the server is a Bukkit (or other)
				Locale computerLocale = Locale.getDefault();
				lowerCaseLocale = computerLocale.toString();
			}
		// Spigot/Bukkit version 1.12+
		} else {
			lowerCaseLocale = ((Player) sender).getLocale();
		}
		// Restore the correct case format (xx_XX)
		String playerLocale = ValuesConverter.returnCorrectLocaleCase(lowerCaseLocale);
		
		// If locale is unavailable in the yaml keys, try to use the first part to reach the nearest existing language
		if(!(MainTM.getInstance().langConf.getConfigurationSection("languages").getKeys(false).contains(playerLocale))) {
			playerLocale = ValuesConverter.returnNearestLang(playerLocale);
		} 
		return playerLocale;
	};
	
};