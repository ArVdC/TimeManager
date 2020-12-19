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

public class WorldSleepHandler implements Listener {

	// Define a max waiting count for the sleeping time
	private static Long waitingCount = 500L; // TODO >>> Add this in the configuration file >>> Force day to come after this timer (?)
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
		double speedModifier = MainTM.getInstance().getConfig().getDouble(MainTM.CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t));
		// Ignore: Nether and Ender worlds AND worlds with a fixed time (speed 0 or 24)
		if (!(world.contains("_nether")) && !(world.contains("_the_end")) && !(speedModifier == 24.00) && !(speedModifier == 0)) {
			// Begin to count the ticks while a player is sleeping
			sleepTicksCount(p, w, speedModifier, 0);
		}
	}

	// # 2. Wait the nearly end of the sleep to adapt gamerule doDaylightCycle to permit or refuse the night spend until the dawn
	public static void sleepTicksCount(Player p, World w, double speedModifier, int sleepTicks) {
		BukkitScheduler sleepTimerSheduler = MainTM.getInstance().getServer().getScheduler();
		sleepTimerSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				// Check if the player is actually sleeping
				if (p.isSleeping() == true) {
					int st = p.getSleepTicks();
					if (st == 1) {
						if (MainTM.debugMode) Bukkit.getServer().getConsoleSender().sendMessage(MainTM.prefixDebugMode + " Player §e" + p.getName() + "§b is sleeping now (1/100 ticks)."); // Console debug msg					
					}
					// Wait just before the end of the sleep (= 100 ticks)
					if (st <= 99) { // TODO >>> Add this in the configuration file
						sleepTicksCount(p, w, speedModifier, st);
					} else {
						String world = w.getName();
						Boolean isSleepPermited = true;
						// Relaunch the correct settings after sleeping
						if (MainTM.debugMode) Bukkit.getServer().getConsoleSender().sendMessage(MainTM.prefixDebugMode + " Sleep time is almost reached (99/100 ticks)."); // Console debug msg
						if (watingForTheDay == false && MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP).equals("true")) {
							watingForTheDay(w, world);
							if (MainTM.debugMode) Bukkit.getServer().getConsoleSender().sendMessage(MainTM.prefixDebugMode + " Achieved ! (100/100 ticks) Now waiting for the morning."); // Console debug msg
							// Check if sleep is permitted in this world
						} else if (MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP).equals("false")) {
							isSleepPermited = false;
							p.wakeup(true);
							if (MainTM.debugMode) Bukkit.getServer().getConsoleSender().sendMessage(MainTM.prefixDebugMode + " Sleeping is forbid in the world §e" + world + "§b. The process ends here."); // Console debug msg
						}						
						// Use doDaylightCycle to forbid/permit the ending of sleep
						if ((isSleepPermited.equals(false) && speedModifier >= 1.0) || (isSleepPermited.equals(true) && speedModifier < 1.0)) {
							if (MainTM.decimalOfMcVersion < 13.0) w.setGameRuleValue("doDaylightCycle", isSleepPermited.toString());
							else w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, isSleepPermited);
						}
					}
				} else {
					if (MainTM.debugMode && sleepTicks > 0) Bukkit.getServer().getConsoleSender().sendMessage(MainTM.prefixDebugMode + " Player §e" + p.getName() + "§b is awake after §e" + sleepTicks + "§b ticks without having been able to sleep."); // Console debug msg
				}
			}
		}, 1L);
	}

	// # 3. After sleeping, check if a new day is starting or not
	public static void watingForTheDay(World w, String world) {
		BukkitScheduler newSettingsSheduler = MainTM.getInstance().getServer().getScheduler();
		newSettingsSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				watingForTheDay = true;
				Long time = w.getTime();
				Long wakeUpTick = MainTM.getInstance().getConfig().getLong(MainTM.CF_WAKEUPTICK);
				// Check if the sun is already rising
				if (time >= 0 && time <= (wakeUpTick + 100) && MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP).equals("true")) {
					if (MainTM.debugMode) Bukkit.getServer().getConsoleSender().sendMessage(MainTM.prefixDebugMode + " §aWake up, it's morning !!!"); // Console debug msg
					afterSleepingSettings(w, wakeUpTick); // If yes, go further
				} else if (waitingCount > 0) { // If not, try more, until x ticks later
					waitingCount--;
					watingForTheDay(w, world);
				} else if (waitingCount == 0) { // Reset the active/inactive variable
					watingForTheDay = false;
					if (MainTM.debugMode) Bukkit.getServer().getConsoleSender().sendMessage(MainTM.prefixDebugMode + " §cToo late...  morning might never come."); // Console debug msg
				}
			}
		}, 1L);
	}

	// # 4. Adjust the time from 6:00 to 12:00 am, the doDaylightCycle gamerule and relaunch the speed schedules
	public static void afterSleepingSettings(World w, Long wakeUpTick) {
		String world = w.getName();
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
		// Reset the active/inactive variable
		watingForTheDay = false;
	}

};