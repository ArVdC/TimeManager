package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;

import net.vdcraft.arvdc.timemanager.MainTM;

public class DaylightCycleHandler extends MainTM {
	
    /**
     * Configure the gamerule doDaylightCycle in targeted world(s), based on actual speed
     *
     */
    public static void doDaylightCheck(String worldToSet)
    {
    	if(worldToSet.equalsIgnoreCase("all")) // For all listed worlds
	    {    	
    		for(String w :MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false))
    		{
    			doDaylightCheck(w);
    		}
		} else // For a single world
		{
	    	double speedModifier = MainTM.getInstance().getConfig().getDouble("worldsList."+worldToSet+".speed"); // Read config.yml to get the world's 'speed' value    		
			if(speedModifier == 0) {
				Bukkit.getWorld(worldToSet).setGameRuleValue("doDaylightCycle", "false");
			} else {
			    Bukkit.getWorld(worldToSet).setGameRuleValue("doDaylightCycle", "true");
			}
		}
	};
}
