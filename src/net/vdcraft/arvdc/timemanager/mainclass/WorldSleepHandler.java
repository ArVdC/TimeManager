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

	// Define a max waiting count for the sleeping time
	private static long waitingCount = 500L; // TODO >>> Add this in the configuration file >>> Force day to come after this timer (?)
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
						MsgHandler.debugMsg("Player §e" + p.getName() + "§b is sleeping now (1/100 ticks)."); // Console debug msg					
					}
					// Wait just before the end of the sleep (= 100 ticks)
					if (st <= 99) { // TODO >>> Add this in the configuration file
						sleepTicksCount(p, w, speedModifier, st);
					} else {
						String world = w.getName();
						Boolean isSleepPermited = true;
						// Relaunch the correct settings after sleeping
						MsgHandler.debugMsg("Sleep time is almost reached (99/100 ticks)."); // Console debug msg
						if (watingForTheDay == false && MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP).equals("true")) {
							watingForTheDay(w, world);
							MsgHandler.debugMsg("Achieved ! (100/100 ticks) Now waiting for the morning."); // Console debug msg
							// Check if sleep is permitted in this world
						} else if (MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP).equals("false")) {
							isSleepPermited = false;
							p.wakeup(true);
							MsgHandler.debugMsg("Sleeping is forbid in the world §e" + world + "§b. The process ends here."); // Console debug msg
						}						
						// Use doDaylightCycle to forbid/permit the ending of sleep
						if ((isSleepPermited.equals(false) && speedModifier >= 1.0) || (isSleepPermited.equals(true) && speedModifier < 1.0)) {
							if (MainTM.decimalOfMcVersion < 13.0) w.setGameRuleValue("doDaylightCycle", isSleepPermited.toString());
							else w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, isSleepPermited);
						}
						
						// Change the doDaylightCycle value if it needs to be (avoid a bug when the sleep is interrupt)
						WorldDoDaylightCycleHandler.adjustDaylightCycle(world);
					}
				} else if (sleepTicks > 0) {
					MsgHandler.debugMsg("Player §e" + p.getName() + "§b is awake after §e" + sleepTicks + "§b ticks without having been able to sleep."); // Console debug msg
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
				long time = w.getTime();
				long wakeUpTick = MainTM.getInstance().getConfig().getLong(MainTM.CF_WAKEUPTICK);
				// Check if the sun is already rising
				if (time >= 0 && time <= (wakeUpTick + 2) && MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP).equals("true")) {
					MsgHandler.debugMsg("§aWake up, it's morning !!!"); // Console debug msg
					afterSleepingSettings(w, wakeUpTick + 2); // If yes, go further
				} else if (waitingCount > 0) { // If not, try more, until x ticks later
					waitingCount--;
					watingForTheDay(w, world);
				} else if (waitingCount == 0) { // Reset the active/inactive variable
					watingForTheDay = false;
					MsgHandler.debugMsg("§cToo late...  morning might never come."); // Console debug msg
				}
			}
		}, 1L);
	}

	// # 4. Adjust the time from 6:00 to 12:00 am, relaunch the speed scheduler and refresh the doDaylightCycle gamerule
	public static void afterSleepingSettings(World w, long wakeUpTick) {
		String world = w.getName();
		// If sleeping was complete, waking up at a custom hour
		w.setTime(wakeUpTick);
		// Get the world daySpeed
		// Detect if this world needs to change its speed value
		WorldSpeedHandler.speedScheduler(world);
		// Change the doDaylightCycle value if it needs to be
		WorldDoDaylightCycleHandler.adjustDaylightCycle(world);
		// Reset the active/inactive variable
		watingForTheDay = false;
	}

};