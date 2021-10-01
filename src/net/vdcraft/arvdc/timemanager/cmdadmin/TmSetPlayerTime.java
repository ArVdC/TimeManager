package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class TmSetPlayerTime extends MainTM {

	/**
	 * CMD /tm set playerTime [tick|daypart|HH:mm:ss|reset] [all|player]
	 */
	public static void cmdSetPlayerTime(CommandSender sender, long tick, String player, boolean reset) {

		// Adapt wrong values in the arg
		tick = ValuesConverter.correctDailyTicks(tick);

		// Calculate an offset based on wanted tick
		Player p = Bukkit.getPlayer(player);
		World w = p.getWorld();
		long worldtime = w.getTime(); 
		long offset = tick - worldtime;
		if (reset) offset = 0L;

		// Use the offset method
		TmSetPlayerOffset.cmdSetPlayerOffset(sender, offset, player, true);
	}

};