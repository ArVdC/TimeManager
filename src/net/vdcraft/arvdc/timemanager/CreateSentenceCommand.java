/**********************
 **** TAB COMPLETER ****
 **********************/

package net.vdcraft.arvdc.timemanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.LgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;

public class CreateSentenceCommand implements TabCompleter {

	/*****************
	 *** VARIABLES ***
	 *****************/

	// List of admin sub-commands
	List<String> tmCmdArgsList() {
		if (MainTM.serverMcVersion >= MainTM.reqMcVForUpdate) return Arrays.asList(MainTM.CMD_CHECKCONFIG, MainTM.CMD_CHECKSQL, MainTM.CMD_CHECKTIME, MainTM.CMD_CHECKUPDATE, MainTM.CMD_HELP, MainTM.CMD_TMNOW, MainTM.CMD_RELOAD, MainTM.CMD_RESYNC, MainTM.CMD_SET);
		else return Arrays.asList(MainTM.CMD_CHECKCONFIG, MainTM.CMD_CHECKSQL, MainTM.CMD_CHECKTIME, MainTM.CMD_HELP, MainTM.CMD_TMNOW, MainTM.CMD_RELOAD, MainTM.CMD_RESYNC, MainTM.CMD_SET);
	}
	// List of admin sub-commands having a 'help'
	List<String> tmHelpArgsList() {
		if (MainTM.serverMcVersion >= MainTM.reqMcVForUpdate) return Arrays.asList(MainTM.CMD_CHECKCONFIG, MainTM.CMD_CHECKSQL, MainTM.CMD_CHECKTIME, MainTM.CMD_CHECKUPDATE, MainTM.CMD_TMNOW, MainTM.CMD_RELOAD, MainTM.CMD_RESYNC, MainTM.CMD_SET);
		else return Arrays.asList(MainTM.CMD_CHECKCONFIG, MainTM.CMD_CHECKSQL, MainTM.CMD_CHECKTIME, MainTM.CMD_TMNOW, MainTM.CMD_RELOAD, MainTM.CMD_RESYNC, MainTM.CMD_SET);
	}
	// List of online players
	List<String> onlinePlayersList() {
		List<String> playersArgs = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers()) {
		playersArgs.add(p.getName());
		}
		return playersArgs;
	}
	// List of available worlds
	List<String> worldsList(CommandSender sender) {
		List<String> worlds = new ArrayList<>();
		worlds = CfgFileHandler.setAnyListFromConfig(MainTM.CF_WORLDSLIST);
		// Names with spaces : Hack it only for players and commandblocks  (Useless since MC 1.13.2)
		if ((sender instanceof Player) || (sender instanceof BlockCommandSender)) {
			if (MainTM.serverMcVersion < MainTM.maxMcVForTabCompHack) {
				worlds = replaceSpacesInList(worlds);
			}
		}
		return worlds;
	}
	// Arguments list for '/tm checkupdate'
	List<String> tmCheckupdateArgsList = Arrays.asList(MainTM.ARG_NONE, MainTM.ARG_BUKKIT, MainTM.ARG_CURSE, MainTM.ARG_SPIGOT, MainTM.ARG_GITHUB);
	// Arguments list for '/tm reload'
	List<String> tmReloadArgsList = Arrays.asList(MainTM.ARG_ALL, MainTM.ARG_CONFIG, MainTM.ARG_LANG, MainTM.ARG_CMDS);
	// Arguments list for '/tm set'
	List<String> tmSetArgsList() {
		List<String> SetArgs = Arrays.asList(MainTM.CMD_SET_DATE, MainTM.CMD_SET_DEBUG, MainTM.CMD_SET_DEFLANG, MainTM.CMD_SET_E_DAYS, MainTM.CMD_SET_INITIALTICK, MainTM.CMD_SET_MULTILANG, MainTM.CMD_SET_REFRESHRATE, MainTM.CMD_SET_PLAYEROFFSET, MainTM.CMD_SET_PLAYERTIME, MainTM.CMD_SET_SLEEP, MainTM.CMD_SET_SPEED, MainTM.CMD_SET_D_SPEED, MainTM.CMD_SET_N_SPEED, MainTM.CMD_SET_START, MainTM.CMD_SET_SYNC, MainTM.CMD_SET_TIME, MainTM.CMD_SET_UPDATE, MainTM.CMD_SET_USECMDS);
		if (MainTM.serverMcVersion < MainTM.reqMcVForUpdate) SetArgs.remove(MainTM.CMD_SET_UPDATE);
		return SetArgs;
	}
	// Arguments list for '/tm set deflang'
	List<String> tmDefLangArgsList() {
		return LgFileHandler.setAnyListFromLang(MainTM.CF_LANGUAGES);
	}
	// Arguments list for '/tm set multilang'
	List<String> tmBooleanArgsList = Arrays.asList(MainTM.ARG_TRUE, MainTM.ARG_FALSE);
	// First 'tick' arguments for '/tm set start' et '/tm set time'
	List<String> tmTimeArgsList = Arrays.asList("day", "morning", "noon", "midday", "sunset", "dusk", "evening", "night", "midnight", "sunrise", "dawn");
	// Add 'reset' arg to '/tm set playerTime' // TODO 1.6.0
	List<String> tmPlayerTimeArgsList() {
		List<String> tmPlayerTimeArgs = new ArrayList<>();
		tmPlayerTimeArgs.addAll(tmTimeArgsList);
		tmPlayerTimeArgs.add(MainTM.ARG_RESET);
		return tmPlayerTimeArgs;
	}
	// Arguments list for '/tm set playerOffset' // TODO 1.6.0
	List<String> tmSetPlayerOffsetList = Arrays.asList("0", "01000", "02000", "03000", "04000", "05000", "06000", "07000", "08000", "09000", "10000", "11000", "12000", "13000", "14000", "15000", "16000", "17000", "18000", "19000", "20000", "21000", "22000", "23000");
	// Number of days arguments for '/tm set elapsedDays'
	List<String> tmSetDateArgsList = Arrays.asList("today", "0001-01-01");
	// Number of days arguments for '/tm set elapsedDays'
	List<String> tmSetDaysArgsList = Arrays.asList("000", "031", "059", "090", "120", "151", "181", "212", "243", "273", "304", "334", "365");
	// Arguments list for '/tm set sleep
	List<String> tmSetSleepArgsList = Arrays.asList(MainTM.ARG_TRUE, MainTM.ARG_FALSE, MainTM.ARG_LINKED);
	// Modifier arguments for '/tm set speed'
	List<String> tmSpeedArgsList = Arrays.asList("0.0", "0.5", "1.0", "1.5", "2.0", "2.5", "5.0", "realtime");
	// 'tick' arguments list for '/tm set initialtick'
	List<String> tmInitialTickArgsList = Arrays.asList("000000", "001200", "072000", "864000");
	// 'tick' arguments list for '/tm set refreshrate'
	List<String> tmRefRateArgsList = Arrays.asList("05", "10", "15", "20");
	// 'worlds' arguments list for '/tm resync', '/tm set speed', '/tm set start' and '/tm set time' etc.
	List<String> tmWorldsArgsList(CommandSender sender) {
		List<String> tmWorldsArgs = new ArrayList<>();
		tmWorldsArgs.add(MainTM.ARG_ALL);
		tmWorldsArgs.addAll(worldsList(sender));
		return tmWorldsArgs;
	}
	// Arguments list for '/tm checktime'
	List<String> tmCheckTimeArgsList(CommandSender sender) {
		List<String> allAndServerArg = Arrays.asList(MainTM.ARG_ALL, MainTM.ARG_SERVER);
		List<String> tmWorldsArgs = new ArrayList<>();
		tmWorldsArgs.addAll(allAndServerArg);
		tmWorldsArgs.addAll(worldsList(sender));
		return tmWorldsArgs;		
	}
	// Arguments list for '/tm now'
	List<String> tmNowDisplayArgsList = Arrays.asList(MainTM.ARG_MSG, MainTM.ARG_TITLE, MainTM.ARG_ACTIONBAR);
	List<String> tmNowTargetArgsList(CommandSender sender) {
		List<String> nowTargetArgs = new ArrayList<>();
		nowTargetArgs.add(MainTM.ARG_ALL);
		nowTargetArgs.addAll(onlinePlayersList());
		nowTargetArgs.addAll(worldsList(sender));
		return nowTargetArgs;
	}
	// Arguments list for '/now'
	List<String> nowDisplayArgsList = Arrays.asList(MainTM.ARG_MSG, MainTM.ARG_TITLE, MainTM.ARG_ACTIONBAR);
	List<String> nowWorldsArgsList(CommandSender sender) {
		List<String> tmWorldsArgs = new ArrayList<>();
		tmWorldsArgs.addAll(worldsList(sender));
		return tmWorldsArgs;	
	}

	/*****************
	 ***** EVENT *****
	 *****************/

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> outputArgsList = new ArrayList<String>();
		MainTM.getInstance();

		if (command.getName().equalsIgnoreCase(MainTM.CMD_TM)) {

			if (!sender.hasPermission(MainTM.PERM_TM) && !sender.isOp()) {
				return null;
			}

			// Always check argument's length BEFORE calling it
			if (args.length == 1) { // Command '/tm <...>'
				for (String verif : tmCmdArgsList()) {
					if (verif.toLowerCase().startsWith(args[0].toLowerCase()))
						outputArgsList.add(verif);
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase(MainTM.CMD_CHECKTIME)) // Command '/tm checktime <...>'
				{
					for (String verif : tmCheckTimeArgsList(sender)) {
						if (verif.toLowerCase().startsWith(args[1].toLowerCase()))
							outputArgsList.add(verif);
					}
				} else if (args[0].equalsIgnoreCase(MainTM.CMD_CHECKUPDATE) && MainTM.serverMcVersion >= MainTM.reqMcVForUpdate) // Command '/tm checkupdate <...>'
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
				} else if (args[0].equalsIgnoreCase(MainTM.CMD_TMNOW)) // Command '/tm now <...>'
				{
					for (String verif : tmNowDisplayArgsList) {
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
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_PLAYEROFFSET)) // Command '/tm set playerOffset <...>' // TODO 1.6.0
					{
						for (String verif : tmSetPlayerOffsetList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))								
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_PLAYERTIME)) // Command '/tm set playerTime <...>' // TODO 1.6.0
					{
						for (String verif : tmPlayerTimeArgsList()) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))								
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_REFRESHRATE)) // Command '/tm set refreshRate <...>'
					{
						for (String verif : tmRefRateArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_SLEEP)) // Command '/tm set sleep <...>'
					{
						for (String verif : tmSetSleepArgsList) {
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
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_DATE)) // Command '/tm set date <...>'
					{
						for (String verif : tmSetDateArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))								
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_E_DAYS)) // Command '/tm set elapsedDays <...>'
					{
						for (String verif : tmSetDaysArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))								
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_UPDATE) && MainTM.serverMcVersion >= MainTM.reqMcVForUpdate) // Command '/tm set update <...>'
					{
						for (String verif : tmCheckupdateArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					} else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_USECMDS)) // Command '/tm set useCmds <...>'
					{
						for (String verif : tmBooleanArgsList) {
							if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
								outputArgsList.add(verif);
						}
					}
				} else if (args[0].equalsIgnoreCase(MainTM.CMD_TMNOW)) // Command '/tm now <...> <...>'
				{
					for (String verif : tmNowTargetArgsList(sender)) {
						if (verif.toLowerCase().startsWith(args[2].toLowerCase()))							
							outputArgsList.add(verif);
					}
				}  else if (args[0].equalsIgnoreCase(MainTM.CMD_HELP) && args[1].equalsIgnoreCase(MainTM.CMD_SET)) // Command '/tm help set <...>'
				{
					for (String verif : tmSetArgsList()) {
						if (verif.toLowerCase().startsWith(args[2].toLowerCase()))
							outputArgsList.add(verif);
					}
				} else {
					return null;
				}
			} else if (args.length == 4) {
				if ((args[0].equalsIgnoreCase(MainTM.CMD_SET)) // Command '/tm set <...> <...> <...>'
						&& args[1].equalsIgnoreCase(MainTM.CMD_SET_DATE)
						|| args[1].equalsIgnoreCase(MainTM.CMD_SET_E_DAYS)
						|| args[1].equalsIgnoreCase(MainTM.CMD_SET_D_SPEED)
						|| args[1].equalsIgnoreCase(MainTM.CMD_SET_N_SPEED)
						|| args[1].equalsIgnoreCase(MainTM.CMD_SET_SPEED)
						|| args[1].equalsIgnoreCase(MainTM.CMD_SET_SLEEP)
						|| args[1].equalsIgnoreCase(MainTM.CMD_SET_START)
						|| args[1].equalsIgnoreCase(MainTM.CMD_SET_SYNC)
						|| args[1].equalsIgnoreCase(MainTM.CMD_SET_TIME))
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
				if (sender.hasPermission(MainTM.PERM_NOW_DISPLAY)
						|| sender.isOp()) {
					for (String verif : nowDisplayArgsList) {
						if (verif.toLowerCase().startsWith(args[0].toLowerCase()))							
							outputArgsList.add(verif);
					}
				}
				else if (sender.hasPermission(MainTM.PERM_NOW_WORLD)
						|| sender.isOp()) {
					for (String verif : nowWorldsArgsList(sender)) {
						if (verif.toLowerCase().startsWith(args[0].toLowerCase()))							
							outputArgsList.add(verif);
					}
				}
			} else if (args.length == 2) // Command '/now <...> <...>'
			{
				if ((sender.hasPermission(MainTM.PERM_NOW_WORLD) && sender.hasPermission(MainTM.PERM_NOW_DISPLAY)
						|| sender.isOp())) {
					for (String verif : nowWorldsArgsList(sender)) {
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

	/**
	 * Replaces 'spaces' in a given list (Useless since MC 1.13.2)
	 * (returns a List<String>)
	 */
	public static List<String> replaceSpacesInList(List<String> worldsList) {
		// Using u02d9 for "˙" or u2800 for "white braille", " " for "reset"
		List<String> l = new ArrayList<>();
		l.addAll(worldsList);
		for (String nameWithSpaces : l) {
			if (nameWithSpaces.contains(" ")) {
				worldsList.remove(nameWithSpaces);
				String nameWithoutSpaces = nameWithSpaces.replace(" ", "\u02d9");
				worldsList.add(nameWithoutSpaces);
				MsgHandler.devMsg("Spaces have been changed in §e" + nameWithSpaces + "§9 to become §e" + nameWithoutSpaces + "§9.");
			}
		}
		return worldsList;
	}

	/**
	 * Restores missing 'spaces' in a String (Useless since MC 1.13.2)
	 * (returns a String)
	 */
	public static String restoreSpacesInString(String nameWithoutSpaces) {
		// Using u02d9 for "˙" or u2800 for "white braille", " " for "reset"
		if (nameWithoutSpaces.contains("\u02d9")) {
			String nameWithSpaces = nameWithoutSpaces.replace("\u02d9", " ");
			MsgHandler.devMsg("Spaces have been changed in §e" + nameWithoutSpaces + "§9 to become §e" +nameWithSpaces  + "§9.");
			return nameWithSpaces;
		}
		return nameWithoutSpaces;
	}

};
