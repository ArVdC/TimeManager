package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
			double speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(time));
			// Get the current 'sync' value
			String sync = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC);
			// Do a first daylightCycle to avoid mistakes
			DoDaylightCycleHandler.adjustDaylightCycle(world);

			// #B.1. Do not treat frozen worlds
			if (speed == 0.0) {
				MsgHandler.debugMsg("The world " + ChatColor.YELLOW + world + ChatColor.AQUA + " " + schedulerOffDebugMsg);
			} else {
				// #B.2. Real 24h time calculation
				if (speed == realtimeSpeed) {
					if (!realSpeedSchedulerIsActive.contains(world)) {
						MsgHandler.debugMsg("The world " + ChatColor.YELLOW + world + ChatColor.AQUA + " " + schedulerWillUseDebugMsg + scheduler24DebugMsg);
						// Declare the world as having an active scheduler
						realSpeedSchedulerIsActive.add(world);
						// Launch real speed scheduler
						realSpeedScheduler(world);
					}
				// #B.3. Others synchronous time calculation
				} else if (sync.equalsIgnoreCase(ARG_TRUE)) {
					// Get the world's 'daySpeed' value
					double daySpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED);
					// Get the world's 'nightSpeed' value
					double nightSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED);
					// #B.3.a. If it is a (daySpeed == nightSpeed) world
					if (daySpeed == nightSpeed) {
						if (!syncConstantSpeedSchedulerIsActive.contains(world)) {
							MsgHandler.debugMsg("The world " + ChatColor.YELLOW + world + ChatColor.AQUA + " " + schedulerWillUseDebugMsg + schedulerConstantSyncDebugMsg);
							// Declare the world as having an active scheduler
							syncConstantSpeedSchedulerIsActive.add(world);
							// Launch synchronous constant speed scheduler
							syncConstantSpeedScheduler(world, speed);		
						}
					}
					// #B.3.b. ... or if it is a (daySpeed != nightSpeed) world
					else if (daySpeed != nightSpeed) {
						if (!syncVariableSpeedSchedulerIsActive.contains(world)) {
							MsgHandler.debugMsg("The world " + ChatColor.YELLOW + world + ChatColor.AQUA + " " + schedulerWillUseDebugMsg + schedulerVariableSyncDebugMsg);
							// Declare the world as having an active scheduler
							syncVariableSpeedSchedulerIsActive.add(world);
							// Launch synchronous constant speed scheduler
							syncVariableSpeedScheduler(world, speed);
						}
					}
				// #B.4. Asynchronous time calculation
				} else if (!sync.equalsIgnoreCase(ARG_TRUE)) {							
					// #B.4.a. If it is an increased speed world, ...
					if (speed > 1.0 && !asyncIncreaseSpeedSchedulerIsActive.contains(world)) {
							MsgHandler.debugMsg("The world " + ChatColor.YELLOW + world + ChatColor.AQUA + " " + schedulerWillUseDebugMsg + schedulerAsyncIncreaseDebugMsg);
							// Declare the world as having an active scheduler
							asyncIncreaseSpeedSchedulerIsActive.add(world);
							// Launch asynchronous increase speed scheduler
							asyncIncreaseSpeedScheduler(world, speed);
					}
					// #B.4.b. ... or if it is a decreased speed world, ...
					else if (speed < 1.0 && !asyncDecreaseSpeedSchedulerIsActive.contains(world)) {
							MsgHandler.debugMsg("The world " + ChatColor.YELLOW + world + ChatColor.AQUA + " " + schedulerWillUseDebugMsg + schedulerAsyncDecreaseDebugMsg);
							// Declare the world as having an active scheduler
							asyncDecreaseSpeedSchedulerIsActive.add(world);
							// Launch asynchronous decrease speed scheduler
							asyncDecreaseSpeedScheduler(world, speed);
					}
					// #B.4.c. ... or if it is a normal speed world
					else if (speed == 1.0 && !asyncNormalSpeedSchedulerIsActive.contains(world)) {
							MsgHandler.debugMsg("The world " + ChatColor.YELLOW + world + ChatColor.AQUA + " " + schedulerWillUseDebugMsg + schedulerAsyncNormalDebugMsg);
							// Declare the world as having an active scheduler
							asyncNormalSpeedSchedulerIsActive.add(world);
							// Launch asynchronous increase speed scheduler
							asyncNormalSpeedScheduler(world, speed);
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
				// Get the world's current time
				Long currentTime = Bukkit.getWorld(world).getTime();
				// Timer msg
				MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE + schedulerIsRunningDebugMsg + scheduler24DebugMsg);
				MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE + "speed = " + ChatColor.YELLOW + realtimeSpeed + ChatColor.DARK_PURPLE + " (" + ValuesConverter.wichSpeedParam(currentTime) + ") | refreshRate = " + ChatColor.YELLOW + "72" + ChatColor.DARK_PURPLE + " | tick = " + ChatColor.YELLOW + Bukkit.getWorld(world).getTime());
				// Get the world's 'speed' value
				long t = Bukkit.getWorld(world).getTime();
				double speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t));
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
				// While the world is not cancelled and the speed still 24h, launch the loop again ...
				if (speed == realtimeSpeed) {
					realSpeedScheduler(world);
				}  // ... or break the loop
				else {
					// Delete the world from the active scheduler list
					if (realSpeedSchedulerIsActive.contains(world)) realSpeedSchedulerIsActive.remove(world);
					MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE  + "is " + ChatColor.DARK_RED + "cancelled " + ChatColor.DARK_PURPLE + "from " + scheduler24DebugMsg);
					// Detect if this world needs to change its speed value
					speedScheduler(world);
				}
			}
		}, 72L);
	}

	/**
	 * Modify worlds synchronously with a constant speed at a custom rate with an auto cancel/repeat capable scheduler
	 */
	public static void syncConstantSpeedScheduler(String world, double currentSpeed) {

		BukkitScheduler syncConstantSpeedScheduler = MainTM.getInstance().getServer().getScheduler();
		syncConstantSpeedScheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				// Get the refresh rate
				refreshRateLong = MainTM.getInstance().getConfig().getLong(CF_REFRESHRATE);
				// Get the world's current time
				Long currentTime = Bukkit.getWorld(world).getTime();
				// Timer msg
				MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE + schedulerIsRunningDebugMsg + schedulerConstantSyncDebugMsg);
				MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE + "speed = " + ChatColor.YELLOW + currentSpeed + ChatColor.DARK_PURPLE + " (" + ValuesConverter.wichSpeedParam(currentTime) + ") | refreshRate = " + ChatColor.YELLOW + refreshRateLong + ChatColor.DARK_PURPLE + " | tick = " + ChatColor.YELLOW + Bukkit.getWorld(world).getTime());
				// Get the current server time
				long currentServerTick = ValuesConverter.getServerTick();
				// Get the world's 'start' value
				long startAtTickNb = (MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + CF_START));
				// Get the total server's elapsed time
				long elapsedServerTime = currentServerTick - initialTick;
				// Calculate the new time ...
				Long newTime = (long) ((startAtTickNb + (elapsedServerTime * currentSpeed)) % 24000); // Next tick = Start at #tick + (Elapsed time * speed modifier)
				// Restrain too big and too small values
				newTime = ValuesConverter.correctDailyTicks(newTime);
				// Change the world's time
				Bukkit.getWorld(world).setTime(newTime);
				// Change the doDaylightCycle gamerule if it is needed
				double newSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(newTime));
				if ((currentSpeed <= 1 && newSpeed > 1) || (currentSpeed > 1 && newSpeed <= 1))
					DoDaylightCycleHandler.adjustDaylightCycle(world);
				// Get the world's 'daySpeed' value
				double daySpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED);
				// Get the world's 'nightSpeed' value
				double nightSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED);
				// While the world is not cancelled and still constant custom synchronous, launch the loop again ...
				if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase(ARG_TRUE)
						&& (newSpeed != realtimeSpeed)
						&& (daySpeed == nightSpeed)) {
					syncConstantSpeedScheduler(world, newSpeed);
				} // ... or break the loop
				else {
					// Delete the world from the active scheduler list
					if (syncConstantSpeedSchedulerIsActive.contains(world)) syncConstantSpeedSchedulerIsActive.remove(world);
					MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE  + "is " + ChatColor.DARK_RED + "cancelled " + ChatColor.DARK_PURPLE + "from " + schedulerConstantSyncDebugMsg);
					// Detect if this world needs to change its speed value
					speedScheduler(world);
				}
			}
		}, refreshRateLong);
	}

	/**
	 * Modify worlds synchronously with a variable speed at a custom rate with an auto cancel/repeat capable scheduler
	 */
	public static void syncVariableSpeedScheduler(String world, double currentSpeed) {

		BukkitScheduler syncVariableSpeedScheduler = MainTM.getInstance().getServer().getScheduler();
		syncVariableSpeedScheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				// Get the refresh rate
				refreshRateLong = MainTM.getInstance().getConfig().getLong(CF_REFRESHRATE);
				// Get the world's current time
				Long currentTime = Bukkit.getWorld(world).getTime();
				// Timer msg
				MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE + schedulerIsRunningDebugMsg + schedulerVariableSyncDebugMsg);
				MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE + "speed = " + ChatColor.YELLOW + currentSpeed + ChatColor.DARK_PURPLE + " (" + ValuesConverter.wichSpeedParam(currentTime) + ") | refreshRate = " + ChatColor.YELLOW + refreshRateLong + ChatColor.DARK_PURPLE + " | tick = " + ChatColor.YELLOW + Bukkit.getWorld(world).getTime());
				// Get the current server time
				long currentServerTick = ValuesConverter.getServerTick();
				// Get the world's 'start' value
				long worldStartAt = (MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + CF_START));
				// Get the world's speed value at server start
				double speedAtStart = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(worldStartAt));
				// Get the world's 'daySpeed' value
				double daySpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED);
				// Get the world's 'nightSpeed' value
				double nightSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED);
				// Get the total server's elapsed time
				long elapsedServerTime = currentServerTick - initialTick;
				// Calculate the new time ...
				Long newTime = SyncHandler.differentSpeedsNewTime(world, worldStartAt, elapsedServerTime, currentServerTick, speedAtStart, daySpeed, nightSpeed, false);
				// Restrain too big and too small values
				newTime = ValuesConverter.correctDailyTicks(newTime);
				// Change the world's time
				Bukkit.getWorld(world).setTime(newTime);
				// Change the doDaylightCycle gamerule if it is needed
				double newSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(newTime));
				if ((currentSpeed <= 1 && newSpeed > 1) || (currentSpeed > 1 && newSpeed <= 1))
					DoDaylightCycleHandler.adjustDaylightCycle(world);
				// While the world is not cancelled and still variable synchronous, launch the loop again ...
				if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase(ARG_TRUE)
						&& (newSpeed != realtimeSpeed)
						&& (daySpeed != nightSpeed)) {
					syncVariableSpeedScheduler(world, newSpeed);
				} // ... or break the loop
				else {
					// Delete the world from the active scheduler list
					if (syncVariableSpeedSchedulerIsActive.contains(world)) syncVariableSpeedSchedulerIsActive.remove(world);
					MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE  + "is " + ChatColor.DARK_RED + "cancelled " + ChatColor.DARK_PURPLE + "from " + schedulerVariableSyncDebugMsg);
					// Detect if this world needs to change its speed value
					speedScheduler(world);
				}
			}
		}, refreshRateLong);
	}

	/**
	 * Increase worlds speed asynchronously at a custom rate with an auto cancel/repeat capable scheduler
	 */
	public static void asyncIncreaseSpeedScheduler(String world, double currentSpeed) {

		BukkitScheduler asyncSpeedIncreaseScheduler = MainTM.getInstance().getServer().getScheduler();
		asyncSpeedIncreaseScheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				// Get the refresh rate
				refreshRateLong = MainTM.getInstance().getConfig().getLong(CF_REFRESHRATE);
				// Get the world's current time
				Long currentTime = Bukkit.getWorld(world).getTime();
				// Timer msg
				MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE  + schedulerIsRunningDebugMsg + schedulerAsyncIncreaseDebugMsg);
				MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE + "speed = " + ChatColor.YELLOW + currentSpeed + ChatColor.DARK_PURPLE + " (" + ValuesConverter.wichSpeedParam(currentTime) + ") | refreshRate = " + ChatColor.YELLOW + refreshRateLong + ChatColor.DARK_PURPLE + " | tick = " + ChatColor.YELLOW + Bukkit.getWorld(world).getTime());
				// Calculate the new time
				long newTime = currentTime + (long) Math.ceil(refreshRateInt * currentSpeed) - refreshRateInt;
				// Restrain too big and too small values
				newTime = ValuesConverter.correctDailyTicks(newTime);
				// Change the world's time
				Bukkit.getWorld(world).setTime(newTime);
				// Change the doDaylightCycle gamerule if it is needed
				double newSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(newTime));
				if (newSpeed < 1) DoDaylightCycleHandler.adjustDaylightCycle(world);
				// While the world is not cancelled, asynchronous and with a speed bigger than 1, launch the loop again ...
				if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase(ARG_FALSE)
						&& (newSpeed > 1)
						&& (newSpeed <= speedMax)) {
					asyncIncreaseSpeedScheduler(world, newSpeed);
				} // ... or break the loop
				else {
					// Delete the world from the active scheduler list
					if (asyncIncreaseSpeedSchedulerIsActive.contains(world)) asyncIncreaseSpeedSchedulerIsActive.remove(world);
					MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE  + "is " + ChatColor.DARK_RED + "cancelled " + ChatColor.DARK_PURPLE + "from " + schedulerAsyncIncreaseDebugMsg);
					// Detect if this world needs to change its speed value
					speedScheduler(world);
				}
			}
		}, refreshRateLong);
	}

	/**
	 * Decrease worlds speed asynchronously at a custom rate with an auto cancel/repeat capable scheduler
	 */
	public static void asyncDecreaseSpeedScheduler(String world, double currentSpeed) {

		// Get the refresh rate
		final Long decreaseRefreshRate = ValuesConverter.fractionFromDecimal(currentSpeed, "refreshRate");

		BukkitScheduler asyncSpeedDecreaseScheduler = MainTM.getInstance().getServer().getScheduler();
		asyncSpeedDecreaseScheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				// Get the world's current time
				Long currentTime = Bukkit.getWorld(world).getTime();
				// Timer msg
				MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE  + schedulerIsRunningDebugMsg + schedulerAsyncDecreaseDebugMsg);
				MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE + "speed = " + ChatColor.YELLOW + currentSpeed + ChatColor.DARK_PURPLE + " (" + ValuesConverter.wichSpeedParam(currentTime) + ") | refreshRate = " + ChatColor.YELLOW + decreaseRefreshRate + ChatColor.DARK_PURPLE + " | tick = " + ChatColor.YELLOW + Bukkit.getWorld(world).getTime());
				// Calculate the new time
				long newTime = currentTime + ValuesConverter.fractionFromDecimal(currentSpeed, "modifTime");
				// Restrain too big and too small values
				newTime = ValuesConverter.correctDailyTicks(newTime);
				// Change the world's time
				Bukkit.getWorld(world).setTime(newTime);
				// Change the doDaylightCycle gamerule if it is needed
				double newSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(newTime));
				if (newSpeed >= 1) DoDaylightCycleHandler.adjustDaylightCycle(world);
				// While the world is not cancelled, asynchronous and with a speed between 0 and 1, launch the loop again ...
				if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase(ARG_FALSE)
						&& (newSpeed > 0)
						&& (newSpeed < 1)) {
					asyncDecreaseSpeedScheduler(world, newSpeed);
				} // ... or break the loop
				else {
					// Delete the world from the active scheduler list
					if (asyncDecreaseSpeedSchedulerIsActive.contains(world)) asyncDecreaseSpeedSchedulerIsActive.remove(world);
					MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE  + "is " + ChatColor.DARK_RED + "cancelled " + ChatColor.DARK_PURPLE + "from " + schedulerAsyncDecreaseDebugMsg);
					// Detect if this world needs to change its speed value
					speedScheduler(world);
				}
			}
		}, decreaseRefreshRate);
	}

	/**
	 * Watch asynchronous worlds with normal speed at a custom rate with an auto cancel/repeat capable scheduler
	 */
	public static void asyncNormalSpeedScheduler(String world, double currentSpeed) {

		BukkitScheduler asyncNormalSpeedScheduler = MainTM.getInstance().getServer().getScheduler();
		asyncNormalSpeedScheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				// Get the world's current time
				Long currentTime = Bukkit.getWorld(world).getTime();
				// Get the refresh rate
				refreshRateLong = MainTM.getInstance().getConfig().getLong(CF_REFRESHRATE);
				// Timer msg
				MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE  + schedulerIsRunningDebugMsg + schedulerAsyncNormalDebugMsg);
				MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE + "speed = " + ChatColor.YELLOW + currentSpeed + ChatColor.DARK_PURPLE + " (" + ValuesConverter.wichSpeedParam(currentTime) + ") | refreshRate = " + ChatColor.YELLOW + refreshRateLong + ChatColor.DARK_PURPLE + " | tick = " + ChatColor.YELLOW + Bukkit.getWorld(world).getTime());
				// Change the doDaylightCycle gamerule if it is needed
				double newSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(currentTime));
				if (newSpeed != 1 ) DoDaylightCycleHandler.adjustDaylightCycle(world);
				// While the world is not cancelled, asynchronous and with a speed = 1, launch the loop again ...
				if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase(ARG_FALSE)
						&& (newSpeed == 1)) { //
					asyncNormalSpeedScheduler(world, newSpeed);
				} // ... or break the loop
				else {
					// Delete the world from the active scheduler list
					if (asyncNormalSpeedSchedulerIsActive.contains(world)) asyncNormalSpeedSchedulerIsActive.remove(world);
					MsgHandler.timerMsg("The world " + ChatColor.YELLOW + world + " " + ChatColor.DARK_PURPLE  + "is " + ChatColor.DARK_RED + "cancelled " + ChatColor.DARK_PURPLE + "from " + schedulerAsyncNormalDebugMsg);
					// Detect if this world needs to change its speed value
					speedScheduler(world);
				}
			}
		}, refreshRateLong);
	}
	
};