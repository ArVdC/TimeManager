package net.vdcraft.arvdc.timemanager.mainclass;

import net.vdcraft.arvdc.timemanager.MainTM;

public class DebugModeHandler extends MainTM {

	/**
	 * Display or not the colored debug messages in the console
	 */
	public static void debugModeOnOff() {
		if (MainTM.getInstance().getConfig().getKeys(false).contains(CF_DEBUGMODE)) { // If the option exists and is not 'true', set it on false
			if (!(MainTM.getInstance().getConfig().getString(CF_DEBUGMODE).equals(ARG_TRUE))) {
				MainTM.getInstance().getConfig().set(CF_DEBUGMODE, ARG_FALSE);
			}
		} else { // If the option doesn't exist, create it and set it on 'false'
			MainTM.getInstance().getConfig().set(CF_DEBUGMODE, ARG_FALSE);
		}
		String debugOnOff = MainTM.getInstance().getConfig().getString(CF_DEBUGMODE);
		if (debugOnOff.equalsIgnoreCase(ARG_TRUE)) {
			debugMode = true;
			MsgHandler.debugMsg(enableDebugModeDebugMsg); // Console debug msg
		} else {
			MsgHandler.debugMsg(disableDebugModeDebugMsg); // Console debug msg
			debugMode = false;
		}
	}

};