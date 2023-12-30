package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmCheckTime;

public class SyncHandler extends MainTM {

	/**
	 * Delayed sync all on startup
	 */
	public static void firstSync() { // Run only once
		BukkitScheduler firstSyncSheduler = MainTM.getInstance().getServer().getScheduler();
		firstSyncSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				// #1. Get the current server time and save it as the reference tick
				getOrSetInitialTickAndTime(true);
				// #2. Synchronize the worlds, based on a server constant point
				worldSync(Bukkit.getServer().getConsoleSender(), ARG_ALL, ARG_START);
				// #3. Launch the good scheduler if it is inactive
				BukkitScheduler firstSpeedLaunch = MainTM.getInstance().getServer().getScheduler();
				firstSpeedLaunch.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
					@Override
					public void run() {
						// #1. Detect if this world needs to change its speed value
						SpeedHandler.speedScheduler(ARG_ALL);
						// #2. Notifications
						MsgHandler.infoMsg(resyncIntroMsg); // Console log msg (always)
					}
				}, 2L);
			}
		}, 2L);
	}

	/**
	 * Sync method, adding the default third argument "time"
	 */
	public static void worldSync(CommandSender sender, String target) {
		worldSync(sender, target, ARG_TIME);
	}

	/**
	 * Sync method for every kind of speed. Args are : <sender>, <world> or <all>, <start> or <time>
	 */
	public static void worldSync(CommandSender sender, String world, String startOrTime) {
		// Get the current server # tick
		long currentServerTick = ValuesConverter.getServerTick();
		// Get the current server HH:mm:ss time
		String currentServerTime = ValuesConverter.getServerTime();
		// Get the total server's elapsed time
		long elapsedServerTime = currentServerTick - initialTick;
		long startAtTickNb;
		double speedAtStart;
		double speed;
		double daySpeed;
		double nightSpeed;

		// #1. Re-synchronize all worlds
		if (world.equalsIgnoreCase(ARG_ALL)) {
			MsgHandler.infoMsg(serverInitTickMsg + " #" + initialTick + " (" + initialTime + ")."); // Final console msg // Console log msg
			MsgHandler.infoMsg(serverCurrentTickMsg + " #" + currentServerTick + " (" + currentServerTime + ")."); // Console log msg
			for (World w : Bukkit.getServer().getWorlds()) {
				if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(w.getName())) { // Read config.yml to check if the world's name is listed
					worldSync(sender, w.getName(), startOrTime);
				}
			} 

		// #2. Re-synchronize a single world
		} else {			
			Long initElapsedDays = ValuesConverter.elapsedDaysFromTick(Bukkit.getWorld(world).getFullTime()); // Get the number of elapsed days
			long t = Bukkit.getWorld(world).getTime(); // Get the world's current time
			startAtTickNb = (MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + CF_START)); // Get the world's 'start' value
			speedAtStart = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(startAtTickNb)); // Get the world's speed value at server start
			if (startOrTime.equalsIgnoreCase(ARG_START)) {
				speed = speedAtStart; // Get the world's first 'daySpeed/nigthSpeed' value
			} else {
				speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t)); // Get the world's current 'daySpeed/nigthSpeed' value
			}
			daySpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED); // Get the world's 'daySpeed' value
			nightSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED); // Get the world's 'nightSpeed' value
			long newTime = Bukkit.getServer().getWorld(world).getTime();			
			String firstStartTime = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_FIRSTSTARTTIME); // Get the firstStartTime value
			
			// #2.A. If it is a realtime world ...
			if (speed == realtimeSpeed) {
				// #2.A.a. Next tick = start at #tick - difference between a real day that starts at 0:00 and a minecraft day that starts at 6:00 + (Current tick / difference between a 24h real day length and a minecraft day that lasts 20min)
				newTime = startAtTickNb - 6000L + (currentServerTick / 72L);
				// #2.A.b Notifications
				MsgHandler.infoMsg("The world " + world + " " + world24hNoSyncChgMsg); // Console final msg (always)
				MsgHandler.playerAdminMsg(sender, "The world §e" + world + " §r" + world24hNoSyncChgMsg); // Player final msg (in case)
				// #2.A.c Debug Msg
				MsgHandler.debugMsg("Resync: Calculation of " + actualTimeVar + " for world §e" + world + "§b:");
				MsgHandler.debugMsg(worldTicksCalculation + " = §8" + currentServerTick + " §b/ §672 §b= §3" + ((currentServerTick / 72L) % 24000)); // Console debug msg
				MsgHandler.debugMsg(realActualTimeCalculation + " = §e" + startAtTickNb + " §b- §96000 §b+ §3" + ((currentServerTick / 72L) % 24000) + " §b= §c" + (startAtTickNb - 6000L + (currentServerTick / 72L)) % 24000 + " §brestrained to one day = §ctick #" + ValuesConverter.correctDailyTicks(newTime)); // Console debug msg

			// #2.B. ... or if it is a frozen world ...
			} else if (speed == 0.0) {
				// #2.B.a. Next tick = (Start at #tick)
				newTime = startAtTickNb;
				// #2.B.b. Debug Msg
				MsgHandler.infoMsg("The world " + world + " " + worldFrozenNoSyncChgMsg); // Console final msg (always)
				MsgHandler.playerAdminMsg(sender, "The world §e" + world + " §r" + worldFrozenNoSyncChgMsg); // Player final msg (in case)
				MsgHandler.debugMsg(actualTimeVar + " = " + worldStartAtVar + " = §e" + startAtTickNb + " §brestrained to one day = §ctick #" + ValuesConverter.correctDailyTicks(newTime)); // Console debug msg

			// #2.C. ... or if it is a synchronized world ...
			} else if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase(ARG_TRUE)) {
				//  #2.C.a. Eventually make a first sync		
				if (startOrTime.equalsIgnoreCase(ARG_START))  {
					// #2.C.a.1. If it is a (daySpeed == nightSpeed) world ... 
					if (daySpeed == nightSpeed) { // Next tick = Start at #tick + (Elapsed time * speed modifier)
						newTime = (long) ((startAtTickNb + (elapsedServerTime * speed)) % 24000);
					// #2.C.a.2. ... or if it is a (daySpeed != nightSpeed) world
					} else { // Next tick is calculated with specific method
						newTime = differentSpeedsNewTime(world, startAtTickNb, elapsedServerTime, currentServerTick, speedAtStart, daySpeed, nightSpeed, true);
					}
				}
				// #2.C.b. Notifications
				MsgHandler.infoMsg("The world " + world + " " + noResyncNeededMsg); // Console final msg (always)
				MsgHandler.playerAdminMsg(sender, "The world §e" + world + " §r" + noResyncNeededMsg); // Player final msg (in case)
			
			// #2.D. ... or if it is an exception at the first start time calculation : 'previous' or 'start'	
			} else if (!firstStartTime.equalsIgnoreCase(ARG_DEFAULT)) {
				// #2.D.a. Next tick = (original #tick)
				long oldTime = Bukkit.getServer().getWorld(world).getTime();
				newTime = oldTime;
				// #2.D.b. Notifications
				MsgHandler.infoMsg("The world " + world + " " + worldPreviousTimeResetMsg); // Console final msg (always)
				MsgHandler.playerAdminMsg(sender, "The world §e" + world + " §r" + worldPreviousTimeResetMsg); // Player final msg (in case)
					
			// #2.E. ... or if it is an exception at the first start time calculation : 'START' ...	
			} else if (firstStartTime.equalsIgnoreCase(ARG_START) && startOrTime.equalsIgnoreCase(ARG_START)) {
				// #2.E.a. Next tick = (cfg 'start' #tick)
				newTime = startAtTickNb;
				// #2.E.b. Notifications
				MsgHandler.infoMsg("The world " + world + " " + worldStartTimeResetMsg); // Console final msg (always)
				MsgHandler.playerAdminMsg(sender, "The world §e" + world + " §r" + worldStartTimeResetMsg); // Player final msg (in case)
				
			// #2.F. ... or if it is a (daySpeed == nightSpeed) world ...
			} else if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED).equalsIgnoreCase(MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED))) {
				// #2.F.a. Next tick = StartAtTick + (Elapsed time * speedModifier)
				newTime = (long) (startAtTickNb + ((ValuesConverter.correctDailyTicks(((currentServerTick - initialTick))) * speed) % 24000));
				// #2.F.b. Notifications
				MsgHandler.infoMsg("The world " + world + " " + resyncDoneMsg); // Console final msg (always)
				MsgHandler.playerAdminMsg(sender, "The world §e" + world + " §r" + resyncDoneMsg); // Player final msg (in case)		
				// #2.F.c. Debug Msg
				MsgHandler.debugMsg("Resync: Calculation of " + actualTimeVar + " for world §e" + world + "§b:");
				MsgHandler.debugMsg(elapsedTimeCalculation
						+ " = (§8" + currentServerTick + " §b- §7" + initialTick + "§b) % §624000 §b="
						+ " §d" + ((currentServerTick - initialTick) % 24000)
						+ " §b(Restrained to one day = §d" + ValuesConverter.correctDailyTicks(((currentServerTick % 24000) - (initialTick % 24000))) + "§b)"); // Console debug msg
				MsgHandler.debugMsg(worldElapsedTimeCalculation
						+ " = §d" + ((ValuesConverter.correctDailyTicks(((currentServerTick - initialTick % 24000))) + " §b* §a" + speed
						+ " §b= §5" + ((ValuesConverter.correctDailyTicks(((currentServerTick - initialTick) % 24000))) * speed)))); // Console debug msg
				MsgHandler.debugMsg(actualTimeCalculation
						+ " = §e" + startAtTickNb + " §b+ §5" + ((ValuesConverter.correctDailyTicks(((currentServerTick - initialTick) % 24000))) * speed)
						+ " §b= §c" + (startAtTickNb + ((ValuesConverter.correctDailyTicks(((currentServerTick - initialTick) % 24000))) * speed))
						+ " §b(Restrained to one day = §ctick #" + ValuesConverter.correctDailyTicks(newTime) + "§b)"); // Console debug msg
				
			// #2.G. ... or if it is a (daySpeed != nightSpeed) world
			} else {
				// #2.G.a Next tick is calculated with specific method
				newTime = differentSpeedsNewTime(world, startAtTickNb, elapsedServerTime, currentServerTick, speedAtStart, daySpeed, nightSpeed, true);
				// #2.G.b Notifications
				MsgHandler.infoMsg("The world " + world + " " + resyncDoneMsg); // Console final msg (always)
				MsgHandler.playerAdminMsg(sender, "The world §e" + world + " §r" + resyncDoneMsg); // Player final msg (in case)
			}

			// #2.H. Apply modifications
			newTime = ValuesConverter.correctDailyTicks(newTime);
			Bukkit.getServer().getWorld(world).setTime(newTime);

			// #2.I. Adjust doDaylightCycle value
			DoDaylightCycleHandler.adjustDaylightCycle(world);
			
			// #2.J. Restore the number of elapsed days
			Long nft = (initElapsedDays * 24000) + newTime;
			Bukkit.getWorld(world).setFullTime(nft);

			// #2.K. Extra notifications (for each cases)
			String listedWorldStartTime = ValuesConverter.formattedTimeFromTick(startAtTickNb);
			String listedWorldCurrentTime = ValuesConverter.formattedTimeFromTick(newTime);
			String formattedUTC = ValuesConverter.formattedUTCShiftfromTick(startAtTickNb);
			String formattedUTCTick = ValuesConverter.tickUTCShiftfromTick(startAtTickNb);
			String elapsedDays = initElapsedDays.toString();
			String date = ValuesConverter.dateFromElapsedDays(initElapsedDays, PH_YYYY) + "-" + ValuesConverter.dateFromElapsedDays(initElapsedDays, PH_MM) + "-" + ValuesConverter.dateFromElapsedDays(initElapsedDays, PH_DD);
			if (speed == realtimeSpeed) { // Display realtime message (speed is equal to 24.00)
				MsgHandler.infoMsg("The world " + world + " " + worldCurrentElapsedDaysMsg + " " + elapsedDays + " whole day(s) (" + date + ")."); // Final console msg
				MsgHandler.infoMsg("The world " + world + " " + worldCurrentStartMsg + " " + formattedUTC + " (" + formattedUTCTick + " ticks)."); // Final console msg
				MsgHandler.infoMsg("The world " + world + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " (#" + newTime + ")."); // Final console msg
				MsgHandler.infoMsg("The world " + world + worldCurrentSpeedMsg + " " + worldRealSpeedMsg); // Final console msg
			} else if (daySpeed == nightSpeed) { // Display usual message (one speed)
				MsgHandler.infoMsg("The world " + world + " " + worldCurrentElapsedDaysMsg + " " + elapsedDays + " whole day(s) (" + date + ")."); // Final console msg
				MsgHandler.infoMsg("The world " + world + " " + worldCurrentStartMsg + " " + listedWorldStartTime + " (+" + startAtTickNb + " ticks)."); // Final console msg
				MsgHandler.infoMsg("The world " + world + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " (#" + newTime + ")."); // Final console msg
				MsgHandler.infoMsg("The world " + world + worldCurrentSpeedMsg + " " + MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED) + "."); // Final console msg
			} else if (daySpeed != nightSpeed) { // Display usual message (two different speeds)
				MsgHandler.infoMsg("The world " + world + " " + worldCurrentElapsedDaysMsg + " " + elapsedDays + " whole day(s) (" + date + ")."); // Final console msg
				MsgHandler.infoMsg("The world " + world + " " + worldCurrentStartMsg + " " + listedWorldStartTime + " (+" + startAtTickNb + " ticks)."); // Final console msg
				MsgHandler.infoMsg("The world " + world + worldCurrentTimeMsg + " " + listedWorldCurrentTime + " (#" + newTime + ")."); // Final console msg
				MsgHandler.infoMsg("The world " + world + worldCurrentDaySpeedMsg + " " + MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED) + "."); // Final console msg
				MsgHandler.infoMsg("The world " + world + worldCurrentNightSpeedMsg + " " + MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED) + "."); // Final console msg
			}
		}
	}

	/**
	 * Calculate world time when daySpeed and nightSpeed are not equal (returns a long)
	 */
	public static long differentSpeedsNewTime(String world, long worldStartAt, long elapsedServerTime, long currentServerTick, double speedAtStart, double daySpeed, double nightSpeed, boolean displayMsg) {
		// Get the required server time for spending a day or a night or both in the target world
		long worldDayTimeInServerTicks = (long) (13000 / daySpeed);
		long worldNightTimeInServerTicks = (long) (11000 / nightSpeed);
		long worldFullTimeInServerTicks = worldDayTimeInServerTicks + worldNightTimeInServerTicks;
		// Use two variables for speed, depending of day/night starting time
		double firstSpeed = daySpeed; // if world starts with a day
		double secondSpeed = nightSpeed;
		long firstHalfDaylightCycle = 13000;
		long secondHalfDaylightCycle = 11000;
		long firstCycleDuration = worldDayTimeInServerTicks;
		long secondCycleDuration = worldNightTimeInServerTicks;
		if (speedAtStart == nightSpeed) { // if world starts with a night
			firstSpeed = nightSpeed;	
			secondSpeed = daySpeed;
			firstHalfDaylightCycle = 11000;
			secondHalfDaylightCycle = 13000;
			firstCycleDuration = worldNightTimeInServerTicks;
			secondCycleDuration = worldDayTimeInServerTicks;
		}
		// Use a clone of elapsedTime to subtract the number of ticks remaining
		long serverRemainingTime = elapsedServerTime;
		long newTime;
		
		// #1. If elapsed time is smaller than the difference between an half day minus the startTime (= no day/night change) ...
		if ((elapsedServerTime * firstSpeed) < (firstHalfDaylightCycle - (worldStartAt % firstHalfDaylightCycle))) {
			// #1.A. Use the classic easy formula
			newTime = (long) ((worldStartAt + (elapsedServerTime * firstSpeed)) % 24000);
			// #1.B. Debug Msg
			if (displayMsg) {
				MsgHandler.debugMsg("Resync: Calculation of " + actualTimeVar + " for world §e" + world + "§b:");
				MsgHandler.debugMsg(elapsedTimeCalculation
						+ " = (§8" + currentServerTick + " §b- §7" + initialTick + "§b) % §624000 §b="
						+ " §d" + ((currentServerTick - initialTick) % 24000)
						+ " §b(Restrained to one day = §d" + ValuesConverter.correctDailyTicks(((currentServerTick % 24000) - (initialTick % 24000))) + "§b)"); // Console debug msg
				MsgHandler.debugMsg(worldElapsedTimeCalculation
						+ " = §d" + ((ValuesConverter.correctDailyTicks(((currentServerTick - initialTick % 24000))) + " §b* §a" + firstSpeed
						+ " §b= §5" + ((ValuesConverter.correctDailyTicks(((currentServerTick - initialTick) % 24000))) * firstSpeed)))); // Console debug msg
				MsgHandler.debugMsg(actualTimeCalculation
						+ " = §e" + worldStartAt + " §b+ §5" + ((ValuesConverter.correctDailyTicks(((currentServerTick - initialTick) % 24000))) * firstSpeed)
						+ " §b= §c" + (worldStartAt + ((ValuesConverter.correctDailyTicks(((currentServerTick - initialTick) % 24000))) * firstSpeed))
						+ " §b(Restrained to one day = §ctick #" + ValuesConverter.correctDailyTicks(newTime) + "§b)"); // Console debug msg
			}
		// #2. ... else if elapsed time is bigger than an half-day (= a least one day/night change)
		} else {			
			
			// #2.A. Count the elapsed 1st half-cycle (day or night)
				newTime = firstHalfDaylightCycle; // (+) 1st complete half-cycle
				serverRemainingTime = (long) (elapsedServerTime - firstCycleDuration + (worldStartAt / firstSpeed)); // (-) 1st complete half-cycle
				// #2.B. Count down all full-days
				if (serverRemainingTime > worldFullTimeInServerTicks) {
					serverRemainingTime = (long) (serverRemainingTime % (worldFullTimeInServerTicks)); // (-) all full daylightCycles
				}
				// #2.C.a. Count an eventual complete half-cycle (day or night) ...
				if (serverRemainingTime > secondCycleDuration) {
					newTime = (long) (newTime + secondHalfDaylightCycle); // (+) 2nd complete half-cycle
					serverRemainingTime = serverRemainingTime - secondCycleDuration; // (-) 2nd complete half-cycle

					// #2.C.b. ... and finally count the rest of the last half-cycle (day or night)
					newTime = (long) (newTime + (serverRemainingTime * firstSpeed)); // (+) 3nd and last partial half-cycle

				// #2.D. ... or directly count the rest of the last half-cycle (day or night)
				} else {
					newTime = (long) (newTime + (serverRemainingTime * secondSpeed)); // (+) 2nd and last partial half-cycle
			}	
			// #2.E. Restrain too big and too small values
			newTime = ValuesConverter.correctDailyTicks(newTime);
			
			// #2.F. Debug Msg
			if (displayMsg) {
				MsgHandler.debugMsg("Resync: Calculation of " + actualTimeVar + " for world §e" + world + "§b:");
				MsgHandler.debugMsg(serverRemainingTimeVar
						+ " = (" + elapsedTimeVar + " - ((" + firstHalfDaylightCycleVar + " - " + worldStartAtVar + ")"
						+ " / (" + firstSpeedModifierVar + ")))"
						+ " % ((" + firstHalfDaylightCycleVar + " / " + firstSpeedModifierVar
						+ ") + (" + secondHalfDaylightCycleVar + " / " + secondSpeedModifierVar + ")))"
						+ " - (" + secondHalfDaylightCycleVar + ")"
						+ " / (" + firstSpeedModifierVar + " || " + secondSpeedModifierVar + "))"
						+ " = §5" + serverRemainingTime); // Console debug msg
				MsgHandler.debugMsg(actualTimeVar
						+ " = " + firstHalfDaylightCycleVar
						+ " + (" + serverRemainingTimeVar + " * " + secondSpeedModifierVar
						+ " || " + secondHalfDaylightCycleVar + " + " + serverRemainingTimeVar + " * "
						+ firstSpeedModifierVar + ")"
						+ " = §c" + "§ctick #" + newTime); // Console debug msg
			}
		} 
		return newTime;
	}

	/**
	 * Delayed actualization of the initialTickNb if the MySQL parameter is on
	 */
	public static void refreshInitialTickMySql() {
		long refreshTimeInTick = (sqlInitialTickAutoUpdateValue * 1200);
		BukkitScheduler firstSyncSheduler = MainTM.getInstance().getServer().getScheduler();
		firstSyncSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equalsIgnoreCase(ARG_TRUE)) {
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
		if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equalsIgnoreCase(ARG_TRUE)) { // If mySQL is true
			if (SqlHandler.openTheConnectionIfPossible(msgOnOff) == true) {
				if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_RESETONSTARTUP).equalsIgnoreCase(ARG_FALSE)) { // If reset false
					// Try to read database
					initialTick = SqlHandler.getServerTickSQL(); // Get existing reference tick from SQL database
					if (initialTick == null) { // If db is null, create the initial tick
						initialTick = ValuesConverter.getServerTick();
						SqlHandler.setServerTickSQL(initialTick); // Save tick in SQL database
					}
					initialTime = ValuesConverter.realFormattedTimeFromTick(initialTick); // Convert the initial time in HH:mm:ss UTC
					MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_INITIALTICKNB, initialTick); // Save tick in config
					setOrGet = "get from";
				} else { // If reset is true
					// Define a new reference tick
					initialTick = ValuesConverter.getServerTick(); // Create the initial tick
					initialTime = ValuesConverter.getServerTime(); // Create the initial time in HH:mm:ss UTC
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
		} else if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equalsIgnoreCase(ARG_FALSE)) { // When mySQL is false
			// If reset true OR initialTickNb doesn't exist
			if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_RESETONSTARTUP).equalsIgnoreCase(ARG_FALSE) && !MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_INITIALTICKNB).equals("")) {
				// If reset false AND initialTickNb exists
				initialTick = MainTM.getInstance().getConfig().getLong(CF_INITIALTICK + "." + CF_INITIALTICKNB); // Get existing reference tick from config.yml
				initialTime = ValuesConverter.realFormattedTimeFromTick(initialTick); // Convert the initial time in HH:mm:ss UTC
				setOrGet = "get from";
			} else { // Define a new reference tick
				initialTick = ValuesConverter.getServerTick(); // Create the initial tick
				initialTime = ValuesConverter.realFormattedTimeFromTick(initialTick); // Convert the initial time in HH:mm:ss UTC
				MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_INITIALTICKNB, initialTick); // Save tick in config.yml
				setOrGet = "set in";
			}
			ymlOrSql = "the config.yml";
		}
		MainTM.getInstance().saveConfig(); // Save config.yml file
		SqlHandler.closeConnection("DB"); // Close connection
		if (msgOnOff) {
			MsgHandler.infoMsg("The server's initial tick was " + setOrGet + " " + ymlOrSql + "."); // Console log msg
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
		if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equalsIgnoreCase(ARG_TRUE)) {
			if (SqlHandler.openTheConnectionIfPossible(true)) {
				sqlTick = SqlHandler.getServerTickSQL();
			}
		}
		// If mySql is false, try to actualize the configuration:
		if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equalsIgnoreCase(ARG_FALSE)) {
			// If there are changes in the configuration:
			if (!(oldTick.equals(newTick))) {
				// Actualize the global variables
				initialTick = newTick;
				initialTime = ValuesConverter.realFormattedTimeFromTick(initialTick);
				// Notifications
				MsgHandler.infoMsg(initialTickYmlMsg); // Notify the console
				TmCheckTime.cmdCheckTime(Bukkit.getServer().getConsoleSender(), ARG_SERVER); // Notify the console
			}
			// Else if there are NO changes in the configuration: Don't do nothing at all
			// Else, if mySql is true, try to set or get the initialTickNb:
		} else if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equalsIgnoreCase(ARG_TRUE)) {
			// If there are changes in the configuration:
			if (!(oldTick.equals(newTick))) {
				// Actualize the global variables
				initialTick = newTick;
				initialTime = ValuesConverter.realFormattedTimeFromTick(initialTick);
				// Actualize the MySQL database
				SqlHandler.updateServerTickSQL(newTick);
				// Notifications
				MsgHandler.infoMsg(initialTickSqlMsg); // Notify the console
				TmCheckTime.cmdCheckTime(Bukkit.getServer().getConsoleSender(), ARG_SERVER); // Notify the console
				// Else, if there are NO changes in the configuration AND if sqlTick is null:
			} else if (sqlTick == null) {
				// Actualize the global variables
				initialTick = newTick;
				initialTime = ValuesConverter.realFormattedTimeFromTick(initialTick);
				// Actualize the MySQL database
				SqlHandler.updateServerTickSQL(newTick);
				// Notifications
				MsgHandler.infoMsg(initialTickSqlMsg); // Notify the console
				TmCheckTime.cmdCheckTime(Bukkit.getServer().getConsoleSender(), ARG_SERVER); // Notify the console
				// Else, if there are NO changes in the configuration AND if sqlTick isn't null
				// AND if sqlTick is different from the newTick:
			} else if (!(sqlTick.equals(newTick))) {
				// Actualize the configuration
				MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_INITIALTICKNB, sqlTick);
				// Actualize the global variables
				initialTick = sqlTick;
				initialTime = ValuesConverter.realFormattedTimeFromTick(initialTick);
				// Notifications
				MsgHandler.infoMsg(initialTickGetFromSqlMsg); // Notify the console
				TmCheckTime.cmdCheckTime(Bukkit.getServer().getConsoleSender(), ARG_SERVER); // Notify the console
			}
			// Else, don't do nothing at all
		}
	}

};