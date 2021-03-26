package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;

import net.vdcraft.arvdc.timemanager.MainTM;

public class ValuesConverter extends MainTM {

	/**
	 * Checks and corrects any 'speed' value
	 * (returns a double)
	 */
	public static double correctSpeed(double speed) {
		if (speed != realtimeSpeed) { // Don't modify the real time value
			if (speed > speedMax) { // Forbid too big numbers
				speed = speedMax;
			} else if (speed < 0) { // Forbid too small numbers
				speed = 0.0;
			}
		}
		return speed;
	}

	/**
	 * Checks and corrects the 'refreshRate' value
	 * (returns an integer)
	 */
	public static Integer correctRefreshRate(int newRefreshRate) {
		if (newRefreshRate > refreshMax) { // Forbid too big numbers
			newRefreshRate = refreshMax;
		} else if (newRefreshRate < refreshMin) {
			newRefreshRate = refreshMin; // Forbid too small numbers
		}
		return newRefreshRate;
	}

	/**
	 * Converts a decimal to a fraction, to produce the speed change with the ratio between the time to add and the refresh rate
	 * (returns a Long)
	 */
	public static Long fractionFromDecimal(Double currentSpeed, String value) {
		Long modifTime = 0L;
		Long refreshRate = 0L;
		if (currentSpeed >= 0.9) {
			modifTime = 10L;
			refreshRate = 11L;
		} else if (currentSpeed >= 0.8) {
			modifTime = 5L;
			refreshRate = 6L;
		} else if (currentSpeed >= 0.7) {
			modifTime = 5L;
			refreshRate = 7L;
		} else if (currentSpeed >= 0.65) {
			modifTime = 2L;
			refreshRate = 3L;
		} else if (currentSpeed >= 0.6) {
			modifTime = 5L;
			refreshRate = 8L;
		} else if (currentSpeed >= 0.55) {
			modifTime = 5L;
			refreshRate = 9L;
		} else if (currentSpeed >= 0.5) {
			modifTime = 4L;
			refreshRate = 8L;
		} else if (currentSpeed >= 0.45) {
			modifTime = 5L;
			refreshRate = 11L;
		} else if (currentSpeed >= 0.4) {
			modifTime = 2L;
			refreshRate = 5L;
		} else if (currentSpeed >= 0.3) {
			modifTime = 2L;
			refreshRate = 6L;
		} else if (currentSpeed >= 0.25) {
			modifTime = 2L;
			refreshRate = 8L;
		} else if (currentSpeed >= 0.2) {
			modifTime = 2L;
			refreshRate = 10L;
		} else if (currentSpeed >= 0.1) {
			modifTime = 1L;
			refreshRate = 10L;
		} else if (currentSpeed > 0.05) {
			modifTime = 1L;
			refreshRate = 15L;
		} else if (currentSpeed <= 0.05) {
			modifTime = 1L;
			refreshRate = 20L;
		}
		if (value.equalsIgnoreCase("modifTime")) return modifTime;
		else if (value.equalsIgnoreCase("refreshRate")) return refreshRate;
		else return null;
	}

	/**
	 * Checks and corrects any 'start' or 'time' tick value
	 * (returns a long)
	 */
	public static long correctDailyTicks(long time) {
		time = ((time % 24000) + 24000) % 24000; // Forbid numbers lower than 0 and higher than 23999 (= end of the MC day)
		return time;
	}

	/**
	 * Checks and corrects the 'initialTIckNb' tick value
	 * (returns a long)
	 */
	public static long correctInitTicks(long time) {
		time = ((time % 1728000) + 1728000) % 1728000; // Forbid numbers lower than 0 and higher than 727999 (= end of the real day)
		return time;
	}

	/**
	 * Checks and corrects the 'wakeUpTick' tick value
	 * (returns a long)
	 */
	public static long correctwakeUpTick(long time) {
		if (time > 6000) time = 6000L;// Forbid numbers higher than 6000
		if (time < 0) time = 0L; // Forbid numbers smaller than 0
		return time;
	}

