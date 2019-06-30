package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;

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
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + enableDebugModeDebugMsg); // Console debug msg (always)
		if (sender instanceof Player) {
		    sender.sendMessage(prefixDebugMode + " " + enableDebugModeDebugMsg); // Player debug msg (in case)
		}
	    } else if (onOff.equalsIgnoreCase("false")) {
		debugMode = false;
		Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + disableDebugModeDebugMsg); // Console debug msg (always)
		if (sender instanceof Player) {
		    sender.sendMessage(prefixDebugMode + " " + disableDebugModeDebugMsg); // Player debug msg (in case)
		}
	    }
	}
	// Else, return an error and help message
	else {
	    TmHelp.sendErrorMsg(sender, MainTM.isNotBooleanMsg, "set debugmode");
	}
    }

};