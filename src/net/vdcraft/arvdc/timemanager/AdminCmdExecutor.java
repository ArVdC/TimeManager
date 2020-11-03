/***********************
 **** ADMIN COMMANDS ****
 ***********************/

package net.vdcraft.arvdc.timemanager;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.cmdadmin.TmHelp;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmReload;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmResync;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetDebugMode;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmCheckTime;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmCheckUpdate;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetDefLang;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetInitialTick;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetMultiLang;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetSync;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetRefreshRate;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetTime;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetUpdateMsgSrc;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetSpeed;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetStart;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetSleep;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmCheckConfig;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmCheckSql;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

public class AdminCmdExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		MainTM.getInstance();
		// Count # of arguments
		int argsNumb = args.length;
		String defaultWorld = "world"; // Create a default world value in case of missing argument
		if (sender instanceof Player) {
			World w = ((Player) sender).getWorld();
			defaultWorld = w.getName();
		}

		if (argsNumb >= 1) {
			// Display a summary of the configuration informations
			if (args[0].equalsIgnoreCase(MainTM.CMD_CHECKCONFIG)) {
				TmCheckConfig.cmdCheckConfig(sender);
				return true;
			} else if (args[0].equalsIgnoreCase(MainTM.CMD_CHECKSQL) || args[0].equalsIgnoreCase("sqlcheck")) { // alias for v1.0 compatibility
				// Try a connection to provided host and display results
				TmCheckSql.cmdSqlcheck(sender);
				return true;
			}
			// Display initial and current server's clock, initial and current server's tick
			// and all worlds initial and current timers.
			else if ((args[0].equalsIgnoreCase(MainTM.CMD_CHECKTIME) && argsNumb == 1) || (args[0].equalsIgnoreCase("checktimers") && argsNumb == 1)) { // alias for v1.0 compatibility
				TmCheckTime.cmdCheckTime(sender, "all"); // In case of missing argument, use "all" as default value
				return true;
			}
			// Check for an update on configured server
			else if ((args[0].equalsIgnoreCase(MainTM.CMD_CHECKUPDATE)) 
					&& (MainTM.decimalOfMcVersion >= MainTM.requiredMcVersionForUpdate)) {
				if (argsNumb < 2) {
					TmCheckUpdate.cmdCheckUpdate(sender);
					return true;
				} else {
					TmCheckUpdate.cmdCheckUpdate(sender, args[1]);
					return true;
				}
			}
			// If 'set' is used alone
			else if (args[0].equalsIgnoreCase(MainTM.CMD_SET) && argsNumb == 1) {
				TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET); // Send error and help msg
				return true;
			}
			// Display details about commands use
			else if (args[0].equalsIgnoreCase(MainTM.CMD_HELP)) {
				boolean cmdListOnOff = TmHelp.cmdHelp(sender, args);
				return cmdListOnOff;
			}
			// Reload data from yaml file(s)
			else if (args[0].equalsIgnoreCase(MainTM.CMD_RELOAD)) {
				if (argsNumb < 2) {
					TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_RELOAD); // Send error and help msg
					return true;
				} else {
					TmReload.cmdReload(sender, args[1]);
					return true;
				}
			}
			// Synchronize all worlds timers based on server initial time
			else if (args[0].equalsIgnoreCase(MainTM.CMD_RESYNC)) {
				if (argsNumb < 2) {
					if (sender instanceof Player) {
						TmResync.cmdResync(sender, defaultWorld);
						return true;
					}
					TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_RESYNC); // Send error and help msg
					return true;
				} else {
					// Concatenate world argument
					int leftArgsCount = argsNumb - 2; // Count extra arguments
					int currentArgNb = argsNumb - 1; // Stock the highest argument number
					String concatWorldArgs = args[currentArgNb];
					while (leftArgsCount-- > 0) { // Loop arguments, beginning with the last one
						--currentArgNb;
						concatWorldArgs = (args[currentArgNb] + " " + concatWorldArgs);
					}
					TmResync.cmdResync(sender, concatWorldArgs);
					return true;
				}
			}
		}
		if (argsNumb >= 2) {
			// Display initial and current server's clock, initial and current server's tick and all worlds initial and current timers.
			if (args[0].equalsIgnoreCase(MainTM.CMD_CHECKTIME)) {
				TmCheckTime.cmdCheckTime(sender, args[1]);
				return true;
			}
			if (args[0].equalsIgnoreCase(MainTM.CMD_SET)) {
				// Enable or disable the console colored verbose messages
				if (args[1].equalsIgnoreCase(MainTM.CMD_SET_DEBUG)) {
					if (argsNumb < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_DEBUG); // Send error and help msg
						return true;
					} else {
						TmSetDebugMode.cmdDebugMode(sender, args[2]);
						return true;
					}
				}
				// Define the default language to use, in case the asked locale doesn't exist
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_DEFLANG)) {
					if (argsNumb < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_DEFLANG); // Send error and help msg
						return true;
					} else {
						TmSetDefLang.cmdDefLg(sender, args[2]);
						return true;
					}
				}
				// Define the initial tick
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_INITIALTICK)) {
					if (argsNumb < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_INITIALTICK); // Send error and help msg
						return true;
					} else {
						String tickString = args[2];
						Long tickToSet;
						if (!args[2].contains(":")) {
							tickString = ValuesConverter.returnTickFromStringValue(tickString); // Check if the value is a part of the day
						} else {
							tickString = ValuesConverter.returnTickFromServerTimeValue(tickString); // Check if the value have an HH:mm:ss format
						}
						try {
							tickToSet = Long.parseLong(tickString);
							TmSetInitialTick.cmdInitTick(sender, tickToSet);
							return true;
						} catch (NumberFormatException nfe) {
							TmHelp.sendErrorMsg(sender, MainTM.tickNotNbMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_INITIALTICK); // Send error and help msg
							return true;
						}
					}
				}
				// Define the auto-translation on/off
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_MULTILANG)) {
					if (argsNumb < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_MULTILANG); // Send error and help msg
						return true;
					} else {
						TmSetMultiLang.cmdMultiLg(sender, args[2]);
						return true;
					}
				}
				// Define the refresh rate
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_REFRESHRATE)) {
					if (argsNumb < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_REFRESHRATE); // Send error and help msg
						return true;
					} else {
						try {
							int refRate = Integer.parseInt(args[2]);
							TmSetRefreshRate.cmdRefRate(sender, refRate);
							return true;
						} catch (NumberFormatException nfe) {
							TmHelp.sendErrorMsg(sender, MainTM.rateNotNbMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_REFRESHRATE); // Send error and help msg
							return true;
						}
					}
				}
				// Define the source server of the update message
				else if ((args[1].equalsIgnoreCase(MainTM.CMD_SET_UPDATE))
						&& (MainTM.decimalOfMcVersion >= MainTM.requiredMcVersionForUpdate)) {
					String source = "false";
					if (argsNumb >= 3) source = args[2];		
					TmSetUpdateMsgSrc.cmdSetUpdateSrc(sender, source);
					return true;
				}
			}
		}
		String concatWorldArgs = "";
		if (argsNumb == 3) {
			if (sender instanceof Player)
				concatWorldArgs = defaultWorld;
		}
		if (argsNumb >= 4) {
			// Concatenate world argument
			int leftArgsCount = args.length - 4; // Count extra arguments
			int currentArgNb = args.length - 1; // Stock the highest argument number
			concatWorldArgs = args[currentArgNb];
			while (leftArgsCount-- > 0) { // Loop arguments, beginning with the last one
				--currentArgNb;
				concatWorldArgs = (args[currentArgNb] + " " + concatWorldArgs);
			}
		}
		if (argsNumb >= 3) {
			if (args[0].equalsIgnoreCase(MainTM.CMD_SET)) {
				// Define the sleeping possibility for a world
				if (args[1].equalsIgnoreCase(MainTM.CMD_SET_SLEEP) || args[1].equalsIgnoreCase("sleepUntilDawn")) { // alias for v1.0 compatibility
					if (args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_SLEEP); // Send error and help msg
						return true;
					} else {
						String sleepOrNo = args[2];
						TmSetSleep.cmdSetSleep(sender, sleepOrNo, concatWorldArgs);
						return true;
					}
				}
				// Define the speed modifier for a world
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_SPEED) || args[1].equalsIgnoreCase(MainTM.CMD_SET_D_SPEED) || args[1].equalsIgnoreCase(MainTM.CMD_SET_N_SPEED)) {
					String when = args[1];
					if (args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_SPEED); // Send error and help msg
						return true;
					} else {
						double speedModif;
						if (args[2].equalsIgnoreCase("realtime")) {
							speedModif = MainTM.realtimeSpeed;
							TmSetSpeed.cmdSetSpeed(sender, speedModif, when, concatWorldArgs);
							return true;
						} else
							try {
								speedModif = Double.parseDouble(args[2]);
								TmSetSpeed.cmdSetSpeed(sender, speedModif, when, concatWorldArgs);
								return true;
							} catch (NumberFormatException nfe) {
								TmHelp.sendErrorMsg(sender, MainTM.speedNotNbMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_SPEED); // Send error and help msg
								return true;
							}
					}
				}
				// Define the start time for a world
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_START)) {
					if (args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_START); // Send error and help msg
						return true;
					} else {
						String tickString = args[2];
						Long tickToSet;
						if (!args[2].contains(":")) {
							tickString = ValuesConverter.returnTickFromStringValue(tickString); // Check if the value is a part of the day
						} else {
							tickString = ValuesConverter.returnTickFromTimeValue(tickString); // Check if the value have an HH:mm:ss format
						}
						try {
							tickToSet = Long.parseLong(tickString);
							TmSetStart.cmdSetStart(sender, tickToSet, concatWorldArgs);
							return true;
						} catch (NumberFormatException nfe) {
							TmHelp.sendErrorMsg(sender, MainTM.tickNotNbMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_START); // Send error and help msg
							return true;
						}
					}
				}
				// Define the permanent synchronization of a world
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_SYNC)) {
					if (args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_SYNC); // Send error and help msg
						return true;
					} else {
						String syncOrNo = args[2];
						TmSetSync.cmdSetSync(sender, syncOrNo, concatWorldArgs);
						return true;
					}
				}
				// Define the current time for a world
				else if (args[1].equalsIgnoreCase(MainTM.CMD_SET_TIME)) {
					if (args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_TIME); // Send error and help msg
						return true;
					} else {
						String tickString = args[2];
						Long tickToSet;
						if (!args[2].contains(":")) {
							tickString = ValuesConverter.returnTickFromStringValue(tickString); // Check if the value is a part of the day
						} else {
							tickString = ValuesConverter.returnTickFromTimeValue(tickString); // Check if the value have an HH:mm:ss format
						}
						try {
							tickToSet = Long.parseLong(tickString);
							TmSetTime.cmdSetTime(sender, tickToSet, concatWorldArgs);
							return true;
						} catch (NumberFormatException nfe) {
							TmHelp.sendErrorMsg(sender, MainTM.tickNotNbMsg, MainTM.CMD_SET + " " + MainTM.CMD_SET_TIME); // Send error and help msg
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