package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.SqlHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
import net.vdcraft.arvdc.timemanager.mainclass.WorldSyncHandler;

public class TmSetInitialTick extends MainTM {

    /**
     * CMD /tm set initialtick [tick|HH:mm:ss]
     */
    public static void cmdInitTick(CommandSender sender, Long newTick) {

	// Get the previous initial tick value
	Long oldTick = MainTM.getInstance().getConfig().getLong(CF_INITIALTICK + "." + CF_INITIALTICKNB);
	newTick = ValuesConverter.returnCorrectInitTicks(newTick);
	Long sqlTick = null;
	if (MainTM.getInstance().getConfig().getString(CF_INITIALTICK + "." + CF_USEMYSQL).equals("true")) {
	    if (SqlHandler.openTheConnectionIfPossible(false)) {
		sqlTick = SqlHandler.getServerTickSQL();
	    }
	}

	// Check if the new tick is different than the old one
	if (!newTick.equals(oldTick) && !newTick.equals(sqlTick)) {
	    // Adapt the value
	    initialTick = newTick;
	    initialTime = ValuesConverter.returnRealTimeFromTickValue(initialTick);

	    // Save the value in the config.yml
	    MainTM.getInstance().getConfig().set(CF_INITIALTICK + "." + CF_INITIALTICKNB, initialTick);
	    MainTM.getInstance().saveConfig();
	    if (MainTM.getInstance().getConfig().getString(CF_USEMYSQL).equalsIgnoreCase("false")) {
		Bukkit.getLogger().info(prefixTM + " " + initialTickYmlMsg); // Notify the console
	    }
	    // Save the value in the yml and if necessary in the MySql database
	    // Try to use MySQL if needed
	    if (MainTM.getInstance().getConfig().getString(CF_USEMYSQL).equalsIgnoreCase("true")) { // If mySQL is true
		if (SqlHandler.openTheConnectionIfPossible(true)) {
		    SqlHandler.updateServerTickSQL(newTick); // Set the reference tick from the config.yml to the mySQL
		    // database
		    Bukkit.getLogger().info(prefixTM + " " + initialTickSqlMsg); // Notify the console
		}
	    }
	    // Resynchronization
	    WorldSyncHandler.worldSync(sender, "all");

	} else {

	    // Notifications
	    if (sender instanceof Player) {
		sender.sendMessage(prefixTMColor + " " + initialTickNoChgMsg); // Notify the player (in case)
	    }
	    Bukkit.getLogger().info(prefixTM + " " + initialTickNoChgMsg); // Notify the console (in case)
	}
    }
};