	/**
	 * Converts a tick in its related part of the day
	 * (returns a String)
	 */
	public static String getDayPart(long tick) {
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
	 * Gets the correct speed value's name (daySpeed or nightSpeed) for a given tick
	 * (returns a String)
	 */
	public static String wichSpeedParam(long tick) {
		String speedParam;
		if (getDayPart(tick).equalsIgnoreCase("night")) {	    
			speedParam = CF_N_SPEED;
		} else {
			speedParam = CF_D_SPEED;
		}
		return speedParam;
	}

	/**
	 * Converts a listed String value to a tick value
	 * (returns a Long)
	 */
	public static Long tickFromString(String dayPart) {
		if (dayPart.equalsIgnoreCase("day")) {
			return 1000L;
		} else if (dayPart.equalsIgnoreCase("midday") || dayPart.equalsIgnoreCase("noon")) {
			return 6000L;
		} else if (dayPart.equalsIgnoreCase("dusk") || dayPart.equalsIgnoreCase("sunset") || dayPart.equalsIgnoreCase("evening")) {
			return 12000L;
		} else if (dayPart.equalsIgnoreCase("night")) {
			return 13000L;
		} else if (dayPart.equalsIgnoreCase("midnight")) {
			return 18000L;
		} else if (dayPart.equalsIgnoreCase("dawn") || dayPart.equalsIgnoreCase("sunrise") || dayPart.equalsIgnoreCase("morning")) {
			return 0L;
		} else {
			try { // If not a formatted hour neither a word, try to get a tick number
				dayPart = dayPart.replace("#", "");
				return Long.parseLong(dayPart);
			} catch (NumberFormatException nfe) { // If not a Long, set a default value
				MsgHandler.errorMsg(tickFormatMsg); // Console error msg
				return 0L;
			}
		}
	}

	/**
	 * Gets and converts current milliseconds UTC+0 time to a 1/1728000 tick value
	 * (returns a long)
	 */
	public static long getServerTick() {
		long ticksSinceEpoch = System.currentTimeMillis() / 50L; // Get the server actual time in milliseconds and convert it into ticks
		long daillyServerTick = ticksSinceEpoch % 1728000L; // Display a 24h day loop (1728000 ticks = 1 real day)
		return daillyServerTick;
	}

	/**
	 * Converts a tick value and returns a correct UTC value
	 * (returns a long)
	 */
	public static long formattedUTCFromTick(long tick) {
		tick = (long) Math.floor(tick / 1000); // Use the 'start' value as an UTC modifier
		return (((tick % 12) + 12) % 12);
	}

	/**
	 * Formats a positive/negative number and return a formatted UTC+/-n value
	 * (returns a String)
	 */
	public static String formatAsUTC(long tick) {
		tick = formattedUTCFromTick(tick);
		String formattedUTC;
		if (tick < 0) {
			formattedUTC = "UTC" + tick + "h";
		} else {
			formattedUTC = "UTC+" + tick + "h";
		}
		return formattedUTC;
	}

	/**
	 * Gets and converts the current millisecond UTC+0 time to HH:mm:ss
	 * (returns a String)
	 */
	public static String getServerTime() {
		long seconds = System.currentTimeMillis() / 1000L; // x ms in 1 second
		long s = seconds % 60;
		long m = (seconds / 60) % 60;
		long H = (seconds / (60 * 60)) % 24;
		return String.format("%02d:%02d:%02d", H, m, s) + " UTC";
	}

	/**
	 * Gets and converts a real time tick (1/1728000) to HH:mm:ss
	 * (returns a String)
	 */
	public static String realFormattedTimeFromTick(long tick) {
		long newTick = tick / 20L; // x tick in 1 seconds
		long s = newTick % 60;
		long m = (newTick / 60) % 60;
		long H = (newTick / (60 * 60)) % 24;
		return String.format("%02d:%02d:%02d", H, m, s) + " UTC";
	}

	/**
	 * Gets and converts HH:mm[:ss] to a tick nb (1/1728000)
	 * (returns a Long)
	 */
	public static Long tickFromServerTime(String time) {
		String[] splitedHms = time.split(":");
		try {
			long H = Long.parseLong(splitedHms[0]) % 24;
			long m = 0L;
			long s = 0L;
			if (splitedHms.length >= 2)
				m = Long.parseLong(splitedHms[1]) % 60;
			if (splitedHms.length >= 3)
				s = Long.parseLong(splitedHms[2]) % 60;
			Long calcTick = ((H * 72000) + (m * 1200) + (s * 20)) % 1728000;
			return calcTick;
		} catch (NumberFormatException nfe) {
			MsgHandler.errorMsg(hourFormatMsg); // Console error msg
			return 0L;
		}
	}

	/**
	 * Returns correct case of the locale (xx_XX)
	 * (returns a String)
	 */
	public static String getCorrectLocaleCase(String l) {
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
	 * Uses the first part to reach the nearest lang [en_GB] >>> [en_] >>> [en_US]
	 * (returns a String)
	 */
	public static String findNearestLang(String l) {
		String nearestLocale = serverLang; // If not existing, use the default language value
		if (l.contains("_")) {
			String[] splitLocale = l.split("_");
			String xx_Locale = splitLocale[0] + "_";
			List<String> existingLangList = LgFileHandler.setAnyListFromLang(CF_LANGUAGES);
			for (String lang : existingLangList) {
				if (lang.contains(xx_Locale)) {
					nearestLocale = lang;
				}
			}
		}
		return nearestLocale;
	}

	/**
	 * Replaces 'spaces' in a given list
	 * (returns a String)
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
	 * Restores missing 'spaces' in a String
	 * (returns a String)
	 */
	public static String restoreSpacesInString(String s) {
		if (s.contains("\u02d9")) {
			s = s.replace("\u02d9", " ");
		}
		return s;
	}

	/**
	 * Gets and converts a MC tick (1/24000) to HH:mm:ss
	 * (returns a String)
	 */
	public static String formattedTimeFromTick(long ticks) {
		long newTicks = (ticks + 6000L) * 72L; // Adjust offset and go real time
		newTicks = correctInitTicks(newTicks);
		newTicks = newTicks / 20L; // x tick in 1 seconds
		long s = newTicks % 60;
		long m = (newTicks / 60) % 60;
		long H = (newTicks / (60 * 60)) % 24;
		String output = String.format("%02d:%02d:%02d", H, m, s);
		if (debugMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Given tick \"§e" + ticks + "§b\" was converted to \"§e" + output+ "§b\"."); // Console debug msg
		return output;
	}

	/**
	 * Gets and converts [HH:mm:ss] to a tick nb (1/24000)
	 * (returns a Long)
	 */
	public static Long tickFromFormattedTime(String time) {
		String[] splitedHms = time.split(":");
		try {
			long H = Long.parseLong(splitedHms[0]) % 24;
			long m = 0L;
			long s = 0L;
			if (splitedHms.length >= 2)
				m = Long.parseLong(splitedHms[1]) % 60;
			if (splitedHms.length >= 3)
				s = Long.parseLong(splitedHms[2]) % 60;
			Float calcTick = (float) (((H * 24000 / 24) + (m * 16.678) + (s * 0.278) - 6000L) % 24000);
			return (long) Math.floor(calcTick);
		} catch (NumberFormatException nfe) {
			MsgHandler.errorMsg(hourFormatMsg); // Console error msg
			return 0L;
		}
	}
	
	/**
	 * Gets and converts [yyyy-mm-dd] to a tick
	 * (returns a Long)
	 */
	public static Long tickFromFormattedDate(String date) {
		try {
			String[] splitedDate = date.split("-");
			Long year = Long.parseLong(splitedDate[0]);
			Long month = Long.parseLong(splitedDate[1]);
			Long day = Long.parseLong(splitedDate[2]);
			Long monthToDay = 0L; // January
			Long maxDays = 31L;
			if (month == 2) {
				monthToDay = 31L; // February
				maxDays = 28L;
			} else if (month == 3) {
				monthToDay = 59L; // March
			} else if (month == 4) {
				monthToDay = 90L; // April
				maxDays = 30L;
			} else if (month == 5) {
				monthToDay = 120L; // May
			} else if (month == 6) {
				monthToDay = 151L; // June
				maxDays = 30L;
			} else if (month == 7) {
				monthToDay = 181L; // July
			} else if (month == 8) {
				monthToDay = 212L; // August
			} else if (month == 9) {
				monthToDay = 243L; // September
				maxDays = 30L;
			} else if (month == 10) {
				monthToDay = 273L; // October
			} else if (month == 11) {
				monthToDay = 304L; // November
				maxDays = 30L;
			} else if (month >= 12) {
				monthToDay = 334L; // December
			}
			// Avoid wrong values, send warn msgs
			if (year > 9999) {
				MsgHandler.warnMsg(yearFormatMsg); // Console warn msg
				year = 9999L;
			}
			if (month > 12)	MsgHandler.warnMsg(monthFormatMsg); // Console warn msg
			if (day > maxDays) {
				MsgHandler.warnMsg(dayFormatMsg + maxDays + "."); // Console warn msg	
				day = maxDays;		
			}
			Long y;
			Long m;			
			Long d;
			y = (year - 1) * 8760000; // (= 365j * 20m * 60s * 20t)	
			m = (monthToDay) * 24000; // (= 20m * 60s * 20t)			
			d = (day - 1) * 24000; // (= 20m * 60s * 20t)
			Long tick =  y + m + d;
			return tick;
		} catch (NumberFormatException nfe) {
			MsgHandler.errorMsg(dateFormatMsg); // Console error msg
			return null;
		}	
	}
	
	/**
	 * Gets and converts the current real date to a number of days
	 * (returns a Long)
	 */
	public static Long daysFromCurrentDate() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		Long elapsedDays = (long) (((year - 1) * 365) + (day - 1));
		return elapsedDays;
	}

	/**
	 * Gets and converts a tick (current Fulltime) to a number of elapsed days
	 * (returns a Long)
	 */
	public static Long elapsedDaysFromTick(long fulltime) {
		// A real day begin at 00:00
		if (MainTM.getInstance().getConfig().getString(CF_NEWDAYAT).equalsIgnoreCase(newDayStartsAt_0h00)) {
			MsgHandler.debugMsg("New day begins at §e00:00§b, so the calculation of elapsed days is : (§e" + fulltime
					+ " §b+ §e6000 §b= §e" + (fulltime + 6000) + "§b) / §e24000 §b= §e" + (fulltime + 6000) / 24000 + "§b."); // Console dev msg
			return (fulltime + 6000) / 24000;
		} else { // A MC day begin at 06:00
			MsgHandler.debugMsg("New day begins at §e06:00§b, so the calculation of elapsed days is : §e" + fulltime
					+ " §b/ §e24000 §b= §e" + (fulltime / 24000) + "§b."); // Console dev msg
			return fulltime / 24000;
		}
	}

	/**
	 * Gets and converts a tick (current Fulltime) to the number of the week in the year (returns a Long)
	 */
	public static Long yearWeekFromTick(long fulltime) {
		long daysNb = elapsedDaysFromTick(fulltime) % 365;
		return 1 + (daysNb / 7);
	}

	/**
	 * Gets and converts a number of days to a date part [dd] or [mm] or [yy] or [yyyy]
	 * (returns a String)
	 */
	public static String dateFromElapsedDays(long daysNb, String datePart) {
		// #1. Years
		if (datePart.contains("yy")) {
			long years = (1 + (long) Math.floor(daysNb / 365));
			if (datePart.equalsIgnoreCase("yyyy")) {
				years = years % 10000;
				return String.format("%04d", years);
			} else {
				years = years % 100;
				return String.format("%02d", years);
			}
		}
		// #2. Months
		long dayOfYear = 1 + (daysNb % 365); // Check what day of the year it is today to set the correct month length
		long dayOfMonth = 0L;
		Integer month = 0;
		if (dayOfYear >=1 && dayOfYear <=31) { month = 1; dayOfMonth = dayOfYear; // January 
		} else if (dayOfYear >=32 && dayOfYear <=59) { month = 2; dayOfMonth = dayOfYear - 31; // February 
		} else if (dayOfYear >=60 && dayOfYear <=90) { month = 3; dayOfMonth = dayOfYear - 59; // March 
		} else if (dayOfYear >=91 && dayOfYear <=120) { month = 4; dayOfMonth = dayOfYear - 90; // April 
		} else if (dayOfYear >=121 && dayOfYear <=151) { month = 5; dayOfMonth = dayOfYear - 120; // May 
		} else if (dayOfYear >=152 && dayOfYear <=181) { month = 6; dayOfMonth = dayOfYear - 151; // June
		} else if (dayOfYear >=182 && dayOfYear <=212) { month = 7; dayOfMonth = dayOfYear - 181; // July
		} else if (dayOfYear >=213 && dayOfYear <=243) { month = 8; dayOfMonth = dayOfYear - 212; // August 
		} else if (dayOfYear >=244 && dayOfYear <=273) { month = 9; dayOfMonth = dayOfYear - 243; // September
		} else if (dayOfYear >=274 && dayOfYear <=304) { month = 10; dayOfMonth = dayOfYear - 273; // October
		} else if (dayOfYear >=305 && dayOfYear <=334) { month = 11; dayOfMonth = dayOfYear - 304; // November
		} else if (dayOfYear >=335 && dayOfYear <=365) { month = 12; dayOfMonth = dayOfYear - 334; // December
		}
		if (datePart.equalsIgnoreCase("mm")) {
			String mm = String.format("%02d", month);
			return mm;
		}
		// #3. Days
		if (datePart.equalsIgnoreCase("dd")) {
			long days = dayOfMonth;
			String dd = String.format("%02d", days);
			return dd;
		}
		return null;
	}

	/**
	 * Compares two TimeManager versions, returns "true" if edgeVersion is bigger than the current one
	 * (returns a boolean)
	 */
	public static boolean tmVersionIsOk(String srcFile, int edgeMajor, int edgeMinor, int edgePatch, int edgeRelease, int edgeDev) {
		String currentVersion = null;
		int currentMajor = 0;
		int currentMinor = 0;
		int currentPatch = 0;
		int currentRelease = 4;
		int currentDev = 0;
		// Check current version
		if (srcFile.equalsIgnoreCase("lg"))
			currentVersion = MainTM.getInstance().langConf.getString(CF_VERSION);
		else
			currentVersion = versionTM();    	
		currentVersion = replaceChars(currentVersion);    	
		// Split version numbers
		String[] currentVersionNb = currentVersion.split("[.]");
		if (currentVersionNb.length >= 2) {
			currentMajor = Integer.parseInt(currentVersionNb[0]);
			currentMinor = Integer.parseInt(currentVersionNb[1]);
		}
		if (currentVersionNb.length >= 3) currentPatch = Integer.parseInt(currentVersionNb[2]);
		if (currentVersionNb.length >= 4) currentRelease = Integer.parseInt(currentVersionNb[3]);
		if (currentVersionNb.length >= 5) currentDev = Integer.parseInt(currentVersionNb[4]);    	
		// Compares versions
		if ((edgeMajor > currentMajor) 
				|| (edgeMajor == currentMajor && edgeMinor > currentMinor)
				|| (edgeMajor == currentMajor && edgeMinor == currentMinor && edgePatch > currentPatch)
				|| (edgeMajor == currentMajor && edgeMinor == currentMinor && edgePatch == currentPatch && edgeRelease > currentRelease)
				|| (edgeMajor == currentMajor && edgeMinor == currentMinor && edgePatch == currentPatch && edgeRelease == currentRelease && edgeDev > currentDev)) {
			return true;
		}
		return false;
	}

	/**
	 * Replaces characters before splitting version String into integers
	 */
	public static String replaceChars(String version) {
		version = version.replace("dev", "d")
				.replace("alpha", "a")
				.replace("beta", "b")
				.replace("d", "-0.")
				.replace("a", "-1.")
				.replace("b", "-2.")
				.replace("rc", "-3.")
				.replace("--", ".")
				.replace("-", ".")
				.replace("..", ".");
		try {
			String versionIntTest = version.replace(".", "");
			Integer.parseInt(versionIntTest); // Prevent all other parse errors
		} catch (NumberFormatException e) {
			MsgHandler.errorMsg(versionTMFormatMsg); // Console error msg
			return null;
		}
		return version;
	}

	/**
	 * Restrains refresh rate
	 * (modifies the configuration without saving the file)
	 */
	public static void restrainRate() {
		try { // Check if value is an integer
			refreshRateInt = MainTM.getInstance().getConfig().getInt(CF_REFRESHRATE);
			refreshRateInt = correctRefreshRate(refreshRateInt);
		} catch (NumberFormatException nfe) { // If not an integer, use the default refresh value
			refreshRateInt = defRefresh;
		}
		MainTM.getInstance().getConfig().set(CF_REFRESHRATE, refreshRateInt);
	}

	/**
	 * Restrains initial tick (modifies the configuration without saving the file)
	 */
	public static void restrainInitTick() {
		long newInitialTick;
		try { // Check if value is a long
			initialTick = MainTM.getInstance().getConfig().getLong(CF_INITIALTICK + "." + CF_INITIALTICKNB);
			newInitialTick = correctInitTicks(initialTick);
		} catch (NumberFormatException nfe) { // If not a long, use the current time value
			newInitialTick = getServerTick(); // Create the initial tick
		}
		initialTick = newInitialTick;
		MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_INITIALTICKNB, newInitialTick);
	}

