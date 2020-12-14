package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.List;

import org.bukkit.Bukkit;

import net.vdcraft.arvdc.timemanager.MainTM;

public class ValuesConverter extends MainTM {
	
	/**
	 * Check and correct any 'speed' value (returns a double)
	 */
	public static double returnCorrectSpeed(Double speed) {
		if (!speed.equals(realtimeSpeed)) { // Don't modify the real time value
			if (speed > speedMax) { // Forbid too big numbers
				speed = speedMax;
			} else if (speed < 0) { // Forbid too small numbers
				speed = 0.0;
			}
		}
		return speed;
	}

	/**
	 * Check and correct the 'refreshrate' value (returns an integer)
	 */
	public static Integer returnCorrectRate(Integer newRefreshRate) {
		if (newRefreshRate > refreshMax) { // Forbid too big numbers
			newRefreshRate = refreshMax;
		} else if (newRefreshRate < refreshMin) {
			newRefreshRate = refreshMin; // Forbid too small numbers
		}
		return newRefreshRate;
	}

	/**
	 * Check and correct any 'start' or 'time' tick value (returns a long)
	 */
	public static long returnCorrectTicks(Long time) {
		while (time >= 24000) { // Forbid numbers higher than 23999 (= end of the MC day)
			time -= 24000;
		}
		while (time < 0) { // Forbid numbers smaller than 0 (= start of the MC day)
			time += 24000;
		}
		return time;
	}

	/**
	 * Check and correct the 'initialTIckNb' tick value (returns a long)
	 */
	public static long returnCorrectInitTicks(Long time) {
		while (time >= 1728000) { // Forbid numbers higher than 727999 (= end of the real day)
			time -= 1728000;
		}
		while (time < 0) { // Forbid numbers smaller than 0 (= start of the real day)
			time += 1728000;
		}
		return time;
	}

	/**
	 * Check and correct the 'wakeUpTick' tick value (returns a long)
	 */
	public static long returnCorrectwakeUpTick(Long time) {
		if (time > 6000) time = 6000L;// Forbid numbers higher than 6000
		if (time < 0) time = 0L; // Forbid numbers smaller than 0
		return time;
	}

	/**
	 * Convert a tick in its related part of the day (returns a string)
	 */
	public static String getDayPartToDisplay(long tick) {
		String wichPart = new String();
		if (tick >= dawnStart && tick < dayStart) {
			wichPart = "dawn";
		} else if (tick >= dayStart && tick < duskStart) {
			wichPart = "day";
		} else if (tick >= duskStart && tick < nightStart) {
			wichPart = "dusk";
		} else if (tick >= nightStart && tick < mcDayEnd) {
			wichPart = "night";
		} else {
			return null;
		}
		return wichPart;
	}

	/**
	 *  Get the correct speed value's name (daySpeed or nightSpeed) for a given tick (returns a string)
	 */
	public static String wichSpeedParam(long tick) {
		String speedParam;
		if (getDayPartToDisplay(tick).equalsIgnoreCase("day") || getDayPartToDisplay(tick).equalsIgnoreCase("dusk")) {	    
			speedParam = CF_D_SPEED;	
		} else {
			speedParam = CF_N_SPEED;
		}
		return speedParam;
	}

	/**
	 * Convert a listed string value to a 'start' or 'time' tick value (returns a
	 * string)
	 */
	public static String returnTickFromStringValue(String tick) {
		if (tick.equalsIgnoreCase("day")) {
			tick = "1000";
		} else if (tick.equalsIgnoreCase("midday") || tick.equalsIgnoreCase("noon")) {
			tick = "6000";
		} else if (tick.equalsIgnoreCase("dusk") || tick.equalsIgnoreCase("sunset") || tick.equalsIgnoreCase("evening")) {
			tick = "12000";
		} else if (tick.equalsIgnoreCase("night")) {
			tick = "13000";
		} else if (tick.equalsIgnoreCase("midnight")) {
			tick = "18000";
		} else if (tick.equalsIgnoreCase("dawn") || tick.equalsIgnoreCase("sunrise") || tick.equalsIgnoreCase("morning")) {
			tick = "23000";
		}
		return tick;
	}

