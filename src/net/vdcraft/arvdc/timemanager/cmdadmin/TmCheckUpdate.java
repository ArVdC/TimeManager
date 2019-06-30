package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.UpdateHandler;

public class TmCheckUpdate extends MainTM {

    /**
     * CMD /tm checkupdate [server]
     */
    public static void cmdCheckUpdate(CommandSender sender, String updateSource) {
	if (updateSource == null) { // If the argument is empty, try to get the value from the configuration
	    if	(MainTM.getInstance().getConfig().getKeys(false).contains(CF_UPDATEMSGSRC))
		updateSource = MainTM.getInstance().getConfig().getString(CF_UPDATEMSGSRC).toLowerCase();
	}
	// If the argument still doesn't exist or doesn't match with any expected case, use "CF_BUKKIT" as the default value
	if ((!updateSource.equalsIgnoreCase(CF_CURSE))
		&& (!updateSource.equalsIgnoreCase(CF_TWITCH))
		&& (!updateSource.equalsIgnoreCase(CF_SPIGOT))
		&& (!updateSource.equalsIgnoreCase(CF_PAPER))
		&& (!updateSource.equalsIgnoreCase(CF_GITHUB))) {
	    updateSource = CF_BUKKIT;
	}
	UpdateHandler.checkForUpdate(sender, updateSource, false);
    }

    // In case of missing arg
    public static void cmdCheckUpdate(CommandSender sender) {
	cmdCheckUpdate(sender, null);
    }

};