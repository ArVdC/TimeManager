/***********************
**** ADMIN COMMANDS ****
***********************/

package net.vdcraft.arvdc.timemanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.cmdadmin.TmHelp;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmReload;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmResync;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmServTime;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetDefLang;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetMultiLang;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetRefreshRate;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetSpeed;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmSetStart;
import net.vdcraft.arvdc.timemanager.mainclass.RestrainValuesHandler;

public class AdminCmdExecutor implements CommandExecutor {
	
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {	
	    MainTM.getInstance();	
		// Count # of arguments
		int argsNumb = args.length;

		if(argsNumb >= 1) {
			// Display initial and current server's clock, initial and current server's tick and all worlds initial and current timers.
			if(args[0].equalsIgnoreCase("servtime")) {
				TmServTime.cmdServerTime(sender);
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
					TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "resync"); // Send error and help msg
					return true;
				} else {
					TmResync.cmdResync(sender, args[1]);
					return true;
				}
			}
		}
		if(argsNumb >= 2) {
			if(args[0].equalsIgnoreCase("set")) {
				// Set the default language to use in case the asked locale doesn't exist
				if(args[1].equalsIgnoreCase("deflang"))	{
					if(args.length < 3) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set deflang"); // Send error and help msg
						return true;
					} else {
						TmSetDefLang.cmdDefLg(sender, args[2]);
						return true;
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
		if(argsNumb >= 3) {
			if(args[0].equalsIgnoreCase("set")) {	
				// Set the speed modifier for a world
				if(args[1].equalsIgnoreCase("speed")) {
					if(args.length < 4) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set speed"); // Send error and help msg
						return true;
					} else {
						try {
							double speedModif = Double.parseDouble(args[2]);			
							TmSetSpeed.cmdSetSpeed(sender, speedModif, args[3]);
							return true;			
						} catch (NumberFormatException nfe) {		
							TmHelp.sendErrorMsg(sender, MainTM.speedNotNbMsg, "set speed"); // Send error and help msg
							return true;
						}
					}
				}
				// Set the start time for a world
				else if(args[1].equalsIgnoreCase("start")) {
					if(args.length < 4) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set start"); // Send error and help msg
						return true;
					} else {
						String timeToSet = RestrainValuesHandler.returnTimeFromString(args[2]);
						try {
							long tickStart = Long.parseLong(timeToSet);	
							TmSetStart.cmdSetStart(sender, tickStart, args[3]);
							return true;
						} catch (NumberFormatException nfe) {		
							TmHelp.sendErrorMsg(sender, MainTM.tickNotNbMsg, "set start"); // Send error and help msg
							return true;
						}
					}
				}
				// Set the current time for a world
				else if(args[1].equalsIgnoreCase("time")) {
					if(args.length < 4) {
						TmHelp.sendErrorMsg(sender, MainTM.missingArgMsg, "set time"); // Send error and help msg
						return true;
					} else {
						String timeToSet = RestrainValuesHandler.returnTimeFromString(args[2]);
						try {
							long tickCurrent = Long.parseLong(timeToSet);			
							net.vdcraft.arvdc.timemanager.cmdadmin.TmSetTime.cmdSetTime(sender, tickCurrent, args[3]);
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
		return false;
	};
	
}