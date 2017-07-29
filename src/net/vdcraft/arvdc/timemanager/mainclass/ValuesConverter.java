package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.List;

import org.bukkit.Bukkit;

import net.vdcraft.arvdc.timemanager.MainTM;

public class ValuesConverter extends MainTM {
	
	/** 
	 * Check and correct any 'speed' value
	 */
    public static double returnCorrectSpeed(Double newSpeed) {
    	if(!newSpeed.equals(realtimeSpeed)) { // Don't modify the real time value
	    	if(newSpeed > speedMax) { // Forbid too big numbers
				newSpeed = speedMax;		
			} else if(newSpeed < 0) { // Forbid too small numbers
				newSpeed = 0.0;
	    	}
    	}
		return newSpeed;
	};

	/** 
	 * Check and correct the 'refreshrate' value
	 */
    public static Integer returnCorrectRate(Integer newRefreshRate) {
		if(newRefreshRate > refreshMax) { // Forbid too big numbers    	
			newRefreshRate = refreshMax;
		} else if(newRefreshRate < refreshMin) {
			newRefreshRate = refreshMin; // Forbid too small numbers
		}
		return newRefreshRate;
    };
	
	/** 
	 * Check and correct any 'start' or 'time' tick value
	 */
	public static long returnCorrectTicks(Long newTime) {
		while(newTime >= 24000) { // Forbid numbers higher than 23999 (= end of the day)
			newTime -= 24000;
		}
		while(newTime < 0) { // Forbid numbers smaller than 0 (= start of the day)
			newTime += 24000;
		}
		return newTime;
	};
	
	/** 
	 * Check and correct the 'initialTIckNb' tick value
	 */
	public static long returnCorrectInitTicks(Long newTime) {
		while(newTime >= 1728000) { // Forbid numbers higher than 727999 (= end of the day)
			newTime -= 1728000;
		}	
		while(newTime < 0) { // Forbid numbers smaller than 0 (= start of the day)
			newTime += 1728000;
		}
		return newTime;
	};
	
	/** 
	 * Convert a listed string value to a 'start' or 'time' tick value
	 */
	public static String returnTimeFromString(String newTime) {
		if(newTime.equalsIgnoreCase("day")) {
			newTime = "0";
		}
		else if(newTime.equalsIgnoreCase("midday") || newTime.equalsIgnoreCase("noon")) {
			newTime = "6000";
		}
		else if(newTime.equalsIgnoreCase("dusk") || newTime.equalsIgnoreCase("sunset") || newTime.equalsIgnoreCase("evening")) {
			newTime = "11500";
		}
		else if(newTime.equalsIgnoreCase("night")) {
			newTime = "13000";
		}
		else if(newTime.equalsIgnoreCase("midnight")) {
			newTime = "18000";
		}
		else if(newTime.equalsIgnoreCase("dawn") || newTime.equalsIgnoreCase("sunrise") || newTime.equalsIgnoreCase("morning")) {
			newTime = "22500";
		}
		return newTime;
	};
    
	/** 
	 * Get and convert current milliseconds UTC+0 time to a 1/1728000 tick value
	 */
    public static Long returnServerTick() {
		long ticksSinceEpoch = (long) (System.currentTimeMillis() / 50L); // Get the server actual time in milliseconds and convert it into ticks
		long daillyServerTick = ticksSinceEpoch % 1728000L; // Display a 24h day loop (1728000 ticks = 1 real day)
		return daillyServerTick;
	};

	/** 
	 * Convert a tick value and return a correct UTC value
	 */
    public static long returnCorrectUTC(Long tickValue) {
    	tickValue = (long) Math.floor(tickValue / 1000); // Use the 'start' value as an UTC modifier
    	if(tickValue > 12) { // Forbid too big numbers
    		tickValue = 12 - tickValue;		
		} else if(tickValue < -12) { // Forbid too small numbers
			tickValue = 12 + tickValue;
    	}
		return tickValue;
	};

