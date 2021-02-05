package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;

public class TmSetDefLang extends MainTM {

	/**
	 * CMD /tm set deflang [lg_LG]
	 */
	public static void cmdDefLg(CommandSender sender, String newLang) {

		// Check if the argument matches what is expected
		if (MainTM.getInstance().langConf.getConfigurationSection(CF_LANGUAGES).getKeys(false).contains(newLang)) {
			MainTM.getInstance().langConf.set(CF_DEFAULTLANG, newLang);
			LgFileHandler.SaveLangYml();
			serverLang = newLang;
			MsgHandler.infoMsg(defLangCheckMsg + " " + newLang + "."); // Console final msg (always)
			MsgHandler.playerMsg(sender, defLangCheckMsg + " §e" + newLang + "§r."); // Player final msg (in case)
		}
		// Else, return an error and help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongLangMsg, "set deflang");
		}
	}

};