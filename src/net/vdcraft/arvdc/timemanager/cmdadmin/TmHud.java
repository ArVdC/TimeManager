package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ActionBarHandler;

/**
 * /tm hud [on|off]
 *
 * Per-player toggle for the optional ActionBar HUD. Players use this to opt
 * out (e.g. they prefer to keep the action bar clear for other plugins).
 * Default opt-out state matches {@code hud.actionbar.enabled} in config —
 * if the HUD is globally off no one sees it regardless.
 *
 * Opt-out is kept in memory only and resets on restart, by design — opt-out
 * is a quality-of-life toggle, not a persistent setting worth a data file.
 */
public class TmHud extends MainTM {

	public static void cmdHud(CommandSender sender, String onOffArg) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "/tm hud is player-only.");
			return;
		}
		Player p = (Player) sender;

		boolean off;
		if (onOffArg == null || onOffArg.isEmpty() || onOffArg.equalsIgnoreCase("toggle")) {
			off = !ActionBarHandler.isOptedOut(p.getUniqueId());
		} else if (onOffArg.equalsIgnoreCase("on") || onOffArg.equalsIgnoreCase("true")) {
			off = false;
		} else if (onOffArg.equalsIgnoreCase("off") || onOffArg.equalsIgnoreCase("false")) {
			off = true;
		} else {
			p.sendMessage(ChatColor.RED + "Usage: /tm hud [on|off|toggle]");
			return;
		}

		ActionBarHandler.setOptOut(p.getUniqueId(), off);
		p.sendMessage(ChatColor.GOLD + "ActionBar HUD is now "
				+ (off ? ChatColor.RED + "OFF" : ChatColor.GREEN + "ON")
				+ ChatColor.GOLD + " for you.");
	}
}