	/**
	 * Restrains wakeUpTick tick (modifies the configuration without saving the file)
	 */
	public static void restrainWakeUpTick() {
		long newWakeUpTick = defWakeUpTick;
		try { // Check if value is a long
			newWakeUpTick = MainTM.getInstance().getConfig().getLong(CF_WAKEUPTICK);
			newWakeUpTick = correctwakeUpTick(newWakeUpTick);
		} catch (NumberFormatException nfe) { // If not a long, keep the default value
			MsgHandler.errorMsg(wakeUpTickFormatMsg); // Console error msg
		}
		MainTM.getInstance().getConfig().set(CF_WAKEUPTICK, newWakeUpTick);
	}

	/**
	 * Restrains start timers
	 * (modifies the configuration without saving the file)
	 */
	public static void restrainStart(String world) {
		long t = Bukkit.getWorld(world).getTime();
		String time = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_START);
		time = tickFromString(time).toString(); // Check if value is a part of the day
		String currentSpeed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + wichSpeedParam(t));
		long tick;
		try { // Check if value is a long
			tick = Long.parseLong(time);
			if (currentSpeed.contains(realtimeSpeed.toString()) || currentSpeed.equalsIgnoreCase("realtime")) { // First if speed is 'realtime', use UTC
				tick = formattedUTCFromTick(tick) * 1000;
			} else {
				tick = correctDailyTicks(tick); // else, use ticks
			}
		} catch (NumberFormatException nfe) { // If not a long, use the default start value
			MsgHandler.errorMsg(startTickFormatMsg); // Console error msg
			tick = defStart;
		}
		MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_START, tick);
		MsgHandler.debugMsg(startAdjustDebugMsg + " §e" + time + "§b to §e" + tick + "§b for the world §e" + world + "§b."); // Console debug msg
	}

	/**
	 * Restrains speed modifiers
	 * (modifies the configuration without saving the file)
	 */
	public static void restrainSpeed(String world) {
		// Transform the old 'speed' value (v1.2.1 &-) in the new 'daySpeed' and 'nightSpeed' (v1.3.0 &+) TODO >>> Should be erased someday
		if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST + "." + world).getKeys(false).contains(CF_SPEED)) {
			double speed;
			try { // Check if the old speed value is a double
				speed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_SPEED);
			} catch (NumberFormatException nfe) { // If not a double, use the default refresh value
				MsgHandler.errorMsg(speedFormatMsg); // Console error msg
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
			daySpeedNb = realtimeSpeed;
			nightSpeedNb = realtimeSpeed;
		} else {
			try { // Check if day value is a double
				daySpeedNb = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED);
				daySpeedNb = correctSpeed(daySpeedNb);
			} catch (NumberFormatException nfe) { // If not a double, use the default refresh value
				MsgHandler.errorMsg(speedFormatMsg); // Console error msg
				daySpeedNb = defSpeed;
			}
			try { // Check if night value is a double
				nightSpeedNb = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED);
				nightSpeedNb = correctSpeed(nightSpeedNb);
			} catch (NumberFormatException nfe) { // If not a double, use the default refresh value
				MsgHandler.errorMsg(speedFormatMsg); // Console error msg
				nightSpeedNb = defSpeed;
			} 
		}
		MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED, daySpeedNb);
		MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED, nightSpeedNb);
		MsgHandler.debugMsg(daySpeedAdjustDebugMsg + " §e" + daySpeed + "§b to §e" + daySpeedNb + "§b for the world §e" + world + "§b."); // Console debug msg
		MsgHandler.debugMsg(nightSpeedAdjustDebugMsg + " §e" + nightSpeed + "§b to §e" + nightSpeedNb + "§b for the world §e" + world + "§b."); // Console debug msg
	}

	/**
	 * Force 'sync' to true for the 24.0 speed, then false when change to another speed ratio
	 * Force 'sync' to false for the 0.0 speed
	 * (modifies the configuration without saving the file)
	 */
	public static void restrainSync(String world, double oldSpeed) {
		long t = Bukkit.getWorld(world).getTime();
		double currentSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + wichSpeedParam(t));
		if (currentSpeed == 24.0) { // new speed is 24
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, "true");
			MsgHandler.debugMsg(syncAdjustTrueDebugMsg + " §e" + world + "§b."); // Console debug msg
		} else if (currentSpeed == 0.0) { // new speed is 0
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, "false");
			MsgHandler.debugMsg(syncAdjustFalseDebugMsg + " §e" + world + "§b."); // Console debug msg
		} else if (oldSpeed == 24.0) { // new speed is anything else with previous value 24
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, "false");
			MsgHandler.debugMsg(syncAdjustFalseDebugMsg + " §e" + world + "§b."); // Console debug msg
		} // else, don't do anything
	}

	/**
	 * If a world gets a speed of "0" or "24", force 'sleep' to false
	 * (modifies the configuration without saving the file)
	 */
	public static void restrainSleep(String world) {
		long t = Bukkit.getWorld(world).getTime();
		String currentSpeed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + wichSpeedParam(t));
		if (currentSpeed.equalsIgnoreCase("0.0") || currentSpeed.contains("24")) {
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SLEEP, "false");
			MsgHandler.debugMsg(sleepAdjustFalseDebugMsg + " §e" + world + "§b."); // Console debug msg
		}
	}

};