	/**
	 * Get and convert current milliseconds UTC+0 time to a 1/1728000 tick value
	 * (returns a long)
	 */
	public static Long returnServerTick() {
		long ticksSinceEpoch = System.currentTimeMillis() / 50L; // Get the server actual time in milliseconds and
		// convert it into ticks
		long daillyServerTick = ticksSinceEpoch % 1728000L; // Display a 24h day loop (1728000 ticks = 1 real day)
		return daillyServerTick;
	}

	/**
	 * Convert a tick value and return a correct UTC value (returns a long)
	 */
	public static long returnCorrectUTC(Long tick) {
		tick = (long) Math.floor(tick / 1000); // Use the 'start' value as an UTC modifier
		if (tick > 12) { // Forbid too big numbers
			tick = 12 - tick;
		} else if (tick < -12) { // Forbid too small numbers
			tick = 12 + tick;
		}
		return tick;
	}

	/**
	 * Format a positive/negative number and return a formatted UTC+/-n value
	 * (returns a string)
	 */
	public static String formatAsUTC(Long tick) {
		tick = returnCorrectUTC(tick);
		String formattedUTC;
		if (tick < 0) {
			formattedUTC = "UTC" + tick + "h";
		} else {
			formattedUTC = "UTC+" + tick + "h";
		}
		return formattedUTC;
	}

	/**
	 * Get and convert the current millisecond UTC+0 time to HH:mm:ss (returns a
	 * string)
	 */
	public static String returnServerTime() {
		long seconds = System.currentTimeMillis() / 1000L; // x ms in 1 second
		long s = seconds % 60;
		long m = (seconds / 60) % 60;
		long H = (seconds / (60 * 60)) % 24;
		return String.format("%02d:%02d:%02d", H, m, s) + " UTC";
	}

	/**
	 * Get and convert a real time tick (1/1728000) to HH:mm:ss (returns a string)
	 */
	public static String returnRealTimeFromTickValue(Long tick) {
		Long newTick = tick / 20L; // x tick in 1 seconds
		long s = newTick % 60;
		long m = (newTick / 60) % 60;
		long H = (newTick / (60 * 60)) % 24;
		return String.format("%02d:%02d:%02d", H, m, s) + " UTC";
	}

	/**
	 * Get and convert HH:mm[:ss] to a tick nb (1/1728000) (returns a string)
	 */
	public static String returnTickFromServerTimeValue(String time) {
		String[] splitedHms = time.split(":");
		try {
			Long H = Long.parseLong(splitedHms[0]) % 24;
			Long m = 0L;
			Long s = 0L;
			if (splitedHms.length >= 2)
				m = Long.parseLong(splitedHms[1]) % 60;
			if (splitedHms.length >= 3)
				s = Long.parseLong(splitedHms[2]) % 60;
			Long calcTick = ((H * 72000) + (m * 1200) + (s * 20)) % 1728000;
			return calcTick.toString();
		} catch (NumberFormatException nfe) {
			return time;
		}
	}

	/**
	 * Return correct case of the locale (xx_XX) (returns a string)
	 */
	public static String returnCorrectLocaleCase(String l) {
		String checkedLocale;
		if (l.contains("_")) {
			String[] splitLocale = l.split("_");
			String xx_XXLocale = splitLocale[0] + "_" + splitLocale[1].toUpperCase();
			checkedLocale = xx_XXLocale;
		} else {
			checkedLocale = l;
		}
		return checkedLocale;
	}

	/**
	 * Use the first part to reach the nearest lang [en_GB] >>> [en_] >>> [en_US]
	 * (returns a string)
	 */
	public static String returnNearestLang(String l) {
		String nearestLocale = serverLang; // If not existing, use the default language value
		if (l.contains("_")) {
			String[] splitLocale = l.split("_");
			String xx_Locale = splitLocale[0] + "_";
			List<String> existingLangList = LgFileHandler.setAnyListFromLang(CF_lANGUAGES);
			for (String lang : existingLangList) {
				if (lang.contains(xx_Locale)) {
					nearestLocale = lang;
				}
			}
		}
		return nearestLocale;
	}

	/**
	 * Replace 'spaces' in a given list (returns a string)
	 */
	public static List<String> replaceSpacesInList(List<String> l) {
		// TODO >>> Find a more appropriate solution for world names with spaces
		for (String nameWithSpaces : l) {
			if (nameWithSpaces.contains(" ")) {
				l.remove(nameWithSpaces);
				// u00a0 is for "NBSP", u02d9 is for "˙", u00A70 is for "black", " " is for "reset"
				nameWithSpaces = nameWithSpaces.replace(" ", "\u02d9");
				l.add(nameWithSpaces);
			}
		}
		return l;
	}

