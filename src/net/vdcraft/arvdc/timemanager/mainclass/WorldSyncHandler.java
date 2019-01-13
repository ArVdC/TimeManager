package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmCheckTime;

public class WorldSyncHandler extends MainTM {

    /**
     * Delayed sync all on startup
     */
    public static void WorldSyncFirst() { // Run only once
	BukkitScheduler firstSyncSheduler = MainTM.getInstance().getServer().getScheduler();
	firstSyncSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
	    @Override
	    public void run() {
		// #A. Get the current server time and save it as the reference tick
		getOrSetInitialTickAndTime(true);
		// #B. Synchronize the worlds, based on a server constant point
		Bukkit.getLogger().info(prefixTM + " " + resyncIntroMsg); // Console log msg (always)
		WorldSyncRe(Bukkit.getServer().getConsoleSender(), "all");
		// #C. Launch the good scheduler if it is inactive
		if (increaseScheduleIsOn == false) {
		    WorldSpeedHandler.WorldIncreaseSpeed();
		}
		if (decreaseScheduleIsOn == false) {
		    WorldSpeedHandler.WorldDecreaseSpeed();
		}
		if (realScheduleIsOn == false) {
		    WorldSpeedHandler.WorldRealSpeed();
		}
	    }
	}, 2L);
    }

    /**
     * Sync method <world> or <all>
     */
    public static void WorldSyncRe(CommandSender sender, String wichWorld) {
	// Get the current server time
	long currentServerTick = ValuesConverter.returnServerTick();
	// Get the current server time
	String currentServerTime = ValuesConverter.returnServerTime();
	long startAtTickNb;
	double speedModifNb;
	// #A. Re-synchronize all worlds
	if (wichWorld.equalsIgnoreCase("all")) {
	    Bukkit.getLogger().info(prefixTM + " " + serverInitTickMsg + " #" + initialTick + " (" + initialTime + ")."); // Final console msg // Console log msg
	    Bukkit.getLogger().info(prefixTM + " " + serverCurrentTickMsg + " #" + currentServerTick + " (" + currentServerTime + ")."); // Console log msg
	    for (World w : Bukkit.getServer().getWorlds()) {
		if (MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(w.getName())) { // Read config.yml to check if the world's name is listed
		    WorldSyncRe(sender, w.getName());
		}
	    }
	    // #B. Re-synchronize a single world
	} else {
	    startAtTickNb = (MainTM.getInstance().getConfig().getLong("worldsList." + wichWorld + ".start")); // Read config.yml to get the world's 'start' value
	    speedModifNb = (MainTM.getInstance().getConfig().getDouble("worldsList." + wichWorld + ".speed")); // Read config.yml to get the world's 'speed' value
	    long newTick = Bukkit.getServer().getWorld(wichWorld).getTime();
	    WorldDayCycleHandler.doDaylightCheck(wichWorld);
	    if (speedModifNb == 24.0) { // if realtime world
		// Next tick = start at #tick - difference between a real day that starts at 0:00 and a minecraft day that starts at 6:00 + (Current tick / difference between a 24h real day length and a minecraft day that lasts 20min)
		newTick = startAtTickNb - 6000L + (currentServerTick / 72L);
		// Notifications
		Bukkit.getLogger().info(prefixTM + " The world " + wichWorld + " " + world24hNoSyncChgMsg); // Console final msg (always)
		if (sender instanceof Player) {
		    sender.sendMessage(prefixTMColor + " The world §e" + wichWorld + " §r" + world24hNoSyncChgMsg); // Player final msg (in case)
		}
		if (debugMode == true)
		    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Resync: Calculation of " + actualTimeVar + " for world §e" + wichWorld + "§b:");
		if (debugMode == true)
		    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + adjustedTicksCalculation + " = §8" + currentServerTick + " §b/ §672 §b= §3" + ((currentServerTick / 72L) % 24000)); // Console debug msg
		if (debugMode == true)
		    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + realActualTimeCalculation + " = §e" + startAtTickNb + " §b- §96000 §b+ §3" + ((currentServerTick / 72L) % 24000) + " §b= §c" + (startAtTickNb - 6000L + (currentServerTick / 72L)) % 24000 + " §brestrained to one day = §ctick #" + ValuesConverter.returnCorrectTicks(newTick)); // Console debug msg
	    } else if (speedModifNb == 0.0) { // if frozen world
		// Next tick = (Start at #tick)
		newTick = startAtTickNb;
		// Notifications
		Bukkit.getLogger().info(prefixTM + " The world " + wichWorld + " " + worldFrozenNoSyncChgMsg); // Console final msg (always)
		if (sender instanceof Player) {
		    sender.sendMessage(prefixTMColor + " The world §e" + wichWorld + " §r" + worldFrozenNoSyncChgMsg); // Player final msg (in case)
		}
		if (debugMode == true)
		    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + actualTimeVar + " = " + worldStartAtVar + " = §e" + startAtTickNb + " §brestrained to one day = §ctick #" + ValuesConverter.returnCorrectTicks(newTick)); // Console debug msg
	    } else { // if other speed world // Next tick = Start at #tick + (Elapsed time * speed modifier)
		newTick = (long) (startAtTickNb + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick))) * speedModifNb) % 24000));
		// Notifications
		Bukkit.getLogger().info(prefixTM + " The world " + wichWorld + " " + resyncDoneOneMsg); // Console final msg (always)
		if (sender instanceof Player) {
		    sender.sendMessage(prefixTMColor + " The world §e" + wichWorld + " §r" + resyncDoneOneMsg); // Player final msg (in case)
		}
		if (debugMode == true) {
		    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Resync: Calculation of " + actualTimeVar + " for world §e" + wichWorld + "§b:");
		    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + elapsedTimeCalculation + " = (§8" + currentServerTick + " §b- §7" + initialTick + "§b) % §624000 §b= §d" + ((currentServerTick - initialTick) % 24000) + " §brestrained to one day = §d" + ValuesConverter.returnCorrectTicks(((currentServerTick % 24000) - (initialTick % 24000)))); // Console debug msg
		    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + adjustedElapsedTimeCalculation + " = §d" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick % 24000))) + " §b* §a" + speedModifNb + " §b= §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speedModifNb)))); // Console debug msg
		    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + actualTimeCalculation + " = §e" + startAtTickNb + " §b+ §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speedModifNb) + " §b= §c" + (startAtTickNb + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speedModifNb)) + " §brestrained to one day = §ctick #" + ValuesConverter.returnCorrectTicks(newTick)); // Console debug msg
		}
	    }
	    newTick = ValuesConverter.returnCorrectTicks(newTick);
	    Bukkit.getServer().getWorld(wichWorld).setTime(newTick);
	    // Notifications (in both cases)
	    String listedWorldCurrentTime = ValuesConverter.returnTimeFromTickValue(newTick);
	    String listedWorldStartTime = ValuesConverter.returnTimeFromTickValue(startAtTickNb);
	    String formattedUTC = ValuesConverter.formatAsUTC(startAtTickNb);
	    if (speedModifNb == 24.0) { // Display realtime messages
		Bukkit.getLogger().info(prefixTM + " The world " + wichWorld + " " + worldCurrentStartMsg + " " + formattedUTC + " (+" + startAtTickNb + " ticks)."); // Final console msg
		Bukkit.getLogger().info(prefixTM + " The world " + wichWorld + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " (#" + newTick + ")."); // Final console msg
		Bukkit.getLogger().info(prefixTM + " The world " + wichWorld + worldCurrentSpeedMsg + " " + worldRealSpeedMsg); // Final console msg
	    } else { // Display normal messages
		Bukkit.getLogger().info(prefixTM + " The world " + wichWorld + " " + worldCurrentStartMsg + " " + listedWorldStartTime + " (+" + startAtTickNb + " ticks)."); // Final console msg
		Bukkit.getLogger().info(prefixTM + " The world " + wichWorld + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " (#" + newTick + ")."); // Final console msg
		Bukkit.getLogger().info(prefixTM + " The world " + wichWorld + worldCurrentSpeedMsg + " " + speedModifNb + "."); // Final console msg
	    }
	}
    }

    /**
     * Delayed actualization of the initialTickNb if the MySQL parameter is on
     */
    public static void refreshInitialTickMySql() {
	Long refreshTimeInTick = (sqlInitialTickAutoUpdateValue * 1200);
	BukkitScheduler firstSyncSheduler = MainTM.getInstance().getServer().getScheduler();
	firstSyncSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
	    @Override
	    public void run() {
		if (MainTM.getInstance().getConfig().getString("initialTick.useMySql").equals("true")) {
		    getOrSetInitialTickAndTime(false);
		    refreshInitialTickMySql();
		} else {
		    mySqlRefreshIsAlreadyOn = false;
		}
	    }
	}, refreshTimeInTick);
    }

    /**
     * Get the reference tick on startup (or set by default if empty)
     */
    public static void getOrSetInitialTickAndTime(Boolean msgOnOff) {
	String setOrGet = "null";
	String ymlOrSql = "null";
	if (MainTM.getInstance().getConfig().getString("initialTick.useMySql").equalsIgnoreCase("true")) { // If mySQL is true
	    if (SqlHandler.openTheConnectionIfPossible(msgOnOff) == true) {
		if (MainTM.getInstance().getConfig().getString("initialTick.resetOnStartup").equalsIgnoreCase("false")) { // If reset false
		    // Try to read database
		    initialTick = SqlHandler.getServerTickSQL(); // Get existing reference tick from SQL database
		    if (initialTick == null) { // If db is null, create the initial tick
			initialTick = ValuesConverter.returnServerTick();
			SqlHandler.setServerTickSQL(initialTick); // Save tick in SQL database
		    }
		    initialTime = ValuesConverter.returnRealTimeFromTickValue(initialTick); // Convert the initial time in HH:mm:ss UTC
		    MainTM.getInstance().getConfig().set("initialTick.initialTickNb", initialTick); // Save tick in config
		    setOrGet = "get from";
		} else { // If reset true
		    // Define a new reference tick
		    initialTick = ValuesConverter.returnServerTick(); // Create the initial tick
		    initialTime = ValuesConverter.returnServerTime(); // Create the initial time in HH:mm:ss UTC
		    MainTM.getInstance().getConfig().set("initialTick.initialTickNb", initialTick); // Save tick in config
		    Long testInitialTickSQL = SqlHandler.getServerTickSQL(); // Get existing reference tick from SQL database
		    if (testInitialTickSQL == null) {
			SqlHandler.setServerTickSQL(initialTick); // Save tick in SQL database
		    } else {
			SqlHandler.updateServerTickSQL(initialTick); // Update tick in SQL database
		    }
		    setOrGet = "set in";
		}
		ymlOrSql = "the mySQL database";
	    } else { // When a connection fails, the key 'useMySql' is set on false, so this will retry sync but using the config.yml
		getOrSetInitialTickAndTime(msgOnOff);
	    }
	} else if (MainTM.getInstance().getConfig().getString("initialTick.useMySql").equalsIgnoreCase("false")) { // When mySQL is false
	    // If reset true OR initialTickNb doesn't exist
	    if (MainTM.getInstance().getConfig().getString("initialTick.resetOnStartup").equalsIgnoreCase("false") && !MainTM.getInstance().getConfig().getString("initialTick.initialTickNb").equals("")) {
		// If reset false AND initialTickNb exists
		initialTick = MainTM.getInstance().getConfig().getLong("initialTick.initialTickNb"); // Get existing reference tick from config.yml
		initialTime = ValuesConverter.returnRealTimeFromTickValue(initialTick); // Convert the initial time in HH:mm:ss UTC
		setOrGet = "get from";
	    } else { // Define a new reference tick
		initialTick = ValuesConverter.returnServerTick(); // Create the initial tick
		initialTime = ValuesConverter.returnRealTimeFromTickValue(initialTick); // Convert the initial time in HH:mm:ss UTC
		MainTM.getInstance().getConfig().set("initialTick.initialTickNb", initialTick); // Save tick in config.yml
		setOrGet = "set in";
	    }
	    ymlOrSql = "the config.yml";
	}
	MainTM.getInstance().saveConfig(); // Save config.yml file
	SqlHandler.closeConnection("DB"); // Close connection
	if (msgOnOff) {
	    Bukkit.getLogger().info(prefixTM + " " + "The server's initial tick was " + setOrGet + " " + ymlOrSql + "."); // Console log msg
	}
    }

    /**
     * Update the reference tick on reload and store it in the DB if necessary
     */
    public static void updateInitialTickAndTime(Long oldTick) {

	// Get the new initialTickNb from the reloaded config.yml
	Long newTick = MainTM.getInstance().getConfig().getLong("initialTick.initialTickNb");

	// Get the previous initialTickNb from the MySQL database
	Long sqlTick = null;
	if (SqlHandler.openTheConnectionIfPossible(true)) {
	    sqlTick = SqlHandler.getServerTickSQL();
	}

	// If mySql is false, try to actualize the configuration:
	if (MainTM.getInstance().getConfig().getString("initialTick.useMySql").equalsIgnoreCase("false")) {
	    // If there are changes in the configuration:
	    if (!(oldTick.equals(newTick))) {
		// Actualize the global variables
		initialTick = newTick;
		initialTime = ValuesConverter.returnRealTimeFromTickValue(initialTick);
		// Notifications
		Bukkit.getLogger().info(prefixTM + " " + initialTickYmlMsg); // Notify the console
		TmCheckTime.cmdCheckTime(Bukkit.getServer().getConsoleSender(), "server"); // Notify the console
	    }
	    // Else if there are NO changes in the configuration:
	    // Don't do nothing

	    // Else, if mySql is true, try to set or get the initialTickNb:
	} else if (MainTM.getInstance().getConfig().getString("initialTick.useMySql").equalsIgnoreCase("true")) {
	    // If there are changes in the configuration:
	    if (!(oldTick.equals(newTick))) {
		// Actualize the global variables
		initialTick = newTick;
		initialTime = ValuesConverter.returnRealTimeFromTickValue(initialTick);
		// Actualize the MySQL database
		SqlHandler.updateServerTickSQL(newTick);
		// Notifications
		Bukkit.getLogger().info(prefixTM + " " + initialTickSqlMsg); // Notify the console
		TmCheckTime.cmdCheckTime(Bukkit.getServer().getConsoleSender(), "server"); // Notify the console

		// Else, if there are NO changes in the configuration AND if sqlTick is null:
	    } else if (sqlTick == null) {
		// Actualize the global variables
		initialTick = newTick;
		initialTime = ValuesConverter.returnRealTimeFromTickValue(initialTick);
		// Actualize the MySQL database
		SqlHandler.updateServerTickSQL(newTick);
		// Notifications
		Bukkit.getLogger().info(prefixTM + " " + initialTickSqlMsg); // Notify the console
		TmCheckTime.cmdCheckTime(Bukkit.getServer().getConsoleSender(), "server"); // Notify the console

		// Else, if there are NO changes in the configuration AND if sqlTick isn't null AND if sqlTick is different from the newTick:
	    } else if (!(sqlTick.equals(newTick))) {
		// Actualize the configuration
		MainTM.getInstance().getConfig().set("initialTick.initialTickNb", sqlTick);
		// Actualize the global variables
		initialTick = sqlTick;
		initialTime = ValuesConverter.returnRealTimeFromTickValue(initialTick);
		// Notifications
		Bukkit.getLogger().info(prefixTM + " " + initialTickGetFromSqlMsg); // Notify the console
		TmCheckTime.cmdCheckTime(Bukkit.getServer().getConsoleSender(), "server"); // Notify the console
	    }
	    // Else, don't do nothing at all
	}
    }

};