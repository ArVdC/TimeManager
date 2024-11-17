package net.vdcraft.arvdc.timemanager.mainclass;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.vdcraft.arvdc.timemanager.MainTM;

public class ValuesConverter extends MainTM {
	
	// ========== Converting Methods ========== //
	
	/**
	 * Converts a formated time [00d-00h-00m-00s] to a decimal speed multiplier
	 * (returns a double)
	 */
	public static double doubleFromFormatedTime(String formatedTime, String dayPart) {
		try {
			String t = formatedTime.replace("d", "").replace("h", "").replace("m", "").replace("s", "");
			String[] splitedTime = t.split("-");
			int d = Integer.parseInt(splitedTime[0]);
			int h = Integer.parseInt(splitedTime[1]);
			int m = Integer.parseInt(splitedTime[2]);
			int s = Integer.parseInt(splitedTime[3]);
			// Find the expected time length
			Long ticks = (d*1728000L) + (h*72000L) + (m*1200L) + (s*20L);
			if (ticks > 0.0) {
				// Set the asked cycle
				double cycle;
				switch (dayPart) {
					default :
					case CMD_SET_DURATION :
						cycle = 24000;
						break;
					case CMD_SET_D_DURATION :
						cycle = 13000;
						break;
					case CMD_SET_N_DURATION :
						cycle = 11000;
						break;
					}
				// Find the corresponding multiplier
				double denominator = ticks / cycle;
				double multiplier = 1.0 / denominator;
				multiplier = (Math.round(multiplier * Math.pow(10,9))) / Math.pow(10,9);
				MsgHandler.debugMsg(durationToFractionDebugMsg + ChatColor.YELLOW + formatedTime + ChatColor.AQUA + " = " + ChatColor.YELLOW + + 1.0 + "/" + denominator + ChatColor.AQUA + " = " + ChatColor.YELLOW + multiplier);  // Console debug msg
				if (multiplier > speedMax) multiplier = speedMax;
				return multiplier;
			} else return 0.0;
		} catch (NumberFormatException nfe) {
			MsgHandler.errorMsg(durationFormatMsg); // Console error msg
			return 1.0;
		}
	}
	
