package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;

public class SleepHandler implements Listener {

	// Define a max waiting count for the sleeping time
	private static long defWaitingCount = 100L;
	private static long waitingCount = defWaitingCount;
	// Create an active/inactive variable
	public static Boolean watingForTheDay = false;

	/**
	 * When a player try to sleep, authorize entering the bed but check if the time need to be spend until the dawn or not
	 */
	// # 1. Listen to PlayerBedEnterEvent in relevant worlds
	@EventHandler
	public void whenPlayerTryToSleep(PlayerBedEnterEvent e) throws InterruptedException {
		// Get the event's world name
		Player p = e.getPlayer();
		World w = e.getBed().getWorld();
		String world = w.getName();
		long t = w.getTime();
		double s = MainTM.getInstance().getConfig().getDouble(MainTM.CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t));
		// Ignore: Nether and Ender worlds AND worlds with a fixed time (speed 0 or 24)
		if (!(world.contains(MainTM.ARG_NETHER)) && !(world.contains(MainTM.ARG_THEEND)) && !(s == 24.00) && !(s == 0)) {
			// Begin to count the ticks while a player is sleeping
			sleepTicksCount(p, w, s, 0);
		}
	}

	// # 2. Wait the nearly end of the sleep to permit or refuse the night spend until the dawn
	public static void sleepTicksCount(Player p, World w, double speedModifier, int sleepTicks) {
		BukkitScheduler sleepTicksCountSheduler = MainTM.getInstance().getServer().getScheduler();
		sleepTicksCountSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {

				// #2.A Check if the player is actually sleeping
				if (p.isSleeping() == true) {
					int st = p.getSleepTicks();
					String world = w.getName();

					// #2.A.1. Only once at the start
					if (st == 1) {
						MsgHandler.debugMsg("Player §e" + p.getName() + MainTM.sleepProcessStartsDebugMsg); // Console debug msg
						// #2.A.4.b. Go to the next step
						if (!watingForTheDay) {
							watingForTheDay = true;
							delayedDoesDayStart(w, world, 50L); // TODO time to wait
						}				
					}
					// #2.A.2. Wait just before the end of the sleep (= 99 ticks) // TODO 1.17
					if (st <= 98) {
						sleepTicksCount(p, w, speedModifier, st);
						return;
					}
					Boolean sleepIsPermited = false;
					if (MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP).equals(MainTM.ARG_TRUE)
							|| MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "."+ MainTM.CF_SLEEP).equals(MainTM.ARG_LINKED)) {
						sleepIsPermited = true;
					}
					// #2.A.3. The 99 ticks stage has been reached
					if (st >= 99) {
						MsgHandler.debugMsg(MainTM.sleepProcess99TicksDebugMsg); // Console debug msg
						// #2.A.3.a. Awake the player if sleep is not permitted in this world and stop the process
						if (!sleepIsPermited) {
							MsgHandler.debugMsg(MainTM.sleepProcessSleepForbid1DebugMsg + " §e" + world + "§b. " + MainTM.sleepProcessSleepForbid2DebugMsg); // Console debug msg
							p.wakeup(true);
							return; 
						}
					}
					// #2.A.4. The 100 ticks stage has been reached
					MsgHandler.debugMsg(MainTM.sleepProcess100TicksDebugMsg); // Console debug msg					
					if (speedModifier < 1.0) { // Eventually active doDaylightCycle to permit the ending of sleep
						MsgHandler.debugMsg(MainTM.daylightTrueDebugMsg + " §e" + world + "§b.");
						if (MainTM.serverMcVersion < MainTM.reqMcVForDaylightCycle) w.setGameRuleValue("doDaylightCycle", sleepIsPermited.toString());
						else w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, sleepIsPermited);
					}
				// #2.B. If the player stops sleeping
				} else if (sleepTicks > 0) {
					MsgHandler.debugMsg("Player §e" + p.getName() + "§b is awake after §e" + sleepTicks + "§b ticks " + MainTM.sleepProcessAwakeNoSleepDebugMsg); // Console debug msg
				}
			}

		}, 2L);
	}

	// # 3. Wait a little bit before checking
	public static void delayedDoesDayStart(World w, String world, long timeToWait) {
		BukkitScheduler delayedDoesDayStartSheduler = MainTM.getInstance().getServer().getScheduler();
		delayedDoesDayStartSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				MsgHandler.debugMsg(MainTM.sleepProcessWaitMorningTicksDebugMsg); // Console debug msg
				doesDayStart(w, world);
			}
		}, timeToWait);
	}

	// # 4. After sleeping, check if a new day is starting or not
	public static void doesDayStart(World w, String world) {
		BukkitScheduler doesDayStartSheduler = MainTM.getInstance().getServer().getScheduler();
		doesDayStartSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				long time = w.getTime();
				long wakeUpTick = MainTM.getInstance().getConfig().getLong(MainTM.CF_WAKEUPTICK);
				// Check if the sun is already rising
				if (time >= 0 && time <= (wakeUpTick + 50)
						&& (MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP).equalsIgnoreCase(MainTM.ARG_TRUE)
						|| MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP).equalsIgnoreCase(MainTM.ARG_LINKED))) {
					watingForTheDay = false;
					waitingCount = defWaitingCount;
					MsgHandler.debugMsg(MainTM.sleepOkMorningDebugMsg); // Console debug msg
					afterSleepingSettings(w, wakeUpTick); // If yes, go further
				} else if (waitingCount > 0) { // If not, try more, until x ticks later
					waitingCount--;
					doesDayStart(w, world);
				} else if (waitingCount == 0) { // Reset the active/inactive variable
					watingForTheDay = false;
					waitingCount = defWaitingCount;
					MsgHandler.debugMsg(MainTM.sleepNoMorningDebugMsg); // Console debug msg
				}
			}
		}, 4L);
	}

	// # 5. Adjust the time from 6:00 to 12:00 am, relaunch the speed scheduler and refresh the doDaylightCycle gamerule
	public static void afterSleepingSettings(World w, long wakeUpTick) {
		BukkitScheduler afterSleepingSheduler = MainTM.getInstance().getServer().getScheduler();
		afterSleepingSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				String world = w.getName();
				// Get the number of elapsed days
				Long initElapsedDays = ValuesConverter.elapsedDaysFromTick(Bukkit.getWorld(world).getFullTime());
				// If sleeping was complete, waking up at a custom hour
				w.setTime(wakeUpTick);
				MsgHandler.debugMsg(MainTM.sleepProcessAdjustMorningTicksDebugMsg + " §e" + wakeUpTick + "§b."); // Console debug msg
				// Restore the number of elapsed days
				Long nft = (initElapsedDays * 24000) + wakeUpTick;
				w.setFullTime(nft);
				// Detect if this world needs to change its speed value
				SpeedHandler.speedScheduler(world);
				// Change the doDaylightCycle value if it needs to be
				DoDaylightCycleHandler.adjustDaylightCycle(world);
				// Notify the console
				MsgHandler.infoMsg(MainTM.sleepNewDayMsg + " "  + world + ", it is now tick #" + wakeUpTick + " (" + ValuesConverter.formattedTimeFromTick(wakeUpTick) + ")."); // Console final msg
				// Check if other worlds timers must be change
				String sleep = MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP);
				if (sleep.equalsIgnoreCase(MainTM.ARG_LINKED)) {
					for (String linkedWorld : MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST).getKeys(false)) {
						String linkedSleep = MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + linkedWorld + "." + MainTM.CF_SLEEP);
						if (linkedSleep.equalsIgnoreCase(MainTM.ARG_LINKED) && !linkedWorld.equalsIgnoreCase(world)) {
							// Get the number of elapsed days
							Long linkedInitElapsedDays = ValuesConverter.elapsedDaysFromTick(Bukkit.getWorld(world).getFullTime());
							// If sleeping was complete, waking up at a custom hour
							Bukkit.getServer().getWorld(linkedWorld).setTime(wakeUpTick);
							// Restore the number of elapsed days
							Long linkedNft = (linkedInitElapsedDays * 24000) + wakeUpTick;
							Bukkit.getWorld(linkedWorld).setFullTime(linkedNft);
							// Notify the console
							MsgHandler.infoMsg("The world " + linkedWorld + " " + MainTM.sleepLinkedNewDayMsg); // Console final msg
						}
					}
				}
			}
		}, 5L);
	}
};