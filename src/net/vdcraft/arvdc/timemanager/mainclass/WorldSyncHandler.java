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
	public static void firstSync() { // Run only once
		BukkitScheduler firstSyncSheduler = MainTM.getInstance().getServer().getScheduler();
		firstSyncSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				// #A. Get the current server time and save it as the reference tick
				getOrSetInitialTickAndTime(true);
				// #B. Synchronize the worlds, based on a server constant point
				WorldSync(Bukkit.getServer().getConsoleSender(), "all", "start");
				// #C. Launch the good scheduler if it is inactive
				waitTime(10);
				if (increaseScheduleIsOn == false) WorldSpeedHandler.worldIncreaseSpeed();
				if (decreaseScheduleIsOn == false) WorldSpeedHandler.worldDecreaseSpeed();
				if (realScheduleIsOn == false) WorldSpeedHandler.worldRealSpeed();
				// #D. Notifications
				Bukkit.getLogger().info(prefixTM + " " + resyncIntroMsg); // Console log msg (always)
			}
		}, 2L);
	}

	/**
	 * Sync method, adding the default third argument "time"
	 */
	public static void worldSync(CommandSender sender, String world) {
		WorldSync(sender, world, "time");
	}

	/**
	 * Sync method for every kind of speed. Args are : <sender>, <world> or <all>, <start> or <time>
	 */
	public static void WorldSync(CommandSender sender, String world, String startOrTime) {
		// Get the current server time
		long currentServerTick = ValuesConverter.returnServerTick();
		// Get the current server time
		String currentServerTime = ValuesConverter.returnServerTime();
		// Get the total server's elapsed time
		long elapsedServerTime = currentServerTick - initialTick;
		long startAtTickNb;
		double speed;
		double daySpeed;
		double nightSpeed;

		// #A. Re-synchronize all worlds
		if (world.equalsIgnoreCase("all")) {
			Bukkit.getLogger().info(prefixTM + " " + serverInitTickMsg + " #" + initialTick + " (" + initialTime + ")."); // Final console msg // Console log msg
			Bukkit.getLogger().info(prefixTM + " " + serverCurrentTickMsg + " #" + currentServerTick + " (" + currentServerTime + ")."); // Console log msg
			for (World w : Bukkit.getServer().getWorlds()) {
				if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(w.getName())) { // Read config.yml to check if the world's name is listed
					WorldSync(sender, w.getName(), startOrTime);
				}
			}

		// #B. Re-synchronize a single world
		} else {
			long t = Bukkit.getWorld(world).getTime();
			startAtTickNb = (MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + CF_START)); // Get the world's 'start' value
			double speedAtStart = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(startAtTickNb)); // Get the world's speed value at server start
			if (startOrTime.equalsIgnoreCase("start")) {
				speed = speedAtStart; // Get the world's current 'daySpeed/nigthSpeed' value
			} else {
				speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t)); // Get the world's current 'daySpeed/nigthSpeed' value
			}
			daySpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED); // Get the world's 'daySpeed' value
			nightSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED); // Get the world's 'nightSpeed' value
			long newTime = Bukkit.getServer().getWorld(world).getTime();

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
				//  #B.3.A. Eventually make a first sync		
				if (startOrTime.equalsIgnoreCase("start"))  {
					// #B.3.A.a. If it is a (daySpeed == nightSpeed) world ...
					if (daySpeed == nightSpeed) { // Next tick = Start at #tick + (Elapsed time * speed modifier)
						newTime = (long) ((startAtTickNb + (elapsedServerTime * speed)) % 24000);
					// #B.3.A.b. ... or if it is a (daySpeed != nightSpeed) world
					} else { // Next tick is calculated with specific method
						newTime = differentSpeedsNewTime(world, startAtTickNb, elapsedServerTime, currentServerTick, speedAtStart, daySpeed, nightSpeed, true);
					}
				}
				// #B.3.B. Notifications
				Bukkit.getLogger().info(prefixTM + " The world " + world + " " + noResyncNeededMsg); // Console final msg (always)
				if (sender instanceof Player) {
					sender.sendMessage(prefixTMColor + " The world §e" + world + " §r" + noResyncNeededMsg); // Player final msg (in case)
				}	

			// #B.4. ... or if it is a (daySpeed == nightSpeed) world ...
			} else if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED).equalsIgnoreCase(MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED))) {
				// #B.4.A. Next tick = StartAtTick + (Elapsed time * speedModifier)
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
				// #B.5.A. Next tick is calculated with specific method
				newTime = differentSpeedsNewTime(world, startAtTickNb, elapsedServerTime, currentServerTick, speedAtStart, daySpeed, nightSpeed, true);
				// #B.5.B. Notifications
				Bukkit.getLogger().info(prefixTM + " The world " + world + " " + resyncDoneMsg); // Console final msg (always)
				if (sender instanceof Player) {
					sender.sendMessage(prefixTMColor + " The world §e" + world + " §r" + resyncDoneMsg); // Player final msg (in case)
				}
			}

			// #B.6. Apply modifications
			newTime = ValuesConverter.returnCorrectTicks(newTime);
			Bukkit.getServer().getWorld(world).setTime(newTime);
			
			// #B.7. Adjust doDaylightCycle value
			WorldDoDaylightCycleHandler.adjustDaylightCycle(world);

			// #B.8. Extra notifications (for each cases)
			String listedWorldStartTime = ValuesConverter.returnTimeFromTickValue(startAtTickNb);
			String listedWorldCurrentTime = ValuesConverter.returnTimeFromTickValue(newTime);
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
	 * Calculate world time when daySpeed and nightSpeed are not equal (returns a long)
	 */
	public static long differentSpeedsNewTime(String world, long startAtTickNb, long elapsedServerTime, long currentServerTick, double speedAtStart, double daySpeed, double nightSpeed, boolean displayMsg) {
		// Get the required server time for spending a day or a night or both in the target world
		long worldDayTimeInServerTicks = (long) (12000 / daySpeed);
		long worldNightTimeInServerTicks = (long) (12000 / nightSpeed);
		long worldFullTimeInServerTicks = worldDayTimeInServerTicks + worldNightTimeInServerTicks;
		// Use two variables for speed, depending of day/night starting time
		double firstSpeed = daySpeed; // day
		double secondSpeed = nightSpeed;
		long secondCycleDuration = worldNightTimeInServerTicks;
		long halfDaylightCycle = 12000;
		if (speedAtStart == nightSpeed) { // night
			firstSpeed = nightSpeed;	
			secondSpeed = daySpeed;
			secondCycleDuration = worldDayTimeInServerTicks;
			halfDaylightCycle = 24000;
		}
		// Use a clone of elapsedTime to subtract the number of ticks remaining
		long serverRemainingTime = elapsedServerTime;
		long newTime;
		// #1. If elapsed time is smaller than the difference between an half day minus and the startTime (= no day/night change) ...
		if ((elapsedServerTime * firstSpeed) < (12000 - (startAtTickNb % 12000))) {
			// #1.A. Use the classic easy formula
			newTime = (long) (startAtTickNb + (elapsedServerTime * firstSpeed));
			// #1.B. Debug Msg
			if (debugMode && displayMsg) {
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Resync: Calculation of " + actualTimeVar + " for world §e" + world + "§b:");
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + elapsedTimeCalculation + " = (§8" + currentServerTick + " §b- §7" + initialTick + "§b) % §624000 §b= §d" + ((currentServerTick - initialTick) % 24000) + " §brestrained to one day = §d" + ValuesConverter.returnCorrectTicks(((currentServerTick % 24000) - (initialTick % 24000)))); // Console debug msg
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + adjustedElapsedTimeCalculation + " = §d" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick % 24000))) + " §b* §a" + firstSpeed + " §b= §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * firstSpeed)))); // Console debug msg
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + actualTimeCalculation + " = §e" + startAtTickNb + " §b+ §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * firstSpeed) + " §b= §c" + (startAtTickNb + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * firstSpeed)) + " §brestrained to one day = §ctick #" + ValuesConverter.returnCorrectTicks(newTime)); // Console debug msg
			}
		// #2. ... or if elapsed time is bigger than an half-day (= a least one day/night change)
		} else {
			// #2.A. Count the 1st cycle (<= half-day)
			newTime = halfDaylightCycle; // (+) 1st cycle
			serverRemainingTime = (long) (serverRemainingTime - ((halfDaylightCycle - startAtTickNb) / firstSpeed)); // (-) 1st cycle
			// #2.B. Count down all full-days
			if (serverRemainingTime > worldFullTimeInServerTicks) {
				serverRemainingTime = (long) (serverRemainingTime % (worldFullTimeInServerTicks)); // (-) all full daylightCycles
			}
			// #2.C. Count an eventual complete day or night cycle ...
			if (serverRemainingTime > secondCycleDuration) {
				newTime = (long) (newTime + 12000); // (+) a complete day or night cycle
				serverRemainingTime = serverRemainingTime - secondCycleDuration; // (-) a complete day or night cycle

				// #2.C.1. ... and finally count the rest of the last day or night cycle
				newTime = (long) (newTime + (serverRemainingTime * firstSpeed)); // (+) last partial 

			// #2.C.2. ... or directly count the rest of the last day or night cycle
			} else {
				newTime = (long) (newTime + serverRemainingTime * secondSpeed); // (+) last partial cycle
			}
			// Restrain too big and too small values
			newTime = ValuesConverter.returnCorrectTicks(newTime);
			// #2.D. Debug Msg
			if (debugMode && displayMsg) {
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Resync: Calculation of " + actualTimeVar + " for world §e" + world + "§b:");
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + serverRemainingTimeVar + " = (" + elapsedTimeVar + " - ((" + halfDaylightCycleVar + " - " + worldStartAtVar + ") / (" + daySpeedModifierVar + " || " + nightSpeedModifierVar + "))) % ((§f12000 §b/ " + daySpeedModifierVar + ") + (§f12000 §b/ " + nightSpeedModifierVar + "))) - (§f0 §b|| §f12000§b) / (" + daySpeedModifierVar + " || " + nightSpeedModifierVar + ")) = §5" + serverRemainingTime); // Console debug msg
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + actualTimeVar + " = " + halfDaylightCycleVar + " + §b(§f0 §b|| §f12000§b) + (" + serverRemainingTimeVar + " * (" + daySpeedModifierVar + " || " + nightSpeedModifierVar + ")) = §c" + "§ctick #" + newTime); // Console debug msg
			}
		} 
		return newTime;
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
				} else { // If reset is true
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