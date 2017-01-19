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
		if(argsNb == 2) {		
			// /tm help reload
			if(args[1].equalsIgnoreCase("reload")) {
				sender.sendMessage(prefixTMColor + " " + reloadHelpMsg); // Final msg (in case of arg)
				return true;
			}
			// /tm help resync
			else if(args[1].equalsIgnoreCase("resync")) {
				sender.sendMessage(prefixTMColor + " " + resyncHelpMsg); // Final msg (in case of arg)
				return true;
			}
			// /tm help servtime
			else if(args[1].equalsIgnoreCase("servtime")) {
				sender.sendMessage(prefixTMColor + " " + servtimeHelpMsg); // Final msg (in case of arg)
				return true;
			}
			// /tm set <null>   
			else if(args[1].equalsIgnoreCase("set")) {
				sender.sendMessage(prefixTMColor + " §c" + missingSetArgHelpMsg);  // Final msg (in case of arg)
				return true;
			}
		}
		// /tm help set [arg]
		else if(argsNb == 3) {
			if(args[1].equalsIgnoreCase("set")) {
				// /tm help set multilang
				if(args[2].equalsIgnoreCase("multilang")) {
					sender.sendMessage(prefixTMColor + " " + setMultilangHelpMsg); // Final msg (in case of arg)
					return true;
				}
				// /tm help set deflang
				else if(args[2].equalsIgnoreCase("deflang")) {
					sender.sendMessage(prefixTMColor + " " + setDefLangHelpMsg); // Final msg (in case of arg)
					return true;
				}
				// /tm help set refreshrate
				else if(args[2].equalsIgnoreCase("refreshrate")) {
					sender.sendMessage(prefixTMColor + " " + setRefreshRateHelpMsg); // Final msg (in case of arg)
					return true;
				}
				// /tm help set speed
				else if(args[2].equalsIgnoreCase("speed")) {
					sender.sendMessage(prefixTMColor + " " + setSpeedHelpMsg); // Final msg (in case of arg)
					return true;
				}
				// /tm help set start
				else if(args[2].equalsIgnoreCase("start")) {
					sender.sendMessage(prefixTMColor + " " + setStartHelpMsg); // Final msg (in case of arg
					return true;
				}
				// /tm help set time
				else if(args[2].equalsIgnoreCase("time")) {
					sender.sendMessage(prefixTMColor + " " + setTimeHelpMsg); // Final msg (in case of arg)
					return true;
				}
			}
		}
		sender.sendMessage(prefixTMColor + " " + helpHelpMsg); // Final msg (always)
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