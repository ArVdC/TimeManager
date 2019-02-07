package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;

public class TmSetMultiLang extends MainTM {

    /**
     * CMD /tm set multilang [true|false]
     */
    public static void cmdMultiLg(CommandSender sender, String onOff) {

	// Check if the argument matches what is expected
	if (onOff.equalsIgnoreCase("true") || onOff.equalsIgnoreCase("false")) {
	    MainTM.getInstance().langConf.set(CF_USEMULTILANG, onOff);
	    LgFileHandler.SaveLangYml();
	    if (onOff.equalsIgnoreCase("true")) {
		Bukkit.getLogger().info(prefixTM + " " + multiLangIsOnMsg); // Console final msg (always)
		if (sender instanceof Player) {
		    sender.sendMessage(prefixTMColor + " " + multiLangIsOnMsg); // Player final msg (in case)
		}
	    } else if (onOff.equalsIgnoreCase("false")) {
		Bukkit.getLogger().info(prefixTM + " " + multiLangIsOffMsg); // Console final msg (always)
		if (sender instanceof Player) {
		    sender.sendMessage(prefixTMColor + " " + multiLangIsOffMsg); // Player final msg (in case)
		}
	    }
	}
	// Else, return an error and help message
	else {
	    TmHelp.sendErrorMsg(sender, MainTM.isNotBooleanMsg, "set multilang");
	}
    }

};