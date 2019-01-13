package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.List;

import org.bukkit.Bukkit;

import net.vdcraft.arvdc.timemanager.MainTM;

public class ValuesConverter extends MainTM {

    /**
     * Check and correct any 'speed' value (returns a double)
     */
    public static double returnCorrectSpeed(Double newSpeed) {
	if (!newSpeed.equals(realtimeSpeed)) { // Don't modify the real time value
	    if (newSpeed > speedMax) { // Forbid too big numbers
		newSpeed = speedMax;
	    } else if (newSpeed < 0) { // Forbid too small numbers
		newSpeed = 0.0;
	    }
	}
	return newSpeed;
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
    public static long returnCorrectTicks(Long newTime) {
	while (newTime >= 24000) { // Forbid numbers higher than 23999 (= end of the day)
	    newTime -= 24000;
	}
	while (newTime < 0) { // Forbid numbers smaller than 0 (= start of the day)
	    newTime += 24000;
	}
	return newTime;
    }

    /**
     * Check and correct the 'initialTIckNb' tick value (returns a long)
     */
    public static long returnCorrectInitTicks(Long newTime) {
	while (newTime >= 1728000) { // Forbid numbers higher than 727999 (= end of the day)
	    newTime -= 1728000;
	}
	while (newTime < 0) { // Forbid numbers smaller than 0 (= start of the day)
	    newTime += 1728000;
	}
	return newTime;
    }

    /**
     * Convert a tick in its related part of the day (returns a string)
     */
    public static String SetDayPartToDisplay(long actualTick) {
	String wichPart = new String();
	if (actualTick >= dayStart && actualTick < duskStart) {
	    wichPart = "day";
	} else if (actualTick >= duskStart && actualTick < nightStart) {
	    wichPart = "dusk";
	} else if (actualTick >= nightStart && actualTick < dawnStart) {
	    wichPart = "night";
	} else if (actualTick >= dawnStart && actualTick < dayEnd) {
	    wichPart = "dawn";
	} else {
	    return null;
	}
	return wichPart;
    }

    /**
     * Convert a listed string value to a 'start' or 'time' tick value (returns a
     * string)
     */
    public static String returnTickFromStringValue(String tick) {
	if (tick.equalsIgnoreCase("day")) {
	    tick = "0";
	} else if (tick.equalsIgnoreCase("midday") || tick.equalsIgnoreCase("noon")) {
	    tick = "6000";
	} else if (tick.equalsIgnoreCase("dusk") || tick.equalsIgnoreCase("sunset") || tick.equalsIgnoreCase("evening")) {
	    tick = "11500";
	} else if (tick.equalsIgnoreCase("night")) {
	    tick = "13000";
	} else if (tick.equalsIgnoreCase("midnight")) {
	    tick = "18000";
	} else if (tick.equalsIgnoreCase("dawn") || tick.equalsIgnoreCase("sunrise") || tick.equalsIgnoreCase("morning")) {
	    tick = "22500";
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
    public static long returnCorrectUTC(Long tickValue) {
	tickValue = (long) Math.floor(tickValue / 1000); // Use the 'start' value as an UTC modifier
	if (tickValue > 12) { // Forbid too big numbers
	    tickValue = 12 - tickValue;
	} else if (tickValue < -12) { // Forbid too small numbers
	    tickValue = 12 + tickValue;
	}
	return tickValue;
    }

    /**
     * Format a positive/negative number and return a formatted UTC+/-n value
     * (returns a string)
     */
    public static String formatAsUTC(Long tickValue) {
	tickValue = returnCorrectUTC(tickValue);
	String formattedUTC;
	if (tickValue < 0) {
	    formattedUTC = "UTC" + tickValue + "h";
	} else {
	    formattedUTC = "UTC+" + tickValue + "h";
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
	    List<String> existingLangList = LgFileHandler.setAnyListFromLang("languages");
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
    // TODO Find a more appropriate solution for world names with spaces
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
    // TODO add (Long ticks, CommandSender sender) to create a specific debug msg
    public static String returnTimeFromTickValue(Long ticks) {
	Long newTicks = (ticks + 6000L) * 72L; // Adjust offset and go real time
	newTicks = returnCorrectInitTicks(newTicks);
	newTicks = newTicks / 20L; // x tick in 1 seconds
	Long s = newTicks % 60;
	Long m = (newTicks / 60) % 60;
	Long H = (newTicks / (60 * 60)) % 24;
	return String.format("%02d:%02d:%02d", H, m, s);
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
     * Restrain refresh rate (modifies the configuration)
     */
    public static void restrainRate() {
	try { // Check if value is an integer
	    refreshRateInt = MainTM.getInstance().getConfig().getInt("refreshRate");
	    refreshRateInt = returnCorrectRate(refreshRateInt);
	} catch (NumberFormatException nfe) { // If not an integer, use the default refresh value
	    refreshRateInt = defRefresh;
	}
	MainTM.getInstance().getConfig().set("refreshRate", refreshRateInt);
    }

    /**
     * Restrain initial tick (modifies the configuration)
     */
    public static void restrainInitTick() {
	long newInitialTick;
	try { // Check if value is a long
	    initialTick = MainTM.getInstance().getConfig().getLong("initialTick.initialTickNb");
	    newInitialTick = returnCorrectInitTicks(initialTick);
	} catch (NumberFormatException nfe) { // If not a long, use the current time value
	    newInitialTick = returnServerTick(); // Create the initial tick
	}
	initialTick = newInitialTick;
	MainTM.getInstance().getConfig().set("initialTick.initialTickNb", newInitialTick);
    }

    /**
     * Restrain speed modifiers (modifies the configuration without saving the file)
     */
    public static void restrainSpeed(String worldToSet) {
	double speedModifier;
	String isSpeedRealtime = MainTM.getInstance().getConfig().getString("worldsList." + worldToSet + ".speed");
	if (isSpeedRealtime.equalsIgnoreCase("realtime")) {
	    speedModifier = MainTM.realtimeSpeed;
	} else {
	    try { // Check if value is a double
		speedModifier = MainTM.getInstance().getConfig().getDouble("worldsList." + worldToSet + ".speed");
		speedModifier = returnCorrectSpeed(speedModifier);
	    } catch (NumberFormatException nfe) { // If not a double, use the default refresh value
		speedModifier = defSpeed;
	    }
	}
	MainTM.getInstance().getConfig().set("worldsList." + worldToSet + ".speed", speedModifier);
	if (debugMode == true)
	    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + speedAdjustDebugMsg + " §e"
		    + isSpeedRealtime + "§b to §e" + speedModifier + "§b for the world §e" + worldToSet + "§b."); // Console debug msg
    }

    /**
     * Restrain start timers (modifies the configuration without saving the file)
     */
    public static void restrainStart(String worldToSet) {
	String timeToSet = MainTM.getInstance().getConfig().getString("worldsList." + worldToSet + ".start");
	String currentSpeed = MainTM.getInstance().getConfig().getString("worldsList." + worldToSet + ".speed");
	timeToSet = ValuesConverter.returnTickFromStringValue(timeToSet); // Check if value is a part of the day
	long tickToSet;
	try { // Check if value is a long
	    tickToSet = Long.parseLong(timeToSet);
	    if (currentSpeed.equals("24")) { // First if speed is 'realtime', use UTC
		tickToSet = returnCorrectUTC(tickToSet) * 1000;
	    } else {
		tickToSet = returnCorrectTicks(tickToSet); // else, use ticks
	    }
	} catch (NumberFormatException nfe) { // If not a long, use the default start value
	    tickToSet = defStart;
	}
	MainTM.getInstance().getConfig().set("worldsList." + worldToSet + ".start", tickToSet);
	if (debugMode == true)
	    Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + startAdjustDebugMsg + " §e"
		    + timeToSet + "§b to §e" + tickToSet + "§b for the world §e" + worldToSet + "§b."); // Console debug
	// msg
    }

    /**
     * Force 'sync' to true for the 24.0 speed, then false when change to another
     * speed ratio. & force 'sync' to false for the 0.0 speed. (modifies the
     * configuration without saving the file)
     */
    public static void restrainSync(String worldToSet, Double oldSpeed) {
	Double currentSpeed = MainTM.getInstance().getConfig().getDouble("worldsList." + worldToSet + ".speed");
	if (currentSpeed == 24.0) { // new speed is 24
	    MainTM.getInstance().getConfig().set("worldsList." + worldToSet + ".sync", "true");
	    if (debugMode == true)
		Bukkit.getServer().getConsoleSender()
			.sendMessage(prefixDebugMode + " " + syncAdjustTrueDebugMsg + " §e" + worldToSet + "§b."); // Console
	    // debug
	    // msg
	} else if (currentSpeed == 0.0) { // new speed is 0
	    MainTM.getInstance().getConfig().set("worldsList." + worldToSet + ".sync", "false");
	    if (debugMode == true)
		Bukkit.getServer().getConsoleSender()
			.sendMessage(prefixDebugMode + " " + syncAdjustFalseDebugMsg + " §e" + worldToSet + "§b."); // Console
	    // debug
	    // msg
	} else if (oldSpeed == 24.0) { // new speed is anything else with previous value 24
	    MainTM.getInstance().getConfig().set("worldsList." + worldToSet + ".sync", "false");
	    if (debugMode == true)
		Bukkit.getServer().getConsoleSender()
			.sendMessage(prefixDebugMode + " " + syncAdjustFalseDebugMsg + " §e" + worldToSet + "§b."); // Console
	    // debug
	    // msg
	} // else, don't do anything
    }

    /**
     * If a world's speed:00. or speed:24.0 force 'sleep' to false (modifies the
     * configuration without saving the file)
     */
    public static void restrainSleep(String worldToSet) {
	String currentSpeed = MainTM.getInstance().getConfig().getString("worldsList." + worldToSet + ".speed");
	if (currentSpeed.equals("0.0") || currentSpeed.equals("24.0")) {
	    MainTM.getInstance().getConfig().set("worldsList." + worldToSet + ".sleep", "false");
	    if (debugMode == true)
		Bukkit.getServer().getConsoleSender()
			.sendMessage(prefixDebugMode + " " + sleepAdjustFalseDebugMsg + " §e" + worldToSet + "§b."); // Console
	    // debug
	    // msg
	}
    }

};