	/**
	 * Converts a decimal to a fraction, to produce the speed change with the ratio between the time to add and the refresh rate
	 * (returns a Long)
	 */
	public static Long fractionFromDecimal(Double currentSpeed, String value) {
		Long modifTime = 0L;
		Long refreshRate = 0L;
		if (currentSpeed >= 0.95) {
			modifTime = 19L;
			refreshRate = 20L;
		} else if (currentSpeed >= 0.947) {
			modifTime = 18L;
			refreshRate = 19L;
		} else if (currentSpeed >= 0.944) {
			modifTime = 17L;
			refreshRate = 18L;
		} else if (currentSpeed >= 0.941) {
			modifTime = 16L;
			refreshRate = 17L;
		} else if (currentSpeed >= 0.937) {
			modifTime = 15L;
			refreshRate = 16L;
		} else if (currentSpeed >= 0.933) {
			modifTime = 14L;
			refreshRate = 15L;
		} else if (currentSpeed >= 0.928) {
			modifTime = 13L;
			refreshRate = 14L;
		} else if (currentSpeed >= 0.923) {
			modifTime = 12L;
			refreshRate = 13L;
		} else if (currentSpeed >= 0.916) {
			modifTime = 11L;
			refreshRate = 12L;
		} else if (currentSpeed >= 0.909) {
			modifTime = 10L;
			refreshRate = 11L;
		} else if (currentSpeed >= 0.9) {
			modifTime = 9L;
			refreshRate = 10L;
		} else if (currentSpeed >= 0.888) {
			modifTime = 8L;
			refreshRate = 9L;
		} else if (currentSpeed >= 0.875) {
			modifTime = 7L;
			refreshRate = 8L;
		} else if (currentSpeed >= 0.857) {
			modifTime = 6L;
			refreshRate = 7L;
		} else if (currentSpeed >= 0.846) {
			modifTime = 11L;
			refreshRate = 13L;
		} else if (currentSpeed >= 0.833) {
			modifTime = 5L;
			refreshRate = 6L;
		} else if (currentSpeed >= 0.818) {
			modifTime = 9L;
			refreshRate = 11L;
		} else if (currentSpeed >= 0.8) {
			modifTime = 4L;
			refreshRate = 5L;
		} else if (currentSpeed >= 0.785) {
			modifTime = 11L;
			refreshRate = 14L;
		} else if (currentSpeed >= 0.777) {
			modifTime = 7L;
			refreshRate = 9L;
		} else if (currentSpeed >= 0.75) {
			modifTime = 3L;
			refreshRate = 4L;
		} else if (currentSpeed >= 0.727) {
			modifTime = 8L;
			refreshRate = 11L;
		} else if (currentSpeed >= 0.714) {
			modifTime = 5L;
			refreshRate = 7L;
		} else if (currentSpeed >= 0.7) {
			modifTime = 7L;
			refreshRate = 10L;
		} else if (currentSpeed >= 0.692) {
			modifTime = 9L;
			refreshRate = 13L;
		} else if (currentSpeed >= 0.666) {
			modifTime = 2L;
			refreshRate = 3L;
		} else if (currentSpeed >= 0.636) {
			modifTime = 7L;
			refreshRate = 11L;
		} else if (currentSpeed >= 0.625) {
			modifTime = 5L;
			refreshRate = 8L;
		} else if (currentSpeed >= 0.615) {
			modifTime = 8L;
			refreshRate = 13L;
		} else if (currentSpeed >= 0.6) {
			modifTime = 3L;
			refreshRate = 5L;
		} else if (currentSpeed >= 0.583) {
			modifTime = 7L;
			refreshRate = 12L;
		} else if (currentSpeed >= 0.571) {
			modifTime = 4L;
			refreshRate = 7L;
		} else if (currentSpeed >= 0.555) {
			modifTime = 5L;
			refreshRate = 9L;
		} else if (currentSpeed >= 0.545) {
			modifTime = 6L;
			refreshRate = 11L;
		} else if (currentSpeed >= 0.538) {
			modifTime = 7L;
			refreshRate = 13L;
		} else if (currentSpeed >= 0.5) {
			modifTime = 1L;
			refreshRate = 2L;
		} else if (currentSpeed >= 0.461) {
			modifTime = 6L;
			refreshRate = 13L;
		} else if (currentSpeed >= 0.454) {
			modifTime = 5L;
			refreshRate = 11L;
		} else if (currentSpeed >= 0.444) {
			modifTime = 4L;
			refreshRate = 9L;
		} else if (currentSpeed >= 0.428) {
			modifTime = 3L;
			refreshRate = 7L;
		} else if (currentSpeed >= 0.416) {
			modifTime = 5L;
			refreshRate = 12L;
		} else if (currentSpeed >= 0.4) {
			modifTime = 2L;
			refreshRate = 5L;
		} else if (currentSpeed >= 0.384) {
			modifTime = 5L;
			refreshRate = 13L;
		} else if (currentSpeed >= 0.375) {
			modifTime = 3L;
			refreshRate = 8L;
		} else if (currentSpeed >= 0.363) {
			modifTime = 4L;
			refreshRate = 11L;
		} else if (currentSpeed >= 0.357) {
			modifTime = 5L;
			refreshRate = 14L;
		} else if (currentSpeed >= 0.333) {
			modifTime = 1L;
			refreshRate = 3L;
		} else if (currentSpeed >= 0.315) {
			modifTime = 6L;
			refreshRate = 19L;
		} else if (currentSpeed >= 0.3) {
			modifTime = 3L;
			refreshRate = 10L;
		} else if (currentSpeed >= 0.285) {
			modifTime = 2L;
			refreshRate = 7L;
		} else if (currentSpeed >= 0.272) {
			modifTime = 3L;
			refreshRate = 11L;
		} else if (currentSpeed >= 0.266) {
			modifTime = 4L;
			refreshRate = 15L;
		} else if (currentSpeed >= 0.25) {
			modifTime = 1L;
			refreshRate = 4L;
		} else if (currentSpeed >= 0.235) {
			modifTime = 4L;
			refreshRate = 17L;
		} else if (currentSpeed >= 0.222) {
			modifTime = 2L;
			refreshRate = 9L;
		} else if (currentSpeed >= 0.214) {
			modifTime = 3L;
			refreshRate = 14L;
		} else if (currentSpeed >= 0.2) {
			modifTime = 1L;
			refreshRate = 5L;
		} else if (currentSpeed >= 0.187) {
			modifTime = 3L;
			refreshRate = 16L;
		} else if (currentSpeed >= 0.181) {
			modifTime = 2L;
			refreshRate = 11L;
		} else if (currentSpeed >= 0.176) {
			modifTime = 3L;
			refreshRate = 17L;
		} else if (currentSpeed >= 0.166) {
			modifTime = 1L;
			refreshRate = 6L;
		} else if (currentSpeed >= 0.153) {
			modifTime = 2L;
			refreshRate = 13L;
		} else if (currentSpeed >= 0.15) {
			modifTime = 3L;
			refreshRate = 20L;
		} else if (currentSpeed >= 0.142) {
			modifTime = 1L;
			refreshRate = 7L;
		} else if (currentSpeed >= 0.133) {
			modifTime = 2L;
			refreshRate = 15L;
		} else if (currentSpeed >= 0.125) {
			modifTime = 1L;
			refreshRate = 8L;
		} else if (currentSpeed >= 0.117) {
			modifTime = 2L;
			refreshRate = 17L;
		} else if (currentSpeed >= 0.111) {
			modifTime = 1L;
			refreshRate = 9L;
		} else if (currentSpeed >= 0.1) {
			modifTime = 1L;
			refreshRate = 10L;
		} else { // When numerator needs to be equal to 1
		    double tolerance = 1.0E-6;
		    double h1=1;
		    double h2=0;
		    double k1=0;
		    double k2=1;
		    double b = currentSpeed;
		    do {
		        double a = Math.floor(b);
		        double aux = h1;
		        h1 = a*h1+h2;
		        h2 = aux;
		        aux = k1;
				k1 = a*k1+k2;
				k2 = aux;
		        b = 1/(b-a);
		    } while (Math.abs(currentSpeed-h1/k1) > currentSpeed*tolerance);
		    modifTime = 1L;
		    refreshRate =  Math.round(k1/h1);
		}
		if (value.equalsIgnoreCase("modifTime")) return modifTime;
		else if (value.equalsIgnoreCase("refreshRate")) return refreshRate;
		else return null;
	}

