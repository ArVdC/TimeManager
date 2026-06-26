package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.CmdsFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SpeedHandler;

public class TmReload extends MainTM {

	/**
	 * CMD /tm reload [all|config|lang|cmds]
	 */
	public static void cmdReload(CommandSender sender, String whatToReload) {

		// When do reload the config.yml file
		if (whatToReload.equalsIgnoreCase(ARG_CONFIG) || whatToReload.equalsIgnoreCase("conf") || whatToReload.equalsIgnoreCase("cfg") || whatToReload.equalsIgnoreCase("all")) {
			CfgFileHandler.loadConfig(ARG_RE);
			// Re-synchronize all the worlds based on a server constant point
			TmResync.cmdResync(Bukkit.getServer().getConsoleSender(), ARG_ALL);
			// Detect if this world needs to change its speed value
			SpeedHandler.speedScheduler(ARG_ALL);
			// 'config.yml is reloaded' notification
			MsgHandler.playerAdminMsg(sender, cfgFileReloadMsg); // Player final msg (in case)
			MsgHandler.infoMsg(cfgFileReloadMsg); // Console final msg (always)
			return;
		}		
		// When do reload the lang.yml file
		if (whatToReload.equalsIgnoreCase(ARG_LANG) || whatToReload.equalsIgnoreCase("languages") || whatToReload.equalsIgnoreCase("lg") || whatToReload.equalsIgnoreCase("all")) {
			LgFileHandler.loadLang(ARG_RE);
			// 'lang.yml is reloaded' notification
			MsgHandler.playerAdminMsg(sender, lgFileReloadMsg); // Player final msg (in case)
			MsgHandler.infoMsg(lgFileReloadMsg); // Console final msg (always)
			return;
		}		
		// When do reload the cmds.yml file
		if (whatToReload.equalsIgnoreCase(ARG_CMDS) || whatToReload.equalsIgnoreCase("commands") || whatToReload.equalsIgnoreCase("cmd") || whatToReload.equalsIgnoreCase("all")) {
			CmdsFileHandler.loadCmds(ARG_RE);
			// 'cmds.yml is reloaded' notification
			MsgHandler.playerAdminMsg(sender, cmdsFileReloadMsg); // Player final msg (in case)
			MsgHandler.infoMsg(cmdsFileReloadMsg); // Console final msg (always)
			return;
		}		
		// Else, return an error and help message
		MsgHandler.cmdErrorMsg(sender, wrongYmlMsg, "reload");
	}

};