package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSpeedHandler;

public class TmReload extends MainTM {

	/** 
	 * CMD /tm reload [all|config|lang]
	 */
	public static void cmdReload(CommandSender sender, String whatToReload) {
		
		// Display the wrong argument message if it stay true at the end
		boolean argOk = true; 
		
		// When do reload the config.yml file
		if(whatToReload.equalsIgnoreCase("config") || whatToReload.contains("conf") || whatToReload.contains("cfg") || whatToReload.contains("all")) {
			CfgFileHandler.loadConfig("re");					
			
			// Re-synchronize all the worlds based on a server constant point
			TmResync.cmdResync(MainTM.getInstance().laConsole, "all");
			// Launch scheduler if is inactive
	    	if (ScheduleIsOn = false) {
	    		WorldSpeedHandler.WorldSpeedModify();
	    	}
			// 'config.yml is reloaded' notification
			if(sender instanceof Player) {
				sender.sendMessage(prefixTMColor + " " + cfgFileReloadMsg); // Player final msg (in case)
			}
			Bukkit.getLogger().info(prefixTM + " " + cfgFileReloadMsg); // Console final msg (always)
			argOk = false; 
		}
		// When do reload the lang.yml file
		if(whatToReload.equalsIgnoreCase("language") || whatToReload.contains("lang") || whatToReload.contains("lg") || whatToReload.contains("all")) {
			LgFileHandler.loadLang("re");
			// 'lang.yml is reloaded' notification		
			if(sender instanceof Player) {
				sender.sendMessage(prefixTMColor + " " + lgFileReloadMsg); // Player final msg (in case)
			}
			Bukkit.getLogger().info(prefixTM + " " + lgFileReloadMsg); // Console final msg (always)
			argOk = false; 			
		}
		// Else, return an error and help message
		if( argOk == true) {
			TmHelp.sendErrorMsg(sender, wrongYmlMsg, "reload");
		}

	};

}