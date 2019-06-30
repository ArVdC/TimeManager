package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;

public class TmSetUpdateMsgSrc extends MainTM {

    /**
     * CMD /tm set updatesrc [bukkit|spigot|github]
     */
    public static void cmdSetUpdateSrc(CommandSender sender, String updateSource) {

	// Check if the argument matches what is expected
	if (updateSource.equalsIgnoreCase(CF_BUKKIT)
		|| updateSource.equalsIgnoreCase(CF_CURSE)
		|| updateSource.equalsIgnoreCase(CF_TWITCH)
		|| updateSource.equalsIgnoreCase(CF_SPIGOT)
		|| updateSource.equalsIgnoreCase(CF_PAPER)
		|| updateSource.equalsIgnoreCase(CF_GITHUB)) {

	    // Format the configuration value
	    updateSource = updateSource.replaceFirst(".", (updateSource.charAt(0) + "").toUpperCase());
	    MainTM.getInstance().getConfig().set(CF_UPDATEMSGSRC, updateSource);

	    Bukkit.getLogger().info(prefixTM + " " + updateEnableCheckMsg + " " + updateSource + " as the source."); // Console final msg (always)
	    if (sender instanceof Player)
		sender.sendMessage(prefixTMColor + " " + updateEnableCheckMsg + " §e" + updateSource + "§r as the source."); // Player final msg (in case)

	} // Disable auto update if the updateSource is false, void or unknown
	else { 
	    MainTM.getInstance().getConfig().set(CF_UPDATEMSGSRC, "");

	    Bukkit.getLogger().info(prefixTM + " " + updateDisableCheckMsg + " disable."); // Console final msg (always)
	    if (sender instanceof Player)
		sender.sendMessage(prefixTMColor + " " + updateDisableCheckMsg + "§e disable§r."); // Player final msg (in case)
	}
	// Save changes in config.yml
	MainTM.getInstance().saveConfig();
    }

};