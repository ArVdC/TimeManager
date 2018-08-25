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
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetDefLang;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetInitialTick;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetMultiLang;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetSync;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetRefreshRate;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetTime;
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
		if(sender instanceof Player) {
			World w = ((Player) sender).getWorld();
			defaultWorld = w.getName();
		}

		if(argsNumb >= 1) {
			// Display a summary of the configuration informations
			if(args[0].equalsIgnoreCase("checkconfig")) {
				TmCheckConfig.cmdCheckConfig(sender);
				return true;
			}
			else if(args[0].equalsIgnoreCase("checksql") || args[0].equalsIgnoreCase("sqlcheck")) { // alias for v1.0 compatibility
			// Try a connection to provided host and display results
				TmCheckSql.cmdSqlcheck(sender);
				return true;
			}
			// Display initial and current server's clock, initial and current server's tick and all worlds initial and current timers.
			else if((args[0].equalsIgnoreCase("checktime") && argsNumb == 1) || (args[0].equalsIgnoreCase("checktimers") && argsNumb == 1) ) { // alias for v1.0 compatibility
				TmCheckTime.cmdCheckTime(sender, "all"); // In case of missing argument, use "all" as default value
				return true;
			}
			// If 'set' is use alone
			else if(args[0].equalsIgnoreCase("set") && argsNumb == 1) {
				TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set"); // Send error and help msg
				return true;
			}
			// Display details about commands use	
			else if(args[0].equalsIgnoreCase("help")) {
				boolean cmdListOnOff = TmHelp.cmdHelp(sender, args);
				return cmdListOnOff;
			}
			// Reload data from yaml file(s)
			else if(args[0].equalsIgnoreCase("reload")) {
				if(args.length < 2) {
					TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "reload"); // Send error and help msg
					return true;
				} else {
					TmReload.cmdReload(sender, args[1]);
					return true;
				}
			}
			// Synchronize all worlds timers based on server initial time
			else if(args[0].equalsIgnoreCase("resync")) {
				if(args.length < 2) {
					if(sender instanceof Player) {
						TmResync.cmdResync(sender, defaultWorld);
						return true;
					}
					TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "resync"); // Send error and help msg
					return true;
				} else {
					// Concatenate world argument
					int leftArgsCount = args.length - 2; // Count extra arguments						
					int currentArgNb = args.length - 1; // Stock the highest argument number
					String concatWorldArgs = args[currentArgNb];
					while(leftArgsCount-- > 0) { // Loop arguments, beginning with the last one
						--currentArgNb;
						concatWorldArgs = (args[currentArgNb] + " " + concatWorldArgs);
					}
					TmResync.cmdResync(sender, concatWorldArgs);
					return true;
				}
			}
		}
		if(argsNumb >= 2) {
			// Display initial and current server's clock, initial and current server's tick and all worlds initial and current timers.
			if(args[0].equalsIgnoreCase("checktime")) {
				TmCheckTime.cmdCheckTime(sender, args[1]);
				return true;
			}
			if(args[0].equalsIgnoreCase("set")) {
				// Enable or disable the console colored verbose messages
				if(args[1].equalsIgnoreCase("debugmode")) {
					if(args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set debugmode"); // Send error and help msg
						return true;
					} else {
						TmSetDebugMode.cmdDebugMode(sender, args[2]);
						return true;
					}
				}	
				// Set the default language to use in case the asked locale doesn't exist
				else if(args[1].equalsIgnoreCase("deflang")) {
					if(args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set deflang"); // Send error and help msg
						return true;
					} else {
						TmSetDefLang.cmdDefLg(sender, args[2]);
						return true;
					}
				}
				// Modify the initial tick
				else if(args[1].equalsIgnoreCase("initialtick")) {
					if(args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set initialtick"); // Send error and help msg
						return true;
					} else {
						try {
							long newTick = Long.parseLong(args[2]);	
							TmSetInitialTick.cmdInitTick(sender, newTick);
							return true;
						} catch (NumberFormatException nfe) {		
							TmHelp.sendErrorMsg(sender, MainTM.tickNotNbMsg, "set initialtick"); // Send error and help msg
							return true;
						}
					}
				}
				// Set the auto-translation on/off
				else if(args[1].equalsIgnoreCase("multilang")) {
					if(args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set multilang"); // Send error and help msg
						return true;
					} else {
						TmSetMultiLang.cmdMultiLg(sender, args[2]);
						return true;
					}
				}
				// Set the refresh rate
				else if(args[1].equalsIgnoreCase("refreshrate")) {
					if(args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set refreshrate"); // Send error and help msg
						return true;
					} else {
						try {
							int refRate = Integer.parseInt(args[2]);			
							TmSetRefreshRate.cmdRefRate(sender, refRate);
							return true;
						} catch (NumberFormatException nfe) {
							TmHelp.sendErrorMsg(sender, MainTM.rateNotNbMsg, "set refreshrate"); // Send error and help msg
							return true;
						}
					}
				}
			}
		}
		String concatWorldArgs = "";
		if(argsNumb == 3) {
			if(sender instanceof Player) concatWorldArgs = defaultWorld;
		}
		if(argsNumb >= 4) {
			// Concatenate world argument
			int leftArgsCount = args.length - 4; // Count extra arguments						
			int currentArgNb = args.length - 1; // Stock the highest argument number
			concatWorldArgs = args[currentArgNb];
			while(leftArgsCount-- > 0) { // Loop arguments, beginning with the last one
				--currentArgNb;
				concatWorldArgs = (args[currentArgNb] + " " + concatWorldArgs);
			}
		}
		if(argsNumb >= 3) {
			if(args[0].equalsIgnoreCase("set")) {
				// Set the sleeping possibility for a world
				if(args[1].equalsIgnoreCase("sleep") || args[1].equalsIgnoreCase("sleepUntilDawn")) { // alias for v1.0 compatibility
					if(args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set sleep"); // Send error and help msg
						return true;
					} else {
						String sleepOrNo = args[2];
						TmSetSleep.cmdSetSleep(sender, sleepOrNo, concatWorldArgs);
						return true;
					}
				}
				// Set the speed modifier for a world
				else if(args[1].equalsIgnoreCase("speed")) {
					if(args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set speed"); // Send error and help msg
						return true;
					} else {
						double speedModif;
						if(args[2].equalsIgnoreCase("realtime")) {
							speedModif = MainTM.realtimeSpeed;			
							TmSetSpeed.cmdSetSpeed(sender, speedModif, concatWorldArgs);
							return true;		
						} else try {
							speedModif = Double.parseDouble(args[2]);			
							TmSetSpeed.cmdSetSpeed(sender, speedModif, concatWorldArgs);
							return true;			
						} catch (NumberFormatException nfe) {		
							TmHelp.sendErrorMsg(sender, MainTM.speedNotNbMsg, "set speed"); // Send error and help msg
							return true;
						}
					}
				}
				// Set the start time for a world
				else if(args[1].equalsIgnoreCase("start")) {
					if(args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set start"); // Send error and help msg
						return true;
					} else {
						String timeToSet = ValuesConverter.returnTimeFromString(args[2]);
						try {
							long tickStart = Long.parseLong(timeToSet);	
							TmSetStart.cmdSetStart(sender, tickStart, concatWorldArgs);
							return true;
						} catch (NumberFormatException nfe) {		
							TmHelp.sendErrorMsg(sender, MainTM.tickNotNbMsg, "set start"); // Send error and help msg
							return true;
						}
					}
				}
				// Set the permanent synchronization of a world
				else if(args[1].equalsIgnoreCase("sync") || args[1].equalsIgnoreCase("synchro")) { // alias for commodity
					if(args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set sync"); // Send error and help msg
						return true;
					} else {
						String syncOrNo = args[2];	
						TmSetSync.cmdSetSync(sender, syncOrNo, concatWorldArgs);
						return true;
					}
				}
				// Set the current time for a world
				else if(args[1].equalsIgnoreCase("time")) {
					if(args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set time"); // Send error and help msg
						return true;
					} else {
						String timeToSet = ValuesConverter.returnTimeFromString(args[2]);
						try {
							long tickCurrent = Long.parseLong(timeToSet);			
							TmSetTime.cmdSetTime(sender, tickCurrent, concatWorldArgs);
							return true;			
						} catch (NumberFormatException nfe) {		
							TmHelp.sendErrorMsg(sender, MainTM.tickNotNbMsg, "set time"); // Send error and help msg
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