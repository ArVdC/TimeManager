package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;

public class TmSetMultiLang extends MainTM {

	/**
	 * CMD /tm set multilang [true|false]
	 */
	public static void cmdMultiLg(CommandSender sender, String onOff) {

		// Check if the argument matches what is expected
		if (onOff.equalsIgnoreCase("true") || onOff.equalsIgnoreCase("false")) {
			MainTM.getInstance().langConf.set(CF_USEMULTILANG, onOff);
			LgFileHandler.SaveLangYml();
			if (onOff.equalsIgnoreCase("true")) {
				MsgHandler.infoMsg(multiLangIsOnMsg); // Console final msg (always)
				MsgHandler.playerMsg(sender, multiLangIsOnMsg); // Player final msg (in case)
			} else if (onOff.equalsIgnoreCase("false")) {
				MsgHandler.infoMsg(multiLangIsOffMsg); // Console final msg (always)
				MsgHandler.playerMsg(sender, multiLangIsOffMsg); // Player final msg (in case)
			}
		}
		// Else, return an error and help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.isNotBooleanMsg, "set multilang");
		}
	}

};