	/**
	 * Restore missing 'spaces' in a string (returns a string)
	 */
	public static String restoreSpacesInString(String s) {
		if (s.contains("\u02d9")) {
			s = s.replace("\u02d9", " ");
		}
		return s;
	}

	/**
	 * Get and convert a MC tick (1/2400) to HH:mm:ss (returns a string)
	 */
	public static String returnTimeFromTickValue(Long ticks) {
		Long newTicks = (ticks + 6000L) * 72L; // Adjust offset and go real time
		newTicks = returnCorrectInitTicks(newTicks);
		newTicks = newTicks / 20L; // x tick in 1 seconds
		Long s = newTicks % 60;
		Long m = (newTicks / 60) % 60;
		Long H = (newTicks / (60 * 60)) % 24;
		String output = String.format("%02d:%02d:%02d", H, m, s);
		if (debugMode) Bukkit.getServer().getConsoleSender().sendMessage(MainTM.prefixDebugMode + " Given tick \"§e" + ticks + "§b\" was converted to \"§e" + output+ "§b\".");
		return output;
	}

	/**
	 * Get and convert [HH:mm:ss] to a tick nb (1/24000) (returns a string)
	 */
	public static String returnTickFromTimeValue(String time) {
		String[] splitedHms = time.split(":");
		try {
			Long H = Long.parseLong(splitedHms[0]) % 24;
			Long m = 0L;
			Long s = 0L;
			if (splitedHms.length >= 2)
				m = Long.parseLong(splitedHms[1]) % 60;
			if (splitedHms.length >= 3)
				s = Long.parseLong(splitedHms[2]) % 60;
			Float calcTick = (float) (((H * 24000 / 24) + (m * 16.678) + (s * 0.278) - 6000L) % 24000);
			Long newTick = (long) Math.floor(calcTick);
			return newTick.toString();
		} catch (NumberFormatException nfe) {
			return time;
		}
	}

	/**
	 * Restrain refresh rate (modifies the configuration without saving the file)
	 */
	public static void restrainRate() {
		try { // Check if value is an integer
			refreshRateInt = MainTM.getInstance().getConfig().getInt(CF_REFRESHRATE);
			refreshRateInt = returnCorrectRate(refreshRateInt);
		} catch (NumberFormatException nfe) { // If not an integer, use the default refresh value
			refreshRateInt = defRefresh;
		}
		MainTM.getInstance().getConfig().set(CF_REFRESHRATE, refreshRateInt);
	}

