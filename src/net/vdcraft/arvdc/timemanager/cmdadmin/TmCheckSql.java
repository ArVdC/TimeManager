package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SqlHandler;

public class TmCheckSql extends MainTM {

	/**
	 * CMD /tm checkSql
	 */
	public static void cmdSqlcheck(CommandSender sender) {

		BukkitRunnable asyncCmdSqlcheck = new BukkitRunnable() {
			@Override
			public void run() {
				String isSslOn = " without";
				if (ssl.equalsIgnoreCase(ARG_TRUE)) {
					isSslOn = "";
				}
				// Start notifications
				MsgHandler.playerAdminMsg(sender, tryReachHostMsg + " \"§e" + host + "§r\" on port §e#" + port + "§r" + isSslOn + " using ssl."); // Player final msg (in case)
				MsgHandler.infoMsg(tryReachHostMsg + " \"" + host + "\" on port #" + port + isSslOn + " using ssl."); // Console final msg (always)
				// Test the connection
				boolean okOrNot = SqlHandler.connectionToHostIsAvailable(true);
				// Notifications
				if (sender instanceof Player) { // Console messages are displayed by 'SqlHandler.connectionToHostIsAvailable()'
					if (okOrNot == true) { // If the connection is ok
						MsgHandler.playerAdminMsg(sender, "The mySQL host \"§e" + host + "§r\" " + connectionOkMsg + " §e#" + port + "§r" + isSslOn + " using ssl."); // Player final msg (in case)
						SqlHandler.closeConnection("Host"); // Stop the connection
					} else { // If the connection is not ok
						MsgHandler.playerAdminMsg(sender, connectionFailMsg + " \"§e" + host + "§r\". " + checkLogMsg); // Player final msg (in case)
					}
				}
			}
		};

		asyncCmdSqlcheck.runTaskAsynchronously(MainTM.getInstance());

	}

};