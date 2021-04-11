package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;

public class TmSetMultiLang extends MainTM {

	/**
	 * CMD /tm set multiLang [true|false]
	 */
	public static void cmdMultiLg(CommandSender sender, String onOff) {

		// Check if the argument matches what is expected
		if (onOff.equalsIgnoreCase(ARG_TRUE) || onOff.equalsIgnoreCase(ARG_FALSE)) {
			MainTM.getInstance().langConf.set(CF_USEMULTILANG, onOff);
			LgFileHandler.SaveLangYml();
			if (onOff.equalsIgnoreCase(ARG_TRUE)) {
				MsgHandler.infoMsg(multiLangIsOnMsg); // Console final msg (always)
				MsgHandler.playerAdminMsg(sender, multiLangIsOnMsg); // Player final msg (in case)
			} else if (onOff.equalsIgnoreCase(ARG_FALSE)) {
				MsgHandler.infoMsg(multiLangIsOffMsg); // Console final msg (always)
				MsgHandler.playerAdminMsg(sender, multiLangIsOffMsg); // Player final msg (in case)
			}
		}
		// Else, return an error and help message
		else {
			MsgHandler.cmdErrorMsg(sender, MainTM.isNotBooleanMsg, MainTM.CMD_SET + " " + CMD_SET_MULTILANG);
		}
	}

};