package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSyncHandler;

public class TmResync extends MainTM {

	/**
	 * CMD /tm resync [all|world]
	 */
	public static void cmdResync(CommandSender sender, String worldToSet) {
		// If using a world name in several parts
		if (sender instanceof Player)
			worldToSet = ValuesConverter.restoreSpacesInString(worldToSet);

		// Re-synchronize all worlds
		if (worldToSet.equalsIgnoreCase("all")) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdResync(sender, listedWorld);
			}
		}
		// Else, if the string argument is a listed world, re-synchronize a single world
		else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(worldToSet)) {
			// Do the synchronization
			WorldSyncHandler.worldResync(sender, worldToSet);
		}
		// Else, return an error and display help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "resync");
		}
	}

};