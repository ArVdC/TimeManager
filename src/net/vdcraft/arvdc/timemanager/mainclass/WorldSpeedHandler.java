package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;

public class WorldSpeedHandler extends MainTM {

	/**
	 * Increase worlds speed to a custom rate with an auto cancel/repeat capable scheduler
	 */
	public static void worldIncreaseSpeed() {
		
		increaseScheduleIsOn = true;
		refreshRateLong = MainTM.getInstance().getConfig().getLong(CF_REFRESHRATE);

		BukkitScheduler speedSheduler = MainTM.getInstance().getServer().getScheduler();
		speedSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {

				// By default, don't launch the loop again
				boolean loopAgain = false;

				for (String world : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
					// Get the current time of the world
					long currentTime = Bukkit.getWorld(world).getTime();
					// Get the current 'speed' value
					double speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(currentTime));
					// Get the current 'sync' value
					String sync = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC);

					long newTime = 0L;

					// Only treat worlds with normal or increased timers
					if (speed >= 1.0 && speed <= speedMax) {

						// #A. Synchronized time calculation
						if (sync.equalsIgnoreCase("true")) {
							// Get the current server time
							long currentServerTick = ValuesConverter.returnServerTick();
							// Get the world's 'start' value
							long startAtTickNb = (MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + CF_START));
							// Get the world's speed value at server start
							double speedAtStart = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(startAtTickNb));
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
									Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Calculation of " + actualTimeVar + " for world §e" + world + "§b:");
									Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + elapsedTimeCalculation + " = (§8" + currentServerTick + " §b- §7" + initialTick + "§b) % §624000 §b= §d" + ((currentServerTick - initialTick) % 24000) + " §brestrained to one day = §d" + ValuesConverter.returnCorrectTicks(((currentServerTick % 24000) - (initialTick % 24000)))); // Console debug msg
									Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + adjustedElapsedTimeCalculation + " = §d" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick % 24000))) + " §b* §a" + speed + " §b= §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speed)))); // Console debug msg
									Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + actualTimeCalculation + " = §e" + startAtTickNb + " §b+ §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speed) + " §b= §c" + (startAtTickNb + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speed)) + " §brestrained to one day = §ctick #" + ValuesConverter.returnCorrectTicks(newTime)); // Console debug msg
								}

							// #A.2. ... or if it is a (daySpeed != nightSpeed) world
							} else {
								newTime = WorldSyncHandler.differentSpeedsNewTime(world, startAtTickNb, elapsedServerTime, currentServerTick, speedAtStart, daySpeed, nightSpeed, false);
							}

						// #B. Normal time calculation
						} else if (speed > 1.0) { // Only treat worlds with increased timers
							long modifTime = (long) Math.ceil(refreshRateInt * speed);
							newTime = currentTime + modifTime - refreshRateLong;
						}
						// Restrain too big and too small values
						newTime = ValuesConverter.returnCorrectTicks(newTime);
						// Change world's timer (not for 1.0 unsynchronized worlds)
						if ((speed != 1.0) || ((speed == 1.0) && (sync.equalsIgnoreCase("true")))) Bukkit.getWorld(world).setTime(newTime);
						// Check if there is a new 'speed' value
						double newSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(newTime));
						// If any world is concerned, launch the loop again
						if (newSpeed > 1.0 || (newSpeed == 1 && sync.equalsIgnoreCase("true"))) loopAgain = true;
						// Change the doDaylightCycle gamerule if it is needed				
						if (newSpeed < 1.0) WorldDoDaylightCycleHandler.doDaylightSet(world);
						// Activate the decrease schedule if it is needed and not already activated
						if (decreaseScheduleIsOn == false && (newSpeed < 1.0 && newSpeed > 0.0)) worldDecreaseSpeed();
					}
				}
				// Permit or not to launch the loop again
				if (loopAgain == true) worldIncreaseSpeed();
				else increaseScheduleIsOn = false;
			}
		}, refreshRateLong);
	}

	/**
	 * Decrease worlds speed to a custom rate with an auto cancel/repeat capable scheduler
	 */
	public static void worldDecreaseSpeed() {

		decreaseScheduleIsOn = true;
		refreshRateLong = MainTM.getInstance().getConfig().getLong(CF_REFRESHRATE);

		BukkitScheduler speedSheduler = MainTM.getInstance().getServer().getScheduler();
		speedSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {

				// By default, don't launch the loop again
				boolean loopAgain = false;

				for (String world : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
					// Get the current time of the world
					long currentTime = Bukkit.getWorld(world).getTime();
					// Get the current 'speed' value
					double speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(currentTime));
					// Get the current 'sync' value
					String sync = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC);

					long newTime = 0L;

					// Only treat worlds with decreased timers
					if (speed > 0.0 && speed < 1.0) {

						// #A. Synchronized time calculation
						if (sync.equalsIgnoreCase("true")) {
							// Get the current server time
							long currentServerTick = ValuesConverter.returnServerTick();
							// Get the world's 'start' value
							long startAtTickNb = (MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + CF_START));
							// Get the world's speed value at server start
							double speedAtStart = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(startAtTickNb));
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
									Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Calculation of " + actualTimeVar + " for world §e" + world + "§b:");
									Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + elapsedTimeCalculation + " = (§8" + currentServerTick + " §b- §7" + initialTick + "§b) % §624000 §b= §d" + ((currentServerTick - initialTick) % 24000) + " §brestrained to one day = §d" + ValuesConverter.returnCorrectTicks(((currentServerTick % 24000) - (initialTick % 24000)))); // Console debug msg
									Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + adjustedElapsedTimeCalculation + " = §d" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick % 24000))) + " §b* §a" + speed + " §b= §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speed)))); // Console debug msg
									Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + actualTimeCalculation + " = §e" + startAtTickNb + " §b+ §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speed) + " §b= §c" + (startAtTickNb + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speed)) + " §brestrained to one day = §ctick #" + ValuesConverter.returnCorrectTicks(newTime)); // Console debug msg
								}

							// #A.2. ... or if it is a (daySpeed != nightSpeed) world 
							} else {
								newTime = WorldSyncHandler.differentSpeedsNewTime(world, startAtTickNb, elapsedServerTime, currentServerTick, speedAtStart, daySpeed, nightSpeed, false);
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
						// Check if there is a new 'speed' value
						double newSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(newTime));						
						// If any world is concerned, launch the loop again
						if (newSpeed < 1.0 && newSpeed > 0.0) loopAgain = true;
						// Change the doDaylightCycle gamerule if it is needed
						if (newSpeed >= 1.0) WorldDoDaylightCycleHandler.doDaylightSet(world);
						// Activate the increase schedule if it is needed and not already activated
						if (increaseScheduleIsOn == false && ((MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase("true") && newSpeed == 1.0) || newSpeed > 1.0)) {
							worldIncreaseSpeed();
						}
					}
				}
				// Permit or not to launch the loop again
				if (loopAgain == true) worldDecreaseSpeed();
				else decreaseScheduleIsOn = false;
			}
		}, refreshRateLong);
	}

	/**
	 * Modify worlds speed to real time speed with an auto cancel/repeat capable scheduler
	 */
	public static void worldRealSpeed() {
		
		realScheduleIsOn = true;
		
		BukkitScheduler realSpeedSheduler = MainTM.getInstance().getServer().getScheduler();
		realSpeedSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				
				// By default, don't launch the loop again
				boolean loopAgain = false;
				
				for (String world : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
					long t = Bukkit.getWorld(world).getTime();
					Double speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t));
					long worldStartAt = MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + CF_START);
					if (speed == 24.00) { // Only treat worlds with a '24' timer
						// Get the current server tick
						long currentServerTick = ValuesConverter.returnServerTick();
						long newTime = (currentServerTick / 72L) + (worldStartAt - 6000L); // -6000 cause a mc's day start at 6:00
						// Restrain too big and too small values
						newTime = ValuesConverter.returnCorrectTicks(newTime);
						// Change world's timer
						Bukkit.getWorld(world).setTime(newTime);
						// If any world is concerned, launch the loop again
						loopAgain = true;
					}
				}
				// Permit or not to launch the loop again
				if (loopAgain == true) worldRealSpeed();
				else realScheduleIsOn = false;
			}
		}, 72L);
	}

};