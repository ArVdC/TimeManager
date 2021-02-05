package net.vdcraft.arvdc.timemanager.placeholders;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;

import org.bukkit.event.EventHandler;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.UserMsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class MVdWPAPIHandler extends MainTM {

	/**
	 * MVdWPlaceholderAPI (See www.spigotmc.org/resources/mvdwplaceholderapi.11182 for credits)
	 */

	@EventHandler
	public static void loadMVdWPlaceholderAPI() {

		// Returns the current tick for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, "tm_tick", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Long t = e.getPlayer().getWorld().getTime();
				String tick = t.toString();
				return tick;
			}
		});

		// Returns the current time (in HH:mm:ss) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, "tm_time", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				long t = e.getPlayer().getWorld().getTime();
				String time = ValuesConverter.formattedTimeFromTick(t);
				return time;
			}
		});

		// Returns the current day part for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, "tm_daypart", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				long t = e.getPlayer().getWorld().getTime();
				String lg = UserMsgHandler.setLangToUse(e.getPlayer());
				String dayPart = ValuesConverter.getDayPart(t);
				return MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lg + "." + CF_DAYPARTS + "." + dayPart);
			}
		});

		// Returns the total # of days elapsed for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, "tm_elapseddays", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				return ValuesConverter.elapsedDaysFromTick(e.getPlayer().getWorld().getFullTime()).toString();
			}
		});

		// Returns the current day # for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, "tm_currentday", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Long currentDay = ValuesConverter.elapsedDaysFromTick(e.getPlayer().getWorld().getFullTime());
				return (++currentDay).toString();
			}
		});

		// Returns the current week of the year for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, "tm_yearweek", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Long week = ValuesConverter.yearWeekFromTick(e.getPlayer().getWorld().getFullTime());
				return week.toString() ;
			}
		});

		// Returns the current month's name for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, "tm_monthname", new PlaceholderReplacer() {
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				String lg = UserMsgHandler.setLangToUse(e.getPlayer());
				long elapsedDays = ValuesConverter.elapsedDaysFromTick(e.getPlayer().getWorld().getFullTime());
				String mm = "m" + ValuesConverter.dateFromElapsedDays(elapsedDays, "mm");
				return MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lg + "." + CF_MONTHS + "." + mm);
			}
		});

		// Returns the current day in (00 format) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, "tm_dd", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				long elapsedDays = ValuesConverter.elapsedDaysFromTick(e.getPlayer().getWorld().getFullTime());
				return ValuesConverter.dateFromElapsedDays(elapsedDays, "dd");
			}
		});

		// Returns the current month in (00 format) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, "tm_mm", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				long elapsedDays = ValuesConverter.elapsedDaysFromTick(e.getPlayer().getWorld().getFullTime());
				return ValuesConverter.dateFromElapsedDays(elapsedDays, "mm");
			}
		});

		// Returns the current year in (00 format) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, "tm_yy", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				long elapsedDays = ValuesConverter.elapsedDaysFromTick(e.getPlayer().getWorld().getFullTime());
				return ValuesConverter.dateFromElapsedDays(elapsedDays, "yy");
			}
		});

		// Returns the current year in (0000 format) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, "tm_yyyy", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				long elapsedDays = ValuesConverter.elapsedDaysFromTick(e.getPlayer().getWorld().getFullTime());
				return ValuesConverter.dateFromElapsedDays(elapsedDays, "yyyy");
			}
		});

	}

};