package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class TmSetRefreshRate extends MainTM {

	/**
	 * CMD /tm set refreshRate [ticks]
	 */
	public static void cmdRefRate(CommandSender sender, Integer refreshRate) {

		// Adapt wrong values
		refreshRate = ValuesConverter.correctRefreshRate(refreshRate);

		MainTM.getInstance().getConfig().set(CF_REFRESHRATE, refreshRate);
		MainTM.getInstance().saveConfig();

		// Notifications
		MsgHandler.playerAdminMsg(sender, refreshRateMsg + " §e" + refreshRate + " ticks§r."); // Notify the player (in case)
		MsgHandler.infoMsg(MsgHandler.prefixTM + " " + refreshRateMsg + " " + refreshRate + " ticks."); // Notify the console (always)
	}

};