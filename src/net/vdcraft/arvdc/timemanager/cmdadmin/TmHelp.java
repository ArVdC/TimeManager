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
	private static String headerHelp = ChatColor.YELLOW + "---------" + ChatColor.GRAY + " Help: " + prefixTMColor + ChatColor.YELLOW + " ---------";
	private static String checkconfigHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_CHECKCONFIG
			+ " " + ChatColor.GRAY + "Admins and console can display a summary of the config.yml and lang.yml files.";
	private static String checkSqlHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_CHECKSQL
			+ " " + ChatColor.GRAY + "Check the availability of the mySQL server according to the values provided in the config.yml file. This only checks the ip address and the correct port opening.";
	private static String checktimeHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_CHECKTIME
			+ " [all|server|world] " + ChatColor.GRAY + "Admins and console can display a debug/managing message, who displays the startup server's time, the current server's time and the current time, start time and speed for a specific world (or for all of them).";
	private static String checkupdateHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_CHECKUPDATE
			+ " [bukkit|spigot|github] " + ChatColor.GRAY + "Search if a newer version of the plugin exists on the chosen server. (MC 1.18.9+ only)";
	private static String helpHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_HELP
			+ " [cmd] [<subCmd>] " + ChatColor.GRAY + "Help provides you the correct usage and a short description of targeted command or subcommand.";
	private static String tmNowHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_NOW
			+ " [msg|title|actionbar] [all|player|world] " + ChatColor.GRAY + "Send the '/now' (chat, title or action bar) message to a specific player, all players in a specific world, or all online players.";
	private static String reloadHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_RELOAD
			+ " [all|cmds|config|lang] " + ChatColor.GRAY + "This command allows you to reload datas from yaml files after manual modifications. All timers will be immediately resynchronized.";
	private static String resyncHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_RESYNC
			+ " [all|world] " + ChatColor.GRAY + "This command will re-synchronize a single or all worlds timers, based on the startup server's time, the elapsed time and the current speed modifier.";
	private static String setDateHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_DATE
			+ " [today|yyyy-mm-dd] [all|world] " + ChatColor.GRAY + "Sets current date for the specified world (or all of them). Could be 'today' or any yyyy-mm-dd date. The length of the months corresponds to reality, with the exception of February which always lasts 28 days. A year therefore always lasts 365 days.";
	private static String setDebugHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_DEBUG
			+ " [true|false] " + ChatColor.GRAY + "Set true to enable colored verbose messages in the console. Useful to understand some mechanisms of this plugin.";
	private static String setDefLangHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_DEFLANG
			+ " [lg_LG] " + ChatColor.GRAY + "Choose the translation to use if player's locale doesn't exist in the lang.yml or when 'multiLang' is 'false'.";
	private static String setDurationHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_DURATION
			+ " [00d-00h-00m-00s] [all|world] " + ChatColor.GRAY + "Sets the speed of the world based on the desired duration rather than with a speed multiplier.";
	private static String setDuration_D_N_HelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_D_DURATION + " " + ChatColor.GRAY + "or " + ChatColor.GOLD + CMD_SET_N_DURATION
			+ " [00d-00h-00m-00s] [all|world] " + ChatColor.GRAY + "The length of day and night can be defined separately.";
	private static String setE_DaysHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_E_DAYS
			+ " [0 → ∞] [all|world] " + ChatColor.GRAY + "Sets current number of elapsed days for the specified world (or all of them). Could be an integer between '0' and infinity (or almost). Setting this to '0' will bring the world back to day one.";
	private static String setFirstStartTimeHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_FIRSTSTARTTIME
			+ " [default|previous|start] [all|world] " + ChatColor.GRAY + "Forces the time at which a world starts when starting the server. The value 'default' allows the usual resynchronization at startup. The value 'start' forces the world to start at the time specified in the world's 'start' node. The value 'previous' returns the time in the world before the server was shut down.";
	private static String setInitialTickHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_INITIALTICK
			+ " [ticks|HH:mm:ss] " + ChatColor.GRAY + "Modify the server's initial tick.";
	private static String setMultilangHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_MULTILANG
			+ " [true|false] " + ChatColor.GRAY + "Set true or false to use an automatic translation for the " + ChatColor.ITALIC + "/now" + ChatColor.GRAY + " command.";
	private static String setPlayerOffsetHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_PLAYEROFFSET
			+ " [-23999 → 23999] [all|player] " + ChatColor.GRAY + "Define a specific offset relative to the world time on player's client (the world speed will be still active). Set to '0' to cancel.";
	private static String setPlayerTimeHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_PLAYERTIME
			+ " [ticks|daypart|HH:mm:ss|reset] [all|player] " + ChatColor.GRAY + "Define a specific time on player's client (the world speed will be still active). Use the 'reset' argument to cancel.";
	private static String setRefreshRateHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_REFRESHRATE
			+ " [ticks] " + ChatColor.GRAY + "Set the delay (in ticks) before actualizing the speed stretch/expand effect. Must be an integer between '" + refreshMin + "' and '" + refreshMax + "'. Default value is '" + defRefresh + " ticks', please note that a too small value can cause server lags.";
	private static String setSleepHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_SLEEP
			+ " [true|false|linked] [all|world] " + ChatColor.GRAY + "Define if players can sleep until the next day in the specified world (or in all of them). By default, all worlds will start with parameter true, unless their timer is in real time who will be necessary false. If you want to both allow sleep and keep the same time in multiple worlds, you can use the 'linked' function which allows a group of worlds to spend the night together.";
	private static String setSpeedHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_SPEED
			+ " [0.0 → 20.0] [all|world] " + ChatColor.GRAY + "The decimal number argument will multiply the world(s) speed. Use 0.0 to freeze time, numbers from 0.01 to 0.99 to slow time, 1.0 to get normal speed and numbers from 1.1 to " + speedMax + " to speedup time. Set this value to 24.0 or 'realtime' to make the world time match the real speed time.";
	private static String setSpeed_D_N_HelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_D_SPEED + " " + ChatColor.GRAY + "or " + ChatColor.GOLD + CMD_SET_N_SPEED
			+ " [0.0 → 20.0] [all|world] " + ChatColor.GRAY + "Night and day speeds can be different from each other.";
	private static String setStartHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_START
			+ " [ticks|daypart|HH:mm:ss|timeShift] [all|world] " + ChatColor.GRAY + "Defines the time at server startup for the specified world (or all of them). By default, all worlds will start at tick #0. The timer(s) will be immediately resynchronized. If a world is using the real time speed, the start value will determine the UTC time shift and values like +1 or -1 will be accepted.";
	private static String setSyncHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_SYNC
			+ " [true|false] [all|world] " + ChatColor.GRAY + "Define if the speed distortion method will increase/decrease the world's actual tick, or fit the theoretical tick value based on the server one. By default, all worlds will start with parameter false. Real time based worlds and frozen worlds do not use this option, on the other hand this will affect even the worlds with a normal speed.";
	private static String setTimeHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_TIME
			+ " [ticks|daypart|HH:mm:ss] [all|world] " + ChatColor.GRAY + "Sets current time for the specified world (or all of them). Consider using this instead of the vanilla " + ChatColor.ITALIC + "/time" + ChatColor.GRAY + " command. The tab completion also provides handy presets like \"day\", \"noon\", \"night\", \"midnight\", etc.";
	private static String setupdateSrcHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_UPDATE
			+ " [bukkit|spigot|github] " + ChatColor.GRAY + "Define the source server for the update search. (MC 1.8.8+ only)";
	private static String setUseCmdsHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " " + CMD_SET_USECMDS
			+ " [true|false] " + ChatColor.GRAY + "Set true to enable a custom commands scheduler. See the " + CMDSFILENAME + " file for details.";
	// ── 1.12.2 additions ──
	private static String lockHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_LOCK
			+ " <world> [time|here|realtime] " + ChatColor.GRAY + "Freezes a world at the given time (noon, dawn, dusk, midnight, day, night, raw tick, HH:mm, 'here' for the world's current tick, or 'realtime' for UTC sync).";
	private static String unlockHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_UNLOCK
			+ " <world> " + ChatColor.GRAY + "Removes the lock-time on the world. Admin must restore daySpeed/nightSpeed manually if normal time flow is desired.";
	private static String placeholdersHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_PLACEHOLDERS
			+ " " + ChatColor.GRAY + "Lists every placeholder the plugin exposes (PAPI form %tm_X% and in-message {tm_X} form).";
	private static String hudHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_HUD
			+ " [on|off|toggle] " + ChatColor.GRAY + "Toggle the per-player ActionBar HUD. Requires hud.actionbar.enabled: true in config for the broadcaster to run globally.";
	private static String nowItemHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_NOWITEM
			+ " [player] " + ChatColor.GRAY + "Gives a custom CLOCK 'Pocket Watch' item that runs the /now action on right-click.";
	private static String animationHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_ANIMATION
			+ " <world> [on|off|toggle] " + ChatColor.GRAY + "Shortcut for nightSkipMode = animation/default on a world. Controls the sleep particle show.";
	// Except this line, used when 'set' is used without additional argument
	private static String missingSetArgHelpMsg = ChatColor.GOLD + "/" + CMD_TM + " " + CMD_SET + " ["
			+ CMD_SET_DATE + "|"
			+ CMD_SET_DEBUG + "|"
			+ CMD_SET_DEFLANG + "|"
			+ CMD_SET_DURATION + "|"
			+ CMD_SET_D_DURATION + "|"
			+ CMD_SET_N_DURATION + "|"
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
			+ "]: " + ChatColor.GRAY + "This command, used with arguments, permit to change plugin parameters.";

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
				case CMD_SET_DURATION : // /tm help set duration
					specificCmdMsg = setDurationHelpMsg; // Help msg (in case of 2 args)
					break;
				case CMD_SET_D_DURATION : // /tm help set durationDay
				case CMD_SET_N_DURATION : // /tm help set durationNight
					specificCmdMsg = setDuration_D_N_HelpMsg; // Help msg (in case of 2 args)
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
			// ── 1.12.2 additions ──
			case CMD_LOCK :
				specificCmdMsg = lockHelpMsg;
				break;
			case CMD_UNLOCK :
				specificCmdMsg = unlockHelpMsg;
				break;
			case CMD_PLACEHOLDERS :
				specificCmdMsg = placeholdersHelpMsg;
				break;
			case CMD_HUD :
				specificCmdMsg = hudHelpMsg;
				break;
			case CMD_NOWITEM :
				specificCmdMsg = nowItemHelpMsg;
				break;
			case CMD_ANIMATION :
				specificCmdMsg = animationHelpMsg;
				break;
			// Maybe someone could forget the 'set' part, so think of its place
			case CMD_SET_DATE :
			case CMD_SET_DEBUG :
			case CMD_SET_DEFLANG :
			case CMD_SET_DURATION :
			case CMD_SET_D_DURATION :
			case CMD_SET_N_DURATION :
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
			// Display a colored, structured list of every subcommand instead
			// of falling back to plugin.yml's plain-text usage block.
			sender.sendMessage(helpHelpMsg);
			sender.sendMessage(ChatColor.GRAY + " ");
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Inspection");
			sender.sendMessage(checkconfigHelpMsg);
			sender.sendMessage(checkSqlHelpMsg);
			sender.sendMessage(checktimeHelpMsg);
			if (MainTM.serverMcVersion >= MainTM.reqMcVForUpdate) sender.sendMessage(checkupdateHelpMsg);
			sender.sendMessage(placeholdersHelpMsg);
			sender.sendMessage(ChatColor.GRAY + " ");
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Run-time control");
			sender.sendMessage(tmNowHelpMsg);
			sender.sendMessage(reloadHelpMsg);
			sender.sendMessage(resyncHelpMsg);
			sender.sendMessage(lockHelpMsg);
			sender.sendMessage(unlockHelpMsg);
			sender.sendMessage(animationHelpMsg);
			sender.sendMessage(hudHelpMsg);
			sender.sendMessage(nowItemHelpMsg);
			sender.sendMessage(ChatColor.GRAY + " ");
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Configuration (/tm set ...)");
			sender.sendMessage(missingSetArgHelpMsg);
			sender.sendMessage(ChatColor.GRAY + "Type " + ChatColor.GOLD + "/" + CMD_TM + " " + CMD_HELP
					+ " <cmd>" + ChatColor.GRAY + " for details on a specific command.");
			return true;
		}
	}

};