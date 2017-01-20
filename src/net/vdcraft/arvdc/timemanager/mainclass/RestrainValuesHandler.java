package net.vdcraft.arvdc.timemanager.mainclass;

import net.vdcraft.arvdc.timemanager.MainTM;

public class RestrainValuesHandler extends MainTM {

	/** 
	 * Restrain speed args by returning a correct value (double)
	 */
    public static double returnCorrectSpeed(Double newSpeed) {
		if(newSpeed > speedMax) { // Forbid too big numbers
			newSpeed = speedMax;		
		} else if(newSpeed < 0) { // Forbid too small numbers
			newSpeed = 0.0;
		}
		return newSpeed;
	};
		
	/** 
	 * Restrain speed modifiers (modify it in config.yml)
	 */
    public static void restrainSpeed() {
		for(String w : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
			double speedModifier;
	    	try { // Check if value is a double			
				speedModifier = (double) MainTM.getInstance().getConfig().getDouble("worldsList."+w+".speed");
				speedModifier = returnCorrectSpeed(speedModifier);
			} catch (NumberFormatException nfe) { // If not a double, use the default refresh value
				speedModifier = defSpeed;
			}
			MainTM.getInstance().getConfig().set("worldsList."+w+".speed", speedModifier);
		}
    };

	/** 
	 * Restrain refreshrate args by returning a correct value (integer)
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
	 * Convert listed strings into time args and returning a correct value (long)
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
	 * Restrain time args by returning a correct value (long)
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
	 * Restrain start timers (modify it in config.yml)
	 */
    public static void restrainStart() {
		for(String w : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {			
			String timeToSet = MainTM.getInstance().getConfig().getString("worldsList."+w+".start");
	    	timeToSet = RestrainValuesHandler.returnTimeFromString(timeToSet); // Check if value is a part of the day    	
			long tickToSet;
	    	try { // Check if value is a long
	    		tickToSet = Long.parseLong(timeToSet);
				tickToSet = returnCorrectTicks(tickToSet);			
	    	} catch (NumberFormatException nfe) { // If not a long, use the default start value
	    		tickToSet = defStart;
	    	}
			MainTM.getInstance().getConfig().set("worldsList."+w+".start", tickToSet);
		}
    };

}
