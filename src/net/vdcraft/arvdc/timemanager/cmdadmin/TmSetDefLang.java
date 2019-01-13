package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;

public class TmSetDefLang extends MainTM {

    /**
     * CMD /tm set deflang [lg_LG]
     */
    public static void cmdDefLg(CommandSender sender, String newLang) {

	// Check if the argument matches what is expected
	if (MainTM.getInstance().langConf.getConfigurationSection("languages").getKeys(false).contains(newLang)) {
	    MainTM.getInstance().langConf.set("defaultLang", newLang);
	    LgFileHandler.SaveLangYml();
	    serverLang = newLang;
	    Bukkit.getLogger().info(prefixTM + " " + defLangCheckMsg + " " + newLang + "."); // Console final msg (always)
	    if (sender instanceof Player) {
		sender.sendMessage(prefixTMColor + " " + defLangCheckMsg + " §e" + newLang + "§r."); // Player final msg (in case)
	    }
	}
	// Else, return an error and help message
	else {
	    TmHelp.sendErrorMsg(sender, MainTM.wrongLangMsg, "set deflang");
	}
    }

};