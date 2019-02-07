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
		for (String w : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
		    long actualWorldTime = Bukkit.getWorld(w).getTime(); // Get the current time of the world
		    double speedModifNb = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + w + "." + CF_SPEED); // Get the current speed of the world
		    String syncValue = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + w + "." + CF_SYNC); // Get the current sync param of the world
		    long newTime = 0L;
		    if (speedModifNb >= 1.0 && speedModifNb <= speedMax && speedModifNb != 24.00) { // Only treat worlds with normal or increased timers
			// #A. Synchronized time calculation
			if (syncValue.equalsIgnoreCase("true")) {
			    loopMore = true;
			    // Get the current server time
			    long currentServerTick = ValuesConverter.returnServerTick();
			    long startAtTickNb = (MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + w + "." + CF_START)); // Read config.yml to get the world's 'start' value
			    newTime = (long) (startAtTickNb + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick))) * speedModifNb) % 24000));
			    if (timerMode == true) {
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Calculation of " + actualTimeVar + ":");
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + elapsedTimeCalculation + " = (§8" + currentServerTick + " §b- §7" + initialTick + "§b) % §624000 §b= §d" + ((currentServerTick - initialTick) % 24000) + " §brestrained to one day = §d" + ValuesConverter.returnCorrectTicks(((currentServerTick % 24000) - (initialTick % 24000)))); // Console debug msg
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + adjustedElapsedTimeCalculation + " = §d" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick % 24000))) + " §b* §a" + speedModifNb + " §b= §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speedModifNb)))); // Console debug msg
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + actualTimeCalculation + " = §e" + startAtTickNb + " §b+ §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speedModifNb) + " §b= §c" + (startAtTickNb + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speedModifNb)) + " §brestrained to one day = §ctick #" + ValuesConverter.returnCorrectTicks(newTime)); // Console debug msg
			    }
			    // #B. Normal time calculation
			} else if (speedModifNb > 1.0) { // Only treat worlds with increased timers
			    loopMore = true;
			    long modifTime = (long) Math.ceil(refreshRateInt * speedModifNb);
			    newTime = actualWorldTime + modifTime - refreshRateLong;
			}
			if (syncValue.equalsIgnoreCase("true") || speedModifNb > 1.0) {
			    // Restrain too big and too small values
			    newTime = ValuesConverter.returnCorrectTicks(newTime);
			    // Change world's timer
			    Bukkit.getWorld(w).setTime(newTime);
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
		for (String w : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
		    long actualWorldTime = Bukkit.getWorld(w).getTime(); // Get the current time of the world
		    double speedModifNb = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + w + "." + CF_SPEED);
		    String isSpeedRealTime = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + w + "." + CF_SPEED);
		    long newTime;
		    if (speedModifNb > 0.0 && speedModifNb < 1.0 && !(isSpeedRealTime.equals("realTime"))) { // Only treat worlds with decreased timers
			loopMore = true;
			// #A. Synchronized time calculation
			if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + w + "." + CF_SYNC).equalsIgnoreCase("true")) {
			    // Get the current server time
			    long currentServerTick = ValuesConverter.returnServerTick();
			    long startAtTickNb = (MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + w + "." + CF_START)); // Read config.yml to get the world's 'start' value
			    newTime = (long) (startAtTickNb + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick))) * speedModifNb) % 24000));
			    if (timerMode == true) {
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Calculation of " + actualTimeVar + ":");
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + elapsedTimeCalculation + " = (§8" + currentServerTick + " §b- §7" + initialTick + "§b) % §624000 §b= §d" + ((currentServerTick - initialTick) % 24000) + " §brestrained to one day = §d" + ValuesConverter.returnCorrectTicks(((currentServerTick % 24000) - (initialTick % 24000)))); // Console debug msg
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + adjustedElapsedTimeCalculation + " = §d" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick % 24000))) + " §b* §a" + speedModifNb + " §b= §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speedModifNb)))); // Console debug msg
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + actualTimeCalculation + " = §e" + startAtTickNb + " §b+ §5" + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speedModifNb) + " §b= §c" + (startAtTickNb + ((ValuesConverter.returnCorrectTicks(((currentServerTick - initialTick) % 24000))) * speedModifNb)) + " §brestrained to one day = §ctick #" + ValuesConverter.returnCorrectTicks(newTime)); // Console
																																																												      // debug msg
			    }
			    // #B. Normal time calculation
			} else {
			    // Try to compensate for missing ticks due to decimals
			    Integer missedTicks = (int) Math.round((refreshRateInt * speedModifNb * 10) % 10); // turn the decimal into an independent integer
			    Integer randomTicks = 0; // By default, no tick would be added
			    if (timerMode == true)
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Missed ticks for world " + w + " is 0," + missedTicks); // Dev console msg
			    if (missedTicks > 0) { // But if the decimal is bigger than 0
				int range = (10 - missedTicks) + 1; // Define a range between the decimal value and 10
				Integer randomNb = (int) (Math.random() * range) + missedTicks; // Create a random number in that range
				if (timerMode == true)
				    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Random roll: " + randomNb); // Dev console msg
				if (randomNb <= missedTicks)
				    randomTicks = 1; // If the random number is smaller than or equals the decimal, add 1 tick to the total count
			    }
			    if (timerMode == true)
				Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Added " + randomTicks + " random ticks for world" + w); // Dev console msg
			    long modifTime = (long) Math.floor((refreshRateInt * speedModifNb));
			    newTime = actualWorldTime + modifTime + randomTicks;
			}
			// Restrain too big and too small values
			newTime = ValuesConverter.returnCorrectTicks(newTime);
			// Change world's timer
			Bukkit.getWorld(w).setTime(newTime);
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
		for (String w : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
		    Double isSpeedRealTime = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + w + "." + CF_SPEED);
		    long worldStartAt = MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + w + "." + CF_START);
		    if (isSpeedRealTime == 24.00) { // Only treat worlds with a '24.0' timers
			loopMore = true;
			// Get the current server tick
			long currentServerTick = ValuesConverter.returnServerTick();
			long newTime = (currentServerTick / 72L) + (worldStartAt - 6000L); // -6000 cause a mc's day start at 6:00
			// Restrain too big and too small values
			newTime = ValuesConverter.returnCorrectTicks(newTime);
			// Change world's timer
			Bukkit.getWorld(w).setTime(newTime);
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

};