	/**
	 * Restrain initial tick (modifies the configuration without saving the file)
	 */
	public static void restrainInitTick() {
		long newInitialTick;
		try { // Check if value is a long
			initialTick = MainTM.getInstance().getConfig().getLong(CF_INITIALTICK + "." + CF_INITIALTICKNB);
			newInitialTick = returnCorrectInitTicks(initialTick);
		} catch (NumberFormatException nfe) { // If not a long, use the current time value
			newInitialTick = returnServerTick(); // Create the initial tick
		}
		initialTick = newInitialTick;
		MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_INITIALTICKNB, newInitialTick);
	}

	/**
	 * Restrain wakeUpTick tick (modifies the configuration without saving the file)
	 */
	public static void restrainWakeUpTick() {
		Long newWakeUpTick = 0L;
		try { // Check if value is a long
			newWakeUpTick = MainTM.getInstance().getConfig().getLong(CF_WAKEUPTICK);
			newWakeUpTick = returnCorrectwakeUpTick(newWakeUpTick);
		} catch (NumberFormatException nfe) {} // If not a long, use the default value
		MainTM.getInstance().getConfig().set(CF_WAKEUPTICK, newWakeUpTick);
	}

	/**
	 * Restrain start timers (modifies the configuration without saving the file)
	 */
	public static void restrainStart(String world) {
		long t = Bukkit.getWorld(world).getTime();
		String time = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_START);
		String currentSpeed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + wichSpeedParam(t));
		time = ValuesConverter.returnTickFromStringValue(time); // Check if value is a part of the day
		long tick;
		try { // Check if value is a long
			tick = Long.parseLong(time);
			if (currentSpeed.contains("24") || currentSpeed.equalsIgnoreCase("realtime")) { // First if speed is 'realtime', use UTC
				tick = returnCorrectUTC(tick) * 1000;
			} else {
				tick = returnCorrectTicks(tick); // else, use ticks
			}
		} catch (NumberFormatException nfe) { // If not a long, use the default start value
			tick = defStart;
		}
		MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_START, tick);
		if (debugMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + startAdjustDebugMsg + " §e" + time + "§b to §e" + tick + "§b for the world §e" + world + "§b."); // Console debug msg
	}

	/**
	 * Restrain speed modifiers (modifies the configuration without saving the file)
	 */
	public static void restrainSpeed(String world) {
		// Transform the old 'speed' value (v1.2.1 &-) in the new 'daySpeed' and 'nightSpeed' (v1.3.0 &+) TODO >>> Should be erased someday
		if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST + "." + world).getKeys(false).contains(CF_SPEED)) {
			double speed;
			try { // Check if the old speed value is a double
				speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_SPEED);
			} catch (NumberFormatException nfe) { // If not a double, use the default refresh value
				speed = defSpeed;
			}
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED, speed);
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED, speed);
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SPEED, null);
		}
		double daySpeedNb;
		String daySpeed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED);
		double nightSpeedNb;
		String nightSpeed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED);
		if (daySpeed.contains("24") || daySpeed.equalsIgnoreCase("realtime") || nightSpeed.contains("24") || nightSpeed.equalsIgnoreCase("realtime")) {
			daySpeedNb = MainTM.realtimeSpeed;
			nightSpeedNb = MainTM.realtimeSpeed;
		} else {
			try { // Check if day value is a double
				daySpeedNb = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED);
				daySpeedNb = returnCorrectSpeed(daySpeedNb);
			} catch (NumberFormatException nfe) { // If not a double, use the default refresh value
				daySpeedNb = defSpeed;
			}
			try { // Check if night value is a double
				nightSpeedNb = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED);
				nightSpeedNb = returnCorrectSpeed(nightSpeedNb);
			} catch (NumberFormatException nfe) { // If not a double, use the default refresh value
				nightSpeedNb = defSpeed;
			} 
		}
		MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED, daySpeedNb);
		MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED, nightSpeedNb);
		// Debug msg
		if (debugMode) {
			Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + daySpeedAdjustDebugMsg + " §e" + daySpeed + "§b to §e" + daySpeedNb + "§b for the world §e" + world + "§b."); // Console debug msg
			Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + nightSpeedAdjustDebugMsg + " §e" + nightSpeed + "§b to §e" + nightSpeedNb + "§b for the world §e" + world + "§b."); // Console debug msg
		}
	}

	/**
	 * Force 'sync' to true for the 24.0 speed, then false when change to another
	 * speed ratio. & force 'sync' to false for the 0.0 speed. (modifies the
	 * configuration without saving the file)
	 */
	public static void restrainSync(String world, Double oldSpeed) {
		long t = Bukkit.getWorld(world).getTime();
		Double currentSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + wichSpeedParam(t));
		if (currentSpeed == 24.0) { // new speed is 24
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, "true");
			if (debugMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + syncAdjustTrueDebugMsg + " §e" + world + "§b."); // Console debug msg
		} else if (currentSpeed == 0.0) { // new speed is 0
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, "false");
			if (debugMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + syncAdjustFalseDebugMsg + " §e" + world + "§b."); // Console debug msg
		} else if (oldSpeed == 24.0) { // new speed is anything else with previous value 24
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, "false");
			if (debugMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + syncAdjustFalseDebugMsg + " §e" + world + "§b."); // Console debug msg
		} // else, don't do anything
	}
	
	/**
	 * If a world's speed:00. or speed:24.0 force 'sleep' to false (modifies the
	 * configuration without saving the file)
	 */
	public static void restrainSleep(String world) {
		long t = Bukkit.getWorld(world).getTime();
		String currentSpeed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + wichSpeedParam(t));
		if (currentSpeed.equalsIgnoreCase("0.0") || currentSpeed.contains("24")) {
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SLEEP, "false");
			if (debugMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + sleepAdjustFalseDebugMsg + " §e" + world + "§b."); // Console debug msg
		}
	}

};