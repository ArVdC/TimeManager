package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;

public class SleepHandler implements Listener {

	/**
	 * When a player try to sleep, authorize entering the bed but check if the time need to be spend until the dawn or not
	 */
	// #1. Listen to PlayerBedEnterEvent in relevant worlds
	@EventHandler
	public void whenPlayerTryToSleep(PlayerBedEnterEvent e) throws InterruptedException {
		
		// #1.A.a. Get the event's player and world
		Player p = e.getPlayer();
		String player = p.getName();
		World w = e.getBed().getWorld();
		String world = w.getName();
		
		// #1.A.b. Get the world's time, speeds and sync parameters
		long t = w.getTime();
		double currentSpeed = MainTM.getInstance().getConfig().getDouble(MainTM.CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t));
		String sync = MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SYNC);
		double ogDaySpeed = MainTM.getInstance().getConfig().getDouble(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_D_SPEED);
		double ogNightSpeed = MainTM.getInstance().getConfig().getDouble(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_N_SPEED);
		boolean nightCycleAnimation = false;
		if (MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_NIGHTCYCLEANIM).equalsIgnoreCase(MainTM.ARG_TRUE)) nightCycleAnimation = true;
		
		// #1.B. Awake the player and stop the process if sleeping is forbid
		if (!w.isBedWorks()) {
			MsgHandler.debugMsg(MainTM.sleepProcessImpossibleDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + ". " + player + MainTM.sleepProcessEndsDebugMsg); // Console debug msg
			e.setCancelled(true); // If it's a world where sleep is impossible (Nether, etc.)
			return;
		}
		if (sync.equalsIgnoreCase(MainTM.ARG_TRUE)) {
			MsgHandler.debugMsg(MainTM.sleepProcessSyncActiveDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + ". " + player + MainTM.sleepProcessEndsDebugMsg); // Console debug msg
			e.setCancelled(true); // If it's a synchronized world
			return;
		}
		if (currentSpeed == 0) {
			MsgHandler.debugMsg(MainTM.sleepProcessTimeFrozenDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + ". " + player + MainTM.sleepProcessEndsDebugMsg); // Console debug msg
			e.setCancelled(true); // If it's a frozen time world
			return;
		}
		if (MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP).equalsIgnoreCase(MainTM.ARG_FALSE)) {
			MsgHandler.debugMsg(MainTM.sleepProcessForbiddenDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + ". " + player + MainTM.sleepProcessEndsDebugMsg); // Console debug msg
			e.setCancelled(true); // If it's a world where sleep is forbid
			return;
		}
		if ((w.isClearWeather() && ((t >= 0 && t < 12542) || (t > 23459))) // night lasts between 12542 and 23459 ticks in clear weather
				|| (w.hasStorm() && !w.isThundering() && ((t >= 0 && t < 12010) || (t > 23991)))) { // night lasts between 12010 and 23991 ticks in rainy weather
			MsgHandler.debugMsg(MainTM.sleepProcessItIsDayDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + ". " + player + MainTM.sleepProcessEndsDebugMsg); // Console debug msg
			e.setCancelled(true); // If this is the day
			return;
		}		
		// #1.C. As soon as the last sleeping player confirms the correct ratio, go further
		if (enoughSleepingPlayers(w, 1)) {
			sleepTicksCount(p, w, world, currentSpeed, 0, ogDaySpeed, ogNightSpeed, nightCycleAnimation);
		}
	}
	
	// #2. Wait the end of the sleep to change the time
	public static void sleepTicksCount(Player p, World w, String world, double currentSpeed, int sleepTicks, double ogDaySpeed, double ogNightSpeed, boolean nightCycleAnimation) {
		BukkitScheduler sleepTicksCountSheduler = MainTM.getInstance().getServer().getScheduler();
		sleepTicksCountSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {					
				// Get the world's name, time and nightCycleAnimation value
				long t = w.getTime();
				
				if (p.isSleeping()) {
					int st = p.getSleepTicks();
	
					// #2.A.a. Only once at the start
					if (st == 0) {
						MsgHandler.debugMsg("Player §e" + p.getName() + MainTM.sleepProcessStartsDebugMsg); // Console debug msg
						if (nightCycleAnimation) startSleepAnimation(world, t);
					}
				
					// #2.A.b. Loop this until the end of the sleep (= 99 ticks)
					if (st <= 99) {
						if (!enoughSleepingPlayers(w, 0)) { // If too much players have woken up, stop the process ...
							if (nightCycleAnimation) stopSleepAnimation(world, ogDaySpeed, ogNightSpeed);
							MsgHandler.debugMsg(MainTM.sleepProcessInterruptedDebugMsg); // Console debug msg
							return;
						} else { // Else, check the sleep count again
							if (st > 0) MsgHandler.debugMsg("Player §e" + p.getName() + "§b is sleeping now (" + st + "/100 ticks)."); // Console debug msg
							sleepTicksCount(p, w, world, currentSpeed, st, ogDaySpeed, ogNightSpeed, nightCycleAnimation);
							return;
						}
					}
					
					// #2.A.c. The 100 ticks stage has been reached
					MsgHandler.debugMsg("Player §e" + p.getName() + MainTM.sleepProcess100TicksDebugMsg); // Console debug msg
					if (nightCycleAnimation) stopSleepAnimation(world, ogDaySpeed, ogNightSpeed);
					clearWeather(p, w);
					if (ogNightSpeed < 1) { // Temporarily allow to move on to the next day
						if (MainTM.serverMcVersion < MainTM.reqMcVForGamerules) w.setGameRuleValue("doDaylightCycle", MainTM.ARG_TRUE);
						else w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
					}
					MsgHandler.debugMsg("Player §e" + p.getName() + "§b achieved sleeping at tick §e#" + w.getTime()); // Console debug msg
					afterSleepingSettings(w, ogNightSpeed);
					
				// #2.B. If the player stops sleeping
				} else {
					MsgHandler.debugMsg("Player §e" + p.getName() + "§b is awake after §e" + sleepTicks + "§b ticks " + MainTM.sleepProcessAwakeNoSleepDebugMsg); // Console debug msg
					if (nightCycleAnimation) stopSleepAnimation(world, ogDaySpeed, ogNightSpeed);
				}
			}
		}, 1L);
	}
	
	// #3. Adjust the time from 6:00 to 12:00 am, relaunch the speed scheduler
	public static void afterSleepingSettings(World w, double ogNightSpeed) {
		BukkitScheduler sleepTicksCountSheduler = MainTM.getInstance().getServer().getScheduler();
		sleepTicksCountSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				String world = w.getName();	
				long wakeUpTick = MainTM.getInstance().getConfig().getLong(MainTM.CF_WAKEUPTICK);
				long ft = w.getFullTime();
				MsgHandler.debugMsg(MainTM.sleepFulltimeTickDebugMsg + ft + "§b."); // Console debug msg
				w.setFullTime(ft - (ft % 24000) + wakeUpTick); // Change world's fulltime
				MsgHandler.debugMsg("New fulltime is §e#" + w.getFullTime() + "§b."); // Console debug msg
				MsgHandler.debugMsg(MainTM.sleepProcessAdjustMorningTicksDebugMsg + wakeUpTick + "§b."); // Console debug msg
				SpeedHandler.speedScheduler(world);
				MsgHandler.infoMsg(MainTM.sleepNewDayMsg + " "  + world + ", it is now tick #" + wakeUpTick + " (" + ValuesConverter.formattedTimeFromTick(wakeUpTick, true) + ")."); // Console final msg
				// Check if other worlds timers must be change
				String sleep = MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP);
				if (sleep.equalsIgnoreCase(MainTM.ARG_LINKED)) linkedSleep(world, w.getFullTime());
			}
		}, 2L);
	}
	
	/**
	 * Checks if enough players are actually sleeping in given world
	 */
	@SuppressWarnings("deprecation")
	public static boolean enoughSleepingPlayers(World w, int preSleeper) {
			List<Player> relevantPlayers = new ArrayList<Player>();
			for (Player player : w.getPlayers()) {
				if (!player.isSleepingIgnored()) relevantPlayers.add(player); // Keep only relevant players
			}
			int rpNb = relevantPlayers.size(); // Get the total number of relevant players
			List<Player> sleepingPlayers = new ArrayList<Player>();
			for (Player player : w.getPlayers()) {
				if (player.isSleeping()) sleepingPlayers.add(player); // Keep only sleeping players
			}
			int spNb = sleepingPlayers.size() + preSleeper; // Get the total number of sleeping players, including the pre-sleeper one
			int psp = 100; // Get the required percentage of sleeping players
			if (MainTM.serverMcVersion < MainTM.reqMcVForGamerules) psp = Integer.parseInt(w.getGameRuleValue("playersSleepingPercentage"));
			else psp = w.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE);		
			if (spNb >= rpNb * (psp / 100)) return true; // Check if the percentage is reached
			else return false;
		}
	
	/**
	 * Starts night a cycle animation (increase speed)
	 */
	public static void startSleepAnimation(String world, long time) {
		MainTM.animationIsInProgress.add(world);
		double skipSpeed;
		if (time > 22000) {
			skipSpeed = 5;
			MsgHandler.debugMsg(MainTM.sleepAnimationDefaultSpeedDebugMsg); // Console debug msg
		} else {
			skipSpeed = (22500 - time) / 100.000;
			if (skipSpeed == 24.000) skipSpeed = 24.001;
			MsgHandler.debugMsg((22500 - time) + MainTM.sleepAnimationSpeedCalculationDebugMsg + skipSpeed + "§b."); // Console debug msg
		}
		MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_D_SPEED, skipSpeed);
		MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_N_SPEED, skipSpeed);
	}

	/**
	 * Stops a night cycle animation and restore initial speed values
	 */
	public static void stopSleepAnimation(String world, double ogDaySpeed, double ogNightSpeed) {
		MainTM.animationIsInProgress.remove(world);
		MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_D_SPEED, ogDaySpeed);
		MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_N_SPEED, ogNightSpeed);
	}

	/**
	 * When a player stops sleeping, prohibit to quit his bed when the animation reaches the end of the progress
	 */
	@EventHandler
	public void whenPlayerStopsSleep(PlayerBedLeaveEvent e) throws InterruptedException {
		Player p = e.getPlayer();
		World w = p.getWorld();
		long t = w.getTime();
		if (MainTM.animationIsInProgress.contains(w.getName()) && (t > 22500 || t < 10)) e.setCancelled(true);
	}
	


	/**
	 * Clears weather
	 */
	@SuppressWarnings("deprecation")
	public static void clearWeather(Player p, World w) {
		if (MainTM.serverMcVersion < MainTM.reqMcVForGamerules) w.setGameRuleValue("WeatherType", "clear");
		else {
			w.setStorm(false);
			w.setThundering(false);
		}
	}
	
	/**
	 * Adjusts fulltime value in linked worlds
	 */
	public static void linkedSleep(String world, Long refFulltime) {
		for (String linkedWorld : MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST).getKeys(false)) {
			String linkedSleep = MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + linkedWorld + "." + MainTM.CF_SLEEP);
			if (linkedSleep.equalsIgnoreCase(MainTM.ARG_LINKED) && !linkedWorld.equalsIgnoreCase(world)) Bukkit.getWorld(linkedWorld).setFullTime(refFulltime);
			// Notify the console
			MsgHandler.infoMsg("The world " + linkedWorld + " " + MainTM.sleepLinkedNewDayMsg); // Console final msg
		}
	}
	
};