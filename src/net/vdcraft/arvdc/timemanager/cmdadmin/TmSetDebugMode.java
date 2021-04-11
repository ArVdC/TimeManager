package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;

public class TmSetDebugMode extends MainTM {

	/**
	 * CMD /tm set debugMode [true|false]
	 */
	public static void cmdDebugMode(CommandSender sender, String onOff) {

		// Check if the argument matches what is expected
		if (onOff.equalsIgnoreCase(ARG_TRUE) || onOff.equalsIgnoreCase(ARG_FALSE)) {
			MainTM.getInstance().getConfig().set(CF_DEBUGMODE, onOff);
			MainTM.getInstance().saveConfig();
			if (onOff.equalsIgnoreCase(ARG_TRUE)) {
				debugMode = true;
				MsgHandler.debugMsg(enableDebugModeDebugMsg); // Console debug msg (always)
				MsgHandler.playerAdminMsg(sender, enableDebugModeDebugMsg); // Player debug msg (in case)
			} else if (onOff.equalsIgnoreCase("false")) {
				MsgHandler.debugMsg(disableDebugModeDebugMsg); // Console debug msg (always)
				MsgHandler.playerAdminMsg(sender, disableDebugModeDebugMsg); // Player debug msg (in case)
				debugMode = false;
			}
		}
		// Else, return an error and help message
		else {
			MsgHandler.cmdErrorMsg(sender, MainTM.isNotBooleanMsg, MainTM.CMD_SET + " " + CMD_SET_DEBUG);
		}
	}

	/**
	 * CMD /tm set devMode [true|false]
	 */
	public static void cmdDevMode(CommandSender sender, String onOff) {

		// Check if the argument matches what is expected
		if (onOff.equalsIgnoreCase(ARG_TRUE) || onOff.equalsIgnoreCase(ARG_FALSE)) {
			if (onOff.equalsIgnoreCase(ARG_TRUE)) {
				devMode = true;
				MsgHandler.devMsg(enableDevModeDebugMsg); // Console dev msg (always)
				MsgHandler.playerAdminMsg(sender, enableDevModeDebugMsg); // Player dev msg (in case)
			} else if (onOff.equalsIgnoreCase("false")) {
				MsgHandler.devMsg(disableDevModeDebugMsg); // Console debug msg (always)
				MsgHandler.playerAdminMsg(sender, disableDevModeDebugMsg); // Player dev msg (in case) 
				devMode = false;
			}
		}
		// Else, return an error and help message
		else {
			MsgHandler.cmdErrorMsg(sender, MainTM.isNotBooleanMsg, MainTM.CMD_SET + " " + CMD_SET_DEV);
		}
	}

	/**
	 * CMD /tm set timerMode [true|false]
	 */
	public static void cmdTimerMode(CommandSender sender, String onOff) {

		// Check if the argument matches what is expected
		if (onOff.equalsIgnoreCase(ARG_TRUE) || onOff.equalsIgnoreCase(ARG_FALSE)) {
			if (onOff.equalsIgnoreCase(ARG_TRUE)) {
				timerMode = true;
				MsgHandler.timerMsg(enableTimerModeDebugMsg); // Console timer msg (always)
				MsgHandler.playerAdminMsg(sender, enableTimerModeDebugMsg); // Player timer msg (in case)
			} else if (onOff.equalsIgnoreCase("false")) {
				MsgHandler.timerMsg(disableTimerModeDebugMsg); // Console timer msg (always)
				MsgHandler.playerAdminMsg(sender, disableTimerModeDebugMsg); // Player timer msg (in case)
				timerMode = false;
			} 
		}
		// Else, return an error and help message
		else {
			MsgHandler.cmdErrorMsg(sender, MainTM.isNotBooleanMsg, MainTM.CMD_SET + " " + CMD_SET_TIMER);
		}
	}

};