package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SyncHandler;

public class TmResync extends MainTM {

	/**
	 * CMD /tm resync [all|world]
	 */
	public static void cmdResync(CommandSender sender, String world) {
		
		// Re-synchronize all worlds
		if (world.equalsIgnoreCase("all")) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdResync(sender, listedWorld);
			}
		}		
		// Else, if the string argument is a listed world, re-synchronize a single world
		else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			// Do the synchronization
			SyncHandler.worldSync(sender, world);
		}		
		// Else, return an error and display help message
		else {
			MsgHandler.cmdErrorMsg(sender, MainTM.wrongWorldMsg, MainTM.CMD_SET + " " + CMD_RESYNC);
		}
	}

};