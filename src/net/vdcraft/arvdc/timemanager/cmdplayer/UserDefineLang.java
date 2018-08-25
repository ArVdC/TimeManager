package net.vdcraft.arvdc.timemanager.cmdplayer;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.McVersionHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class UserDefineLang extends MainTM {
	
	/**
	 * Define the language to use, in regards to the locale and parameters
	 */
	@SuppressWarnings({ "deprecation" })
	public static String setLangToUse(CommandSender sender) {
		Player p = ((Player) sender);
		// If option is disable, use default language
		if(MainTM.getInstance().langConf.getString("useMultiLang").equalsIgnoreCase("false")) {
			return serverLang;
		}
		// Get player locale and format it
		String lowerCaseLocale;
		// Spigot/Bukkit (or other) version 1.12-
		if(McVersionHandler.KeepDecimalOfMcVersion() < 12.0) {
			// If the server is a Spigot
			if(McVersionHandler.KeepTypeOfServer().equalsIgnoreCase("spigot")) {
				lowerCaseLocale = p.spigot().getLocale();
				if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + mcLocaleDebugMsg); // Console debug msg
			} else {
				// If the server is a Bukkit or other fork
				Locale computerLocale = Locale.getDefault();
				lowerCaseLocale = computerLocale.toString();
				if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + pcLocaleDebugMsg); // Console debug msg
			}
		// Spigot/Bukkit version 1.12+
		} else {
			lowerCaseLocale = p.getLocale();
			if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + mcLocaleDebugMsg); // Console debug msg
		}
		// Restore the correct case format (xx_XX)
		String playerLocale = ValuesConverter.returnCorrectLocaleCase(lowerCaseLocale);
		
		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + foundLocaleDebugMsg + " §e" + sender.getName() + "§b is §e" + playerLocale + "§b."); // Console debug msg
		
		// If locale is unavailable in the yaml keys, try to use the first part to reach the nearest existing language
		if(!(MainTM.getInstance().langConf.getConfigurationSection("languages").getKeys(false).contains(playerLocale))) {
			playerLocale = ValuesConverter.returnNearestLang(playerLocale);
		}
		
		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + useLocaleDebugMsg + " §e" + sender.getName() + "§b is §e" + playerLocale + "§b."); // Console debug msg
		
		return playerLocale;
	}
	
};