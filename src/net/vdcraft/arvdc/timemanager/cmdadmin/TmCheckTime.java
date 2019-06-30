package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class TmCheckTime extends MainTM {

    /**
     * CMD /tm checktime [all|server|world]
     */
    public static void cmdCheckTime(CommandSender sender, String worldToGet) {

	// If using a world name in several parts
	if (sender instanceof Player)
	    worldToGet = ValuesConverter.restoreSpacesInString(worldToGet);

	// If the arg is "all" or "server", get the server's initial and actual ticks
	if (worldToGet.equalsIgnoreCase("all") || worldToGet.equalsIgnoreCase("server")) {
	    // Get the current server tick
	    long currentServerTick = ValuesConverter.returnServerTick();
	    // Get the current server UTC time in HH:mm:ss
	    String currentServerTime = ValuesConverter.returnServerTime();

	    // Display the reference tick and HH:mm:ss
	    if (sender instanceof Player) {
		sender.sendMessage(prefixTMColor + " " + serverInitTickMsg + " §e#" + initialTick + " §r(§e" + initialTime + "§r)."); // Final player msg
		waitTime(500);
	    } else {
		Bukkit.getLogger().info(prefixTM + " " + serverInitTickMsg + " #" + initialTick + " (" + initialTime + ")."); // Final console msg
	    }

	    // Get current tick
	    if (sender instanceof Player) {
		sender.sendMessage(prefixTMColor + " " + serverCurrentTickMsg + " §e#" + currentServerTick + " §r(§e" + currentServerTime + "§r)."); // Final player msg
		waitTime(500);
	    } else {
		Bukkit.getLogger().info(prefixTM + " " + serverCurrentTickMsg + " #" + currentServerTick + " (" + currentServerTime + ")."); // Final console msg
	    }
	    waitTime(500);
	}

	// If the arg is "all", get each world start tick, actual tick, speed and sync
	// param
	if (worldToGet.equalsIgnoreCase("all")) {
	    // Relaunch this for each world
	    for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
		cmdDisplayTime(sender, listedWorld);
	    }
	}

	// Else, if the string argument is a listed world, check a single world
	else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(worldToGet)) {
	    cmdDisplayTime(sender, worldToGet);
	}
    }

    /**
     * Display a world time message
     */
    private static void cmdDisplayTime(CommandSender sender, String worldToSet) {
	Long listedWorldStartTick = MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + worldToSet + "." + CF_START);
	World thisWorld = Bukkit.getServer().getWorld(worldToSet);
	Long listedWorldCurrentTick = thisWorld.getTime();
	String listedWorldStartTime = ValuesConverter.returnTimeFromTickValue(listedWorldStartTick);
	String formattedUTC = ValuesConverter.formatAsUTC(listedWorldStartTick);
	String listedWorldCurrentTime = ValuesConverter.returnTimeFromTickValue(listedWorldCurrentTick);
	String listedWorldSpeed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + worldToSet + "." + CF_SPEED);
	String listedWorldSync = "";
	String listedWorldSleep = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + worldToSet + "." + CF_SLEEP);
	if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + worldToSet + "." + CF_SYNC).equals("false"))
	    listedWorldSync = "not ";
	if (listedWorldSpeed.contains("24")) { // Display realtime messages
	    if (sender instanceof Player) {
		sender.sendMessage(prefixTMColor + " The world §e" + worldToSet + " §r" + worldCurrentStartMsg + " §e" + formattedUTC + " §r(§e+" + listedWorldStartTick + " §rticks)."); // Final player msg
		waitTime(1000);
		sender.sendMessage(prefixTMColor + " The world §e" + worldToSet + "§r" + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " §r(§e#" + listedWorldCurrentTick + "§r)."); // Final player msg
		waitTime(1000);
		sender.sendMessage(prefixTMColor + " The world §e" + worldToSet + "§r" + worldCurrentSpeedMsg + " §e" + worldRealSpeedMsg + "§r."); // Final player msg
		waitTime(1000);
		sender.sendMessage(prefixTMColor + " The world §e" + worldToSet + "§r is §e" + listedWorldSync + worldCurrentSyncMsg + "§r."); // Final player msg
		waitTime(1000);
		sender.sendMessage(prefixTMColor + " The world §e" + worldToSet + "§r" + worldCurrentSleepMsg + " " + listedWorldSleep + "§r."); // Final player msg
	    } else {
		Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + " " + worldCurrentStartMsg + " " + formattedUTC + " (+" + listedWorldStartTick + " ticks)."); // Final console msg
		Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " (#" + listedWorldCurrentTick + ")."); // Final console msg
		Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + worldCurrentSpeedMsg + " " + worldRealSpeedMsg + "."); // Final console msg
		Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + " is " + listedWorldSync + worldCurrentSyncMsg + "."); // Final console msg
		Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + worldCurrentSleepMsg + " " + listedWorldSleep + "."); // Final console msg
	    }
	} else { // Display normal messages
	    if (sender instanceof Player) {
		sender.sendMessage(prefixTMColor + " The world §e" + worldToSet + " §r" + worldCurrentStartMsg + " §e" + listedWorldStartTime + " §r(§e+" + listedWorldStartTick + " §rticks)."); // Final player msg
		waitTime(1000);
		sender.sendMessage(prefixTMColor + " The world §e" + worldToSet + "§r" + worldCurrentTimeMsg + " §e" + listedWorldCurrentTime + " §r(§e#" + listedWorldCurrentTick + "§r)."); // Final player msg
		waitTime(1000);
		sender.sendMessage(prefixTMColor + " The world §e" + worldToSet + "§r" + worldCurrentSpeedMsg + " §e" + listedWorldSpeed + "§r."); // Final player msg
		waitTime(1000);
		sender.sendMessage(prefixTMColor + " The world §e" + worldToSet + "§r is §e" + listedWorldSync + worldCurrentSyncMsg + "§r."); // Final player msg
		waitTime(1000);
		sender.sendMessage(prefixTMColor + " The world §e" + worldToSet + "§r" + worldCurrentSleepMsg + " §e" + listedWorldSleep + "§r."); // Final player msg
	    } else {
		Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + " " + worldCurrentStartMsg + " " + listedWorldStartTime + " (+" + listedWorldStartTick + " ticks)."); // Final console msg
		Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " (#" + listedWorldCurrentTick + ")."); // Final console msg
		Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + worldCurrentSpeedMsg + " " + listedWorldSpeed + "."); // Final console msg
		Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + " is " + listedWorldSync + worldCurrentSyncMsg + "."); // Final console msg
		Bukkit.getLogger().info(prefixTM + " The world " + worldToSet + worldCurrentSleepMsg + " " + listedWorldSleep + "."); // Final console msg
	    }
	}
    }

};