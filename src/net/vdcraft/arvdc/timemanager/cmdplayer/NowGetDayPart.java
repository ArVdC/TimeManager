package net.vdcraft.arvdc.timemanager.cmdplayer;

import net.vdcraft.arvdc.timemanager.MainTM;

public class NowGetDayPart extends MainTM {

    /**
     * Define the part of the day
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
};