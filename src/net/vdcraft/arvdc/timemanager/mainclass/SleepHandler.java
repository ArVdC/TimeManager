package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustTransition;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.material.Bed;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;

@SuppressWarnings("deprecation")
public class SleepHandler implements Listener {

	// Add world's name in a list when a night cycle animation is in progress
	public static List<String> worldAnimationIsInProgress = new ArrayList<String>();

	// Add player's name in a list when a night cycle animation is in progress
	public static List<String> playerAnimationIsInProgress = new ArrayList<String>();
	
	/**
	 * When a player try to sleep, authorize entering the bed but check if the time need to be spend until the dawn or not
	 */
	// Listen to PlayerBedEnterEvent in relevant worlds
	@EventHandler
	public void whenPlayerTryToSleep(PlayerBedEnterEvent e) throws InterruptedException {
		
		// #1. Get the event's player and world
		Player p = e.getPlayer();
		String player = p.getName();
		World w = e.getBed().getWorld();
		String world = w.getName();
		
		// #2. Get the world's time, speeds and sync parameters
		long t = w.getTime();
		double currentSpeed = MainTM.getInstance().getConfig().getDouble(MainTM.CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t));
		String sync = MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SYNC);
		double ogDaySpeed = MainTM.getInstance().getConfig().getDouble(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_D_SPEED);
		double ogNightSpeed = MainTM.getInstance().getConfig().getDouble(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_N_SPEED);
		String nightCycleAnimation = MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_NIGHTSKIP_MODE);
		
		// #3. Awake the player and stop the process if sleeping is forbid
		if (MainTM.serverMcVersion >= MainTM.reqMcVForWorldIsBedWorks) {
			if (!w.isBedWorks()) {
				MsgHandler.debugMsg(MainTM.sleepProcessImpossibleDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + ". " + player + MainTM.sleepProcessEndsDebugMsg); // Console debug msg
				e.setCancelled(true);
				return; // If it's a world where sleep is impossible (Nether, etc.)
			}
		}
		if (sync.equalsIgnoreCase(MainTM.ARG_TRUE)) {
			MsgHandler.debugMsg(MainTM.sleepProcessSyncActiveDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + ". " + player + MainTM.sleepProcessEndsDebugMsg); // Console debug msg
			e.setCancelled(true);
			return; // If it's a synchronized world
		}
		if (currentSpeed == 0) {
			MsgHandler.debugMsg(MainTM.sleepProcessTimeFrozenDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + ". " + player + MainTM.sleepProcessEndsDebugMsg); // Console debug msg
			e.setCancelled(true);
			return; // If it's a frozen time world
		}
		if (MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP).equalsIgnoreCase(MainTM.ARG_FALSE)) {
			MsgHandler.debugMsg(MainTM.sleepProcessForbiddenDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + ". " + player + MainTM.sleepProcessEndsDebugMsg); // Console debug msg
			e.setCancelled(true);
			return; // If it's a world where sleep is forbid
		}
		if (!w.isThundering()
				&& ((!w.hasStorm()
					&& ((t >= 0 && t < 12542) || (t > 23459))) // night lasts between 12542 and 23459 ticks in clear weather
				|| (w.hasStorm()
					&& ((t >= 0 && t < 12010) || (t > 23991))))) { // night lasts between 12010 and 23991 ticks in rainy weather
			MsgHandler.debugMsg(MainTM.sleepProcessItIsDayDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + ". " + player + MainTM.sleepProcessEndsDebugMsg); // Console debug msg
			e.setCancelled(true);
			return; // If this is a clear weather day
		}
		if (!p.hasPermission(MainTM.PERM_SLEEP_ALLOWED)) {
			MsgHandler.debugMsg(MainTM.sleepProcessNoSleepPermDebugMsg + " " + ChatColor.YELLOW + player + ChatColor.AQUA + MainTM.sleepProcessEndsDebugMsg); // Console debug msg
			e.setCancelled(true); // If the player has no permission to sleep
			return;
		}
		if(playerAnimationIsInProgress.contains(p.getName())) {
			MsgHandler.debugMsg(MainTM.sleepProcessWaitingBetweenTwoBedsDebugMsg + " " + ChatColor.YELLOW + player + ChatColor.AQUA + MainTM.sleepProcessEndsDebugMsg); // Console debug msg
			e.setCancelled(true); // If the player quit a bed in the last 25 ticks
			return;
		}
		if (!p.hasPermission(MainTM.PERM_SLEEP_COUNTED)) {
			MsgHandler.debugMsg(MainTM.sleepProcessNoCountPermDebugMsg + " " + ChatColor.YELLOW + player + ChatColor.AQUA + MainTM.sleepProcessEndsDebugMsg); // Console debug msg
			return; // Player does not have the permission to be count
		}
		if (p.isSleepingIgnored()) {
			MsgHandler.debugMsg(MainTM.sleepProcessIgnoredDebugMsg + " " + ChatColor.YELLOW + player + ChatColor.AQUA + MainTM.sleepProcessEndsDebugMsg); // Console debug msg
			return;// Player does not have the rule to be count
		}
		
		// #4. Teleport the player (in order to better identify the bed block later)
		Location bedLocation = e.getBed().getLocation();
		p.teleport(bedLocation);
		
		// #5. As soon as the last sleeping player confirms the correct ratio, go further;
		if (enoughSleepingPlayers(w, 1, true)) {
			sleepTicksCount(p, w, world, currentSpeed, 0, ogDaySpeed, ogNightSpeed, nightCycleAnimation);
		}
	}
	
	/**
	 * Waits the end of the sleep (100 ticks) to change the time at morning
	 */
	private static void sleepTicksCount(Player p, World w, String world, double currentSpeed, int sleepTicks, double ogDaySpeed, double ogNightSpeed, String nightCycleAnimation) {
		BukkitScheduler sleepTicksCountSheduler = MainTM.getInstance().getServer().getScheduler();
		sleepTicksCountSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {					
				// Get the world's name, time and nightCycleAnimation value
				long t = w.getTime();		
				// #1. While the last needed player is sleeping
				if (p.isSleeping()) {
					int st = p.getSleepTicks();
					// #1.A. Only once at the start
					if (st == 1) {
						MsgHandler.debugMsg("Player §e" + p.getName() + MainTM.sleepProcessStartsDebugMsg); // Console debug msg
						if (nightCycleAnimation.equalsIgnoreCase(MainTM.ARG_INSTANT)) {
							nightSkipProcess(w, ogDaySpeed, ogNightSpeed);
							return;
						}
						if (nightCycleAnimation.equalsIgnoreCase(MainTM.ARG_ANIMATION)) startSleepAnimation(w, t);
					}				
					// #1.B. Loop this until the end of the sleep (= 99 ticks)
					if (st <= 99) {
						// #1.B.a. If too much players have woken up, stop the process ...
						if (!enoughSleepingPlayers(w, 0, false)) {
							if (nightCycleAnimation.equalsIgnoreCase(MainTM.ARG_ANIMATION)) stopSleepAnimation(w, ogDaySpeed, ogNightSpeed);
							MsgHandler.debugMsg(MainTM.sleepProcessInterruptedDebugMsg); // Console debug msg
							return;
						// #1.B.b. Else, check the sleep count again
						} else {
							if (st > 0) MsgHandler.debugMsg("Player §e" + p.getName() + "§b is sleeping now (" + st + "/100 ticks)."); // Console debug msg
							sleepTicksCount(p, w, world, currentSpeed, st, ogDaySpeed, ogNightSpeed, nightCycleAnimation);
							return;
						}
					}					
					// #1.C. The 100 ticks stage has been reached
					MsgHandler.debugMsg("Player §e" + p.getName() + MainTM.sleepProcess100TicksDebugMsg + w.getTime() + "§b."); // Console debug msg
					nightSkipProcess(w, ogDaySpeed, ogNightSpeed);					
				// #2. If the player stops sleeping
				} else {
					MsgHandler.debugMsg("Player §e" + p.getName() + "§b is awake after §e" + sleepTicks + "§b ticks " + MainTM.sleepProcessAwakeNoSleepDebugMsg); // Console debug msg
					if (nightCycleAnimation.equalsIgnoreCase(MainTM.ARG_ANIMATION)) stopSleepAnimation(w, ogDaySpeed, ogNightSpeed);
				}
			}
		}, 1L);
	}
	
	/**
	 * Achieves the night skip process
	 */
	private static void nightSkipProcess(World w, double ogDaySpeed, double ogNightSpeed) {
		String world = w.getName();
		String nightCycleAnimation = MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_NIGHTSKIP_MODE);
		// In case, stop animation
		if (nightCycleAnimation.equalsIgnoreCase(MainTM.ARG_ANIMATION)) stopSleepAnimation(w, ogDaySpeed, ogNightSpeed);
		// Always clear weather
		clearWeather(w);
		// Artificially change world's fulltime
		long wakeUpTick = MainTM.getInstance().getConfig().getLong(MainTM.CF_WAKEUPTICK);
		long ft = w.getFullTime();
		w.setFullTime(ft - (ft % 24000) + 24000 + wakeUpTick);
		MsgHandler.debugMsg(MainTM.sleepFulltimeTickDebugMsg + ft + "§b. New fulltime is §e#" + w.getFullTime() + "§b."); // Console debug msg
		// Use relevant day speed in the world
		SpeedHandler.speedScheduler(world);
		MsgHandler.infoMsg(MainTM.sleepNewDayMsg + " "  + world + ", it is now tick #" + wakeUpTick + " (" + ValuesConverter.formattedTimeFromTick(wakeUpTick, true) + ")."); // Console final msg
		// Check if other worlds timers must be change
		String sleep = MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_SLEEP);
		if (sleep.equalsIgnoreCase(MainTM.ARG_LINKED)) linkedSleep(world, w.getFullTime());
		for (Player p : w.getPlayers()) {
			if (p.isSleeping()) {
				// Make an accomplishment sound
				if (nightCycleAnimation.equalsIgnoreCase(MainTM.ARG_ANIMATION)) {
					Sound s = Sound.BLOCK_FIRE_EXTINGUISH;
					if (MainTM.serverMcVersion >= MainTM.reqMcVForSounds) s = Sound.BLOCK_BELL_RESONATE; // Check if MC version is at least 1.14.0
					p.playSound(p.getLocation(), s, 1.0f, 1.5f);
					// Reset players sleep statistics
					if (MainTM.serverMcVersion >= MainTM.reqMcVForStatistics) p.setStatistic(Statistic.TIME_SINCE_REST, 0); // Check if MC version is at least 1.13.0
					// Wake up everybody
					if (MainTM.serverMcVersion >= MainTM.reqMcVForWakeup) p.wakeup(true); // Check if MC version is at least 1.14.0					
				}
			}
		}
	}
	
	/**
	 * Checks if enough players are actually sleeping in given world
	 */
	private static boolean enoughSleepingPlayers(World w, int preSleeper, boolean debugMsg) {
		int rpNb = sleepInvolvedPlayersCount(w); // Get the total number of relevant players
		int spNb = sleepingPlayersCount(w) + preSleeper; // Get the total number of sleeping players, including the pre-sleeper one
		int spPercent; // Get the required percentage of sleeping players
		if (MainTM.serverMcVersion < MainTM.reqMcVForSleepPercentage) {				
			String LegacyPercent = MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + w.getName() + "." + MainTM.CF_NIGHTSKIP_LEGACYPERCENTAGE);
			spPercent = Integer.parseInt(LegacyPercent);		
		} else spPercent = w.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE);
		if (debugMsg) MsgHandler.debugMsg(spNb / rpNb * 100 + "% of relevant players are sleeping now (" + spNb + "/" + rpNb + ") and " + spPercent + "% is required."); // Console debug msg
		if (spNb >= rpNb * (spPercent / 100)) return true; // If the percentage is reached, return true
		else return false;
	}
	
	/**
	 * Starts night cycle animation (increases speed & displays effects)
	 */
	private static void startSleepAnimation(World w, long time) {
		String world = w.getName();
		worldAnimationIsInProgress.add(world);
		// Display decorative particles
		startSleepParticles(w, 4, 0L);
		// Display a decorative title
		String color = ValuesConverter.replaceAllHexColors("#ccc9a1");
		if (MainTM.serverMcVersion < MainTM.reqMcVForHexColors) color = ChatColor.WHITE.toString();
		String text = "zZz" + "    ";
		startSleepTitle(w, color + text, 4, 0L);
		// Play breathe sounds
		startSleepSnore( w, 1.0f, 4, 0L);
		// Modify speeds
		double skipSpeed;
		if (time > 22000) {
			skipSpeed = 5;
			MsgHandler.debugMsg(MainTM.sleepAnimationDefaultSpeedDebugMsg); // Console debug msg
		} else {
			skipSpeed = (23000 - time) / 100.000;
			if (skipSpeed == 24.000) skipSpeed = 24.001;
			MsgHandler.debugMsg((23000 - time) + MainTM.sleepAnimationSpeedCalculationDebugMsg + skipSpeed + "§b."); // Console debug msg
		}
		MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_D_SPEED, skipSpeed);
		MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_N_SPEED, skipSpeed);
	}

	/**
	 * Stops a night cycle animation (restore initial speed values)
	 */
	private static void stopSleepAnimation(World w, double ogDaySpeed, double ogNightSpeed) {
		String world = w.getName();
		worldAnimationIsInProgress.remove(world);
		// Recover original speeds
		MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_D_SPEED, ogDaySpeed);
		MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_N_SPEED, ogNightSpeed);
	}	
	
	/**
	 * Broadcast animation's snore sound
	 */
	private static void startSleepSnore(World w, float pitch, int iterationNb, long delay) {
		BukkitScheduler startSleepSnoreScheduler = MainTM.getInstance().getServer().getScheduler();
		startSleepSnoreScheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				Sound s = Sound.ENTITY_PLAYER_BREATH;
				if (enoughSleepingPlayers(w, 0, false)) {
					for (Player p : sleepingPlayersList(w)) {
						p.playSound(p.getLocation(), s, 0.4f, pitch);
					}			
					float newPitch = pitch;
					switch (iterationNb) {
						case 4 :
							newPitch = pitch;
							break;
						case 3 :
							newPitch = pitch + 0.2f;
							break;
						case 2 :
							newPitch = pitch - 0.2f;
							break;
						case 1 :
							newPitch = pitch - 0.1f;
							break;
					}
					if (iterationNb > 0) startSleepSnore(w, newPitch, iterationNb-1, 24L);
				}		
			}
		}, delay);
	}
	
	/**
	 * Displays animation's "zZz" title
	 */
	private static void startSleepTitle(World w, String msg, int iterationNb, long delay) {
		BukkitScheduler startSleepTitleScheduler = MainTM.getInstance().getServer().getScheduler();
		startSleepTitleScheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (enoughSleepingPlayers(w, 0, false)) {
					for (Player p : sleepingPlayersList(w)) {
						MsgHandler.playerTitleMsg(p, msg, " ", 5, 10, 5);
					}
					if (iterationNb > 0) startSleepTitle(w, "   " + msg , iterationNb-1, 24L);
				}
			}
		}, delay);
	}
	
	/**
	 * Displays animation's particles
	 */
	private static void startSleepParticles(World w, int iterationNb, long delay) {
		BukkitScheduler startSleepParticlesScheduler = MainTM.getInstance().getServer().getScheduler();
		startSleepParticlesScheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (enoughSleepingPlayers(w, 0, false)) {
					for (Player p : sleepingPlayersList(w)) {
						Location loc = p.getLocation();
						// Get the block under the player
						Block bedBlock = loc.getBlock().getRelative(0, 0, 0);
						// Retrieve bed direction
                        String facing = findBedDirection(bedBlock);
                        // Adjust the location of the particles depending on bed direction
                        Location adjustedLoc = adjustParticlesPosition(bedBlock.getLocation(), facing);
                        // Display the particles
						if (MainTM.serverMcVersion >= MainTM.reqMcVForDustTransition) { // Check if MC version is at least 1.17.0
							spawnParticles(adjustedLoc, p, iterationNb);
						} else {
							spawnLegacyParticles(adjustedLoc, p, iterationNb);
						}
						if (!p.isSleeping()) break;
					}
				}
				if (iterationNb > 0) startSleepParticles(w, iterationNb-1, 24L);
			}
		}, delay);
	}
			
		/**
		 * Finds the bed direction
		 */
		private static String findBedDirection(Block bedBlock) {
			String facing;
        // Check if the block is a bed
        if (bedBlock.getType().toString().equalsIgnoreCase("BED_BLOCK")) { // Legacy API
            Bed bed = (Bed) bedBlock.getState().getData();
            String bedsss = bed.toString();
            bedsss = bedsss.replace(")", "XX").replace("(", "XX");
            String[] bedByte = bedsss.split("XX");
            String bData  = bedByte[1];
            switch (bData) {
	            case "12": facing = "NORTH"; break;
	            case "13": facing = "EAST"; break;
	            case "14": facing = "SOUTH"; break;
	            case "15": facing = "WEST"; break;
	            default: facing = "UNKNOW"; break;
            }
        } else if (bedBlock.getType().toString().endsWith("_BED")){ // Current API
        	Directional blockDirection = (Directional) bedBlock.getBlockData();
        	facing = blockDirection.getFacing().toString();
        } else facing = "UNKNOW";
    return facing;
	}	
	
	/**
	 * Adjusts particles position depending on the orientation of the bed
	 */
	private static Location adjustParticlesPosition(Location loc, String facing) {
        Location adjustedLoc = loc.clone();
        switch (facing) {
        	default :
                adjustedLoc.setPitch(0f);
            case "NORTH":
            	adjustedLoc.setX(adjustedLoc.getX() + 0.5);
            	adjustedLoc.setZ(Math.round(adjustedLoc.getZ()));
                adjustedLoc.add(0.0, 0.5, -1.5);
                adjustedLoc.setYaw(0f);
                break;
            case "EAST":
            	adjustedLoc.setX(Math.round(adjustedLoc.getX()));
            	adjustedLoc.setZ(adjustedLoc.getZ() + 0.5);
                adjustedLoc.add(1.5, 0.5, 0.0);
                adjustedLoc.setYaw(90f);
                break;
            case "SOUTH":
            	adjustedLoc.setX(adjustedLoc.getX() + 0.5);
            	adjustedLoc.setZ(Math.round(adjustedLoc.getZ()));
                adjustedLoc.add(0.0, 0.5, 1.5);
                adjustedLoc.setYaw(180f);
                break;
            case "WEST":
            	adjustedLoc.setX(Math.round(adjustedLoc.getX()));
            	adjustedLoc.setZ(adjustedLoc.getZ() + 0.5);
                adjustedLoc.add(-1.5, 0.5, 0.0);
                adjustedLoc.setYaw(270f);
                break;
            case "UNKNOW":   	
                adjustedLoc.add(0.0, 0.5, 0.0);
                adjustedLoc.setYaw(0f);
                break;
        }
        return adjustedLoc;
	}

	/**
	 * Spawns particles based on legacy particles Enum
	 */
	private static void spawnLegacyParticles(Location loc, Player p, int iterationNb) {
		// spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data)
		switch (iterationNb) {
		default:
		case 4 :
			p.spawnParticle(Particle.CLOUD, loc, 10, 1, 1, 1);
			MsgHandler.errorMsg("100 " + Particle.CLOUD + " was displayed at location : " + loc);
			break;
		case 3 :
			p.spawnParticle(Particle.CLOUD, loc, 50, 1, 1, 1);
			MsgHandler.errorMsg("50 " + Particle.CLOUD + " was displayed at location : " + loc);
			break;
		case 2 :
			p.spawnParticle(Particle.CLOUD, loc, 100, 1, 1, 1);
			MsgHandler.errorMsg("10 " + Particle.CLOUD + " was displayed at location : " + loc);
			break;
		case 1 :
			p.spawnParticle(Particle.CLOUD, loc, 500, 1, 1, 1);
			MsgHandler.errorMsg("1 " + Particle.CLOUD + " was displayed at location : " + loc);
			break;
		}
	}

	/**
	 * Spawns particles
	 */
	private static void spawnParticles(Location loc, Player p, int iterationNb) {
		// spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data)
		DustTransition dustTransition;
		switch (iterationNb) {
			default:
			case 4 :
			case 2 :
				dustTransition = new DustTransition(Color.fromRGB(24, 0, 55), Color.fromRGB(240, 150, 255), 0.8f);
				break;
			case 3 :
			case 1 :
				dustTransition = new DustTransition(Color.fromRGB(155, 155, 155), Color.fromRGB(255, 255, 255), 0.7f);
				break;
		}
		p.spawnParticle(Particle.DUST_COLOR_TRANSITION, loc, 60, 1, 1, 1, dustTransition);
	}
	
	/**
	 * When a player stops sleeping, hides the "zZz" title
	 */
	@EventHandler
	private void whenPlayerStopsSleepHideTitle(PlayerBedLeaveEvent e) throws InterruptedException {
		MsgHandler.playerTitleMsg(e.getPlayer(), "", "", 5, 5, 0);
	}
	
	/**
	 * When a player stops sleeping near to the morning, prohibits to quit his bed when the animation reaches the end of the progress
	 */
	@EventHandler
	private void whenPlayerStopsSleepTooLate(PlayerBedLeaveEvent e) throws InterruptedException {
		if (MainTM.serverMcVersion >= MainTM.reqMcVForCancelLeaveBedEvent) { // Check if MC version is at least 1.17.0
			Player p = e.getPlayer();
			World w = p.getWorld();
			long t = w.getTime();
			if (worldAnimationIsInProgress.contains(w.getName()) && (t > 22500 || t < 10)) e.setCancelled(true);
		}
	}
	
	/**
	 * When a player stops sleeping when an animation plays, prohibits to go too fast in another bed
	 */
	@EventHandler
	private void whenPlayerStopsSleepWithAnim(PlayerBedLeaveEvent e) throws InterruptedException {
		Player p = e.getPlayer();
		World w = p.getWorld();
		if (worldAnimationIsInProgress.contains(w.getName())) {
			playerAnimationIsInProgress.add(p.getName());
		}
		BukkitScheduler delayForReBed = MainTM.getInstance().getServer().getScheduler();
		delayForReBed.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (playerAnimationIsInProgress.contains(p.getName())) {
					playerAnimationIsInProgress.remove(p.getName());
				}
			}
		}, 25L);
	}	

	/**
	 * Clears weather
	 */
	private static void clearWeather(World w) {
		if (MainTM.serverMcVersion < MainTM.reqMcVForGamerules) {
			w.setGameRuleValue("WeatherType", "clear"); // Check if MC version is at least 1.13.0
		} else {
			w.setStorm(false);
			w.setThundering(false);
		}
	}
	
	/**
	 * Adjusts fulltime value in linked worlds
	 */
	private static void linkedSleep(String world, Long refFulltime) {
		for (String linkedWorld : MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST).getKeys(false)) {
			String linkedSleep = MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + linkedWorld + "." + MainTM.CF_SLEEP);
			if (linkedSleep.equalsIgnoreCase(MainTM.ARG_LINKED) && !linkedWorld.equalsIgnoreCase(world)) Bukkit.getWorld(linkedWorld).setFullTime(refFulltime);
			// Notify the console
			MsgHandler.infoMsg("The world " + linkedWorld + " " + MainTM.sleepLinkedNewDayMsg); // Console final msg
		}
	}
	
	/**
	 * Sets the percentage of sleeping players needed to skip the night from the yaml to the gamerule
	 */
	public static void setSleepingPlayersNeeded(String world) {
		World w = Bukkit.getServer().getWorld(world);
		String stringPlayersRequired = MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_NIGHTSKIP_REQUIREDPLAYERS);
		Integer intPlayersRequired;
		if (stringPlayersRequired.contains("%")) { // If the number is a percentage, use it as is
			stringPlayersRequired = stringPlayersRequired.replace("%", "");
			Double doublePlayersRequired = Double.parseDouble(stringPlayersRequired);
			intPlayersRequired = (int) Math.floor(doublePlayersRequired);
		} else { // If the number is an amount, convert it to a percentage
			intPlayersRequired = Integer.parseInt(stringPlayersRequired);
			Integer nbSleepInvolvedPlayers = sleepInvolvedPlayersCount(w);
			if (nbSleepInvolvedPlayers > 0) intPlayersRequired = (int) Math.floor((intPlayersRequired * 100.0) / nbSleepInvolvedPlayers);
			else intPlayersRequired = 0;
		}
		if (intPlayersRequired < 0) intPlayersRequired = 0; // Avoid negative numbers
		if (intPlayersRequired > 100) intPlayersRequired = 100;	// Avoid too big numbers
		stringPlayersRequired = intPlayersRequired.toString();
		if (MainTM.serverMcVersion < MainTM.reqMcVForSleepPercentage) // Check if MC version is at least 1.17.0
			MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_NIGHTSKIP_LEGACYPERCENTAGE, stringPlayersRequired);
		else {
			w.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, intPlayersRequired); // arg needs to be an integer
		}
		MsgHandler.debugMsg(MainTM.sleepGameRulePart1DebugMsg + stringPlayersRequired + MainTM.sleepGameRulePart2DebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + "."); // Console debug msg
	}
	
	/**
	 * Lists the players involved in sleep in a specific world
	 */
	private static List<Player> sleepInvolvedPlayersList(World w) {
		List<Player> sleepInvolvedPlayers = new ArrayList<Player>();
		for (Player p : w.getPlayers()) {
			if (!p.isSleepingIgnored()
					// Check GameMode
					&& !p.getGameMode().equals(GameMode.SPECTATOR)
					// Check permissions
					&& p.hasPermission(MainTM.PERM_SLEEP_COUNTED)
					// Check online status
					&& p.isOnline()
					) {
				sleepInvolvedPlayers.add(p);
			}
		}
		return sleepInvolvedPlayers;
	}
	
	/**
	 * Counts the number of players involved in sleep in a specific world
	 */
	private static int sleepInvolvedPlayersCount(World w) {
		List<Player> sleepInvolvedPlayers = sleepInvolvedPlayersList(w);
		int size = sleepInvolvedPlayers.size(); // Get the total number of players involved in sleep
		return size;
	}
	
	/**
	 * Lists the sleeping players in a world
	 */
	private static List<Player> sleepingPlayersList(World w) {
		List<Player> sleepingPlayers = new ArrayList<Player>();
		for (Player p : w.getPlayers()) {
			if (p.isSleeping()) sleepingPlayers.add(p);
		}
		return sleepingPlayers;
	}
	
	/**
	 * Counts the number of sleeping players in a world
	 */
	private static int sleepingPlayersCount(World w) {
		List<Player> sleepingPlayers = sleepingPlayersList(w);
		int size = sleepingPlayers.size(); // Get the total number of sleeping players
		return size;		
	}

	/**
	 * Sets the vanilla "Sleeping Ignored" value depending on his permissions
	 */
	@EventHandler // #1. On join
	private void applySleepPermission(PlayerJoinEvent e) throws InterruptedException {
		Player p = e.getPlayer();
		if (p.hasPermission(MainTM.PERM_SLEEP_COUNTED)) p.setSleepingIgnored(false);
		else p.setSleepingIgnored(true);
	}	
	@EventHandler // #2. On world change
	private void applySleepPermission(PlayerChangedWorldEvent e) throws InterruptedException {
		Player p = e.getPlayer();
		if (p.hasPermission(MainTM.PERM_SLEEP_COUNTED)) p.setSleepingIgnored(false);
		else p.setSleepingIgnored(true);
	}	
	
	/**
	 * When the playersSleepingPercentage gameRule is changed by a command, set the new value in the config.yml
	 */
	@EventHandler // #1. Changed by a console command
	private void applySleepPercentage(ServerCommandEvent e) throws InterruptedException {
		String c = e.getCommand();
		if (c.startsWith("gamerule")) {
			String[] args = c.split(" ");
			if (args.length < 3) return;
			if (args[1].equalsIgnoreCase(MainTM.GR_PLAYERS_SLEEPING_PERCENTAGE)) {
				for (World w : Bukkit.getServer().getWorlds())
					MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + w.getName() + "." + MainTM.CF_NIGHTSKIP_REQUIREDPLAYERS, args[2] + "%");
				MainTM.getInstance().saveConfig();
			}
		}
	}	
	@EventHandler // #2. Changed by a player command
	private void applySleepPercentage(PlayerCommandPreprocessEvent e) throws InterruptedException {
		String c = e.getMessage();
		if (c.startsWith("gamerule")) {
			String[] args = c.split(" ");
			if (args.length < 3) return;
			if (args[1].equalsIgnoreCase(MainTM.GR_PLAYERS_SLEEPING_PERCENTAGE)) {
				Player p = e.getPlayer();
				World w = p.getWorld();
				MainTM.getInstance().getConfig().set(MainTM.CF_WORLDSLIST + "." + w.getName() + "." + MainTM.CF_NIGHTSKIP_REQUIREDPLAYERS, args[2] + "%");
				MainTM.getInstance().saveConfig();
			}
		}
	}

	/**
	 * Recalculate the required player percentage to sleep if config.yml contains a count
	 */
	@EventHandler // #1. On join
	private void changeRequiredPercentage(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		World w = p.getWorld();
		if (!MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + w.getName() + "." + MainTM.CF_NIGHTSKIP_REQUIREDPLAYERS).contains("%")) {
			BukkitScheduler changeRequiredPercentageOnjoinScheduler = MainTM.getInstance().getServer().getScheduler();
			changeRequiredPercentageOnjoinScheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
				@Override
				public void run() {
					setSleepingPlayersNeeded(w.getName());
				}
			}, 10L);
		}			
	}
	@EventHandler // #2. On world change
	private void changeRequiredPercentage(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
		World oldWorld = e.getFrom();
		World newWorld = p.getWorld();
		if (!MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + oldWorld.getName() + "." + MainTM.CF_NIGHTSKIP_REQUIREDPLAYERS).contains("%"))
			setSleepingPlayersNeeded(oldWorld.getName());
		if (!MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + newWorld.getName() + "." + MainTM.CF_NIGHTSKIP_REQUIREDPLAYERS).contains("%"))
			setSleepingPlayersNeeded(newWorld.getName());
	}
	@EventHandler // #3. On quit
	private void changeRequiredPercentage(PlayerQuitEvent e) throws InterruptedException {
		Player p = e.getPlayer();
		World w = p.getWorld();
		if (!MainTM.getInstance().getConfig().getString(MainTM.CF_WORLDSLIST + "." + w.getName() + "." + MainTM.CF_NIGHTSKIP_REQUIREDPLAYERS).contains("%")) {
			BukkitScheduler changeRequiredPercentageOnquitScheduler = MainTM.getInstance().getServer().getScheduler();
			changeRequiredPercentageOnquitScheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
				@Override
				public void run() {
					if (Bukkit.getPluginManager().getPlugin(MainTM.nameTM()) != null) setSleepingPlayersNeeded(w.getName());
				}
			}, 10L);
		}
	}
	
};