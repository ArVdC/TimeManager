package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.NowItemHandler;

/**
 * /tm nowitem [player]
 *
 * Gives a pocket-watch (custom {@link org.bukkit.Material#CLOCK}) to the
 * target player. The item is recognised by {@link NowItemHandler} on
 * right-click and runs the {@code /now} action.
 */
public class TmNowItem extends MainTM {

	public static void cmdNowItem(CommandSender sender, String targetName) {
		Player target = null;
		if (targetName == null || targetName.isEmpty()) {
			if (sender instanceof Player) target = (Player) sender;
		} else {
			target = Bukkit.getPlayerExact(targetName);
		}
		if (target == null) {
			sender.sendMessage(ChatColor.RED + "Target player not found or offline.");
			return;
		}
		target.getInventory().addItem(NowItemHandler.createItem());
		sender.sendMessage(ChatColor.GOLD + "Gave a Pocket Watch to " + ChatColor.YELLOW + target.getName() + ChatColor.GOLD + ".");
		if (sender != target) {
			target.sendMessage(ChatColor.GOLD + "You received a " + ChatColor.YELLOW + "Pocket Watch" + ChatColor.GOLD + ".");
		}
	}
}
