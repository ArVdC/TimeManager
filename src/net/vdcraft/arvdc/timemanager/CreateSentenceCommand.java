/**********************
 **** TAB COMPLETER ****
 **********************/

package net.vdcraft.arvdc.timemanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class CreateSentenceCommand implements TabCompleter {

	/*****************
	 *** VARIABLES ***
	 *****************/

	// List of admin sub-commands
	List<String> tmCmdArgsList() {
		if (MainTM.decimalOfMcVersion >= MainTM.requiredMcVersionForUpdate) return Arrays.asList(MainTM.CMD_CHECKCONFIG, MainTM.CMD_CHECKSQL, MainTM.CMD_CHECKTIME, MainTM.CMD_CHECKUPDATE, MainTM.CMD_HELP, MainTM.CMD_RELOAD, MainTM.CMD_RESYNC, MainTM.CMD_SET);
		else return Arrays.asList(MainTM.CMD_CHECKCONFIG, MainTM.CMD_CHECKSQL, MainTM.CMD_CHECKTIME, MainTM.CMD_HELP, MainTM.CMD_RELOAD, MainTM.CMD_RESYNC, MainTM.CMD_SET);
	}
	// List of admin sub-commands having a 'help'
	List<String> tmHelpArgsList() {
		if (MainTM.decimalOfMcVersion >= MainTM.requiredMcVersionForUpdate) return Arrays.asList(MainTM.CMD_CHECKCONFIG, MainTM.CMD_CHECKSQL, MainTM.CMD_CHECKTIME, MainTM.CMD_CHECKUPDATE, MainTM.CMD_RELOAD, MainTM.CMD_RESYNC, MainTM.CMD_SET);
		else return Arrays.asList(MainTM.CMD_CHECKCONFIG, MainTM.CMD_CHECKSQL, MainTM.CMD_CHECKTIME, MainTM.CMD_RELOAD, MainTM.CMD_RESYNC, MainTM.CMD_SET);
	}    
	// Arguments list for '/tm checkupdate'
	List<String> tmCheckupdateArgsList = Arrays.asList(MainTM.CF_BUKKIT, MainTM.CF_CURSE, MainTM.CF_SPIGOT, MainTM.CF_GITHUB);
	// Arguments list for '/tm reload'
	List<String> tmReloadArgsList = Arrays.asList("all", "config", "lang");
	// Arguments list for '/tm set'
	List<String> tmSetArgsList() {
		if (MainTM.decimalOfMcVersion >= MainTM.requiredMcVersionForUpdate) return Arrays.asList(MainTM.CMD_SET_DEBUG, MainTM.CMD_SET_DEFLANG, MainTM.CMD_SET_INITIALTICK, MainTM.CMD_SET_MULTILANG, MainTM.CMD_SET_REFRESHRATE, MainTM.CMD_SET_SLEEP, MainTM.CMD_SET_SPEED, MainTM.CMD_SET_D_SPEED, MainTM.CMD_SET_N_SPEED, MainTM.CMD_SET_START, MainTM.CMD_SET_SYNC, MainTM.CMD_SET_TIME, MainTM.CMD_SET_E_DAYS, MainTM.CMD_SET_UPDATE);
		else return Arrays.asList(MainTM.CMD_SET_DEBUG, MainTM.CMD_SET_DEFLANG, MainTM.CMD_SET_INITIALTICK, MainTM.CMD_SET_MULTILANG, MainTM.CMD_SET_REFRESHRATE, MainTM.CMD_SET_SLEEP, MainTM.CMD_SET_SPEED, MainTM.CMD_SET_D_SPEED, MainTM.CMD_SET_N_SPEED, MainTM.CMD_SET_START, MainTM.CMD_SET_SYNC, MainTM.CMD_SET_TIME, MainTM.CMD_SET_E_DAYS);
	}
	// Arguments list for '/tm set deflang
	List<String> tmDefLangArgsList() {
		return LgFileHandler.setAnyListFromLang("languages");
	}

	// Arguments list for '/tm set multilang
	List<String> tmBooleanArgsList = Arrays.asList("true", "false");
	// First 'tick' arguments for '/tm set start' et '/tm set time'
	List<String> tmTimeArgsList = Arrays.asList("morning", "noon", "midday", "sunset", "dusk", "evening", "night", "midnight", "sunrise", "dawn");
	// Number of days arguments for '/tm set elapsedDays'
	List<String> tmSetDaysArgsList = Arrays.asList("today", "000", "031", "059", "090", "120", "151", "181", "212", "243", "273", "304", "334", "365");
	// Modifier arguments for '/tm set speed'
	List<String> tmSpeedArgsList = Arrays.asList("0.0", "0.5", "1.0", "1.5", "2.0", "2.5", "5.0", "realtime");
	// 'tick' arguments list for '/tm set initialtick'
	List<String> tmInitialTickArgsList = Arrays.asList("000000", "001200", "072000", "864000");
	// 'tick' arguments list for '/tm set refreshrate'
	List<String> tmRefRateArgsList = Arrays.asList("05", "10", "15", "20", "25");
	// 'worlds' arguments list for '/tm resync', '/tm set speed', '/tm set start'
	// and '/tm set time' etc.
	List<String> allArg = Arrays.asList("all");

	List<String> tmWorldsArgsList(CommandSender sender) {
		List<String> worldsArgs = CfgFileHandler.setAnyListFromConfig("worldsList");
		if (sender instanceof Player) { // Hack it only for players
			worldsArgs = ValuesConverter.replaceSpacesInList(worldsArgs);
		}
		return Stream.concat(allArg.stream(), worldsArgs.stream()).collect(Collectors.toList());
	}

	// 'worlds' arguments list for '/tm checktime'
	List<String> allAndServerArg = Arrays.asList("all", "server");

	List<String> tmWorldsTimeArgsList(CommandSender sender) {
		List<String> worldsArgs = CfgFileHandler.setAnyListFromConfig("worldsList");
		if (sender instanceof Player) { // Hack it only for players
			worldsArgs = ValuesConverter.replaceSpacesInList(worldsArgs);
		}
		return Stream.concat(allAndServerArg.stream(), worldsArgs.stream()).collect(Collectors.toList());
	}

	// Arguments list for 'units' and 'world' for '/now x x'
	List<String> nowUnitsArgsList = Arrays.asList("hours", "ticks");

	List<String> nowWorldsArgsList() {
		List<String> worldsArgs = CfgFileHandler.setAnyListFromConfig("worldsList");
		worldsArgs = ValuesConverter.replaceSpacesInList(worldsArgs);
		return worldsArgs;
	}

	/*****************
	 ***** EVENT *****
	 *****************/

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> outputArgsList = new ArrayList<String>();
		MainTM.getInstance();

		if (command.getName().equalsIgnoreCase(MainTM.CMD_TM)) {

			// Always check argument's length BEFORE calling it

			if (args.length == 1) { // Command '/tm <...>'
				for (String verif : tmCmdArgsList()) {
					if (verif.toLowerCase().startsWith(args[0].toLowerCase()))
						outputArgsList.add(verif);
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase(MainTM.CMD_CHECKTIME)) // Command '/tm checktime <...>'
				{
					for (String verif : tmWorldsTimeArgsList(sender)) {
						if (verif.toLowerCase().startsWith(args[1].toLowerCase()))
							outputArgsList.add(verif);
					}
				} else if (args[0].equalsIgnoreCase(MainTM.CMD_CHECKUPDATE) && MainTM.decimalOfMcVersion >= MainTM.requiredMcVersionForUpdate) // Command '/tm checkupdate <...>'
				{
					for (String verif : tmCheckupdateArgsList) {
						if (verif.toLowerCase().startsWith(args[1].toLowerCase()))
							outputArgsList.add(verif);
					}
				} else if (args[0].equalsIgnoreCase(MainTM.CMD_HELP)) // Command '/tm help <...>'
				{
					for (String verif : tmHelpArgsList()) {
						if (verif.toLowerCase().startsWith(args[1].toLowerCase()))
							outputArgsList.add(verif);
					}
				} else if (args[0].equalsIgnoreCase(MainTM.CMD_RELOAD)) // Command '/tm reload <...>'
				{
					for (String verif : tmReloadArgsList) {
						if (verif.toLowerCase().startsWith(args[1].toLowerCase()))
							outputArgsList.add(verif);
					}
				} else if (args[0].equalsIgnoreCase(MainTM.CMD_RESYNC)) // Command '/tm resync <...>'
				{
					for (String verif : tmWorldsArgsList(sender)) {
						if (verif.toLowerCase().startsWith(args[1].toLowerCase()))
							outputArgsList.add(verif);
					}
				} else if (args[0].equalsIgnoreCase(MainTM.CMD_SET)) // Command '/tm set <...>'
				{
					for (String verif : tmSetArgsList()) {
						if (verif.toLowerCase().startsWith(args[1].toLowerCase()))
							outputArgsList.add(verif);
					}
				} else {
					return null;
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase(MainTM.CMD_SET)) // Command /tm set <...> <...>
				{
					if (args[1].equalsIgnoreCase(MainTM.CMD_SET_DEFLANG)) // Command '/tm set deflang <...>'
					{
						for (String verif : tmDefLangArgsList()) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_DEBUG)) // Command '/tm set debugmode <...>'
					{
						for (String verif : tmBooleanArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_INITIALTICK)) // Command '/tm set initialtick <...>'
					{
						for (String verif : tmInitialTickArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_MULTILANG)) // Command '/tm set multilang <...>'
					{
						for (String verif : tmBooleanArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_REFRESHRATE)) // Command '/tm set refreshrate <...>'
					{
						for (String verif : tmRefRateArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_SLEEP)) // Command '/tm set sleep <...>'
					{
						for (String verif : tmBooleanArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_SPEED) || args[1].equalsIgnoreCase(MainTM.CMD_SET_D_SPEED) || args[1].equalsIgnoreCase(MainTM.CMD_SET_N_SPEED)) // Commands '/tm set speed <...>' '/tm set speedDay <...>' '/tm set speedNight <...>'
					{
						for (String verif : tmSpeedArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_START)) // Command '/tm set start <...>'
					{
						for (String verif : tmTimeArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_SYNC)) // Command '/tm set sync <...>'
					{
						for (String verif : tmBooleanArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_TIME)) // Command '/tm set time <...>'
					{
						for (String verif : tmTimeArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_E_DAYS)) // Command '/tm set elapsedDays <...>' TODO 1.4.0
					{
						for (String verif : tmSetDaysArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_UPDATE) && MainTM.decimalOfMcVersion >= MainTM.requiredMcVersionForUpdate) // Command '/tm set update <...>'
					{
						for (String verif : tmCheckupdateArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					}
				} else if (args[0].equalsIgnoreCase(MainTM.CMD_HELP) && args[1].equalsIgnoreCase(MainTM.CMD_SET)) // Command '/tm help set <...>'
				{
					for (String verif : tmSetArgsList()) {
						if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
							outputArgsList.add(verif);
					}
				} else {
					return null;
				}
			} else if (args.length == 4) {
				if ((args[0].equalsIgnoreCase(MainTM.CMD_SET)) && (args[1].equalsIgnoreCase(MainTM.CMD_SET_SPEED)
						|| args[1].equalsIgnoreCase(MainTM.CMD_SET_D_SPEED) || args[1].equalsIgnoreCase(MainTM.CMD_SET_N_SPEED)
						|| args[1].equalsIgnoreCase(MainTM.CMD_SET_START) || args[1].equalsIgnoreCase(MainTM.CMD_SET_TIME)
						|| args[1].equalsIgnoreCase(MainTM.CMD_SET_E_DAYS) || args[1].equalsIgnoreCase(MainTM.CMD_SET_SLEEP) // TODO 1.4.0
						|| args[1].equalsIgnoreCase(MainTM.CMD_SET_SYNC))) // Command '/tm set <...> <...> <...>'
				{
					for (String verif : tmWorldsArgsList(sender)) {
						if (verif.toLowerCase().startsWith(args[3].toLowerCase()))
							outputArgsList.add(verif);
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else if (command.getName().equalsIgnoreCase(MainTM.CMD_NOW)) {
			if (args.length == 1) { // Command '/now <...>'
				if (sender.hasPermission("timemanager.now.units")) {
					for (String verif : nowUnitsArgsList) {
						if (verif.toLowerCase().startsWith(args[0].toLowerCase()))
							outputArgsList.add(verif);
					}
				}
				if (sender.hasPermission("timemanager.now.worlds")) {
					for (String verif : nowWorldsArgsList()) {
						if (verif.toLowerCase().startsWith(args[0].toLowerCase()))
							outputArgsList.add(verif);
					}
				}
			} else if (args.length == 2) // Command '/now <...> <...>'
			{
				if (sender.hasPermission("timemanager.now.worlds") && sender.hasPermission("timemanager.now.units")) {
					for (String verif : nowWorldsArgsList()) {
						if (verif.toLowerCase().startsWith(args[1].toLowerCase()))
							outputArgsList.add(verif);
					}
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
		return outputArgsList;
	}

};
