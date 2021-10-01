package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class TmSetPlayerOffset extends MainTM {

	/**
	 * CMD /tm set playerOffset [-23999 → 23999] [all|player]
	 */
	public static void cmdSetPlayerOffset(CommandSender sender, long offset, String player, boolean timechange) {
		
		// Limit the offset to one day
		offset = offset % 24000;
		
		// Modify all players time
		if (player.equalsIgnoreCase(ARG_ALL)) {
			// Relaunch this for each world
			for (Player listedPlayer : Bukkit.getOnlinePlayers()) {
				String listedPlayerName = listedPlayer.getName();
				cmdSetPlayerOffset(sender, offset, listedPlayerName, timechange);
			}
		} // Else, if the string argument is an online player, modify a single player time
		else {
			Player p = Bukkit.getPlayer(player);
			if (p != null) {
				if (offset != 0) {
					p.setPlayerTime(offset, true);
					// Notifications
					long tick = p.getPlayerTime();
					tick = ValuesConverter.correctDailyTicks(tick);
					String time = ValuesConverter.formattedTimeFromTick(tick);
					if (timechange) {
						MsgHandler.infoMsg(playerTimeChgMsg1 + " " + player + " " + playerTimeChgMsg2 + " tick #" + tick + " (" + time + ")."); // Console final msg (always)
						MsgHandler.playerAdminMsg(sender, playerTimeChgMsg1 + " §e" + player + "§r " + playerTimeChgMsg2 + " §etick #" + tick + " §r(§e" + time + "§r)."); // Player final msg (in case)	
					} else {
						MsgHandler.infoMsg(playerOffsetChgMsg1 + " " + player + " " + playerTimeChgMsg2 + " " + offset + "."); // Console final msg (always)
						MsgHandler.playerAdminMsg(sender, playerOffsetChgMsg1 + " §e" + player + "§r " + playerTimeChgMsg2 + " §e" + offset + "§r."); // Player final msg (in case)
					}
				} else {
					p.resetPlayerTime();
					// Notifications
					if (timechange) {
						MsgHandler.infoMsg(playerTimeChgMsg1 + " " + player + " " + playerTimeResetMsg2); // Console final msg (always)
						MsgHandler.playerAdminMsg(sender, playerTimeChgMsg1 + " §e" + player + "§r " + playerTimeResetMsg2); // Player final msg (in case)	
					} else {
						MsgHandler.infoMsg(playerOffsetChgMsg1 + " " + player + " " + playerTimeResetMsg2); // Console final msg (always)
						MsgHandler.playerAdminMsg(sender, playerOffsetChgMsg1 + " §e" + player + "§r " + playerTimeResetMsg2); // Player final msg (in case)
					}
				}
			} // Else, return an error and help message
			else {
				if (timechange) {
					MsgHandler.cmdErrorMsg(sender, MainTM.playerFormatMsg, MainTM.CMD_SET + " " + CMD_SET_PLAYERTIME);
				} else {
					MsgHandler.cmdErrorMsg(sender, MainTM.playerFormatMsg, MainTM.CMD_SET + " " + CMD_SET_PLAYEROFFSET);
				}
			}
		}
	}

};