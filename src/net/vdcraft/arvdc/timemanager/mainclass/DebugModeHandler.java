package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;

import net.vdcraft.arvdc.timemanager.MainTM;

public class DebugModeHandler extends MainTM {
	
	/** 
	 * Display or not the colored debug messages in the console
	 */	   
    public static void debugModeOnOff() {
    	if(MainTM.getInstance().getConfig().getKeys(false).contains("debugMode")) { // If the option exists and is not 'true', set it on false
    		if(!(MainTM.getInstance().getConfig().getString("debugMode").equals("true"))) {
    			MainTM.getInstance().getConfig().set("debugMode", "false");
    		}
	    } else { // If the option doesn't exist, create it and set it on 'false'
	    	MainTM.getInstance().getConfig().set("debugMode", "false");
	    }
		String debugOnOff = MainTM.getInstance().getConfig().getString("debugMode");
    	if(debugOnOff.equalsIgnoreCase("true")) {
    		debugMode = true;
    		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + enableDebugModeDebugMsg); // Console debug msg
    	} else {
    		if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + disableDebugModeDebugMsg); // Console debug msg
    		debugMode = false;
    	}
    };
}