	/** 
	 * Format a positive/negative number and return a formatted UTC+/-n value
	 */
    public static String formatAsUTC(Long tickValue) {
    	tickValue = returnCorrectUTC(tickValue);
    	String formattedUTC;
    	if(tickValue < 0) {
    		formattedUTC = "UTC" + tickValue + "h";
    	} else {
    		formattedUTC = "UTC+" + tickValue + "h";
    	}
		return formattedUTC;
	};
	
	/** 
	 * Get and convert the current msec UTC+0 time to HH:mm:ss
	 */
    public static String returnServerTime() {
    	long seconds = System.currentTimeMillis() / 1000L; // x ms in 1 second
		long s = seconds % 60;
	    long m = (seconds / 60) % 60;
	    long H = (seconds / (60 * 60)) % 24;
	    return String.format("%02d:%02d:%02d", H,m,s) + " UTC";
    };
    
	/** 
	 * Get and convert a real time tick (1/1728000) to HH:mm:ss
	 */
	public static String returnTickAsHHmmss(Long ticks) {
		Long newTicks = ticks / 20L; // x tick in 1 seconds
		long s = newTicks % 60;
		long m = (newTicks / 60) % 60;		
		long H = (newTicks / (60 * 60)) % 24;
		return String.format("%02d:%02d:%02d", H,m,s) + " UTC";
	};
	
	/**
	 *Get and convert a MC tick (1/2400) to HH:mm:ss
	 */
	public static String returnTicksAsTime(Long ticks) { // add (Long ticks, CommandSender sender) for debug msg		

		Long newTicks = (ticks + 6000L) * 72L; // Adjust offset and go real time
		newTicks = returnCorrectInitTicks(newTicks);
		newTicks = newTicks / 20L; // x tick in 1 seconds
		Long s = newTicks % 60;
		Long m = (newTicks / 60) % 60;		
		Long H = (newTicks / (60 * 60)) % 24;
		return String.format("%02d:%02d:%02d", H,m,s);
	};
		
	/** 
	 * Restrain speed modifiers (modify it in config.yml)
	 */
    public static void restrainSpeed() {
		for(String w : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
			double speedModifier;
			String isSpeedRealtime = MainTM.getInstance().getConfig().getString("worldsList."+w+".speed");
			if(isSpeedRealtime.equalsIgnoreCase("realtime")) {
				speedModifier = MainTM.realtimeSpeed;	
			} else {
		    	try { // Check if value is a double	
					speedModifier = (double) MainTM.getInstance().getConfig().getDouble("worldsList."+w+".speed");
					speedModifier = returnCorrectSpeed(speedModifier);
				} catch (NumberFormatException nfe) { // If not a double, use the default refresh value
					speedModifier = defSpeed;
				}
			}
			MainTM.getInstance().getConfig().set("worldsList."+w+".speed", speedModifier);
		}
    };

	/** 
	 * Restrain refresh rate (modify it in config.yml)
	 */
    public static void restrainRate() {    	
    	try { // Check if value is an integer
    		refreshRateInt = MainTM.getInstance().getConfig().getInt("refreshRate");
    		refreshRateInt = returnCorrectRate(refreshRateInt);
		} catch (NumberFormatException nfe) { // If not an integer, use the default refresh value
			refreshRateInt = defRefresh;
		}
		MainTM.getInstance().getConfig().set("refreshRate", refreshRateInt);
    };

	/** 
	 * Restrain initial tick (modify it in config.yml)
	 */
    public static void restrainInitTick() {
		long newInitialTick; 	
    	try { // Check if value is an integer
    		initialTick = MainTM.getInstance().getConfig().getLong("initialTick.initialTickNb");
    		newInitialTick = returnCorrectInitTicks(initialTick);
		} catch (NumberFormatException nfe) { // If not a long, use the current time value
			newInitialTick = returnServerTick(); // Create the initial tick
		}
		initialTick = newInitialTick;
		MainTM.getInstance().getConfig().set("initialTick.initialTickNb", newInitialTick);
    };
	
