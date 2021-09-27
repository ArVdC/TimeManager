package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;

public class TmHelp extends MainTM {

	/**
	 * Help messages
	 */
	// Always copy this content in the README.md
	private static String headerHelp = "§e---------§r Help: " + prefixTMColor + " §e---------";
	private static String checkconfigHelpMsg = "§6/" + CMD_TM + " " + CMD_CHECKCONFIG
			+ " §rAdmins and console can display a summary of the config.yml and lang.yml files.";
	private static String checkSqlHelpMsg = "§6/" + CMD_TM + " " + CMD_CHECKSQL
			+ " §rCheck the availability of the mySQL server according to the values provided in the config.yml file. This only checks the ip address and the correct port opening.";
	private static String checktimeHelpMsg = "§6/" + CMD_TM + " " + CMD_CHECKTIME
			+ " [all|server|world] §rAdmins and console can display a debug/managing message, who displays the startup server's time, the current server's time and the current time, start time and speed for a specific world (or for all of them).";
	private static String checkupdateHelpMsg = "§6/" + CMD_TM + " " + CMD_CHECKUPDATE
			+ " [bukkit|spigot|github] §rSearch if a newer version of the plugin exists on the chosen server. (MC 1.18.9+ only)";
	private static String helpHelpMsg = "§6/" + CMD_TM + " " + CMD_HELP
			+ " [cmd] [<subCmd>] §rHelp provides you the correct usage and a short description of targeted command or subcommand.";
	private static String tmNowHelpMsg = "§6/" + CMD_TM + " " + CMD_NOW
			+ " [msg|title|actionbar] [all|player|world] §rSend the '/now' (chat, title or action bar) message to a specific player, all players in a specific world, or all online players.";
	private static String reloadHelpMsg = "§6/" + CMD_TM + " " + CMD_RELOAD
			+ " [all|cmds|config|lang] §rThis command allows you to reload datas from yaml files after manual modifications. All timers will be immediately resynchronized.";
	private static String resyncHelpMsg = "§6/" + CMD_TM + " " + CMD_RESYNC
			+ " [all|world] §rThis command will re-synchronize a single or all worlds timers, based on the startup server's time, the elapsed time and the current speed modifier.";
	private static String setDateHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_DATE
			+ " [today|yyyy-mm-dd] [all|world] §rSets current date for the specified world (or all of them). Could be §otoday§r or any yyyy-mm-dd date. The length of the months corresponds to reality, with the exception of February which always lasts 28 days. A year therefore always lasts 365 days.";
	private static String setDebugHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_DEBUG
			+ " [true|false] §rSet true to enable colored verbose messages in the console. Useful to understand some mechanisms of this plugin.";
	private static String setDefLangHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_DEFLANG
			+ " [lg_LG] §rChoose the translation to use if player's locale doesn't exist in the lang.yml or when §o'multiLang'§r is false.";
	private static String setE_DaysHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_E_DAYS
			+ " [0 → ∞] [all|world] §rSets current number of elapsed days for the specified world (or all of them). Could be an integer between §o0§r and §oinfinity§r (or almost). Setting this to §o0§r will bring the world back to day §oone§r.";
	private static String setInitialTickHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_INITIALTICK
			+ " [ticks|HH:mm:ss] §rModify the server's initial tick.";
	private static String setMultilangHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_MULTILANG
			+ " [true|false] §rSet true or false to use an automatic translation for the §o/now §rcommand.";
	private static String setPlayerOffsetHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_PLAYEROFFSET
			+ " [0 → 23999] [player|all] §rDefine a specific offset relative to the world time on player's client (the world speed will be still active). Set to '0' to cancel."; //TODO 1.6.0
	private static String setPlayerTimeHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_PLAYERTIME
			+ " [ticks|daypart|HH:mm:ss|reset] [all|player] §rDefine a specific time on player's client (the world speed will be still active). Use the 'reset' argument to cancel."; //TODO 1.6.0
	private static String setRefreshRateHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_REFRESHRATE
			+ " [ticks] §rSet the delay (in ticks) before actualizing the speed stretch/expand effect. Must be an integer between §o" + refreshMin + "§r and §o" + refreshMax + "§r. Default value is §o" + defRefresh + " ticks§r, please note that a too small value can cause server lags.";
	private static String setSleepHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_SLEEP
			+ " [true|false|linked] [all|world] §rDefine if players can sleep until the next day in the specified world (or in all of them). By default, all worlds will start with parameter true, unless their timer is in real time who will be necessary false. If you want to both allow sleep and keep the same time in multiple worlds, you can use the 'linked' function which allows a group of worlds to spend the night together.";
	private static String setSpeedHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_SPEED
			+ " [multiplier] [all|world] §rThe decimal number argument will multiply the world(s) speed. Use §o0.0§r to freeze time, numbers from §o0.1§r to §o0.9§r to slow time, §o1.0§r to get normal speed and numbers from §o1.1§r to " + speedMax + " to speedup time. Set this value to §o24.0§r or §orealtime§r to make the world time match the real speed time.";
	private static String setSpeed_D_N_HelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_D_SPEED + " §ror §6/"
			+ CMD_TM + " " + CMD_SET + " " + CMD_SET_N_SPEED
			+ " [multiplier] [all|world] §rFrom §o0.0§r to §o10.0§r, the values of daySpeed and nightSpeed can be different from each other.";
	private static String setStartHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_START
			+ " [ticks|daypart|HH:mm:ss|timeShift] [all|world] §rDefines the time at server startup for the specified world (or all of them). By default, all worlds will start at §otick #0§r. The timer(s) will be immediately resynchronized. If a world is using the real time speed, the start value will determine the UTC time shift and values like +1 or -1 will be accepted.";
	private static String setSyncHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_SYNC
			+ " [true|false] [all|world] §rDefine if the speed distortion method will increase/decrease the world's actual tick, or fit the theoretical tick value based on the server one. By default, all worlds will start with parameter false. Real time based worlds and frozen worlds do not use this option, on the other hand this will affect even the worlds with a normal speed.";
	private static String setTimeHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_TIME
			+ " [ticks|daypart|HH:mm:ss] [all|world] §rSets current time for the specified world (or all of them). Consider using this instead of the vanilla §o/time§r command. The tab completion also provides handy presets like \"day\", \"noon\", \"night\", \"midnight\", etc.";
	private static String setupdateSrcHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_UPDATE
			+ " [bukkit|spigot|github] §rDefine the source server for the update search. (MC 1.8.8+ only)";
	private static String setUseCmdsHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_USECMDS
			+ " [true|false] §rSet true to enable a custom commands scheduler. See the " + CMDSFILENAME + " file for details.";
	// Except this line, used when 'set' is used without additional argument
	private static String missingSetArgHelpMsg = "§6/" + CMD_TM + " " + CMD_SET + " ["
			+ CMD_SET_DATE + "|"
			+ CMD_SET_DEBUG + "|"
			+ CMD_SET_DEFLANG + "|"
			+ CMD_SET_E_DAYS + "|"
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
			+ "]: §rThis command, used with arguments, permit to change plugin parameters.";

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