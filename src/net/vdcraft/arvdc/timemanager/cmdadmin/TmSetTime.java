package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
import net.vdcraft.arvdc.timemanager.mainclass.WorldDoDaylightCycleHandler;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSpeedHandler;

public class TmSetTime extends MainTM {

	/**
	 * CMD /tm set time [tick|daypart|HH:mm:ss] [world]
	 */
	public static void cmdSetTime(CommandSender sender, long tick, String world) {
		// If using a world name in several parts
		if (sender instanceof Player)
			world = ValuesConverter.restoreSpacesInString(world);
		// Adapt wrong values in the arg
		tick = ValuesConverter.correctDailyTicks(tick);

		// Modify all worlds
		if (world.equalsIgnoreCase("all")) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
				cmdSetTime(sender, tick, listedWorld);
			}
		}
		// Else, if the string argument is a listed world, modify a single world
		else if (MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false).contains(world)) {
			World w = Bukkit.getWorld(world);
			// Check if sync is activated for this world if true, calculate an equivalent "start" value
			if (MainTM.getInstance().getConfig().getString("worldsList." + world + ".sync").equalsIgnoreCase("true")) {
				long oldStartNb = (MainTM.getInstance().getConfig().getLong("worldsList." + world + ".start"));
				long currentTime = w.getTime();
				long startTickToSet = (ValuesConverter.correctDailyTicks(oldStartNb + tick - currentTime));
				// Debug messages
				MsgHandler.debugMsg("SetTime >>> SetStart: Calculation of " + worldStartAtVar + " for world §e" + world + "§b:");
				MsgHandler.debugMsg(worldStartAtCalculation + " = §3" + oldStartNb + "§b + §8" + tick + "§b - §c" + currentTime + "§b = §e" + (oldStartNb + tick - currentTime) + "§b restrained to one day = §etick #" + startTickToSet);
				// Warning notifications, config.yml will be changed
				if (MainTM.getInstance().getConfig().getString("worldsList." + world + "." + ValuesConverter.wichSpeedParam(tick)).contains("24")) {
					MsgHandler.infoMsg("The time of the world " + world + " " + worldRealSyncTimeChgMsg); // Console warn msg (always)
					MsgHandler.playerMsg(sender, "The time of the world " + world + " " + worldRealSyncTimeChgMsg); // Player warn msg (in case)
				} else {
					MsgHandler.infoMsg("The world " + world + " " + worldSyncTimeChgMsg); // Console warn msg (always)
					MsgHandler.playerMsg(sender, "The world " + world + " " + worldSyncTimeChgMsg); // Player warn msg (in case)
				}
				// Change the 'start' value and do resync
				TmSetStart.cmdSetStart(sender, startTickToSet, world);

			} else { // If false, do the usual time change
				w.setTime(tick);
				// Detect if this world needs to change its speed value
				WorldSpeedHandler.speedScheduler(world);
				// Adjust doDaylightCycle value
				WorldDoDaylightCycleHandler.adjustDaylightCycle(world); 
			}
			// Notifications
			if (MainTM.getInstance().getConfig().getString("worldsList." + world + ".sync").equalsIgnoreCase("false")) {
				String timeToSet = ValuesConverter.formattedTimeFromTick(tick);
				MsgHandler.infoMsg(worldTimeChgMsg1 + " " + world + " " + worldTimeChgMsg2 + " tick #" + tick + " (" + timeToSet + ")."); // Console final msg (always)
				MsgHandler.playerMsg(sender, worldTimeChgMsg1 + " §e" + world + "§r " + worldTimeChgMsg2 + " §etick #" + tick + " §r(§e" + timeToSet + "§r)."); // Player final msg (in case)
			}
		}
		// Else, return an error and help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "set time");
		}
	}

};