	/** 
	 * Restrain start timers (modify it in config.yml)
	 */
    public static void restrainStart() {
		for(String w : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {			
			String timeToSet = MainTM.getInstance().getConfig().getString("worldsList."+w+".start");
			String currentSpeed = MainTM.getInstance().getConfig().getString("worldsList."+w+".speed");
	    	timeToSet = ValuesConverter.returnTimeFromString(timeToSet); // Check if value is a part of the day    	
			long tickToSet;
	    	try { // Check if value is a long
	    		tickToSet = Long.parseLong(timeToSet);
	    		if(currentSpeed.equals("24")) { // First if speed is 'realtime', use UTC
	    			tickToSet = returnCorrectUTC(tickToSet) * 1000;
	    		} else {
	    			tickToSet = returnCorrectTicks(tickToSet); // else, use ticks
	    		}
	    	} catch (NumberFormatException nfe) { // If not a long, use the default start value
	    		tickToSet = defStart;
	    	}
			MainTM.getInstance().getConfig().set("worldsList."+w+".start", tickToSet);
		}
    };
	
	/** 
	 *  If a world's speed is frozen (=0) or realtime (=24), force 'sleepUntilDawn' to 'false'
	 */
	public static void restrainSleep() {
		for(String w : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {	
			String currentSpeed = MainTM.getInstance().getConfig().getString("worldsList."+w+".speed");
			if(currentSpeed.equals("0.0") || currentSpeed.equals("24.0")) {
				MainTM.getInstance().getConfig().set("worldsList."+w+".sleepUntilDawn", "false");
			}
		}
	};
	
	/** 
	 *  Restore correct case of the locales (xx_XX)
	 */
	public static String returnCorrectLocaleCase(String l) {
		String checkedLocale;
		if(l.contains("_")) {
			String[] splitLocale = l.split("_");
			String xx_XXLocale = splitLocale[0] + "_" + splitLocale[1].toUpperCase();
			checkedLocale = xx_XXLocale;
		} else {
			checkedLocale = l;
		}
		return checkedLocale;
	};
	
	/** 
	 *  Use the first part to reach the nearest lang (en_GB >>> en_ >>> en_US)
	 */
	public static String returnNearestLang(String l) {
		String nearestLocale = serverLang; // If not existing, use the default language value
		if(l.contains("_")) {
			String[] splitLocale = l.split("_");
			String xx_Locale = splitLocale[0] + "_";
			List<String> existingLangList = LgFileHandler.setAnyListFromLang("languages");
			for(String lang : existingLangList) {
				if(lang.contains(xx_Locale)) {
					nearestLocale = lang;
				}
			}
		}
		return nearestLocale;
	};
		
	/** 
	 *  Get the version of the server and return only the type (Bukkit/Spigot)
	 */
	public static String KeepTypeOfServer() {
		String serverType;
		String completeServerVersion = Bukkit.getVersion();
		if(completeServerVersion.contains("ukkit") || completeServerVersion.contains("pigot")) {
			String[] SplitOfCompleteServerVersion = completeServerVersion.split("-");
			serverType = SplitOfCompleteServerVersion[1];
		} else { // For others type of servers (less specific format, so it could crash sometimes)
			serverType = "other";
		}
		return serverType;
	};
	
	/** 
	 *  Get the version of the server and return only the MC decimal part
	 */
	public static Double KeepDecimalOfMcVersion() {
		Double mcVersion;
		String completeServerVersion = Bukkit.getVersion();
		if(completeServerVersion.contains("ukkit") || completeServerVersion.contains("pigot")) {
			String[] split1 = completeServerVersion.split("MC: 1.");
			String split2 = split1[1];
			String mcVersionString = split2.substring(0,split2.length()-1);
			mcVersion = Double.parseDouble(mcVersionString);
		} else { // For others type of servers (less specific format, so it could crash sometimes)
			String[] split1 = completeServerVersion.split("1.");
			String split2 = split1[2];
			String mcVersionString = split2.substring(0,split2.length()-1);
			mcVersion = Double.parseDouble(mcVersionString);
		}
		return mcVersion;
	};

};