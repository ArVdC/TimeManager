package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.RestrainValuesHandler;

public class TmSetRefreshRate extends MainTM {

	/** 
	 * CMD /tm set refreshrate [ticks]
	 */
	public static void cmdRefRate(CommandSender sender, Integer refreshRate) {

		// Adapt wrong values
		refreshRate = RestrainValuesHandler.returnCorrectRate(refreshRate);
		
		MainTM.getInstance().getConfig().set("refreshRate", refreshRate);
		MainTM.getInstance().saveConfig();
		
		// Notifications
        if(sender instanceof Player) {
        	sender.sendMessage(prefixTMColor + " " + refreshRateMsg + " §e" + refreshRate + " ticks§r."); // Notify the player (in case)
        }
        Bukkit.getLogger().info(prefixTM + " " + refreshRateMsg + " " + refreshRate + " ticks."); // Notify the console (always)
	};
	
}