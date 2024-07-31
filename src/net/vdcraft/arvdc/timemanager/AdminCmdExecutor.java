/***********************
 **** ADMIN COMMANDS ****
 ***********************/

package net.vdcraft.arvdc.timemanager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.cmdadmin.TmHelp;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmNow;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmCheckConfig;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmCheckSql;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmCheckTime;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmCheckUpdate;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmReload;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmResync;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetDebugMode;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetDefLang;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetDuration;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetFirstStartTime;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetInitialTick;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetMultiLang;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetPlayerOffset;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetPlayerTime;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetRefreshRate;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetFullTime;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetSpeed;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetStart;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetSleep;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetSync;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetTime;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetUpdateMsgSrc;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetUseCmds;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class AdminCmdExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		MainTM.getInstance();

		// #1. If sender is a Player, check if he has permission to use the /tm command, or return false
		if (sender instanceof Player && !sender.hasPermission("timemanager.admin")) {
			return false;
		}
		
		// #2. Count # of arguments
		int nbArgs = args.length;

		// #3. Names with spaces : Hack it only for players and commandblocks (Useless since MC 1.13)
		if (MainTM.serverMcVersion < MainTM.maxMcVForTabCompHack) {
			if ((sender instanceof Player) || (sender instanceof BlockCommandSender)) {
				int nb = nbArgs - 1 ;
				while (nb >= 0) {
					args[nb] = CreateSentenceCommand.restoreSpacesInString(args[nb]);
					nb--;
				}
			}
		}
		
		// # 4. Names with backslash before space
		if (sender instanceof ConsoleCommandSender) {
			int nb = nbArgs - 1 ;
			while (nb >= 0) {
				args[nb] = args[nb].replace("\\", "");
				nb--;
			}
		}
		
		// # 5. Send dev msg 
		MsgHandler.devMsg("Command §e/" + label + "§9 launched with §e" + nbArgs + "§9 arguments :"); // Console dev msg
		int n = 0;
		while (n < nbArgs) MsgHandler.devMsg("[" + (n) + "] : §e" + args[n++]); // Console dev msg

		// #6. Create a default world value in case of missing argument
		String defaultWorld = "world";
		if ((sender instanceof Player) || (sender instanceof BlockCommandSender)) {
			if (sender instanceof Player) {
				World w = ((Player) sender).getWorld();
				defaultWorld = w.getName();
			} else {
				World w = ((BlockCommandSender) sender).getBlock().getWorld();
				defaultWorld = w.getName();
			}
		}

		// #7. Try to launch command
		if (nbArgs >= 1) {
			// Display a summary of the configuration informations
			if (args[0].equalsIgnoreCase(MainTM.CMD_CHECKCONFIG)) {
				TmCheckConfig.cmdCheckConfig(sender);
				return true;
			} else if (args[0].equalsIgnoreCase(MainTM.CMD_CHECKSQL)) {
				// Try a connection to provided host and display results
				TmCheckSql.cmdSqlcheck(sender);
				return true;
			}
			// Display initial and current server's clock, initial and current server's tick and all worlds initial and current timers.
			else if ((args[0].equalsIgnoreCase(MainTM.CMD_CHECKTIME) && nbArgs == 1)) {
				TmCheckTime.cmdCheckTime(sender, "all"); // In case of missing argument, use "all" as default value
				return true;
			}
			// Check for an update on configured server
			else if ((args[0].equalsIgnoreCase(MainTM.CMD_CHECKUPDATE)) 
					&& (MainTM.serverMcVersion >= MainTM.reqMcVForUpdate)) {
				if (nbArgs < 2) {
					TmCheckUpdate.cmdCheckUpdate(sender);
					return true;
				} else {
					TmCheckUpdate.cmdCheckUpdate(sender, args[1]);
					return true;
				}
			}
			// If 'set' is used alone
			else if (args[0].equalsIgnoreCase(MainTM.CMD_SET) && nbArgs == 1) {
				MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET); // Send error and help msg
				return true;
			}
			// Display details about commands use
			else if (args[0].equalsIgnoreCase(MainTM.CMD_HELP)) {
				boolean cmdListOnOff = TmHelp.cmdHelp(sender, args);
				return cmdListOnOff;
			}
			// Reload data from yaml file(s)
			else if (args[0].equalsIgnoreCase(MainTM.CMD_RELOAD)) {
				if (nbArgs < 2) {
					TmReload.cmdReload(sender, "all");
					return true;
				} else {
					TmReload.cmdReload(sender, args[1]);
					return true;
				}
			}
			// Synchronize all worlds timers based on server initial time
			else if (args[0].equalsIgnoreCase(MainTM.CMD_RESYNC)) {
				if (nbArgs < 2) {
					if (sender instanceof Player) {
						TmResync.cmdResync(sender, defaultWorld);
						return true;
					}
					MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_RESYNC); // Send error and help msg
					return true;
				} else if (nbArgs == 2) {
					TmResync.cmdResync(sender, args[1]);
					return true;
				} else {
					String concatWorldName = ValuesConverter.concatenateNameWithSpaces(sender, args, 1);
					TmResync.cmdResync(sender, concatWorldName);
					return true;
				}
			}
			// Display initial and current server's clock, initial and current server's tick and all worlds initial and current timers.
			else if (args[0].equalsIgnoreCase(MainTM.CMD_CHECKTIME)) {
				if (nbArgs < 2) {
					if (sender instanceof Player) {
						TmCheckTime.cmdCheckTime(sender, defaultWorld);
						return true;
					}
					MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_CHECKTIME); // Send error and help msg
					return true;
				} else if (nbArgs == 2) {
					TmCheckTime.cmdCheckTime(sender, args[1]);
					return true;
				} else {
					String concatWorldName = ValuesConverter.concatenateNameWithSpaces(sender, args, 1);
					TmCheckTime.cmdCheckTime(sender, concatWorldName);
					return true;
				}
			}
		}
		if (nbArgs >= 2) {
			// Display custom messages to one or many player(s) at different display places
			if (args[0].equalsIgnoreCase(MainTM.CMD_TMNOW)) {
				if (nbArgs < 3) {
					MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_TMNOW); // Send error and help msg
					return true;
				} else if (nbArgs == 3) {
					TmNow.cmdNow(sender, args[1], args[2]);
					return true;
				} else {
					String concatWorldName = ValuesConverter.concatenateNameWithSpaces(sender, args, 2);
					TmNow.cmdNow(sender, args[1], concatWorldName);
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase(MainTM.CMD_SET)) {
				// Enable or disable the console colored verbose messages
				if (args[1].equalsIgnoreCase(MainTM.CMD_SET_DEBUG)) {
					if (nbArgs < 3) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_DEBUG); // Send error and help msg
						return true;
					} else {
						TmSetDebugMode.cmdDebugMode(sender, args[2]);
						return true;
					}
				}
				// Enable or disable the console colored verbose messages
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_DEV)) {
					if (nbArgs < 3) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_DEV); // Send error and help msg
						return true;
					} else {
						TmSetDebugMode.cmdDevMode(sender, args[2]);
						return true;
					}
				}
				// Enable or disable the console colored messages for time calculation
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_TIMER)) {
					if (nbArgs < 3) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_TIMER); // Send error and help msg
						return true;
					} else {
						TmSetDebugMode.cmdTimerMode(sender, args[2]);
						return true;
					}
				}
				// Set the default language to use, in case the asked locale doesn't exist
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_DEFLANG)) {
					if (nbArgs < 3) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_DEFLANG); // Send error and help msg
						return true;
					} else {
						TmSetDefLang.cmdDefLg(sender, args[2]);
						return true;
					}
				}
				// Set the initial tick
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_INITIALTICK)) {
					if (nbArgs < 3) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_INITIALTICK); // Send error and help msg
						return true;
					} else {
						String tickString = args[2];
						Long tickToSet;
						if (!args[2].contains(":")) {
							tickToSet = ValuesConverter.tickFromString(tickString); // Check if the value is a part of the day
						} else {
							tickToSet = ValuesConverter.tickFromServerTime(tickString); // Check if the value have an HH:mm:ss format
						}
						TmSetInitialTick.cmdInitTick(sender, tickToSet);
						return true;
					}
				}
				// Set the auto-translation on/off
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_MULTILANG)) {
					if (nbArgs < 3) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_MULTILANG); // Send error and help msg
						return true;
					} else {
						TmSetMultiLang.cmdMultiLg(sender, args[2]);
						return true;
					}
				}
				// Set the refresh rate
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_REFRESHRATE)) {
					if (nbArgs < 3) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_REFRESHRATE); // Send error and help msg
						return true;
					} else {
						try {
							int refRate = Integer.parseInt(args[2]);
							TmSetRefreshRate.cmdRefRate(sender, refRate);
							return true;
						} catch (NumberFormatException nfe) {
							MsgHandler.cmdErrorMsg(sender, MainTM.rateFormatMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_REFRESHRATE); // Send error and help msg
							return true;
						}
					}
				}
				// Set the source server of the update message
				else if ((args[1].equalsIgnoreCase(MainTM.CMD_SET_UPDATE))
						&& (MainTM.serverMcVersion >= MainTM.reqMcVForUpdate)) {
					if (nbArgs < 3) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_UPDATE); // Send error and help msg
						return true;
					} else {
						String source = args[2];		
						TmSetUpdateMsgSrc.cmdSetUpdateSrc(sender, source);
						return true;
					}
				}
				// Set the command scheduler on/off
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_USECMDS)) {
					if (nbArgs < 3) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_USECMDS); // Send error and help msg
						return true;
					} else {
						String onOff = args[2];		
						TmSetUseCmds.cmdUseCmds(sender, onOff);
						return true;
					}
				}
			}
		}
		// Set time offset for player(s)
		if (nbArgs >= 2) {
			if (args[1].equalsIgnoreCase(MainTM.CMD_SET_PLAYEROFFSET)) {
				if (((nbArgs < 4) && !(sender instanceof Player)) || ((nbArgs < 3) && (sender instanceof Player))) {
					MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_PLAYEROFFSET); // Send error and help msg
					return true;
				} else {
					// Get the tick arg
					String tickString = args[2];
					Long tickLong;
					try {
						tickLong = Long.parseLong(tickString); // Check if the value is a long
					} catch (NumberFormatException nfe) {
						MsgHandler.cmdErrorMsg(sender, MainTM.offsetTickMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_PLAYEROFFSET); // Send error and help msg
						return true;
					}
					// Get the player arg
					String player;
					if ((sender instanceof Player) && (nbArgs == 3)) {
						player = sender.getName();
					} else {
						player = args[3];
					}
					// Send the command
					TmSetPlayerOffset.cmdSetPlayerOffset(sender, tickLong, player, false);
					return true;
				}
			}
			// Set current time for player(s)
			else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_PLAYERTIME)) {
				if (((nbArgs < 4) && !(sender instanceof Player)) || ((nbArgs < 3) && (sender instanceof Player))) {
					MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_PLAYERTIME); // Send error and help msg
					return true;
				} else {
					// Get the tick arg
					String tickString = args[2];
					Long tickLong;
					boolean reset = false;
					if (tickString.equalsIgnoreCase(MainTM.ARG_RESET)) { // Check if the value is 'reset'
						tickLong = 0L;
						reset = true;
					} else if (!tickString.contains(":")) {
						tickLong = ValuesConverter.tickFromString(tickString); // Check if the value is a part of the day or a number
					} else {
						tickLong = ValuesConverter.tickFromFormattedTime(tickString); // Check if the value have an HH:mm:ss format
					}
					tickLong = ValuesConverter.correctDailyTicks(tickLong);
					// Get the player arg
					String player;
					if ((sender instanceof Player) && (nbArgs == 3)) {
						player = sender.getName();
					} else {
						player = args[3];
					}
					// Send the command
					TmSetPlayerTime.cmdSetPlayerTime(sender, tickLong, player, reset);
					return true;
				}
			}
		}
		String concatWorldName = null;
		if (nbArgs == 3) {
			if ((sender instanceof Player) || (sender instanceof BlockCommandSender))
				concatWorldName = defaultWorld;
				MsgHandler.devMsg("Default world will be used, in absence of argument : §e" + concatWorldName);
		} else if (nbArgs >= 4) {
			concatWorldName = ValuesConverter.concatenateNameWithSpaces(sender, args, 3);
		}
		if (nbArgs >= 2) {
			if (args[0].equalsIgnoreCase(MainTM.CMD_SET)) {
				// Set the first start time for a world
				if (args[1].equalsIgnoreCase(MainTM.CMD_SET_FIRSTSTARTTIME)) {
					if (((nbArgs < 4) && (sender instanceof ConsoleCommandSender)) || ((nbArgs < 3) && ((sender instanceof Player) || (sender instanceof BlockCommandSender)))) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_FIRSTSTARTTIME); // Send error and help msg
						return true;
					} else {
						String firstStartTime = args[2];
						TmSetFirstStartTime.cmdSetFirstStartTime(sender, firstStartTime, concatWorldName);
						return true;
					}
				}
				// Set the duration for a world
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_DURATION) || args[1].equalsIgnoreCase(MainTM.CMD_SET_D_DURATION) || args[1].equalsIgnoreCase(MainTM.CMD_SET_N_DURATION)) {
					String when = args[1];
					if (((nbArgs < 4) && (sender instanceof ConsoleCommandSender)) || ((nbArgs < 3) && ((sender instanceof Player) || (sender instanceof BlockCommandSender)))) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_DURATION); // Send error and help msg
						return true;
					} else {
						String formatedDuration = args[2];
						if (formatedDuration.contains("d") || formatedDuration.contains("h") || formatedDuration.contains("m") || formatedDuration.contains("s")) {
							TmSetDuration.cmdSetDuration(sender, formatedDuration, when, concatWorldName);
							return true;
						} else {
							MsgHandler.cmdErrorMsg(sender, MainTM.durationFormatMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_DURATION); // Send error and help msg
							return true;
						}
					}
				}
				// Set the sleeping possibility for a world
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_SLEEP)) {
					if (((nbArgs < 4) && (sender instanceof ConsoleCommandSender)) || ((nbArgs < 3) && ((sender instanceof Player) || (sender instanceof BlockCommandSender)))) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_SLEEP); // Send error and help msg
						return true;
					} else {
						String sleepOrNo = args[2];
						TmSetSleep.cmdSetSleep(sender, sleepOrNo, concatWorldName);
						return true;
					}
				}
				// Set the speed modifier for a world
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_SPEED) || args[1].equalsIgnoreCase(MainTM.CMD_SET_D_SPEED) || args[1].equalsIgnoreCase(MainTM.CMD_SET_N_SPEED)) {
					String when = args[1];
					if (((nbArgs < 4) && (sender instanceof ConsoleCommandSender)) || ((nbArgs < 3) && ((sender instanceof Player) || (sender instanceof BlockCommandSender)))) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_SPEED); // Send error and help msg
						return true;
					} else {
						double speedModif;
						if (args[2].equalsIgnoreCase("realtime")) {
							speedModif = MainTM.realtimeSpeed;
							TmSetSpeed.cmdSetSpeed(sender, speedModif, when, concatWorldName);
							return true;
						} else
							try {
								speedModif = Double.parseDouble(args[2]);
								TmSetSpeed.cmdSetSpeed(sender, speedModif, when, concatWorldName);
								return true;
							} catch (NumberFormatException nfe) {
								MsgHandler.cmdErrorMsg(sender, MainTM.speedFormatMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_SPEED); // Send error and help msg
								return true;
							}
					}
				}
				// Set the start time for a world
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_START)) {
					if (((nbArgs < 4) && (sender instanceof ConsoleCommandSender)) || ((nbArgs < 3) && ((sender instanceof Player) || (sender instanceof BlockCommandSender)))) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_START); // Send error and help msg
						return true;
					} else {
						String tickString = args[2];
						Long tickToSet;
						double currentSpeed = MainTM.getInstance().getConfig().getDouble(MainTM.CF_WORLDSLIST + "." + concatWorldName + "." + ValuesConverter.wichSpeedParam(Bukkit.getWorld(concatWorldName).getTime()));
						if (currentSpeed == MainTM.realtimeSpeed) { // If the value is a UTC time shift
							tickToSet = ValuesConverter.getUTCShiftFromTick(ValuesConverter.tickFromString(tickString)) * 1000;
						} else if (!args[2].contains(":")) { // If the value is a part of the day, a number or a UTC formatted value
							tickToSet = ValuesConverter.tickFromString(tickString);
						} else { // If the value have an HH:mm:ss format
							tickToSet = ValuesConverter.tickFromFormattedTime(tickString);
							
						}
						TmSetStart.cmdSetStart(sender, tickToSet, concatWorldName);
						return true;
					}
				}
				// Set the permanent synchronization of a world
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_SYNC)) {
					if (((nbArgs < 4) && (sender instanceof ConsoleCommandSender)) || ((nbArgs < 3) && ((sender instanceof Player) || (sender instanceof BlockCommandSender)))) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_SYNC); // Send error and help msg
						return true;
					} else {						
						String syncOrNo = args[2];
						if (!syncOrNo.equalsIgnoreCase("true") && !syncOrNo.equalsIgnoreCase("false")) {
							MsgHandler.cmdErrorMsg(sender, MainTM.isNotBooleanMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_SYNC); // Send error and help msg
							return true;
						} else {
							TmSetSync.cmdSetSync(sender, syncOrNo, concatWorldName);
							return true;
						}
					}
				}
				// Set the current time for a world
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_TIME)) {
					if (((nbArgs < 4) && (sender instanceof ConsoleCommandSender)) || ((nbArgs < 3) && ((sender instanceof Player) || (sender instanceof BlockCommandSender)))) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_TIME); // Send error and help msg
						return true;
					} else {
						String tickString = args[2];
						Long tickLong;
						if (!tickString.contains(":")) {
							tickLong = ValuesConverter.tickFromString(tickString); // Check if the value is a part of the day or a number
						} else {
							tickLong = ValuesConverter.tickFromFormattedTime(tickString); // Check if the value have an HH:mm:ss format
						}
						TmSetTime.cmdSetTime(sender, tickLong, concatWorldName);
						return true;
					}
				}
				// Set the FullTime value of a world, from a days number
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_E_DAYS)) {
					if (((nbArgs < 4) && (sender instanceof ConsoleCommandSender)) || ((nbArgs < 3) && ((sender instanceof Player) || (sender instanceof BlockCommandSender)))) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_E_DAYS); // Send error and help msg
						return true;
					} else {
						Long elapsedDays;
						try {
							elapsedDays = Long.parseLong(args[2]);
							TmSetFullTime.cmdSetDay(sender, elapsedDays, concatWorldName);
							return true;
						} catch (NumberFormatException nfe) {
							MsgHandler.cmdErrorMsg(sender, MainTM.dayFormatMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_E_DAYS); // Send error and help msg
							return true;
						}
					}
				}
				// Set the FullTime value of a world, from a date
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_DATE)) {
					if (((nbArgs < 4) && (sender instanceof ConsoleCommandSender)) || ((nbArgs < 3) && ((sender instanceof Player) || (sender instanceof BlockCommandSender)))) {
						MsgHandler.cmdErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_DATE); // Send error and help msg
						return true;
					} else {
						String dateString = args[2];
						Long elapsedDays;
						if (dateString.equalsIgnoreCase(MainTM.ARG_TODAY)) {
							elapsedDays = ValuesConverter.daysFromCurrentDate();
							TmSetFullTime.cmdSetDay(sender, elapsedDays, concatWorldName);
							return true;
						} else if (dateString.contains("-")) {
							TmSetFullTime.cmdSetDate(sender, dateString, concatWorldName);
							return true;
						} else {
							MsgHandler.cmdErrorMsg(sender, MainTM.dateFormatMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_DATE); // Send error and help msg
							return true;
						}
					}
				}
			}

		}
		// Else, display basic help menu and commands
		TmHelp.cmdHelp(sender, args);
		return true;
	}

};