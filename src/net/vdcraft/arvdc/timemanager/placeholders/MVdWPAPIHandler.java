package net.vdcraft.arvdc.timemanager.placeholders;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.PlayerLangHandler;

public class MVdWPAPIHandler extends MainTM {

	/**
	 * MVdWPlaceholderAPI (See www.spigotmc.org/resources/mvdwplaceholderapi.11182 for credits)
	 */

	@EventHandler
	public static void loadMVdWPlaceholderAPI() {

		// Returns the current player's name
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_PLAYER, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				return e.getPlayer().getName();
			}
		});

		// Returns the current world's name
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_WORLD, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				return e.getPlayer().getWorld().getName();
			}
		});

		// Returns the current tick for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_TICK, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_TICK + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the current time (in hh:mm:ss) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_TIME12, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_TIME12 + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the current time (in HH:mm:ss) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_TIME24, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_TIME24 + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the current hour (in hh) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_HOURS12, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_HOURS12 + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the current hour (in hh) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_HOURS24, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_HOURS24 + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the current minutes (in mm) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_MINUTES, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_MINUTES + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the current seconds (in ss) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_SECONDS, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_SECONDS + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the part of day (AM or PM) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_AMPM, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_AMPM + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the current day part for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_DAYPART, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_DAYPART + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the total # of days elapsed for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_E_DAYS, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_E_DAYS + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the current day # for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_C_DAY, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_C_DAY + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the name of the current day for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_DAYNAME, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_DAYNAME + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the number of the current day in the year for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_YEARDAY, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_YEARDAY + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the number of the current week in the year for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_YEARWEEK, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_YEARWEEK + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the number of elapsed weeks for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_WEEK, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_WEEK + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the current month's name for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_MONTHNAME, new PlaceholderReplacer() {
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_MONTHNAME + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the current day in (00 format) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_DD, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_DD + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the current month in (00 format) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_MM, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_MM + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the current year in (00 format) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_YY, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_YY + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

		// Returns the current year in (0000 format) for the player's world
		PlaceholderAPI.registerPlaceholder(instanceMainClass, PH_PREFIX + PH_YYYY, new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
				Player p = e.getPlayer();
				return PlaceholdersHandler.replacePlaceholder("{" + PH_PREFIX + PH_YYYY + "}", p.getWorld().getName(), PlayerLangHandler.setLangToUse(p), p);
			}
		});

	}

};