package net.vdcraft.arvdc.timemanager.mainclass;

import net.vdcraft.arvdc.timemanager.MainTM;

public class DebugModeHandler extends MainTM {

	/**
	 * Activate/Deactivate the display of the colored debug messages in the console
	 */
	public static void debugModeOnOff() {
		// #1. If the node exists, be sure that is a boolean
		if (MainTM.getInstance().getConfig().getKeys(false).contains(CF_DEBUGMODE)) {
			// #1.A. If the value is not 'true', set it on 'false' ...
			if (!(MainTM.getInstance().getConfig().getString(CF_DEBUGMODE).equalsIgnoreCase(ARG_TRUE))) {
				MainTM.getInstance().getConfig().set(CF_DEBUGMODE, ARG_FALSE);
			// #1.B. ... otherwise confirm the 'true' value
			} else {
				MainTM.getInstance().getConfig().set(CF_DEBUGMODE, ARG_TRUE);
			}
		// #2. If the node does not exist, create it and set it on 'false'
		} else {
			MainTM.getInstance().getConfig().set(CF_DEBUGMODE, ARG_FALSE);
		}
		// #3. Toggle the debug mode	
		String debugOnOff = MainTM.getInstance().getConfig().getString(CF_DEBUGMODE);
		if (debugOnOff.equalsIgnoreCase(ARG_TRUE)) {
			debugMode = true;
			MsgHandler.debugMsg(enableDebugModeDebugMsg); // Console debug msg
		} else {
			MsgHandler.debugMsg(disableDebugModeDebugMsg); // Console debug msg
			debugMode = false;
		}
	}

	/**
	 * Restore the location of the debugMode node in the config.yml
	 */
	public static void debugModeNodeRelocate() {
		String trueOrFalse = MainTM.getInstance().getConfig().getString(CF_DEBUGMODE);
		MainTM.getInstance().getConfig().set(CF_DEBUGMODE, null);
		MainTM.getInstance().getConfig().set(CF_DEBUGMODE, trueOrFalse);
	}
};