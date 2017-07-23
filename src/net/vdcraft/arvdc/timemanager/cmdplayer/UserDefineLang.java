package net.vdcraft.arvdc.timemanager.cmdplayer;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class UserDefineLang extends MainTM {
	
	/**
	 * Define the language to use, in regards to the locale and parameters
	 */
	@SuppressWarnings("deprecation")
	public static String setLangToUse(CommandSender sender) {
		// If option is disable, use default language
		if(MainTM.getInstance().langConf.getString("useMultiLang").equalsIgnoreCase("false")) {
			return serverLang;
		}
		// Get player locale and format it
		String lowerCaseLocale;
		// MC version 1.12-
		if(ValuesConverter.KeepDecimalOfMcVersion() < 12.0) {
			lowerCaseLocale = ((Player) sender).spigot().getLocale();
		// MC version 1.12+
		} else {
			lowerCaseLocale = ((Player) sender).getLocale();
		}
		String checkedLocale;
		if(lowerCaseLocale.contains("_")) {
			String[] splitLocale = lowerCaseLocale.split("_");
			String lower_UpperCaseLocale = splitLocale[0] + "_" + splitLocale[1].toUpperCase();
			checkedLocale = lower_UpperCaseLocale;
		} else {
			checkedLocale = lowerCaseLocale;
		}
		// Check if locale is available in the yaml keys
		if(MainTM.getInstance().langConf.getConfigurationSection("languages").getKeys(false).contains(checkedLocale))
		{
			return checkedLocale;
		} else { // If locale is unavailable, use default language
			return serverLang;
		}
	};
}
