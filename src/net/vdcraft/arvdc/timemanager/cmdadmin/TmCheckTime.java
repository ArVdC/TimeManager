package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class TmCheckTime extends MainTM {

	/**
	 * CMD /tm checktime [all|server|world]
	 */
	public static void cmdCheckTime(CommandSender sender, String world) {
		// If using a world name in several parts
		if ((sender instanceof Player) || (sender instanceof BlockCommandSender)) world = ValuesConverter.restoreSpacesInString(world);
		// If the arg is "all" or "server", get the server's initial and actual ticks
		if (world.equalsIgnoreCase("all") || world.equalsIgnoreCase("server")) {
			// Get the current server tick
			long currentServerTick = ValuesConverter.getServerTick();
			// Get the current server UTC time in HH:mm:ss
			String currentServerTime = ValuesConverter.getServerTime();
			// Display the reference tick and HH:mm:ss
			if (sender instanceof Player) {
				MsgHandler.playerMsg(sender, serverInitTickMsg + " §e#" + initialTick + " §r(§e" + initialTime + "§r)."); // Final player msg
			} else {
				MsgHandler.infoMsg(serverInitTickMsg + " #" + initialTick + " (" + initialTime + ")."); // Final console msg
			}
			// Get the current tick
			if (sender instanceof Player) {
				MsgHandler.playerMsg(sender, serverCurrentTickMsg + " §e#" + currentServerTick + " §r(§e" + currentServerTime + "§r)."); // Final player msg
			} else {
				MsgHandler.infoMsg(serverCurrentTickMsg + " #" + currentServerTick + " (" + currentServerTime + ")."); // Final console msg
			}
		}
		// If the arg is "all", get each world start tick, actual tick, speeds and sync params
		if (world.equalsIgnoreCase("all")) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdDisplayTime(sender, listedWorld);
			}
		}
		// Else, if the string argument is a listed world, check a single world
		else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			cmdDisplayTime(sender, world);
		}
	}

	/**
	 * Display a world time message
	 */
	private static void cmdDisplayTime(CommandSender sender, String world) {
		Long d = ValuesConverter.elapsedDaysFromTick(Bukkit.getWorld(world).getFullTime());
		String elapsedDays = d.toString();
		String date = ValuesConverter.dateFromElapsedDays(d, "yyyy") + "-" + ValuesConverter.dateFromElapsedDays(d, "mm") + "-" + ValuesConverter.dateFromElapsedDays(d, "dd");
		long listedWorldStartTick = MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + CF_START);
		long listedWorldCurrentTick = Bukkit.getServer().getWorld(world).getTime();
		String listedWorldStartTime = ValuesConverter.formattedTimeFromTick(listedWorldStartTick);
		String formattedUTC = ValuesConverter.formatAsUTC(listedWorldStartTick);
		String listedWorldCurrentTime = ValuesConverter.formattedTimeFromTick(listedWorldCurrentTick);
		double listedWorldDaySpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED);
		String listedWorldNightSpeed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED);
		String listedWorldSync = "";
		String listedWorldSleep = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SLEEP);
		if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equals("false"))
			listedWorldSync = "not ";
		if (listedWorldDaySpeed == realtimeSpeed) { // Display realtime message (speed = 24.00)
			if (sender instanceof Player) {
				MsgHandler.playerMsg(sender, "The world §e" + world + "§r " + worldCurrentElapsedDaysMsg + " §e" + elapsedDays + "§r whole day(s) (§e" + date + "§r)."); // Final console msg
				MsgHandler.playerMsg(sender, "The world §e" + world + "§r " + worldCurrentStartMsg + " §e" + formattedUTC + " §r(§e+" + listedWorldStartTick + " §rticks)."); // Final player msg
				MsgHandler.playerMsg(sender, "The world §e" + world + "§r" + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " §r(§e#" + listedWorldCurrentTick + "§r)."); // Final player msg
				MsgHandler.playerMsg(sender, "The world §e" + world + "§r" + worldCurrentSpeedMsg + " §e" + worldRealSpeedMsg + "§r."); // Final player msg
				MsgHandler.playerMsg(sender, "The world §e" + world + "§r is §e" + listedWorldSync + worldCurrentSyncMsg + "§r."); // Final player msg
				MsgHandler.playerMsg(sender, "The world §e" + world + "§r" + worldCurrentSleepMsg + " " + listedWorldSleep + "§r."); // Final player msg
			} else {
				MsgHandler.infoMsg("The world " + world + " " + worldCurrentElapsedDaysMsg + " " + elapsedDays + " whole day(s) (" + date + ")."); // Final console msg
				MsgHandler.infoMsg("The world " + world + " " + worldCurrentStartMsg + " " + formattedUTC + " (+" + listedWorldStartTick + " ticks)."); // Final console msg
				MsgHandler.infoMsg("The world " + world + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " (#" + listedWorldCurrentTick + ")."); // Final console msg
				MsgHandler.infoMsg("The world " + world + worldCurrentSpeedMsg + " " + worldRealSpeedMsg + "."); // Final console msg
				MsgHandler.infoMsg("The world " + world + " is " + listedWorldSync + worldCurrentSyncMsg + "."); // Final console msg
				MsgHandler.infoMsg("The world " + world + worldCurrentSleepMsg + " " + listedWorldSleep + "."); // Final console msg
			}
		} else { // Display usual message (any speed but 24.00)
			if (sender instanceof Player) {
				MsgHandler.playerMsg(sender, "The world §e" + world + "§r " + worldCurrentElapsedDaysMsg + " §e" + elapsedDays + "§r whole day(s) (§e" + date + "§r)."); // Final console msg
				MsgHandler.playerMsg(sender, "The world §e" + world + "§r " + worldCurrentStartMsg + " §e" + listedWorldStartTime + " §r(§e+" + listedWorldStartTick + " §rticks)."); // Final player msg
				MsgHandler.playerMsg(sender, "The world §e" + world + "§r" + worldCurrentTimeMsg + " §e" + listedWorldCurrentTime + " §r(§e#" + listedWorldCurrentTick + "§r)."); // Final player msg
				MsgHandler.playerMsg(sender, "The world §e" + world + "§r" + worldCurrentDaySpeedMsg + " §e" + listedWorldDaySpeed + "§r."); // Final player msg
				MsgHandler.playerMsg(sender, "The world §e" + world + "§r" + worldCurrentNightSpeedMsg + " §e" + listedWorldNightSpeed + "§r."); // Final player msg
				MsgHandler.playerMsg(sender, "The world §e" + world + "§r is §e" + listedWorldSync + worldCurrentSyncMsg + "§r."); // Final player msg
				MsgHandler.playerMsg(sender, "The world §e" + world + "§r" + worldCurrentSleepMsg + " §e" + listedWorldSleep + "§r."); // Final player msg
			} else {
				MsgHandler.infoMsg("The world " + world + " " + worldCurrentElapsedDaysMsg + " " + elapsedDays + " whole day(s) (" + date + ")."); // Final console msg
				MsgHandler.infoMsg("The world " + world + " " + worldCurrentStartMsg + " " + listedWorldStartTime + " (+" + listedWorldStartTick + " ticks)."); // Final console msg
				MsgHandler.infoMsg("The world " + world + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " (#" + listedWorldCurrentTick + ")."); // Final console msg
				MsgHandler.infoMsg("The world " + world + worldCurrentDaySpeedMsg + " " + listedWorldDaySpeed + "."); // Final console msg
				MsgHandler.infoMsg("The world " + world + worldCurrentNightSpeedMsg + " " + listedWorldNightSpeed + "."); // Final console msg
				MsgHandler.infoMsg("The world " + world + " is " + listedWorldSync + worldCurrentSyncMsg + "."); // Final console msg
				MsgHandler.infoMsg("The world " + world + worldCurrentSleepMsg + " " + listedWorldSleep + "."); // Final console msg
			}
		}
	}

};