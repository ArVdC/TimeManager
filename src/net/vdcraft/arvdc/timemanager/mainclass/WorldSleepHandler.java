package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;

public class WorldSleepHandler implements Listener {
	
	private static double waitingCount = 0;
	
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
		double speedModifier = MainTM.getInstance().getConfig().getDouble(MainTM.CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t));
		// Ignore: Nether and Ender worlds AND worlds with a fixed time (speed 0 or 24)
		if (!(world.contains("_nether")) && !(world.contains("_the_end")) && !(speedModifier == 24.00) && !(speedModifier == 0)) {
			// Delay the doDaylightCycle gamerule change
			sleepTicksCount(p, w, speedModifier);
		}
	}

	// # 2. Wait the nearly end of the sleep to adapt gamerule doDaylightCycle to permit or refuse the night spend until the dawn
	public static void sleepTicksCount(Player p, World w, double speedModifier) {
		BukkitScheduler sleepTimerSheduler = MainTM.getInstance().getServer().getScheduler();
		sleepTimerSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				// Check if the player is actually sleeping
				if (p.isSleeping() == true) {
					int st = p.getSleepTicks();
					// Relaunch the correct settings after sleeping
					if (st == 1) afterSleepingTime(w);
					// Wait just before the end of the sleep (= 100 ticks)
					if (st <= 99) {
						sleepTicksCount(p, w, speedModifier);
					} else {
						// Check if sleep is permitted in this world
						Boolean isSleepPermited = true;
						if (MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + w.getName() + "." + MainTM.CF_SLEEP).equals("false")) {
							isSleepPermited = false;
						}
						// Use doDaylightCycle to forbid/permit the ending of sleep
						if ((isSleepPermited.equals(false) && speedModifier >= 1.0) || (isSleepPermited.equals(true) && speedModifier < 1.0)) {
							if (MainTM.decimalOfMcVersion < 13.0) w.setGameRuleValue("doDaylightCycle", isSleepPermited.toString());
							else w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, isSleepPermited);
						}
					}
				}
			}
		}, 1L);
	}

	// # 3. Wait during sleeping time before to check if a new day is starting or not
	public static void afterSleepingTime(World w) {
		BukkitScheduler newSettingsSheduler = MainTM.getInstance().getServer().getScheduler();
		newSettingsSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				watingForTheDay(w);
			}
		}, 100L);
	}

	// # 4. After sleeping, check if a new day is starting or not
	public static void watingForTheDay(World w) {
		BukkitScheduler newSettingsSheduler = MainTM.getInstance().getServer().getScheduler();
		newSettingsSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				String world = w.getName();
				Long time = w.getTime();
				// Check if the sun is already rising
				if (time >= 0 && time < 6000 && MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP).equals("true")) {	
					afterSleepingSettings(w); //
				} else if (waitingCount < 500) { // If not, try more, until 500 ticks later
					waitingCount += waitingCount;
					watingForTheDay(w);
				}
			}
		}, 1L);
	}

	// # 5. Adjust the time from 6:00 to 12:00 am, the doDaylightCycle gamerule and relaunch the speed schedules
	public static void afterSleepingSettings(World w) {
		String world = w.getName();
		Long wakeUpTick = MainTM.getInstance().getConfig().getLong(MainTM.CF_WAKEUPTICK);
		// If sleeping was complete, waking up at a custom hour
		w.setTime(wakeUpTick);
		// Get the world daySpeed
		double speed = MainTM.getInstance().getConfig().getDouble(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_D_SPEED);
		// Activate the increase schedule if it is needed and not already activated
		if (MainTM.increaseScheduleIsOn == false && ((MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SYNC).equalsIgnoreCase("true") && speed == 1.0) || speed > 1.0))
			WorldSpeedHandler.worldIncreaseSpeed();
		// Activate the decrease schedule if it is needed and not already activated
		if (MainTM.decreaseScheduleIsOn == false && (speed > 0.0 && speed < 1.0))
			WorldSpeedHandler.worldDecreaseSpeed();
		// Change the doDaylightCycle value if it needs to be
		WorldDoDaylightCycleHandler.adjustDaylightCycle(world);
	}

};