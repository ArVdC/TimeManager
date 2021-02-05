package net.vdcraft.arvdc.timemanager.cmdplayer;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.McVersionHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class UserMsgHandler extends MainTM {

	/**
	 * Define the language to use, in regards to the locale and parameters
	 */
	public static String setLangToUse(CommandSender sender) {
		Player p = ((Player) sender);
		// If option is disable, use default language
		if (MainTM.getInstance().langConf.getString(CF_USEMULTILANG).equalsIgnoreCase("false")) {
			return serverLang;
		}
		// Get player locale and format it
		String lowerCaseLocale;
		// Spigot/Bukkit (or other) version 1.12-
		if (decimalOfMcVersion < 12.0) {
			// If the server is a Spigot
			if (McVersionHandler.KeepTypeOfServer().equalsIgnoreCase("spigot")) {
				lowerCaseLocale = ((Player) p.spigot()).getLocale();
				if (devMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + mcLocaleDebugMsg); // Console debug msg
			} else {
				// If the server is a Bukkit or other fork
				Locale computerLocale = Locale.getDefault();
				lowerCaseLocale = computerLocale.toString();
				if (devMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + pcLocaleDebugMsg); // Console debug msg
			}
			// Spigot/Bukkit version 1.12+
		} else {
			lowerCaseLocale = p.getLocale();
			if (devMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + mcLocaleDebugMsg); // Console debug msg
		}
		// Restore the correct case format (xx_XX)
		String playerLocale = ValuesConverter.getCorrectLocaleCase(lowerCaseLocale);

		if (devMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + foundLocaleDebugMsg + " §e" + sender.getName() + "§b is §e" + playerLocale + "§b."); // Console debug msg

		// If locale is unavailable in the yaml keys, try to use the first part to reach the nearest existing language
		if (!(MainTM.getInstance().langConf.getConfigurationSection(CF_LANGUAGES).getKeys(false).contains(playerLocale))) {
			playerLocale = ValuesConverter.findNearestLang(playerLocale);
		}

		if (debugMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + useLocaleDebugMsg + " §e" + sender.getName() + "§b is §e" + playerLocale + "§b."); // Console debug msg

		return playerLocale;
	}

	/**
	 * Send final msg to user
	 */
	public static boolean SendNowMsg(CommandSender sender, String finalWorld, String finaldayPart, String finalTime, String finalLang, String elapsedDays, String currentDay, String yearWeek, String dd, String mm, String yy, String yyyy) {
		// #1. Start loading variables from the lang.yml file
		String msgPrefix = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + finalLang + "." + CF_PREFIX);
		String dayPart = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + finalLang + "." + CF_DAYPARTS + "." + finaldayPart);
		String msgNow = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + finalLang + "." + CF_MSG);
		String monthName = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + finalLang + "." + CF_MONTHS + ".m" + mm);
		// #2. Avoid showing actual time if player is in a nether or the_end world
		if (finalWorld.contains("_nether") || finalWorld.contains("_the_end")) {
			msgNow = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + finalLang + "." + CF_NOMSG);
			// #3. If the noMsg in lang.yml file is empty, nothing will be send to the player
			if (msgNow.equalsIgnoreCase("")) {
				return true;
			}
		}
		// #4. Process the message content
		msgPrefix = msgPrefix.replace("&", "§");
		msgNow = msgNow.replace("&", "§");
		msgNow = msgNow.replace("{player}", sender.getName());
		msgNow = msgNow.replace("{time}", finalTime);
		msgNow = msgNow.replace("{world}", finalWorld);
		msgNow = msgNow.replace("{dayPart}", dayPart);
		msgNow = msgNow.replace("{elapsedDays}", elapsedDays);
		msgNow = msgNow.replace("{currentDay}", currentDay);
		msgNow = msgNow.replace("{monthName}", monthName);
		msgNow = msgNow.replace("{yearWeek}", yearWeek);
		msgNow = msgNow.replace("{dd}", dd);
		msgNow = msgNow.replace("{mm}", mm);
		msgNow = msgNow.replace("{yy}", yy);
		msgNow = msgNow.replace("{yyyy}", yyyy);
		// #5. Send the message
		sender.sendMessage(msgPrefix + "§r " + msgNow);
		return true;
	}

};