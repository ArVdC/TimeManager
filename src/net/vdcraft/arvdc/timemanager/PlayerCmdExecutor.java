/***********************
 **** PLAYER COMMAND ****
 ***********************/

package net.vdcraft.arvdc.timemanager;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.cmdplayer.NowMsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class PlayerCmdExecutor implements CommandExecutor {

	/*****************
	 ***** EVENT *****
	 *****************/

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		// #1. Check if sender is a Player or return false
		if (!(sender instanceof Player)) {
			MsgHandler.warnMsg(MainTM.nonPlayerSenderMsg);
			if (!(sender instanceof BlockCommandSender))
				Bukkit.dispatchCommand(sender, MainTM.CMD_TM + " " + MainTM.CMD_HELP + " " + MainTM.CMD_TMNOW); // retry with correct arguments
			return true;
		}

		// #2. Check if player has permission to use the /now command
		if (!sender.hasPermission(MainTM.PERM_NOW) && !sender.isOp()) {
			return false;
		}

		// #3. Set some basic variables
		Integer nbArgs = args.length;
		Player p = ((Player) sender);
		World w = p.getWorld(); // Set the world the player stands in as default value
		String display = MainTM.getInstance().langConf.getString(MainTM.CF_DEFAULTDISPLAY);
		List<String> displays = Arrays.asList(MainTM.ARG_MSG, MainTM.ARG_TITLE, MainTM.ARG_ACTIONBAR);
		List<String> worlds = CfgFileHandler.setAnyListFromConfig(MainTM.CF_WORLDSLIST);

		// #4. Names with spaces : Hack it only for players and commandblocks (Useless since MC 1.13)
		if (MainTM.serverMcVersion < MainTM.maxMcVForTabCompHack) {
			int nb = nbArgs - 1 ;
			while (nb >= 0) {
				args[nb] = CreateSentenceCommand.restoreSpacesInString(args[nb]);
				nb--;
			}
		}

		// #5. Send dev msg 
		MsgHandler.devMsg("Command §e/" + label + "§9 launched with §e" + nbArgs + "§9 arguments :"); // Console dev msg
		int n = 0;
		while (n < nbArgs) MsgHandler.devMsg("[" + (n) + "] : §e" + args[n++]); // Console dev msg

		// #6. If there is no argument, send default arguments
		if (nbArgs == 0) {
			NowMsgHandler.sendNowMsg(sender);
			return true;
		}
		// #7. If player has no permission to use any arguments, send default arguments
		else if (!p.hasPermission(MainTM.PERM_NOW_DISPLAY) && !p.hasPermission(MainTM.PERM_NOW_WORLD) && !sender.isOp()) {
			NowMsgHandler.sendNowMsg(sender);
			return true;
		}

		// #8. If player has only permission to choose the display
		else if (p.hasPermission(MainTM.PERM_NOW_DISPLAY) && !p.hasPermission(MainTM.PERM_NOW_WORLD) && !sender.isOp()) {
			if (displays.contains(args[0])) { // If the display argument is correct, send it
				display = args[0];
				NowMsgHandler.sendNowMsg(sender, display);
				return true;
			} else { // Else, send default display and world arguments
				NowMsgHandler.sendNowMsg(sender);
				return true;
			}
		}

		// #9. If player has only permission to choose the world
		if (!p.hasPermission(MainTM.PERM_NOW_DISPLAY) && p.hasPermission(MainTM.PERM_NOW_WORLD) && !sender.isOp()) {
			switch (nbArgs) {
			case 1 : // If there is only one arg
				if (worlds.contains(args[0])) { // If the display argument is correct, send it
					w = Bukkit.getServer().getWorld(args[0]);
					NowMsgHandler.sendNowMsg(sender, w);
					return true;
				} else { // Else, send both default display and world arguments
					NowMsgHandler.sendNowMsg(sender);
					return true;
				}
			default : // If there is more args
				String concatWorldName = ValuesConverter.concatenateNameWithSpaces(sender, args, 0);
				if (worlds.contains(concatWorldName)) {
					w = Bukkit.getServer().getWorld(concatWorldName);
					NowMsgHandler.sendNowMsg(sender, w);
					return true;
				} // Else, send both default display and world arguments
				NowMsgHandler.sendNowMsg(sender);
				return true;
			}
		}

		// #10. If player has all permissions
		if ((p.hasPermission(MainTM.PERM_NOW_DISPLAY) && p.hasPermission(MainTM.PERM_NOW_WORLD)) || sender.isOp()) {
			switch (nbArgs) {
			case 1 : // If there is only one arg
				if (displays.contains(args[0])) { // If the display argument is correct, send it
					display = args[0];
					NowMsgHandler.sendNowMsg(sender, display);
					return true;
				} else if (worlds.contains(args[0])) { // Else, if the world argument is correct, send it
					w = Bukkit.getServer().getWorld(args[0]);
					NowMsgHandler.sendNowMsg(sender, w);
					return true;
				} else { // Else, send both default display & world arguments
					NowMsgHandler.sendNowMsg(sender);
					return true;
				}

			case 2 : // If there is two args
				if (displays.contains(args[0]) && worlds.contains(args[1])) { // If both the display & the world arguments are correct, send them
					display = args[0];
					w = Bukkit.getServer().getWorld(args[1]);
					NowMsgHandler.sendNowMsg(sender, display, w);
					return true;
				} else if (displays.contains(args[0])) { // If the display argument is correct, send it
					display = args[0];
					NowMsgHandler.sendNowMsg(sender, display);
					return true;
				} else if (worlds.contains(args[1])) { // If only the world argument is correct, send it
					w = Bukkit.getServer().getWorld(args[1]);
					NowMsgHandler.sendNowMsg(sender, w);
					return true;
				} else { // Try to use the 2 args to make a world name
					String concatWorldName = ValuesConverter.concatenateNameWithSpaces(sender, args, 0);
					if (worlds.contains(concatWorldName)) {
						w = Bukkit.getServer().getWorld(concatWorldName);
						NowMsgHandler.sendNowMsg(sender, w);
						return true;
					}
				} // Else, send both default display & world arguments
				NowMsgHandler.sendNowMsg(sender);
				return true;

			default : // If there is more args
				if (displays.contains(args[0]) && worlds.contains(args[1])) { // If both the display & the world arguments are correct, send them
					display = args[0];
					w = Bukkit.getServer().getWorld(args[1]);
					NowMsgHandler.sendNowMsg(sender, display, w);
					return true;
				} else if (displays.contains(args[0])) { // If the first argument is the display
					display = args[0];
					String concatWorldName = ValuesConverter.concatenateNameWithSpaces(sender, args, 1);
					if (worlds.contains(concatWorldName)) {
						w = Bukkit.getServer().getWorld(concatWorldName);
						NowMsgHandler.sendNowMsg(sender, display, w);
						return true;
					} // Else, send default world argument
					NowMsgHandler.sendNowMsg(sender, display);
					return true;
				} else { // If the first argument is not the display
					String concatWorldName = ValuesConverter.concatenateNameWithSpaces(sender, args, 0);
					if (worlds.contains(concatWorldName)) {
						w = Bukkit.getServer().getWorld(concatWorldName);
						NowMsgHandler.sendNowMsg(sender, w);
						return true;
					} // Else, send both default display & world arguments
					NowMsgHandler.sendNowMsg(sender);
					return true;
				}
			}
		}
		return false;
	}

};