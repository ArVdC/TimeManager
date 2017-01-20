package net.vdcraft.arvdc.timemanager.cmdplayer;

import net.vdcraft.arvdc.timemanager.MainTM;

public class NowFormatTime extends MainTM {

	/**
	 * Convert actual tick to MC hours and minutes
	 */
	public static String ticksAsTime(Long ticks) { // add (Long ticks, CommandSender sender) for debug msg
		// #1. Hours
		double decimalTimeH = (double) ticks;
		double dividedTimeH = (double) (decimalTimeH / 1000);
		int roundedTimeH = (int) Math.floor(dividedTimeH);
		int supTimeH = (int) 6;
		int adjustedTimeH = (int) roundedTimeH;
		while(supTimeH-- > 0) {
			if(adjustedTimeH == 24) {
				adjustedTimeH = 0;
			}
			++adjustedTimeH;
		}
		int finalTimeH = adjustedTimeH;
		// #2. Minutes
		double decimalTimeM = (double) dividedTimeH - roundedTimeH;
		double adjustedTimeM = decimalTimeM * 60;
		int roundedTimeM = (int) Math.floor(adjustedTimeM);
		int finalTimeM = (int) roundedTimeM;
		/**
		 * ==Debug==
		 * sender.sendMessage("les heures : " + decimalTimeH + " ticks qui deviennent " + dividedTimeH + "h, qui arrondies deviennent " + roundedTimeH + "h, qui ajustées deviennent " + adjustedTimeH + "h."); // debug msg
		 * sender.sendMessage("les minutes : " + dividedTimeH + "h - " + roundedTimeH + "h = " + decimalTimeM +  "h restantes, qui multipliées par 60 font " + adjustedTimeM + "min, qui arrondies font " + roundedTimeM + "min."); // debug msg
		 * sender.sendMessage(ticks + " ticks deviennent donc " + pad(finalTimeH, 2) + ":" + pad(finalTimeM, 2)); // debug msg
		 */
		// #3. Output
		return pad(finalTimeH, 2) + ":" + pad(finalTimeM, 2);
	};
	
	/**
	 * Format time by adding a '0' before single numbers
	 */
	static private String pad(int number, int padding) {
		String padded = String.valueOf(number);
		while (padded.length() < padding) {
			padded = "0" + padded;
		}
		return padded;
	};
}
