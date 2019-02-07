package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;

import net.vdcraft.arvdc.timemanager.MainTM;

public class DaylightCycleHandler extends MainTM {

    /**
     * Configure the gamerule doDaylightCycle in targeted world(s), based on actual speed
     */
    @SuppressWarnings("deprecation")
    public static void doDaylightCheck(String worldToSet) {
	if (worldToSet.equalsIgnoreCase("all")) // For all listed worlds
	{
	    for (String w : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
		doDaylightCheck(w);
	    }
	} else // For a single world
	{
	    double speedModifier = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST + "." + worldToSet + "." + CF_SPEED); // Read config.yml to get the world's 'speed' value
	    if (speedModifier == realtimeSpeed || speedModifier < 1.0) {
		if (decimalOfMcVersion < 13.0) {
		    Bukkit.getWorld(worldToSet).setGameRuleValue("doDaylightCycle", "false");
		} else {
		    Bukkit.getWorld(worldToSet).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		}
	    } else {
		if (decimalOfMcVersion < 13.0) {
		    Bukkit.getWorld(worldToSet).setGameRuleValue("doDaylightCycle", "true");
		} else {
		    Bukkit.getWorld(worldToSet).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
		}
	    }
	}
    }

};