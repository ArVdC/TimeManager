package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;

public class TmSetDefLang extends MainTM {

	/**
	 * CMD /tm set defLang [lg_LG]
	 */
	public static void cmdDefLg(CommandSender sender, String newLang) {

		// Check if the argument matches what is expected
		if (MainTM.getInstance().langConf.getConfigurationSection(LG_LANGUAGES).getKeys(false).contains(newLang)) {
			MainTM.getInstance().langConf.set(LG_DEFAULTLANG, newLang);
			LgFileHandler.SaveLangYml();
			serverLang = newLang;
			MsgHandler.infoMsg(defLangCheckMsg + " " + newLang + "."); // Console final msg (always)
			MsgHandler.playerAdminMsg(sender, defLangCheckMsg + " §e" + newLang + "§r."); // Player final msg (in case)
		}
		// Else, return an error and help message
		else {
			MsgHandler.cmdErrorMsg(sender, MainTM.wrongLangMsg, MainTM.CMD_SET + " " + CMD_SET_DEFLANG);
		}
	}

};