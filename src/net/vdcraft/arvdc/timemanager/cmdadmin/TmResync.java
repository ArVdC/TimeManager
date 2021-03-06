package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSyncHandler;

public class TmResync extends MainTM {

	/**
	 * CMD /tm resync [all|world]
	 */
	public static void cmdResync(CommandSender sender, String world) {
		// If using a world name in several parts
		if ((sender instanceof Player) || (sender instanceof BlockCommandSender)) world = ValuesConverter.restoreSpacesInString(world);

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
			WorldSyncHandler.worldSync(sender, world);
		}
		// Else, return an error and display help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, MainTM.CMD_SET + " " + CMD_RESYNC);
		}
	}

};