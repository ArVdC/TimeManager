package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;

public class TmHelp extends MainTM {
		
	/** 
	 * CMD /tm help [cmd]
	 */
	public static boolean cmdHelp(CommandSender sender, String[] args) {
		int argsNb = args.length;
		String specificCmdMsg = "";
		// /tm help set [arg]
		if(argsNb >= 3) {
			if(args[1].equalsIgnoreCase("set")) {
				String subCmd = args[2].toLowerCase();
				// /tm help set debugMode
				if(subCmd.contains("debug")) {
					specificCmdMsg = setDebugHelpMsg; // Help msg (in case of 2 args)
				}
				// /tm help set deflang
				if(subCmd.contains("deflang")) {
					specificCmdMsg = setDefLangHelpMsg; // Help msg (in case of 2 args)
				}
				// /tm help set multilang
				else if(subCmd.contains("multilang")) {
					specificCmdMsg = setMultilangHelpMsg; // Help msg (in case of 2 args)
				}
				// /tm help set refreshrate
				else if(subCmd.contains("refreshrate")) {
					specificCmdMsg = setRefreshRateHelpMsg; // Help msg (in case of 2 args)
				}
				// /tm help set sleep
				else if(subCmd.contains("sleep") || args[2].equalsIgnoreCase("sleepUntilDawn")) { // alias for v1.0 compatibility
					specificCmdMsg = setSleepHelpMsg; // Help msg (in case of 2 args)
				}
				// /tm help set speed
				else if(subCmd.contains("speed")) {
					specificCmdMsg = setSpeedHelpMsg; // Help msg (in case of 2 args)
				}
				// /tm help set start
				else if(subCmd.contains("start")) {
					specificCmdMsg = setStartHelpMsg; // Help msg (in case of 2 args)
				}
				// /tm help set sync
				else if(subCmd.contains("sync") || args[2].contains("synchro")) {// alias for commodity
					specificCmdMsg = setSyncHelpMsg; // Help msg (in case of 2 args)
				}
				// /tm help set time
				else if(subCmd.contains("time")) {
					specificCmdMsg = setTimeHelpMsg; // Help msg (in case of 2 args)
				}
			}
		} else if(argsNb >= 2) {	
			String subCmd = args[1].toLowerCase();
			// /tm help reload
			if(subCmd.contains("reload")) {
				specificCmdMsg = reloadHelpMsg; // Help msg (in case of 1 arg)
			}
			// /tm help resync
			else if(subCmd.contains("resync")) {
				specificCmdMsg = resyncHelpMsg; // Help msg (in case of 1 arg)
			}
			// /tm help checktimers
			else if(subCmd.contains("checktimers") || subCmd.contains("servtime")) { // alias for v1.0 compatibility
				specificCmdMsg = checktimersHelpMsg; // Help msg (in case of 1 arg)
			}
			// /tm help checksql
			else if(subCmd.contains("checksql") || subCmd.contains("sqlcheck")) { // alias for v1.0 compatibility
				specificCmdMsg = checkqlHelpMsg; // Help msg (in case of 1 arg)
			}
			// /tm help set <null>   
			else if(subCmd.contains("set")) {
				specificCmdMsg = missingSetArgHelpMsg; // Help msg (in case of 1 arg)
			}
			// Maybe someone could forget the 'set' part, so think of its place
			else if(subCmd.contains("debug") || subCmd.contains("deflang") || subCmd.contains("multilang") || subCmd.contains("refreshrate") || subCmd.contains("sleep") || subCmd.contains("speed") || subCmd.contains("start") || subCmd.contains("sync") || subCmd.contains("time")) {
				Bukkit.dispatchCommand(sender, "tm help set " + subCmd); // retry with correct arguments
				return true;
			}
		}
		// Display Help header
		sender.sendMessage(headerHelp); // Final msg (always)
		// Display specific cmd msg
		if(!(specificCmdMsg.equals(""))) {
			sender.sendMessage(specificCmdMsg);
			return true;
			}
		// Else, display basic help msg and the list of cmds from plugin.yml
		sender.sendMessage(helpHelpMsg); // Final msg (always)
		return false;
	};

	/**
	 * Display an error and its associated help message
	 */	
		public static void sendErrorMsg(CommandSender sender, String msgError, String cmdHelp) {
	    MainTM.getInstance();
	    if(sender instanceof Player) {
			sender.sendMessage(prefixTMColor + " §c" + msgError); // Player error msg (in case is player)
	    }
		Bukkit.getLogger().warning(prefixTM + " " + msgError); // Console error msg (always)
        Bukkit.dispatchCommand(sender, "tm help " + cmdHelp); // Sender help msg (always)
    };
 
}