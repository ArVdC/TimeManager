package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;

public class SpeedHandler extends MainTM {

	/**
	 * Detect worlds that need to change their speed value
	 */
	public static void speedScheduler(String world) {

		// By default, use the refresh rate from the configuration file
		refreshRateLong = MainTM.getInstance().getConfig().getLong(CF_REFRESHRATE);

		// #A. Calculate time for all worlds
		if (world.equalsIgnoreCase(ARG_ALL)) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				speedScheduler(listedWorld);
			}

		// #B. Calculate time for a single world
		} else {

			// Get the current time of the world
			Long time = Bukkit.getWorld(world).getTime();
			// Get the current 'speed' value
			Double speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(time));
			// Get the current 'sync' value
			String sync = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC);

			// Don't treat frozen
			if (speed.equals(0.0)) {
				MsgHandler.debugMsg("The world §e" + world + "§b will no longer use any scheduler.");
			} else if (speed > 0.0) {
				// #B.1. 24h worlds
				if (speed.equals(realtimeSpeed)) {
					MsgHandler.debugMsg("The world §e" + world + "§b will now use the realtime speed scheduler.");
					if (!realSpeedSchedulerIsActive.contains(world)) {
						// Declare the world as having an active scheduler
						realSpeedSchedulerIsActive.add(world);
						// Launch real speed scheduler
						realSpeedScheduler(world);
					}
				// #B.2. Synchronized time calculation
				} else if (sync.equalsIgnoreCase(ARG_TRUE)) {
					MsgHandler.debugMsg("The world §e" + world + "§b will now use the synchronous speed scheduler.");
					if (!syncSpeedSchedulerIsActive.contains(world)) {
						// Declare the world as having an active scheduler
						syncSpeedSchedulerIsActive.add(world);
						// Launch synchronous speed scheduler
						syncSpeedScheduler(world, speed);
					}
				// #B.3. Normal time calculation
				} else if (!sync.equalsIgnoreCase(ARG_TRUE)) {							
					// #B.3.a. If it is an increased speed world
					if (speed > 1.0) {
						MsgHandler.debugMsg("The world §e" + world + "§b will now use the asynchronous increase speed scheduler.");
						if (!asyncIncreaseSpeedSchedulerIsActive.contains(world)) {
							// Declare the world as having an active scheduler
							asyncIncreaseSpeedSchedulerIsActive.add(world);
							// Launch asynchronous increase speed scheduler
							asyncSpeedIncreaseScheduler(world, speed);		
						}
					// #B.3.b. Or if it is a decreased speed world
					} else if (speed > 0.0 && speed < 1.0) {
						MsgHandler.debugMsg("The world §e" + world + "§b will now use the asynchronous decrease speed scheduler.");
						long refreshRate = ValuesConverter.fractionFromDecimal(speed, "refreshRate");
						if (!asyncDecreaseSpeedSchedulerIsActive.contains(world)) {
							// Declare the world as having an active scheduler
							asyncDecreaseSpeedSchedulerIsActive.add(world);
							// Launch asynchronous decrease speed scheduler 
							asyncSpeedDecreaseScheduler(world, speed, refreshRate);
						}
					} else if (speed.equals(1.0)) {
						if (speed.equals(0.0)) MsgHandler.debugMsg("The world §e" + world + "§b will no longer use any scheduler.");
					}
				}
			}
		}
	}


	/**
	 * Modify worlds speed to real time speed with an auto cancel/repeat capable scheduler
	 */
	public static void realSpeedScheduler(String world) {

		BukkitScheduler realSpeedSheduler = MainTM.getInstance().getServer().getScheduler();
		realSpeedSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {

				// Timer msg
				MsgHandler.timerMsg("World §e" + world + "§5 is running in the realtime speed scheduler.");
				MsgHandler.timerMsg("World §e" + world + "§5 speed = §e" + realtimeSpeed + "§5 | refreshRate = §e72§5 | tick = §e" + Bukkit.getWorld(world).getTime());
				// Get the world's 'speed' value
				long t = Bukkit.getWorld(world).getTime();
				Double speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t));
				// Get the world's 'start' value
				long worldStartAt = MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + CF_START);
				// Get the current server tick
				long currentServerTick = ValuesConverter.getServerTick();
				// Calculate the new time
				long newTime = (currentServerTick / 72L) + (worldStartAt - 6000L); // -6000 cause a mc's day start at 6:00

				// Restrain too big and too small values
				newTime = ValuesConverter.correctDailyTicks(newTime);
				// Change world's timer
				Bukkit.getWorld(world).setTime(newTime);

				// While the world is not cancelled and the speed is 24h, launch the loop again
				if (speed.equals(realtimeSpeed)) {
					realSpeedScheduler(world);
				} else {
					// Delete the world from the active scheduler list
					if (realSpeedSchedulerIsActive.contains(world)) realSpeedSchedulerIsActive.remove(world);
					MsgHandler.timerMsg("World " + world + " is §4cancelled§5 from the realtime speed scheduler.");
					// Detect if this world needs to change its speed value
					speedScheduler(world);
				}
			}
		}, 72L);
	}

	/**
	 * Modify synchronous worlds speed to a custom rate with an auto cancel/repeat capable scheduler
	 */
	public static void syncSpeedScheduler(String world, double currentSpeed) {

		BukkitScheduler syncSpeedScheduler = MainTM.getInstance().getServer().getScheduler();
		syncSpeedScheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {

				// Timer msg
				MsgHandler.timerMsg("World §e" + world + "§5 is running in the synchronous scheduler.");
				MsgHandler.timerMsg("World §e" + world + "§5 speed = §e" + currentSpeed + "§5 | refreshRate = §e" + refreshRateLong + "§5 | tick = §e" + Bukkit.getWorld(world).getTime());
				// Get the current server time
				long currentServerTick = ValuesConverter.getServerTick();
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

				// Calculate the new time ...
				Long newTime = 0L;
				// ... if it is a (daySpeed == nightSpeed) world ...
				if (daySpeed == nightSpeed) newTime = (long) ((startAtTickNb + (elapsedServerTime * currentSpeed)) % 24000); // Next tick = Start at #tick + (Elapsed time * speed modifier)
				// ... or if it is a (daySpeed != nightSpeed) world
				else newTime = SyncHandler.differentSpeedsNewTime(world, startAtTickNb, elapsedServerTime, currentServerTick, speedAtStart, daySpeed, nightSpeed, false);

				// Restrain too big and too small values
				newTime = ValuesConverter.correctDailyTicks(newTime);

				// Change the world's time
				Bukkit.getWorld(world).setTime(newTime);

				// Change the doDaylightCycle gamerule if it is needed
				double newSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(newTime));
				if ((newSpeed > 1 && currentSpeed <= 1) || (newSpeed <= 1 && currentSpeed > 1))
					DoDaylightCycleHandler.adjustDaylightCycle(world);

				// While the world is not cancelled and synchronous, launch the loop again
				if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase("true")
						&& newSpeed != 24.00) {
					syncSpeedScheduler(world, newSpeed);
				} else {
					// Delete the world from the active scheduler list
					if (syncSpeedSchedulerIsActive.contains(world)) syncSpeedSchedulerIsActive.remove(world);
					MsgHandler.timerMsg("World " + world + " is §4cancelled§5 from the synchronous speed scheduler.");
					// Detect if this world needs to change its speed value
					speedScheduler(world);
				}
			}
		}, refreshRateLong);
	}

	/**
	 * Increase asynchronous worlds speed to a custom rate with an auto cancel/repeat capable scheduler
	 */
	public static void asyncSpeedIncreaseScheduler(String world, double currentSpeed) {

		BukkitScheduler asyncSpeedIncreaseScheduler = MainTM.getInstance().getServer().getScheduler();
		asyncSpeedIncreaseScheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {

				// Timer msg
				MsgHandler.timerMsg("World §e" + world + "§5 is running in the asynchronous increase scheduler.");
				MsgHandler.timerMsg("World §e" + world + "§5 speed = §e" + currentSpeed + "§5 | refreshRate = §e" + refreshRateInt + "§5 | tick = §e" + Bukkit.getWorld(world).getTime());
				// Get the world's current time
				Long currentTime = Bukkit.getWorld(world).getTime();

				// Calculate the new time
				long newTime = currentTime + (long) Math.ceil(refreshRateInt * currentSpeed) - refreshRateInt;

				// Restrain too big and too small values
				newTime = ValuesConverter.correctDailyTicks(newTime);

				// Change the world's time
				Bukkit.getWorld(world).setTime(newTime);

				// Change the doDaylightCycle gamerule if it is needed
				double newSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(newTime));
				if ((newSpeed > 1 && currentSpeed <= 1) || (newSpeed <= 1 && currentSpeed > 1))
					DoDaylightCycleHandler.adjustDaylightCycle(world);

				// While the world is not cancelled, asynchronous and the speed > 1, launch the loop again
				if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase(ARG_FALSE)
						&& newSpeed > 1 && newSpeed <= speedMax) {
					asyncSpeedIncreaseScheduler(world, newSpeed);
				} else {
					// Delete the world from the active scheduler list
					if (asyncDecreaseSpeedSchedulerIsActive.contains(world)) asyncDecreaseSpeedSchedulerIsActive.remove(world);
					MsgHandler.timerMsg("World " + world + " is §4cancelled§5 from the asynchronous increase speed scheduler.");
					// Detect if this world needs to change its speed value
					speedScheduler(world);
				}
			}
		}, refreshRateLong);
	}

	/**
	 * Decrease asynchronous worlds speed to a custom rate with an auto cancel/repeat capable scheduler
	 */
	public static void asyncSpeedDecreaseScheduler(String world, double currentSpeed, long refreshRate) {

		BukkitScheduler asyncSpeedDecreaseScheduler = MainTM.getInstance().getServer().getScheduler();
		asyncSpeedDecreaseScheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {

				// Timer msg
				MsgHandler.timerMsg("World §e" + world + "§5 is running in the asynchronous decrease speed scheduler.");
				MsgHandler.timerMsg("World §e" + world + "§5 speed = §e" + currentSpeed + "§5 | refreshRate = §e" + refreshRate + "§5 | tick = §e" + Bukkit.getWorld(world).getTime());

				// Get the world's current time
				Long currentTime = Bukkit.getWorld(world).getTime();

				// Calculate the new time
				long newTime = currentTime + ValuesConverter.fractionFromDecimal(currentSpeed, "modifTime");

				// Restrain too big and too small values
				newTime = ValuesConverter.correctDailyTicks(newTime);

				// Change the world's time
				Bukkit.getWorld(world).setTime(newTime);

				// Change the doDaylightCycle gamerule if it is needed
				double newSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(newTime));
				if ((newSpeed > 1 && currentSpeed <= 1) || (newSpeed <= 1 && currentSpeed > 1))
					DoDaylightCycleHandler.adjustDaylightCycle(world);

				// Adapt the refresh rate
				long newRefreshRate = ValuesConverter.fractionFromDecimal(newSpeed, "refreshRate");

				// While the world is not cancelled, asynchronous and the speed < 1, launch the loop again
				if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase(ARG_FALSE)
						&& newSpeed > 0 && newSpeed < 1) {
					asyncSpeedDecreaseScheduler(world, newSpeed, newRefreshRate);
				} else {
					// Delete the world from the active scheduler list
					if (asyncDecreaseSpeedSchedulerIsActive.contains(world)) asyncDecreaseSpeedSchedulerIsActive.remove(world);
					MsgHandler.timerMsg("World " + world + " is §4cancelled§5 from the asynchronous decrease speed scheduler.");
					// Detect if this world needs to change its speed value
					speedScheduler(world);
				}
			}
		}, refreshRate);
	}
	
};