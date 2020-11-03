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
	public static void WorldSyncRe(CommandSender sender, String world) {
		// Get the current server time
		long currentServerTick = ValuesConverter.returnServerTick();
		// Get the current server time
		String currentServerTime = ValuesConverter.returnServerTime();
		long startAtTickNb;
		double speed;

		// #A. Re-synchronize all worlds
		if (world.equalsIgnoreCase("all")) {
			Bukkit.getLogger().info(prefixTM + " " + serverInitTickMsg + " #" + initialTick + " (" + initialTime + ")."); // Final console msg // Console log msg
			Bukkit.getLogger().info(prefixTM + " " + serverCurrentTickMsg + " #" + currentServerTick + " (" + currentServerTime + ")."); // Console log msg
			for (World w : Bukkit.getServer().getWorlds()) {
				if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(w.getName())) { // Read config.yml to check if the world's name is listed
					WorldSyncRe(sender, w.getName());
				}
			}

		// #B. Re-synchronize a single world
		} else {
			long t = Bukkit.getWorld(world).getTime();
			startAtTickNb = (MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + CF_START)); // Get the world's 'start' value
			speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + WorldSpeedHandler.wichSpeedParam(t)); // Get the world's current 'daySpeed/nigthSpeed' value
			double daySpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED); // Get the world's 'daySpeed' value
			double nightSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED); // Get the world's 'nightSpeed' value
			long newTime = Bukkit.getServer().getWorld(world).getTime();
			WorldDoDaylightCycleHandler.doDaylightSet(world);

			// #B.1. If it is a realtime world ...
			if (speed == 24.0) {
				// #B.1.A. Next tick = start at #tick - difference between a real day that starts at 0:00 and a minecraft day that starts at 6:00 + (Current tick / difference between a 24h real day length and a minecraft day that lasts 20min)
				newTime = startAtTickNb - 6000L + (currentServerTick / 72L);
				// #B.1.B. Notifications
				Bukkit.getLogger().info(prefixTM + " The world " + world + " " + world24hNoSyncChgMsg); // Console final msg (always)
				if (sender instanceof Player) {
					sender.sendMessage(prefixTMColor + " The world §e" + world + " §r" + world24hNoSyncChgMsg); // Player final msg (in case)
				}				
				// #B.1.C. Debug Msg
				if (debugMode) {
					Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Resync: Calculation of " + actualTimeVar + " for world §e" + world + "§b:");
					Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + adjustedTicksCalculation + " = §8" + currentServerTick + " §b/ §672 §b= §3" + ((currentServerTick / 72L) % 24000)); // Console debug msg
					Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + realActualTimeCalculation + " = §e" + startAtTickNb + " §b- §96000 §b+ §3" + ((currentServerTick / 72L) % 24000) + " §b= §c" + (startAtTickNb - 6000L + (currentServerTick / 72L)) % 24000 + " §brestrained to one day = §ctick #" + ValuesConverter.returnCorrectTicks(newTime)); // Console debug msg
				}

			// #B.2. ... or if it is a frozen world ...
			} else if (speed == 0.0) {
				// #B.3.A. Next tick = (Start at #tick)
				newTime = startAtTickNb;
				// #B.2.B. Debug Msg
				Bukkit.getLogger().info(prefixTM + " The world " + world + " " + worldFrozenNoSyncChgMsg); // Console final msg (always)
				if (sender instanceof Player) {
					sender.sendMessage(prefixTMColor + " The world §e" + world + " §r" + worldFrozenNoSyncChgMsg); // Player final msg (in case)
				}
				if (debugMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + actualTimeVar + " = " + worldStartAtVar + " = §e" + startAtTickNb + " §brestrained to one day = §ctick #" + ValuesConverter.returnCorrectTicks(newTime)); // Console debug msg
				
			// #B.3. ... or if it is a synchronized world ...
			} else if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase("true")) {
				// #B.3.A. Notifications
				Bukkit.getLogger().info(prefixTM + " The world " + world + " " + noResyncNeededMsg); // Console final msg (always)
				if (sender instanceof Player) {
					sender.sendMessage(prefixTMColor + " The world §e" + world + " §r" + noResyncNeededMsg); // Player final msg (in case)
				}	
				
			// #B.4. ... or if it is a (daySpeed == nightSpeed) world ...
			} else if (MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED) == MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED)) {
				// #B.4.A. Next tick = Start at #tick + (Elapsed time * speed modifier)
				newTime = (long) (startAtTickNb + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick))) * speed) % 24000));
				// #B.4.B. Notifications
				Bukkit.getLogger().info(prefixTM + " The world " + world + " " + resyncDoneMsg); // Console final msg (always)
				if (sender instanceof Player) {
					sender.sendMessage(prefixTMColor + " The world §e" + world + " §r" + resyncDoneMsg); // Player final msg (in case)
				}				
				// #B.4.C. Debug Msg
				if (debugMode) {
					Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Resync: Calculation of " + actualTimeVar + " for world §e" + world + "§b:");
					Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + elapsedTimeCalculation + " = (§8" + currentServerTick + " §b- §7" + initialTick + "§b) % §624000 §b= §d" + ((currentServerTick - initialTick) % 24000) + " §brestrained to one day = §d" + ValuesConverter.returnCorrectTicks(((currentServerTick % 24000) - (initialTick % 24000)))); // Console debug msg
					Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + adjustedElapsedTimeCalculation + " = §d" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick % 24000))) + " §b* §a" + speed + " §b= §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speed)))); // Console debug msg
					Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + actualTimeCalculation + " = §e" + startAtTickNb + " §b+ §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speed) + " §b= §c" + (startAtTickNb + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speed)) + " §brestrained to one day = §ctick #" + ValuesConverter.returnCorrectTicks(newTime)); // Console debug msg
				}

			// #B.5. ... or if it is a (daySpeed != nightSpeed) world 
			} else {
				// #B.5.A. Get the world's speed value at server start
				double speedAtStart = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + WorldSpeedHandler.wichSpeedParam(startAtTickNb));
				// #B.5.B. Get the total server's elapsed time
				long elapsedServerTime = currentServerTick - initialTick;
				newTime = WorldSpeedHandler.worldWithDifferentSpeedNewTime(world, startAtTickNb, elapsedServerTime, currentServerTick, speedAtStart, daySpeed, nightSpeed, true);
				// #B.5.C. Notifications
				Bukkit.getLogger().info(prefixTM + " The world " + world + " " + resyncDoneMsg); // Console final msg (always)
				if (sender instanceof Player) {
					sender.sendMessage(prefixTMColor + " The world §e" + world + " §r" + resyncDoneMsg); // Player final msg (in case)
				}
			}

			// #B.6.
			newTime = ValuesConverter.returnCorrectTicks(newTime);
			Bukkit.getServer().getWorld(world).setTime(newTime);

			// #B.7. Extra notifications (for each cases)
			String listedWorldCurrentTime = ValuesConverter.returnTimeFromTickValue(newTime);
			String listedWorldStartTime = ValuesConverter.returnTimeFromTickValue(startAtTickNb);
			String formattedUTC = ValuesConverter.formatAsUTC(startAtTickNb);
			if (speed == realtimeSpeed) { // Display realtime message (speed is equal to 24.00)
				Bukkit.getLogger().info(prefixTM + " The world " + world + " " + worldCurrentStartMsg + " " + formattedUTC + " (+" + startAtTickNb + " ticks)."); // Final console msg
				Bukkit.getLogger().info(prefixTM + " The world " + world + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " (#" + newTime + ")."); // Final console msg
				Bukkit.getLogger().info(prefixTM + " The world " + world + worldCurrentSpeedMsg + " " + worldRealSpeedMsg); // Final console msg
			} else if (daySpeed == nightSpeed) { // Display usual message (one speed)
				Bukkit.getLogger().info(prefixTM + " The world " + world + " " + worldCurrentStartMsg + " " + listedWorldStartTime + " (+" + startAtTickNb + " ticks)."); // Final console msg
				Bukkit.getLogger().info(prefixTM + " The world " + world + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " (#" + newTime + ")."); // Final console msg
				Bukkit.getLogger().info(prefixTM + " The world " + world + worldCurrentSpeedMsg + " " + MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED) + "."); // Final console msg
			} else if (daySpeed != nightSpeed) { // Display usual message (two different speeds)
				Bukkit.getLogger().info(prefixTM + " The world " + world + " " + worldCurrentStartMsg + " " + listedWorldStartTime + " (+" + startAtTickNb + " ticks)."); // Final console msg
				Bukkit.getLogger().info(prefixTM + " The world " + world + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " (#" + newTime + ")."); // Final console msg
				Bukkit.getLogger().info(prefixTM + " The world " + world + worldCurrentDaySpeedMsg + " " + MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED) + "."); // Final console msg
				Bukkit.getLogger().info(prefixTM + " The world " + world + worldCurrentNightSpeedMsg + " " + MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED) + "."); // Final console msg
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
				if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equalsIgnoreCase("true")) {
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
	private static void getOrSetInitialTickAndTime(Boolean msgOnOff) {
		String setOrGet = "null";
		String ymlOrSql = "null";
		if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equalsIgnoreCase("true")) { // If mySQL is true
			if (SqlHandler.openTheConnectionIfPossible(msgOnOff) == true) {
				if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_RESETONSTARTUP).equalsIgnoreCase("false")) { // If reset false
					// Try to read database
					initialTick = SqlHandler.getServerTickSQL(); // Get existing reference tick from SQL database
					if (initialTick == null) { // If db is null, create the initial tick
						initialTick = ValuesConverter.returnServerTick();
						SqlHandler.setServerTickSQL(initialTick); // Save tick in SQL database
					}
					initialTime = ValuesConverter.returnRealTimeFromTickValue(initialTick); // Convert the initial time in HH:mm:ss UTC
					MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_INITIALTICKNB, initialTick); // Save tick in config
					setOrGet = "get from";
				} else { // If reset true
					// Define a new reference tick
					initialTick = ValuesConverter.returnServerTick(); // Create the initial tick
					initialTime = ValuesConverter.returnServerTime(); // Create the initial time in HH:mm:ss UTC
					MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_INITIALTICKNB, initialTick); // Save tick in config
					Long testInitialTickSQL = SqlHandler.getServerTickSQL(); // Get existing reference tick from SQL database
					if (testInitialTickSQL == null) {
						SqlHandler.setServerTickSQL(initialTick); // Save tick in SQL database
					} else {
						SqlHandler.updateServerTickSQL(initialTick); // Update tick in SQL database
					}
					setOrGet = "set in";
				}
				ymlOrSql = "the mySQL database";
			} else { // When a connection fails, the key 'useMySql' is set on false, so this will
				// retry sync but using the config.yml
				getOrSetInitialTickAndTime(msgOnOff);
			}
		} else if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equalsIgnoreCase("false")) { // When mySQL is false
			// If reset true OR initialTickNb doesn't exist
			if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_RESETONSTARTUP).equalsIgnoreCase("false") && !MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_INITIALTICKNB).equals("")) {
				// If reset false AND initialTickNb exists
				initialTick = MainTM.getInstance().getConfig().getLong(CF_INITIALTICK + "." + CF_INITIALTICKNB); // Get existing reference tick from config.yml
				initialTime = ValuesConverter.returnRealTimeFromTickValue(initialTick); // Convert the initial time in HH:mm:ss UTC
				setOrGet = "get from";
			} else { // Define a new reference tick
				initialTick = ValuesConverter.returnServerTick(); // Create the initial tick
				initialTime = ValuesConverter.returnRealTimeFromTickValue(initialTick); // Convert the initial time in HH:mm:ss UTC
				MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_INITIALTICKNB, initialTick); // Save tick in config.yml
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
		Long newTick = MainTM.getInstance().getConfig().getLong(CF_INITIALTICK + "." + CF_INITIALTICKNB);
		// Get the previous initialTickNb from the MySQL database
		Long sqlTick = null;
		if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equalsIgnoreCase("true")) {
			if (SqlHandler.openTheConnectionIfPossible(true)) {
				sqlTick = SqlHandler.getServerTickSQL();
			}
		}
		// If mySql is false, try to actualize the configuration:
		if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equalsIgnoreCase("false")) {
			// If there are changes in the configuration:
			if (!(oldTick.equals(newTick))) {
				// Actualize the global variables
				initialTick = newTick;
				initialTime = ValuesConverter.returnRealTimeFromTickValue(initialTick);
				// Notifications
				Bukkit.getLogger().info(prefixTM + " " + initialTickYmlMsg); // Notify the console
				TmCheckTime.cmdCheckTime(Bukkit.getServer().getConsoleSender(), "server"); // Notify the console
			}
			// Else if there are NO changes in the configuration: Don't do nothing at all
			// Else, if mySql is true, try to set or get the initialTickNb:
		} else if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equalsIgnoreCase("true")) {
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
				// Else, if there are NO changes in the configuration AND if sqlTick isn't null
				// AND if sqlTick is different from the newTick:
			} else if (!(sqlTick.equals(newTick))) {
				// Actualize the configuration
				MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_INITIALTICKNB, sqlTick);
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