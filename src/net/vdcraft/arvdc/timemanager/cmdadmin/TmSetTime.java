package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
import net.vdcraft.arvdc.timemanager.mainclass.DoDaylightCycleHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SpeedHandler;

public class TmSetTime extends MainTM {

	/**
	 * CMD /tm set time [tick|daypart|HH:mm:ss] [world]
	 */
	public static void cmdSetTime(CommandSender sender, long tick, String world) {
		
		// Adapt wrong values in the arg
		tick = ValuesConverter.correctDailyTicks(tick);

		// Modify all worlds
		if (world.equalsIgnoreCase(ARG_ALL)) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdSetTime(sender, tick, listedWorld);
			}
		}
		// Else, if the string argument is a listed world, modify a single world
		else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			World w = Bukkit.getWorld(world);
			// Avoid real time worlds
			long s = MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(w.getTime()));
			if (s == 24) {
				MsgHandler.infoMsg("The time of the world " + world + " " + worldRealSyncTimeChgMsg); // Console warn msg (always)
				MsgHandler.playerAdminMsg(sender, "The time of the world " + world + " " + worldRealSyncTimeChgMsg); // Player warn msg (in case)
			// Check if sync is activated for this world if true, calculate an equivalent "start" value
			} else if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SYNC).equalsIgnoreCase(ARG_TRUE)) {
				long oldStartNb = (MainTM.getInstance().getConfig().getLong(CF_WORLDSLIST + "." + world + "." + CF_START));
				long currentTime = w.getTime();
				long startTickToSet = (ValuesConverter.correctDailyTicks(oldStartNb + tick - currentTime));
				// Debug messages
				MsgHandler.debugMsg("SetTime >>> SetStart: Calculation of " + worldStartAtVar + " for world §e" + world + "§b:");
				MsgHandler.debugMsg(worldStartAtCalculation + " = §3" + oldStartNb + "§b + §8" + tick + "§b - §c" + currentTime + "§b = §e" + (oldStartNb + tick - currentTime) + "§b restrained to one day = §etick #" + startTickToSet);
				// Notifications
				MsgHandler.infoMsg("The world " + world + " " + worldSyncTimeChgMsg); // Console warn msg (always)
				MsgHandler.playerAdminMsg(sender, "The world " + world + " " + worldSyncTimeChgMsg); // Player warn msg (in case)
				// Change the 'start' value
				TmSetStart.cmdSetStart(sender, startTickToSet, world);
			} else { // If false, do the usual time change
				w.setTime(tick);
				// Detect if this world needs to change its speed value
				SpeedHandler.speedScheduler(world);
				// Adjust doDaylightCycle value
				DoDaylightCycleHandler.adjustDaylightCycle(world);
				// Notifications
				String timeToSet = ValuesConverter.formattedTimeFromTick(tick, true);
				MsgHandler.infoMsg(worldTimeChgMsg1 + " " + world + " " + worldTimeChgMsg2 + " tick #" + tick + " (" + timeToSet + ")."); // Console final msg (always)
				MsgHandler.playerAdminMsg(sender, worldTimeChgMsg1 + " §e" + world + "§r " + worldTimeChgMsg2 + " §etick #" + tick + " §r(§e" + timeToSet + "§r)."); // Player final msg (in case)
			}
		}
		// Else, return an error and help message
		else {
			MsgHandler.cmdErrorMsg(sender, MainTM.wrongWorldMsg, MainTM.CMD_SET + " " + CMD_SET_TIME);
		}
	}

};