	/**
	 * Converts a tick in its related part of the day (MC names)
	 * (returns a String)
	 */
	public static String getMCDayPart(long tick) {
		String wichPart;
		if (tick >= dawnStart || tick < dayStart) {
			wichPart = LG_DAWN;
		} else if (tick >= dayStart && tick < duskStart) {
			wichPart = LG_DAY;
		} else if (tick >= duskStart && tick < nightStart) {
			wichPart = LG_DUSK;
		} else {
			wichPart = LG_NIGHT;
		}
		return wichPart;
	}

	/**
	 * Converts a tick in its related part of the day (AM/PM)
	 * (returns a String)
	 */
	public static String getAmPm(long tick) {
		String wichPart;
		if (tick >= 6000 && tick < 18000) {
			wichPart = PH_PM;
		} else {
			wichPart = PH_AM;
		}
		return wichPart;
	}

	/**
	 * Converts a listed dayPart or any number to a tick value
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
			try { // If not a formatted hour neither a listed word, try to get a tick number
				dayPart = dayPart.replace("#", "");
				dayPart = dayPart.replace("UTC", "");
				dayPart = dayPart.replace("+", "");
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
	 * Gets and converts the current millisecond UTC+0 time to HH:mm:ss
	 * (returns a String)
	 */
	public static String getServerTime() {
		long seconds = System.currentTimeMillis() / 1000L; // >>> 1000 ms in 1 second
		long s = seconds % 60;
		long m = (seconds / 60) % 60;
		long H = (seconds / (60 * 60)) % 24;
		return String.format("%02d:%02d:%02d", H, m, s) + " UTC";
	}

	/**
	 * Converts a tick value and returns a correct UTC value
	 * (returns a long)
	 */
	public static long getUTCShiftFromTick(long tick) {
		if (tick >= 25 || tick <= -25) tick = (long)Math.floor(tick / 1000); // Use the 'start' value as an UTC modifier
		return ((tick % 24) + 24) % 24;
		
	}

	/**
	 * Converts HH:mm[:ss] to a real time tick (1/1728000)
	 * (returns a Long)
	 */
	public static Long tickFromServerTime(String time) {
		String[] splitedHms = time.split(":");
		try {
			long h = Long.parseLong(splitedHms[0]) % 24;
			long m = 0L;
			long s = 0L;
			if (splitedHms.length >= 2)
				m = Long.parseLong(splitedHms[1]) % 60;
			if (splitedHms.length >= 3)
				s = Long.parseLong(splitedHms[2]) % 60;
			Long calcTick = ((h * 72000) + (m * 1200) + (s * 20)) % 1728000;
			return calcTick;
		} catch (NumberFormatException nfe) {
			MsgHandler.errorMsg(hourFormatMsg); // Console error msg
			return 0L;
		}
	}

	/**
	 * Converts a real time tick (1/1728000) to HH:mm:ss
	 * (returns a String)
	 */
	public static String realFormattedTimeFromTick(long tick) {
		long newTick = tick / 20L; // >>> 20 ticks in 1 seconds
		long s = newTick % 60;
		long m = (newTick / 60) % 60;
		long H = (newTick / (60 * 60)) % 24;
		return String.format("%02d:%02d:%02d", H, m, s) + " UTC";
	}

