package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;

public class WorldSpeedHandler extends MainTM {

	/**
	 * Increase worlds speed to a custom rate with an auto cancel/repeat capable scheduler
	 */
	public static void WorldIncreaseSpeed() {
		increaseScheduleIsOn = true;
		refreshRateLong = MainTM.getInstance().getConfig().getLong(CF_REFRESHRATE);

		BukkitScheduler speedSheduler = MainTM.getInstance().getServer().getScheduler();
		speedSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				boolean loopMore = false;
				for (String world : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
					// Get the current time of the world
					long currentTime = Bukkit.getWorld(world).getTime();
					// Get the current 'speed' value
					double speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + wichSpeedParam(currentTime)); // Get the current speed of the world
					long newTime = 0L;
					if (speed >= 1.0 && speed <= speedMax && speed != 24.00) { // Only treat worlds with normal or increased timers
						// #A. Synchronized time calculation
						if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase("true")) {
							loopMore = true;
							// Get the current server time
							long currentServerTick = ValuesConverter.returnServerTick();
							// Get the world's 'start' value
							long startAtTickNb = (MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + CF_START));
							// Get the world's speed value at server start
							double speedAtStart = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + WorldSpeedHandler.wichSpeedParam(startAtTickNb));
							// Get the world's 'daySpeed' value
							double daySpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED);
							// Get the world's 'nightSpeed' value
							double nightSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED);
							// Get the total server's elapsed time
							long elapsedServerTime = currentServerTick - initialTick;
							// #A.1. If it is a (daySpeed == nightSpeed) world ...
							if (daySpeed == nightSpeed) { // Next tick = Start at #tick + (Elapsed time * speed modifier)
								newTime = (long) ((startAtTickNb + (elapsedServerTime * speed)) % 24000);
								if (timerMode) {
									Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Calculation of " + actualTimeVar + ":");
									Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + elapsedTimeCalculation + " = (§8" + currentServerTick + " §b- §7" + initialTick + "§b) % §624000 §b= §d" + ((currentServerTick - initialTick) % 24000) + " §brestrained to one day = §d" + ValuesConverter.returnCorrectTicks(((currentServerTick % 24000) - (initialTick % 24000)))); // Console debug msg
									Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + adjustedElapsedTimeCalculation + " = §d" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick % 24000))) + " §b* §a" + speed + " §b= §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speed)))); // Console debug msg
									Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + actualTimeCalculation + " = §e" + startAtTickNb + " §b+ §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speed) + " §b= §c" + (startAtTickNb + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speed)) + " §brestrained to one day = §ctick #" + ValuesConverter.returnCorrectTicks(newTime)); // Console debug msg
								}
								// #A.2. ... or if it is a (daySpeed != nightSpeed) world
							} else {
								newTime = worldWithDifferentSpeedNewTime(world, startAtTickNb, elapsedServerTime, currentServerTick, speedAtStart, daySpeed, nightSpeed, false); // TODO
							}
							// #B. Normal time calculation
						} else if (speed > 1.0) { // Only treat worlds with increased timers
							loopMore = true;
							long modifTime = (long) Math.ceil(refreshRateInt * speed);
							newTime = currentTime + modifTime - refreshRateLong;
						}
						if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase("true") || speed > 1.0) {
							// Restrain too big and too small values
							newTime = ValuesConverter.returnCorrectTicks(newTime);
							// Change world's timer
							Bukkit.getWorld(world).setTime(newTime);
						}
					}
				}
				if (loopMore == true) {
					WorldIncreaseSpeed();
				} else {
					increaseScheduleIsOn = false;
				}
			}
		}, refreshRateLong);
	}

	/**
	 * Decrease worlds speed to a custom rate with an auto cancel/repeat capable scheduler
	 */
	public static void WorldDecreaseSpeed() {
		
		decreaseScheduleIsOn = true;
		refreshRateLong = MainTM.getInstance().getConfig().getLong(CF_REFRESHRATE);

		BukkitScheduler speedSheduler = MainTM.getInstance().getServer().getScheduler();
		speedSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				boolean loopMore = false;
				for (String world : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
					// Get the current time of the world
					long currentTime = Bukkit.getWorld(world).getTime();
					// Get the current 'speed' value
					double speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + wichSpeedParam(currentTime));
					long newTime;
					// Only treat worlds with decreased timers
					if (speed > 0.0 && speed < 1.0) {
						// By default, launch the loop again
						loopMore = true;
						
						// #A. Synchronized time calculation
						if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase("true")) {
							// Get the current server time
							long currentServerTick = ValuesConverter.returnServerTick();
							// Get the world's 'start' value
							long startAtTickNb = (MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + CF_START));
							// Get the world's speed value at server start
							double speedAtStart = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + WorldSpeedHandler.wichSpeedParam(startAtTickNb));
							// Get the world's 'daySpeed' value
							double daySpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED);
							// Get the world's 'nightSpeed' value
							double nightSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED);
							// Get the total server's elapsed time
							long elapsedServerTime = currentServerTick - initialTick;
							
							// #A.1. if it is a (daySpeed == nightSpeed) world
							if (daySpeed == nightSpeed) { // Next tick = Start at #tick + (Elapsed time * speed modifier)
								newTime = (long) ((startAtTickNb + (elapsedServerTime * speed)) % 24000);
							
								// #A.2. ... or if it is a (daySpeed != nightSpeed) world 
							} else {
								newTime = worldWithDifferentSpeedNewTime(world, startAtTickNb, elapsedServerTime, currentServerTick, speedAtStart, daySpeed, nightSpeed, false);
							}
							
						// #B. Normal time calculation
						} else {
							// Try to compensate for missing ticks due to decimals
							Integer missedTicks = (int) Math.round((refreshRateInt * speed * 10) % 10); // turn the decimal into an independent integer
							Integer randomTicks = 0; // By default, no tick would be added
							if (timerMode == true)
								Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Missed ticks for world " + world + " is 0," + missedTicks); // Dev console msg
							if (missedTicks > 0) { // But if the decimal is bigger than 0
								int range = (10 - missedTicks) + 1; // Define a range between the decimal value and 10
								Integer randomNb = (int) (Math.random() * range) + missedTicks; // Create a random number in that range
								if (timerMode == true)
									Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Random roll: " + randomNb); // Dev console msg
								if (randomNb <= missedTicks)
									randomTicks = 1; // If the random number is smaller than or equals the decimal, add 1 tick to the total count
							}
							if (timerMode == true)
								Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Added " + randomTicks + " random ticks for world " + world); // Dev console msg
							long modifTime = (long) Math.floor((refreshRateInt * speed));
							newTime = currentTime + modifTime + randomTicks;
						}
						
						// Restrain too big and too small values
						newTime = ValuesConverter.returnCorrectTicks(newTime);
						// Change world's timer
						Bukkit.getWorld(world).setTime(newTime);
					}
				}
				if (loopMore == true) {
					WorldDecreaseSpeed();
				} else {
					decreaseScheduleIsOn = false;
				}
			}
		}, refreshRateLong);
	}

	/**
	 * Modify worlds speed to real time speed with an auto cancel/repeat capable scheduler
	 */
	public static void WorldRealSpeed() {
		realScheduleIsOn = true;
		BukkitScheduler realSpeedSheduler = MainTM.getInstance().getServer().getScheduler();
		realSpeedSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				boolean loopMore = false;
				for (String world : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
					long t = Bukkit.getWorld(world).getTime();
					Double speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + wichSpeedParam(t));
					long worldStartAt = MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + CF_START);
					if (speed == 24.00) { // Only treat worlds with a '24.0' timers
						loopMore = true;
						// Get the current server tick
						long currentServerTick = ValuesConverter.returnServerTick();
						long newTime = (currentServerTick / 72L) + (worldStartAt - 6000L); // -6000 cause a mc's day start at 6:00
						// Restrain too big and too small values
						newTime = ValuesConverter.returnCorrectTicks(newTime);
						// Change world's timer
						Bukkit.getWorld(world).setTime(newTime);
					}
				}
				if (loopMore == true) {
					WorldRealSpeed();
				} else {
					realScheduleIsOn = false;
				}
			}
		}, 72L);
	}

	/**
	 * Calculate world time when daySpeed and nightSpeed are not equal (returns a long)
	 */
	public static long worldWithDifferentSpeedNewTime(String world, long startAtTickNb, long elapsedServerTime, long currentServerTick, double speedAtStart, double daySpeed, double nightSpeed, boolean displayMsg) {
		// Get the required server time for spending a day or a night or both in the target world
		long worldDayTimeInServerTicks = (long) (12000 / daySpeed);
		long worldNightTimeInServerTicks = (long) (12000 / nightSpeed);
		long worldFullTimeInServerTicks = worldDayTimeInServerTicks + worldNightTimeInServerTicks;
		// Use two variables for speed, depending of day/night starting time
		double firstSpeed = daySpeed; // day
		double secondSpeed = daySpeed;
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
		// #1. If elapsed time is smaller than an half day minus the startTime (= no day/night change) ...
		if ((elapsedServerTime * firstSpeed) < (12000 - startAtTickNb)) {
			// #1.A. Use the classic easy formula
			newTime = (long) (startAtTickNb + (elapsedServerTime * firstSpeed));
			// #1.B. Debug Msg
			if (debugMode && displayMsg == true) {
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
			// #2.D. Debug Msg
			if (debugMode && displayMsg) {
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Resync: Calculation of " + actualTimeVar + " for world §e" + world + "§b:");
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + serverRemainingTimeVar + " = (" + elapsedTimeVar + " - ((" + halfDaylightCycleVar + " - " + worldStartAtVar + ") / (" + daySpeedModifierVar + " || " + nightSpeedModifierVar + "))) % ((§f12000 §b/ " + daySpeedModifierVar + ") + (§f12000 §b/ " + nightSpeedModifierVar + "))) - (§f0 §b|| §f12000§b) / (" + daySpeedModifierVar + " || " + nightSpeedModifierVar + ")) = §5" + serverRemainingTime); // Console debug msg
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + actualTimeVar + " = " + halfDaylightCycleVar + " + §b|(§f0 §b|| §f12000§b) + (" + serverRemainingTimeVar + " * (" + daySpeedModifierVar + " || " + nightSpeedModifierVar + ")) = §c" + "§ctick #" + newTime); // Console debug msg
			}
		} 
		return newTime;
	}

	/**
	 * Return a string with the correct speed multiplier (daySpeed or nightSpeed)
	 */
	public static String wichSpeedParam(long tick) {
		String speedParam;
		if (ValuesConverter.getDayPartToDisplay(tick).equalsIgnoreCase("day") || ValuesConverter.getDayPartToDisplay(tick).equalsIgnoreCase("dusk")) {	    
			speedParam = CF_D_SPEED;	
		} else {
			speedParam = CF_N_SPEED;
		}
		return speedParam;
	}

};