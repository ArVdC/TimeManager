package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;

public class TmSetUpdateMsgSrc extends MainTM {

	/**
	 * CMD /tm set update [none|bukkit|spigot|github]
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

			MsgHandler.infoMsg(updateEnableCheckMsg + " " + updateSource + " as the source."); // Console final msg (always)
			MsgHandler.playerMsg(sender, updateEnableCheckMsg + " §e" + updateSource + "§r as the source."); // Player final msg (in case)

		} // Disable auto update if the updateSource is false, void or unknown
		else { 
			MainTM.getInstance().getConfig().set(CF_UPDATEMSGSRC, defUpdateMsgSrc);

			MsgHandler.infoMsg(updateDisableCheckMsg + " disable."); // Console final msg (always)
			MsgHandler.playerMsg(sender, updateDisableCheckMsg + "§e disable§r."); // Player final msg (in case)
		}
		// Save changes in config.yml
		MainTM.getInstance().saveConfig();
	}

};