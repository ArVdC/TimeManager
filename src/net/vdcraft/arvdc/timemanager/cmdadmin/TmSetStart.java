package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSyncHandler;

public class TmSetStart extends MainTM {

	/**
	 * CMD /tm set start [tick|daypart|HH:mm:ss] [world]
	 */
	public static void cmdSetStart(CommandSender sender, long tick, String world) {
		// If using a world name in several parts
		if ((sender instanceof Player) || (sender instanceof BlockCommandSender)) world = ValuesConverter.restoreSpacesInString(world);
		// Adapt wrong values in the arg
		tick = ValuesConverter.correctDailyTicks(tick);

		// Modify all worlds
		if (world.equalsIgnoreCase("all")) {
			// Relaunch this for each world
			for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				cmdSetStart(sender, tick, listedWorld);
			}		
		// Else, if the string argument is a listed world, modify a single world
		} else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			// Adapt wrong values
			World w = Bukkit.getWorld(world);
			long t = w.getTime();
			double currentSpeed = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t));
			if (currentSpeed == 24.00) {
				tick = ValuesConverter.formattedUTCFromTick(tick) * 1000;
			} else {
				tick = tick % 24000;
			}
			// Modify and save the start tick in the config.yml
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_START, tick);
			MainTM.getInstance().saveConfig();
			// Resync this world
			WorldSyncHandler.worldSync(sender, world);
			// Notifications
			Bukkit.getLogger().info(prefixTM + " " + worldStartChgMsg1 + " " + world + " " + worldStartChgMsg2); // Console final msg (always)
			MsgHandler.playerMsg(sender, worldStartChgMsg1 + " §e" + world + "§r " + worldStartChgMsg2); // Player final msg (in case)
		}
		// Else, return an error and display help message
		else {
			TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, MainTM.CMD_SET + " " + CMD_SET_START);
		}
	}

};