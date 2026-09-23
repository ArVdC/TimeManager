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

		// 'all' has to fall through every block below, so it can't stop at the first one
		boolean all = whatToReload.equalsIgnoreCase(ARG_ALL);
		boolean reloaded = false;

		// When do reload the config.yml file
		if (all || whatToReload.equalsIgnoreCase(ARG_CONFIG) || whatToReload.equalsIgnoreCase("conf") || whatToReload.equalsIgnoreCase("cfg")) {
			CfgFileHandler.loadConfig(ARG_RE);
			// Re-synchronize all the worlds based on a server constant point
			TmResync.cmdResync(Bukkit.getServer().getConsoleSender(), ARG_ALL);
			// Detect if this world needs to change its speed value
			SpeedHandler.speedScheduler(ARG_ALL);
			// 'config.yml is reloaded' notification
			MsgHandler.playerAdminMsg(sender, cfgFileReloadMsg); // Player final msg (in case)
			MsgHandler.infoMsg(cfgFileReloadMsg); // Console final msg (always)
			reloaded = true;
		}		
		// When do reload the lang.yml file
		if (all || whatToReload.equalsIgnoreCase(ARG_LANG) || whatToReload.equalsIgnoreCase("languages") || whatToReload.equalsIgnoreCase("lg")) {
			LgFileHandler.loadLang(ARG_RE);
			// 'lang.yml is reloaded' notification
			MsgHandler.playerAdminMsg(sender, lgFileReloadMsg); // Player final msg (in case)
			MsgHandler.infoMsg(lgFileReloadMsg); // Console final msg (always)
			reloaded = true;
		}		
		// When do reload the cmds.yml file
		if (all || whatToReload.equalsIgnoreCase(ARG_CMDS) || whatToReload.equalsIgnoreCase("commands") || whatToReload.equalsIgnoreCase("cmd")) {
			CmdsFileHandler.loadCmds(ARG_RE);
			// 'cmds.yml is reloaded' notification
			MsgHandler.playerAdminMsg(sender, cmdsFileReloadMsg); // Player final msg (in case)
			MsgHandler.infoMsg(cmdsFileReloadMsg); // Console final msg (always)
			reloaded = true;
		}		
		if (reloaded) return;

		// Else, return an error and help message
		MsgHandler.cmdErrorMsg(sender, wrongYmlMsg, "reload");
	}

};