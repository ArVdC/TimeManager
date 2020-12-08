package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSpeedHandler;

public class TmSetSync extends MainTM {

    /**
     * CMD /tm set sync [boolean] [world]
     */
    public static void cmdSetSync(CommandSender sender, String syncOrNo, String world) {
	// If using a world name in several parts
	if (sender instanceof Player) {
	    world = ValuesConverter.restoreSpacesInString(world);
	}

	// Modify all worlds
	if (world.equalsIgnoreCase("all")) {
	    // Relaunch this for each world
	    for (String listedWorld : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
		cmdSetSync(sender, syncOrNo, listedWorld);
	    }
	}
	// Else, if the string argument is a listed world, modify a single world
	else if (MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
	    // Avoid impossible values
	    World w = Bukkit.getWorld(world);
	    long t = w.getTime();
	    String speed = MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + ValuesConverter.wichSpeedParam(t));
	    if ((syncOrNo.equals("true") && speed.equals("0.0")) || (syncOrNo.equals("false") && speed.equals("24.0"))) {
		// Notifications
		Bukkit.getLogger().info(prefixTM + " " + worldSyncNoChgMsg + " " + world + "."); // Console final msg (always)
		if (sender instanceof Player) {
		    sender.sendMessage(prefixTMColor + " " + worldSyncNoChgMsg + " §e" + world + "§r."); // Player final msg (in case)
		}
	    } else {
		// Modify the value
		MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SYNC, syncOrNo);
		// If sync is true, make some changes
		if (syncOrNo.equals("true")) {
		    // Start synchronize 1.0 speed worlds
		    if (speed.equals("1.0") && increaseScheduleIsOn == false) {
			WorldSpeedHandler.worldIncreaseSpeed();
		    }
		    // Avoid players to sleep in a synchronized world
		    if (MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + world + "." + CF_SLEEP).equals("true")) {
			MainTM.getInstance().getConfig().set(CF_WORLDSLIST + "." + world + "." + CF_SLEEP, "false");
			Bukkit.getLogger().info(prefixTM + " The world " + world + " " + worldSyncSleepChgMsg); // Console warn msg (always)
			if (sender instanceof Player) {
			    sender.sendMessage(prefixTMColor + " The world §e" + world + "§r " + worldSyncSleepChgMsg); // Player warn msg (in case)
			}
		    }
		}
		// Save the value(s) in the config.yml
		MainTM.getInstance().saveConfig();
		// Notifications
		if (syncOrNo.equals("true")) {
		    Bukkit.getLogger().info(prefixTM + " " + worldSyncTrueChgMsg + " " + world + "."); // Console final msg (always)
		    if (sender instanceof Player) {
			sender.sendMessage(prefixTMColor + " " + worldSyncTrueChgMsg + " §e" + world + "§r."); // Player final msg (in case)
		    }
		} else if (syncOrNo.equals("false")) {
		    Bukkit.getLogger().info(prefixTM + " " + worldSyncFalseChgMsg + " " + world + "."); // Console final msg (always)
		    if (sender instanceof Player) {
			sender.sendMessage(prefixTMColor + " " + worldSyncFalseChgMsg + " §e" + world + "§r."); // Player final msg (in case)
		    }
		}
	    }
	}
	// Else, return an error and help message
	else {
	    TmHelp.sendErrorMsg(sender, MainTM.wrongWorldMsg, "set sync");
	}
    }

};