	/**
	 * Converts a MC tick (1/24000) to HH:mm:ss
	 * (returns a String)
	 */
	public static String formattedTimeFromTick(long ticks, boolean msg) {
		return formattedTimeFromTick(ticks, PH_TIME24, msg);
	}
	public static String formattedTimeFromTick(long ticks, String format, boolean msg) {
		long newTicks = (ticks + 6000L) * 72L; // Adjust offset and go real time
		newTicks = correctInitTicks(newTicks);
		newTicks = newTicks / 20L; // >>> 20 ticks in 1 seconds
		int daylong;
		switch (format) {
		default :
		case PH_TIME24 :
		case PH_HOURS24 :
			daylong = 24;
			break;
		case PH_TIME12 :
		case PH_HOURS12 :
			daylong = 12;
			break;
		}
		long ss = newTicks % 60;
		long mm = (newTicks / 60) % 60;
		long HH = (newTicks / (60 * 60)) % daylong;
		String output;
		switch (format) {
		default :
		case PH_TIME12 :
			if (HH == 0) HH = 12;
		case PH_TIME24 :
			output = String.format("%d:%02d:%02d", HH, mm, ss);
			break;
		case PH_HOURS12 :
			if (HH == 0) HH = 12;
		case PH_HOURS24 :
			output = String.format("%d", HH);
			break;
		case PH_MINUTES :
			output = String.format("%02d", mm);
			break;
		case PH_SECONDS :
			output = String.format("%02d", ss);
			break;
		}
		if (debugMode && msg) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " Given tick \"" + ChatColor.YELLOW + ticks + ChatColor.AQUA + "\" was converted to \"" + ChatColor.YELLOW + output + ChatColor.AQUA + "\"."); // Console debug msg
		return output;
	}

	/**
	 * Gets and converts HH:mm:ss to a tick (1/24000)
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
	 * Gets and converts any [yy]yy-mm-dd date to a fulltime tick
	 * (returns a Long)
	 */
	public static Long tickFromFormattedDate(String date) {
		try {
			String[] splitedDate = date.split("-");
			Integer year = Integer.parseInt(splitedDate[0]);
			Integer month = Integer.parseInt(splitedDate[1]);
			Integer day = Integer.parseInt(splitedDate[2]);
			if (year < 1) year = 1;
			if (month < 1) month = 1;
			if (month > 12) month = 12;
			YearMonth ym = YearMonth.of(year, month);
			int maxLength = ym.lengthOfMonth();
			if (day < 1) day = 1;
			if (day > maxLength) day = maxLength;
			LocalDateTime currentLdt = LocalDateTime.of(year, month, day, 0, 0, 0);
			LocalDateTime zeroLdt = LocalDateTime.of(0001, 01, 01, 0, 0, 0);
			Duration d = Duration.between(zeroLdt, currentLdt);
			Long elapsedDays = TimeUnit.DAYS.convert(d);
			Long fulltime = elapsedDays * 24000; // (= 20m * 60s * 20t)
			return fulltime;
		} catch (NumberFormatException nfe) {
			MsgHandler.errorMsg(dateFormatMsg); // Console error msg
			return null;
		}
	}

	/**
	 * Gets and converts the current real date to a number of elapsed days
	 * (returns a Long)
	 */
	public static Long daysFromCurrentDate() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		int day = Calendar.getInstance().get(Calendar.DATE);
		LocalDate currentLdt = LocalDate.of(year, month, day);
		LocalDate zeroLdt = LocalDate.of(1, 1, 1);
		Duration d = Duration.between(zeroLdt, currentLdt);
		Long elapsedDays = TimeUnit.DAYS.convert(d);
		return elapsedDays;
	}
	
	/**
	 * Converts a fulltime tick to a date
	 * (returns a LocalDateTime)
	 */
	public static LocalDate dateFromTick(long fulltime) {
		Integer days = (int) (0 + daysFromTick(fulltime));
		LocalDate ldt = LocalDate.of(1, 1, 1);
		ldt = ldt.plusDays(days);
		return ldt;
	}

	/**
	 * Converts a number of days to a date part [dd] or [mm] or [yy] or [yyyy]
	 * (returns a String)
	 */
	public static String dateFromElapsedDays(long daysNb, String datePart) {
		LocalDateTime ldt = LocalDateTime.of(1, 1, 1, 0, 0, 0);
		ldt = ldt.plusDays(daysNb);	
		switch(datePart) {
		// #1. Years
		case PH_YY:
			return ldt.format(DateTimeFormatter.ofPattern("yy"));
		case PH_YYYY:	
			return ldt.format(DateTimeFormatter.ofPattern("yyyy"));
		// #2. Months
		case PH_MM:
			return ldt.format(DateTimeFormatter.ofPattern("LL"));
		// #3. Days
		case PH_DD:
			return ldt.format(DateTimeFormatter.ofPattern("dd"));
		}
		return null;
	}

	/**
	 * Uses a fulltime tick to find the total number of weeks
	 * (returns a Long)
	 */
	public static Long weeksFromTick(long fulltime) {
		long daysNb = daysFromTick(fulltime);
		return 1 + (daysNb / 7);
	}

	/**
	 * Uses a fulltime tick to find the total number of days
	 * (returns a Long)
	 */
	public static Long daysFromTick(long fulltime) {
		// A real day begin at 00:00
		if (MainTM.getInstance().getConfig().getString(CF_NEWDAYAT).equalsIgnoreCase(newDayStartsAt_0h00)) {
			MsgHandler.devMsg("New day begins at " + ChatColor.YELLOW + "00:00" + ChatColor.BLUE + ", so the calculation of elapsed days is : (" + ChatColor.YELLOW + fulltime
					+ ChatColor.BLUE + " + " + ChatColor.YELLOW + "6000" + ChatColor.BLUE + " = " + ChatColor.YELLOW + (fulltime + 6000) + ChatColor.BLUE + ") / " + ChatColor.YELLOW + "24000" + ChatColor.BLUE + " = " + ChatColor.YELLOW + (fulltime + 6000) / 24000 + ChatColor.BLUE + "."); // Console dev msg
			return (fulltime + 6000) / 24000;
		} else { // A MC day begin at 06:00
			MsgHandler.devMsg("New day begins at " + ChatColor.YELLOW + "06:00" + ChatColor.BLUE + ", so the calculation of elapsed days is : " + ChatColor.YELLOW + fulltime
					+ ChatColor.BLUE + " / " + ChatColor.YELLOW + "24000" + ChatColor.BLUE + " = " + ChatColor.YELLOW + (fulltime / 24000) + ChatColor.BLUE + "."); // Console dev msg
			return fulltime / 24000;
		}
	}

	/**
	 * Uses a fulltime tick to find the number of the day in the year
	 * (returns a Long)
	 */
	public static Long dayInYear(long fulltime) {
		LocalDate dt = dateFromTick(fulltime);
		Long dayNb = Long.parseLong(dt.format(DateTimeFormatter.ofPattern("D")));
		return dayNb;
	}
	
	/**
	 * Uses a fulltime tick to find the number of a day in the week
	 * (returns a Integer)
	 */
	public static Integer dayInWeek(long fulltime) {
		LocalDate ld = dateFromTick(fulltime);
		DayOfWeek dow = DayOfWeek.from(ld);
		Integer dayNb = dow.getValue() + 1;
		if (dayNb == 8) dayNb = 1;
		return dayNb;
	}
	
	/**
	 * Uses a fulltime tick to get the name of a day in the lang.yml file
	 * (returns a String)
	 */
	public static String dayName(long fulltime, String lang) {
		Integer dayNb = dayInWeek(fulltime);
		String dayName = MainTM.getInstance().langConf.getString(LG_LANGUAGES + "." + lang + "." + LG_DAYS + "." + "d0" + dayNb);
		return dayName;
	}

	/**
	 * Uses a fulltime tick to find the number of the week in the year
	 * (returns a Long)
	 */
	public static Long weekInYear(long fulltime) {
		LocalDate dt = dateFromTick(fulltime);
		Long weekNb = Long.parseLong(dt.format(DateTimeFormatter.ofPattern("w")));
		return weekNb;
	}
	
	// ========== Miscellaneous Methods ========== //

	/**
	 * Returns correct case of the locale (xx_XX)
	 * (returns a String)
	 */
	public static String getCorrectLocaleCase(String l) {
		String checkedLocale;
		if (l.contains("_")) {
			String[] splitLocale = l.split("_");
			String lg_LG_Locale = splitLocale[0] + "_" + splitLocale[1].toUpperCase();
			checkedLocale = lg_LG_Locale;
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
			String lg_Locale = splitLocale[0] + "_";
			List<String> existingLangList = LgFileHandler.setAnyListFromLang(LG_LANGUAGES);
			for (String lang : existingLangList) {
				if (lang.contains(lg_Locale)) {
					nearestLocale = lang;
				}
			}
		}
		return nearestLocale;
	}

	/**
	 * Gets the correct speed value's name (daySpeed or nightSpeed) for a given tick
	 * (returns a String)
	 */
	public static String wichSpeedParam(long tick) {
		String speedParam;
		if (getMCDayPart(tick).equalsIgnoreCase(LG_NIGHT)) {	    
			speedParam = CF_N_SPEED;
		} else {
			speedParam = CF_D_SPEED;
		}
		return speedParam;
	}
	
	/**
	 * Gets the current speed of a world 
	 * (returns a double)
	 */
	public static double getCurrentSpeed(String world) {
		long t = Bukkit.getWorld(world).getTime();
		double currentSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + wichSpeedParam(t));
		return currentSpeed;
	}

	/**
	 * Formats a positive/negative number and return a formatted UTC+/-n value
	 * (returns a String)
	 */
	public static String formattedUTCShiftfromTick(long tick) {
		tick = getUTCShiftFromTick(tick);
		String formattedUTC;
		if (tick < 0) {
			formattedUTC = "UTC" + tick;
		} else {
			formattedUTC = "UTC+" + tick;
		}
		return formattedUTC;
	}

	/**
	 * Formats a positive/negative number and return a tick +/-n value
	 * (returns a String)
	 */
	public static String tickUTCShiftfromTick(Long tick) {
		String shiftUTC;
		if (tick < 0) {
			shiftUTC = tick.toString();
		} else {
			shiftUTC = "+" + tick;
		}
		return shiftUTC;
	}

	/**
	 * Compares two versions of TimeManager, returns "true" if the requested version is newer than the current one
	 * (returns a boolean)
	 */
	public static boolean requestedPluginVersionIsNewerThanCurrent(String srcFile, int requestedMajor, int requestedMinor, int requestedPatch, int requestedRelease, int requestedDev) {
		String currentVersion;
		int currentMajor = 0;
		int currentMinor = 0;
		int currentPatch = 0;
		int currentRelease = 4;
		int currentDev = 0;
		// Check current version
		if (srcFile.equalsIgnoreCase("lg")) {
			currentVersion = MainTM.getInstance().langConf.getString(CF_VERSION);
		} else {
			currentVersion = versionTM();
		}
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
		if ((requestedMajor > currentMajor) 
				|| (requestedMajor == currentMajor && requestedMinor > currentMinor)
				|| (requestedMajor == currentMajor && requestedMinor == currentMinor && requestedPatch > currentPatch)
				|| (requestedMajor == currentMajor && requestedMinor == currentMinor && requestedPatch == currentPatch && requestedRelease > currentRelease)
				|| (requestedMajor == currentMajor && requestedMinor == currentMinor && requestedPatch == currentPatch && requestedRelease == currentRelease && requestedDev > currentDev)) {
			return true;
		}
		return false;
	}

	/**
	 * Replaces characters before splitting version String into integers
	 * (returns a String)
	 */
	public static String replaceChars(String version) {
		MsgHandler.devMsg("Plugin version to convert : " + version);
		version = version.replace("dev", "d")
				.replace("alpha", "a")
				.replace("beta", "b")
				.replace("d", ".0.")
				.replace("a", ".1.")
				.replace("b", ".2.")
				.replace("rc", ".3.")
				.replace("--", ".")
				.replace("-", ".")
				.replace("...", ".")
				.replace("..", ".");
		MsgHandler.devMsg("Plugin version converted : " + version);
		try {
			String versionIntTest = version.replace(".", "");
			Integer.parseInt(versionIntTest); // Prevent all other parse errors
		} catch (NumberFormatException nfe) {
			MsgHandler.errorMsg(versionTMFormatMsg); // Console error msg
			return null;
		}
		return version;
	}
	
	/**
	 * Concatenate world name in several parts
	 * (returns a String)
	 */
	public static String concatenateNameWithSpaces(CommandSender sender, String[] args, int firstPartArg) {
		List<String> worlds = CfgFileHandler.setAnyListFromConfig(MainTM.CF_WORLDSLIST);
		String concatWorldName = args[firstPartArg];
		int nbArgs = args.length;
		int currentArg = firstPartArg + 1;
		while (currentArg < nbArgs) {
			concatWorldName = (concatWorldName + " " + args[currentArg++]);
			if (worlds.contains(concatWorldName)) {
				MsgHandler.devMsg("Concatenate world name : " + concatWorldName);
				return concatWorldName;
			}
		}
		MsgHandler.devMsg("Concatenate world name : " + concatWorldName);
		return concatWorldName;
	}
	
	/**
	 * Replace any hexadecimal color by corresponding ChatColor
	 * (returns a String)
	 */	
	// Define hexadecimal colors pattern
	public static String replaceAllHexColors(String txt) {
		if (serverMcVersion >= reqMcVForHexColors) { // Check if MC version is at least 1.16.0
			Pattern hexpattern = Pattern.compile("#[a-fA-F0-9]{6}");
			Matcher match = hexpattern.matcher(txt);
			while (match.find()) {
				MsgHandler.devMsg("The matcher found an hexadecimal color in the §e/now §9message");	
				String color = match.group(); // Get the first hexadecimal color found
				ChatColor cColor = ChatColor.of(color);
				MsgHandler.devMsg("This hexadecimal color number is " + ChatColor.YELLOW + color);
				MsgHandler.devMsg("The corresponding color is " + ChatColor.translateAlternateColorCodes('&', cColor.toString()) + cColor.getColor());
				txt = txt.replace(color, cColor.toString()); // Convert it to the corresponding ChatColor
			}		
		}
		return txt;
	}
	
	// ========== Value correcting Methods ========== //
	
	/**
	 * Checks and corrects any 'duration' value
	 * (returns a String)
	 */
	public static String correctDuration(String duration) {
		if (duration.contains("d") && !duration.contains("d-")) { // If days are mentioned without the separator "-"
			String[] splitedDuration = duration.split("d");
			if (splitedDuration.length > 0) {
				duration = splitedDuration[0] + "d-";
				if (splitedDuration.length > 1) {
					duration = duration + splitedDuration[1];
				}
			}
		} else if (!duration.contains("d-")) { // If days are not mentioned at all
			duration = "00d-" + duration;
		}
		if (duration.contains("h") && !duration.contains("h-")) { // If hours are mentioned without the separator "-"
			String[] splitedDuration = duration.split("h");
			if (splitedDuration.length > 0) {
				duration = splitedDuration[0] + "h-";
				if (splitedDuration.length > 1) {
					duration = duration + splitedDuration[1];
				}
			}
		} else if (!duration.contains("h-")) { // If hours are not mentioned at all
			String[] splitedDuration = duration.split("d-");
			duration = splitedDuration[0] + "d-" + "00h-";
			if (splitedDuration.length > 1) {
				duration = duration + splitedDuration[1];
			}
		}
		if (duration.contains("m") && !duration.contains("m-")) { // If minutes are mentioned without the separator "-"
			String[] splitedDuration = duration.split("m");
			if (splitedDuration.length > 0) {
				duration = splitedDuration[0] + "m-";
				if (splitedDuration.length > 1) {
					duration = duration + splitedDuration[1];
				}
			}
		} else if (!duration.contains("m-")) { // If minutes are not mentioned at all
			String[] splitedDuration = duration.split("h-");
			duration = splitedDuration[0] + "h-" + "00m-";
			if (splitedDuration.length > 1) {
				duration = duration + splitedDuration[1];
			}
		}
		if (!duration.contains("s")) { // If seconds are not mentioned
			duration = duration + "00s";
		}
		return duration;
	}

	/**
	 * Checks and corrects the 'initialTickNb' tick value
	 * (returns a long)
	 */
	public static long correctInitTicks(long time) {
		time = ((time % 1728000) + 1728000) % 1728000; // Forbid numbers lower than 0 and higher than 727999 (= end of the real day)
		return time;
	}

	/**
	 * Checks and corrects the 'refreshRate' value
	 * (returns an Integer)
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
	 * Checks and corrects any 'start' or 'time' tick value
	 * (returns a long)
	 */
	public static long correctDailyTicks(long time) {
		time = ((time % 24000) + 24000) % 24000; // Forbid numbers lower than 0 and higher than 23999 (= end of the MC day)
		return time;
	}

	/**
	 * Checks and corrects the 'wakeUpTick' tick value
	 * (returns a long)
	 */
	public static long correctwakeUpTick(long time) {
		if (time > 6000) time = 6000L; // Forbid numbers higher than 6000
		if (time < 0) time = 0L; // Forbid numbers smaller than 0
		return time;
	}
	
	// ========== YAML nodes restraining Methods ==========

	/**
	 * Restrains the value of the 'initialTick' key
	 * (modifies the configuration without saving the file)
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
	 * Restrains the value of the 'refreshRate' key
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
	 * Restrains the value of the 'wakeUpTick' key
	 * (modifies the configuration without saving the file)
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
	 * Restrains the value of the 'world.start' key
	 * (modifies the configuration without saving the file)
	 */
	public static void restrainStart(String world) {
		long t = Bukkit.getWorld(world).getTime();
		String time = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_START);
		if (time.contains("UTC")) time = time.replace("UTC", "");
		if (time.contains("+")) time = time.replace("+", "");
		time = tickFromString(time).toString(); // Check if value is a part of the day
		String currentSpeed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + wichSpeedParam(t));
		long tick;
		try { // Check if value is a long
			tick = Long.parseLong(time);
			if (currentSpeed.contains(realtimeSpeed.toString()) || currentSpeed.equalsIgnoreCase("realtime")) { // First if speed is 'realtime', use UTC
				tick = getUTCShiftFromTick(tick) * 1000;
			} else {
				tick = correctDailyTicks(tick); // else, use ticks
			}
		} catch (NumberFormatException nfe) { // If not a long, use the default start value
			MsgHandler.errorMsg(startTickFormatMsg); // Console error msg
			tick = defStart;
		}
		MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_START, tick);
		MsgHandler.debugMsg(startAdjustDebugMsg + " " + ChatColor.YELLOW + time + ChatColor.AQUA + " to " + ChatColor.YELLOW + tick + ChatColor.AQUA + " for the world §e" + world + ChatColor.AQUA + "."); // Console debug msg
	}

	/**
	 * Restrains the value of 'daySpeed' and 'nightSpeed' keys
	 * (modifies the configuration without saving the file)
	 */
	public static void restrainSpeed(String world) {
		String daySpeed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED);
		String nightSpeed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED);
		double daySpeedNb;
		double nightSpeedNb;
		if (daySpeed.equals("24") || daySpeed.equalsIgnoreCase("realtime") || nightSpeed.equals("24") || nightSpeed.equalsIgnoreCase("realtime")) {
			daySpeedNb = realtimeSpeed;
			nightSpeedNb = realtimeSpeed;
		} else {
			if (!daySpeed.contains("d") && !daySpeed.contains("h") && !daySpeed.contains("m") && !daySpeed.contains("s")) {
				try { // Check if day value is a double
					daySpeedNb = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED);
					daySpeedNb = correctSpeed(daySpeedNb);
				} catch (NumberFormatException nfe) { // If not a double, use the default refresh value
					MsgHandler.errorMsg(speedFormatMsg); // Console error msg
					daySpeedNb = defSpeed;
				}
			} else { // Check if daySpeed is in 00d-00h-00m-00s format
				daySpeed = correctDuration(daySpeed);
				daySpeedNb = doubleFromFormatedTime(daySpeed, CMD_SET_D_DURATION);
			}
			if (!nightSpeed.contains("d") && !nightSpeed.contains("h") && !nightSpeed.contains("m") && !nightSpeed.contains("s")) {
				try { // Check if night value is a double
					nightSpeedNb = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED);
					nightSpeedNb = correctSpeed(nightSpeedNb);
				} catch (NumberFormatException nfe) { // If not a double, use the default refresh value
					MsgHandler.errorMsg(speedFormatMsg); // Console error msg
					nightSpeedNb = defSpeed;
				}
			} else { // Check if nightSpeed is in 00d-00h-00m-00s format
				nightSpeed = correctDuration(nightSpeed);
				nightSpeedNb = doubleFromFormatedTime(nightSpeed, CMD_SET_N_DURATION);
			}
		}
		MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_D_SPEED, daySpeedNb);
		MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_N_SPEED, nightSpeedNb);
		MsgHandler.debugMsg(daySpeedAdjustDebugMsg + " " + ChatColor.YELLOW + daySpeed + ChatColor.AQUA + " to " + ChatColor.YELLOW + daySpeedNb + ChatColor.AQUA + " for the world " + ChatColor.YELLOW + world + ChatColor.AQUA + "."); // Console debug msg
		MsgHandler.debugMsg(nightSpeedAdjustDebugMsg + " " + ChatColor.YELLOW + nightSpeed + ChatColor.AQUA + " to " + ChatColor.YELLOW + nightSpeedNb + ChatColor.AQUA + " for the world " + ChatColor.YELLOW + world + ChatColor.AQUA + "."); // Console debug msg
	}

	/**
	 * Restrains the value of the 'world.sleep' key
	 * Forces 'sleep' to 'false' if any expected string is not recognized
	 * Forces 'sleep' to 'false' for the synchronized and frozen worlds
	 * Forbids 'sleep' to be 'true' in Nether and the End worlds
	 * Forces 'sync' to false if 'sleep' is 'true' or 'linked'
	 * (modifies the configuration without saving the file)
	 */
	public static void restrainSleep(String world) {
		String sleep = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SLEEP);
		double currentSpeed = getCurrentSpeed(world);
		if ((!sleep.equalsIgnoreCase(ARG_TRUE) && !sleep.equalsIgnoreCase(ARG_LINKED))
				|| currentSpeed == 24.0
				|| currentSpeed == 0.0
				|| (world.contains(ARG_NETHER) && sleep.equalsIgnoreCase(ARG_TRUE))
				|| (world.contains(ARG_THEEND) && sleep.equalsIgnoreCase(ARG_TRUE))) {
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SLEEP, ARG_FALSE);
			MsgHandler.debugMsg(sleepAdjustFalseDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + "."); // Console debug msg
		} else if (sleep.equalsIgnoreCase(ARG_TRUE) || sleep.equalsIgnoreCase(ARG_LINKED)) {
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, ARG_FALSE);
			MsgHandler.debugMsg(syncAdjustFalseDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + "."); // Console debug msg
		}
	}

	/**
	 * Restrains the value of the 'world.sync' key
	 * Forces 'sync' to 'true' for the 24.0 speed, then 'false' when change to another speed ratio
	 * Forces 'sync' to 'false' for the 0.0 speed
	 * (modifies the configuration without saving the file)
	 */
	public static void restrainSync(String world, double oldSpeed) {
		double currentSpeed = getCurrentSpeed(world);
		if (currentSpeed == 24.0) { // new speed is 24
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, ARG_TRUE);
			MsgHandler.debugMsg(syncAdjustTrueDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + "."); // Console debug msg
		} else if (currentSpeed == 0.0) { // new speed is 0
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, ARG_FALSE);
			MsgHandler.debugMsg(syncAdjustFalseDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + "."); // Console debug msg
		} else if (oldSpeed == 24.0) { // new speed is anything else with previous value 24
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, ARG_FALSE);
			MsgHandler.debugMsg(syncAdjustFalseDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + "."); // Console debug msg
		}
	}

	/**
	 * Restrains the value of the 'world.firstStartTime' key
	 * Forces 'firstStartTime' to 'default' if any expected string is not recognized
	 * Forces 'firstStartTime' to 'default' for the synchronized worlds
	 * (modifies the configuration without saving the file)
	 */
	public static void restrainFirstStartTime(String world) {
		String firstStartTime = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_FIRSTSTARTTIME);
		String sync = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC);
		double currentSpeed = getCurrentSpeed(world);
		if (!firstStartTime.equalsIgnoreCase(ARG_PREVIOUS) && !firstStartTime.equalsIgnoreCase(ARG_START)) {
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_FIRSTSTARTTIME, ARG_DEFAULT);
			MsgHandler.debugMsg(firstStartTimeAdjustDefaultDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + "."); // Console debug msg
		}
		if (sync.equalsIgnoreCase(ARG_TRUE) || currentSpeed == 0.0) {
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_FIRSTSTARTTIME, ARG_DEFAULT);
			MsgHandler.debugMsg(firstStartTimeAdjustDefaultDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + "."); // Console debug msg
		}
	}

	/**
	 * Restrains the value of the 'nightSkipMode' key
	 * Forces 'nightSkipSpeed' to 'default' if any expected string is not recognized
	 * Forces 'nightSkipSpeed' to 'default' for the synchronized and frozen worlds
	 * Forces 'nightSkipSpeed' to 'default' for Nether and the End worlds
	 * (modifies the configuration without saving the file)
	 */
	public static void restrainNightSkipMode(String world) {
		String nightSkipSpeed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_NIGHTSKIP_MODE);
		String sync = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC);
		double currentSpeed = getCurrentSpeed(world);
		if ((!nightSkipSpeed.equalsIgnoreCase(ARG_INSTANT) && !nightSkipSpeed.equalsIgnoreCase(ARG_ANIMATION))
				|| sync.equalsIgnoreCase(ARG_TRUE)
				|| currentSpeed == 0.0
				|| world.contains(ARG_NETHER)
				|| world.contains(ARG_THEEND)) {
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_NIGHTSKIP_MODE, ARG_DEFAULT);
			MsgHandler.debugMsg(nightSkipNbPlayersAdjustDefaultDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + "."); // Console debug msg
		}
	}

	/**
	 * Restrains the value of the 'nightSkipRequiredPlayers' key
	 * Forces 'nightSkipRequiredPlayers' to '100%' if no integer can be retrieved
	 * (modifies the configuration without saving the file)
	 */
	public static void restrainNightSkipRequiredPlayers(String world) {
		String nightSkipNbPlayers = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_NIGHTSKIP_REQUIREDPLAYERS);
		try { // Check if day value is a double
			if (nightSkipNbPlayers.contains("%")) {
				nightSkipNbPlayers = nightSkipNbPlayers.replace("%", "");
				@SuppressWarnings("unused")
				double requiredPlayers = Double.parseDouble(nightSkipNbPlayers);
			} else {
				double requiredPlayers = Double.parseDouble(nightSkipNbPlayers);
				int rp = (int) Math.round(requiredPlayers);
				MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_NIGHTSKIP_REQUIREDPLAYERS, rp);
			}
		} catch (NumberFormatException nfe) { // If not a double, use the default 100% value
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_NIGHTSKIP_REQUIREDPLAYERS, ARG_100PERCENT);
			MsgHandler.errorMsg(nbPlayersMustBeIntMsg); // Console error msg
			MsgHandler.debugMsg(nightSkipNbPlayersAdjustDefaultDebugMsg + " " + ChatColor.YELLOW + world + ChatColor.AQUA + "."); // Console debug msg			
		}
	}
};