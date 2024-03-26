package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;

public class TmHelp extends MainTM {

	/**
	 * Help messages
	 */
	// Always copy this content in the README.md
	private static String headerHelp = ChatColor.YELLOW + "---------" + ChatColor.RESET + " Help: " + prefixTMColor + ChatColor.YELLOW + " ---------";
	private static String checkconfigHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_CHECKCONFIG
			+ " " + ChatColor.RESET + "Admins and console can display a summary of the config.yml and lang.yml files.";
	private static String checkSqlHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_CHECKSQL
			+ " " + ChatColor.RESET + "Check the availability of the mySQL server according to the values provided in the config.yml file. This only checks the ip address and the correct port opening.";
	private static String checktimeHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_CHECKTIME
			+ " [all|server|world] " + ChatColor.RESET + "Admins and console can display a debug/managing message, who displays the startup server's time, the current server's time and the current time, start time and speed for a specific world (or for all of them).";
	private static String checkupdateHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_CHECKUPDATE
			+ " [bukkit|spigot|github] " + ChatColor.RESET + "Search if a newer version of the plugin exists on the chosen server. (MC 1.18.9+ only)";
	private static String helpHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_HELP
			+ " [cmd] [<subCmd>] " + ChatColor.RESET + "Help provides you the correct usage and a short description of targeted command or subcommand.";
	private static String tmNowHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_NOW
			+ " [msg|title|actionbar] [all|player|world] " + ChatColor.RESET + "Send the '/now' (chat, title or action bar) message to a specific player, all players in a specific world, or all online players.";
	private static String reloadHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_RELOAD
			+ " [all|cmds|config|lang] " + ChatColor.RESET + "This command allows you to reload datas from yaml files after manual modifications. All timers will be immediately resynchronized.";
	private static String resyncHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_RESYNC
			+ " [all|world] " + ChatColor.RESET + "This command will re-synchronize a single or all worlds timers, based on the startup server's time, the elapsed time and the current speed modifier.";
	private static String setDateHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_DATE
			+ " [today|yyyy-mm-dd] [all|world] " + ChatColor.RESET + "Sets current date for the specified world (or all of them). Could be 'today' or any yyyy-mm-dd date. The length of the months corresponds to reality, with the exception of February which always lasts 28 days. A year therefore always lasts 365 days.";
	private static String setDebugHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_DEBUG
			+ " [true|false] " + ChatColor.RESET + "Set true to enable colored verbose messages in the console. Useful to understand some mechanisms of this plugin.";
	private static String setDefLangHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_DEFLANG
			+ " [lg_LG] " + ChatColor.RESET + "Choose the translation to use if player's locale doesn't exist in the lang.yml or when 'multiLang' is 'false'.";
	private static String setE_DaysHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_E_DAYS
			+ " [0 → ∞] [all|world] " + ChatColor.RESET + "Sets current number of elapsed days for the specified world (or all of them). Could be an integer between '0' and infinity (or almost). Setting this to '0' will bring the world back to day one.";
	private static String setFirstStartTimeHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_FIRSTSTARTTIME
			+ " [default|previous|start] [all|world] " + ChatColor.RESET + "Forces the time at which a world starts when starting the server. The value 'default' allows the usual resynchronization at startup. The value 'start' forces the world to start at the time specified in the world's 'start' node. The value 'previous' returns the time in the world before the server was shut down.";
	private static String setInitialTickHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_INITIALTICK
			+ " [ticks|HH:mm:ss] " + ChatColor.RESET + "Modify the server's initial tick.";
	private static String setMultilangHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_MULTILANG
			+ " [true|false] " + ChatColor.RESET + "Set true or false to use an automatic translation for the " + ChatColor.ITALIC + "/now" + ChatColor.RESET + " command.";
	private static String setPlayerOffsetHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_PLAYEROFFSET
			+ " [-23999 → 23999] [all|player] " + ChatColor.RESET + "Define a specific offset relative to the world time on player's client (the world speed will be still active). Set to '0' to cancel.";
	private static String setPlayerTimeHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_PLAYERTIME
			+ " [ticks|daypart|HH:mm:ss|reset] [all|player] " + ChatColor.RESET + "Define a specific time on player's client (the world speed will be still active). Use the 'reset' argument to cancel.";
	private static String setRefreshRateHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_REFRESHRATE
			+ " [ticks] " + ChatColor.RESET + "Set the delay (in ticks) before actualizing the speed stretch/expand effect. Must be an integer between '" + refreshMin + "' and '" + refreshMax + "'. Default value is '" + defRefresh + " ticks', please note that a too small value can cause server lags.";
	private static String setSleepHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_SLEEP
			+ " [true|false|linked] [all|world] " + ChatColor.RESET + "Define if players can sleep until the next day in the specified world (or in all of them). By default, all worlds will start with parameter true, unless their timer is in real time who will be necessary false. If you want to both allow sleep and keep the same time in multiple worlds, you can use the 'linked' function which allows a group of worlds to spend the night together.";
	private static String setSpeedHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_SPEED
			+ " [multiplier] [all|world] " + ChatColor.RESET + "The decimal number argument will multiply the world(s) speed. Use '0.0' to freeze time, numbers from '0.01' to '0.9' to slow time, '1.0' to get normal speed and numbers from '1.1' to " + speedMax + " to speedup time. Set this value to '24.0' or 'realtime' to make the world time match the real speed time.";
	private static String setSpeed_D_N_HelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_D_SPEED + " " + ChatColor.RESET + "or " + ChatColor.GOLD + "/"
			+ CMD_TM + " " + CMD_SET + " " + CMD_SET_N_SPEED
			+ " [multiplier] [all|world] " + ChatColor.RESET + "From '0.0' to '" + speedMax + "', the values of daySpeed and nightSpeed can be different from each other.";
	private static String setStartHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_START
			+ " [ticks|daypart|HH:mm:ss|timeShift] [all|world] " + ChatColor.RESET + "Defines the time at server startup for the specified world (or all of them). By default, all worlds will start at tick #0. The timer(s) will be immediately resynchronized. If a world is using the real time speed, the start value will determine the UTC time shift and values like +1 or -1 will be accepted.";
	private static String setSyncHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_SYNC
			+ " [true|false] [all|world] " + ChatColor.RESET + "Define if the speed distortion method will increase/decrease the world's actual tick, or fit the theoretical tick value based on the server one. By default, all worlds will start with parameter false. Real time based worlds and frozen worlds do not use this option, on the other hand this will affect even the worlds with a normal speed.";
	private static String setTimeHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_TIME
			+ " [ticks|daypart|HH:mm:ss] [all|world] " + ChatColor.RESET + "Sets current time for the specified world (or all of them). Consider using this instead of the vanilla " + ChatColor.ITALIC + "/time" + ChatColor.RESET + " command. The tab completion also provides handy presets like \"day\", \"noon\", \"night\", \"midnight\", etc.";
	private static String setupdateSrcHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_UPDATE
			+ " [bukkit|spigot|github] " + ChatColor.RESET + "Define the source server for the update search. (MC 1.8.8+ only)";
	private static String setUseCmdsHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_USECMDS
			+ " [true|false] " + ChatColor.RESET + "Set true to enable a custom commands scheduler. See the " + CMDSFILENAME + " file for details.";
	// Except this line, used when 'set' is used without additional argument
	private static String missingSetArgHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " ["
			+ CMD_SET_DATE + "|"
			+ CMD_SET_DEBUG + "|"
			+ CMD_SET_DEFLANG + "|"
			+ CMD_SET_E_DAYS + "|"
			+ CMD_SET_FIRSTSTARTTIME + "|"
			+ CMD_SET_INITIALTICK + "|"
			+ CMD_SET_MULTILANG + "|"
			+ CMD_SET_PLAYEROFFSET + "|"
			+ CMD_SET_PLAYERTIME + "|"
			+ CMD_SET_REFRESHRATE + "|"
			+ CMD_SET_SLEEP + "|"
			+ CMD_SET_SPEED + "|"
			+ CMD_SET_D_SPEED + "|"
			+ CMD_SET_N_SPEED + "|"
			+ CMD_SET_START + "|"
			+ CMD_SET_SYNC + "|"
			+ CMD_SET_TIME + "|"
			+ CMD_SET_UPDATE + "|"
			+ CMD_SET_USECMDS
			+ "]: " + ChatColor.RESET + "This command, used with arguments, permit to change plugin parameters.";

