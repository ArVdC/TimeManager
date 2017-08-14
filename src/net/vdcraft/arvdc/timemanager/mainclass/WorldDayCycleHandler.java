
package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.World;

import net.vdcraft.arvdc.timemanager.MainTM;

public class WorldDayCycleHandler extends MainTM {
	
    /**
     * Configure the gamerule doDaylightCycle in targeted world(s), based on actual speed
     *
     */
    public static void doDaylightCheck(String worldToSet) {
    	// For all listed worlds
    	if(worldToSet.equalsIgnoreCase("all")) {
    		for(String w :MainTM.getInstance().getConfig().getConfigurationSection("worldsList").getKeys(false)) {
    			doDaylightCheck(w);
    		}
    	// For a single world
		} else { 
	    	World w = Bukkit.getWorld(worldToSet);		
	    	double speedModifier = MainTM.getInstance().getConfig().getDouble("worldsList."+worldToSet+".speed");   		
	    	if(speedModifier == realtimeSpeed || speedModifier < 1.0) {
				w.setGameRuleValue("doDaylightCycle", "false");
				if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + daylightFalseDebugMsg + " §e" + worldToSet + "§b."); // Console debug msg
			} else {
			    w.setGameRuleValue("doDaylightCycle", "true");
				if(debugMode == true) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + daylightTrueDebugMsg + " §e" + worldToSet + "§b."); // Console debug msg
			}
		}
	};
}