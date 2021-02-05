package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;

public class TmSetDebugMode extends MainTM {

	/**
	 * CMD /tm set debugmode [true|false]
	 */
	public static void cmdDebugMode(CommandSender sender, String onOff) {

		// Check if the argument matches what is expected
		if (onOff.equalsIgnoreCase("true") || onOff.equalsIgnoreCase("false")) {
			MainTM.getInstance().getConfig().set(CF_DEBUGMODE, onOff);
			MainTM.getInstance().saveConfig();
			if (onOff.equalsIgnoreCase("true")) {
				debugMode = true;
				MsgHandler.debugMsg(enableDebugModeDebugMsg); // Console debug msg (always)
				MsgHandler.playerMsg(sender, enableDebugModeDebugMsg); // Player debug msg (in case)
			} else if (onOff.equalsIgnoreCase("false")) {
				MsgHandler.debugMsg(disableDebugModeDebugMsg); // Console debug msg (always)
				MsgHandler.playerMsg(sender, disableDebugModeDebugMsg); // Player debug msg (in case)
				debugMode = false;
			}
		}
		// Else, return an error and help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.isNotBooleanMsg, "set debugmode");
		}
	}

};