	/**
	 * CMD /tm help [cmd]
	 */
	public static boolean cmdHelp(CommandSender sender, String[] args) {
		
		int argsNb = args.length;
		String specificCmdMsg = "";
		
		// /tm help set [arg]
		if (argsNb >= 3) {
			if (args[1].equalsIgnoreCase(CMD_SET)) {
				String subCmd = args[2];
				switch (subCmd) {
				// /tm help set date
				case CMD_SET_DATE :
					specificCmdMsg = setDateHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_DEBUG : // /tm help set debugMode
					specificCmdMsg = setDebugHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_DEFLANG : // /tm help set defLang
					specificCmdMsg = setDefLangHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_E_DAYS : // /tm help set elapsedDays
					specificCmdMsg = setE_DaysHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_FIRSTSTARTTIME : // /tm help set firstStartTime 
					specificCmdMsg = setFirstStartTimeHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_INITIALTICK : // /tm help set initialTick
					specificCmdMsg = setInitialTickHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_MULTILANG : // /tm help set multiLang
					specificCmdMsg = setMultilangHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_PLAYEROFFSET : // /tm help set playerOffset
					specificCmdMsg = setPlayerOffsetHelpMsg; // Help msg (in case of 2 args)
					break;				
				case CMD_SET_PLAYERTIME : // /tm help set playerTime
					specificCmdMsg = setPlayerTimeHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_REFRESHRATE : // /tm help set refreshRate
					specificCmdMsg = setRefreshRateHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_SLEEP : // /tm help set sleep
					specificCmdMsg = setSleepHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_SPEED : // /tm help set speed
					specificCmdMsg = setSpeedHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_D_SPEED : // /tm help set speedDay
				case CMD_SET_N_SPEED : // /tm help set speedNight
					specificCmdMsg = setSpeed_D_N_HelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_START : // /tm help set start
					specificCmdMsg = setStartHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_SYNC : // /tm help set sync
					specificCmdMsg = setSyncHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_TIME : // /tm help set time
					specificCmdMsg = setTimeHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_UPDATE : // /tm help set updateMsgSrc
					if (MainTM.serverMcVersion >= MainTM.reqMcVForUpdate)
						specificCmdMsg = setupdateSrcHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_USECMDS : // /tm help set time
					specificCmdMsg = setUseCmdsHelpMsg; // Help msg (in case of 2 args)
					break;
				}
			}
		// /tm help [arg]
		} else if (argsNb >= 2) {
			String subCmd = args[1];
			switch (subCmd) {
			case CMD_CHECKCONFIG : // /tm help checkconfig
				specificCmdMsg = checkconfigHelpMsg; // Help msg (in case of 1 arg)
				break;
			case CMD_CHECKSQL : // /tm help checksql
				specificCmdMsg = checkSqlHelpMsg; // Help msg (in case of 1 arg)
				break;
			case CMD_CHECKTIME : // /tm help checktime
				specificCmdMsg = checktimeHelpMsg; // Help msg (in case of 1 arg)
				break;
			case CMD_CHECKUPDATE : // /tm help checkupdate
				if (MainTM.serverMcVersion >= MainTM.reqMcVForUpdate)
					specificCmdMsg = checkupdateHelpMsg; // Help msg (in case of 1 arg)
				break;
			case CMD_TMNOW : // /tm help now
				specificCmdMsg = tmNowHelpMsg; // Help msg (in case of 1 arg)
				break;
			case CMD_RELOAD : // // /tm help reload
				specificCmdMsg = reloadHelpMsg; // Help msg (in case of 1 arg)
				break;
			case CMD_RESYNC : // // /tm help resync
				specificCmdMsg = resyncHelpMsg; // Help msg (in case of 1 arg)
				break;
			case CMD_SET : // // /tm help set <null>
				specificCmdMsg = missingSetArgHelpMsg; // Help msg (in case of 1 arg)
				break;
			// Maybe someone could forget the 'set' part, so think of its place
			case CMD_SET_DATE :
			case CMD_SET_DEBUG :
			case CMD_SET_DEFLANG :
			case CMD_SET_E_DAYS :
			case CMD_SET_FIRSTSTARTTIME :
			case CMD_SET_INITIALTICK :
			case CMD_SET_MULTILANG :
			case CMD_SET_PLAYERTIME :
			case CMD_SET_REFRESHRATE :
			case CMD_SET_SLEEP :
			case CMD_SET_SPEED :
			case CMD_SET_D_SPEED :
			case CMD_SET_N_SPEED :
			case CMD_SET_START :
			case CMD_SET_SYNC :
			case CMD_SET_TIME :
			case CMD_SET_UPDATE :
			case CMD_SET_USECMDS :
				Bukkit.dispatchCommand(sender, CMD_TM + " " + CMD_HELP + " " + CMD_SET + " " + subCmd); // retry with correct arguments
				return true;
			}
		}
		// Display Help header
		sender.sendMessage(headerHelp); // Final msg (always)
		// Display specific cmd msg
		if (!specificCmdMsg.equalsIgnoreCase("") && !specificCmdMsg.equalsIgnoreCase(" ")) {
			sender.sendMessage(specificCmdMsg);
			return true;
		}
		// Else, display basic help msg and the list of cmds from plugin.yml
		else {
			sender.sendMessage(helpHelpMsg); // Final msg (always)
			return false;
		}
	}

};