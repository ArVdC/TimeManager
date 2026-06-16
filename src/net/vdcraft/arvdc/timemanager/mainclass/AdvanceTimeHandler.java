
package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import net.vdcraft.arvdc.timemanager.MainTM;

public class AdvanceTimeHandler extends MainTM {

	/**
	 * Configure the gamerule doDaylightCycle in targeted world(s), based on actual speed
	 */
	public static void adjustDaylightCycle(String worldToSet) {
		// For all listed worlds
		if (worldToSet.equalsIgnoreCase(ARG_ALL)) {
			for (String w : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				adjustDaylightCycle(w);
			}
			// For a single world
		} else {
			World w = Bukkit.getWorld(worldToSet);
			long t = w.getTime();
			double speedModifier = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST +"." + worldToSet + "." + ValuesConverter.wichSpeedParam(t));
			// If the speed of the world is freeze, decreased or normal & sync 
			if (speedModifier == realtimeSpeed || speedModifier < 1.0 || (speedModifier == 1.0 && MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + worldToSet + "." + CF_SYNC).equalsIgnoreCase(ARG_TRUE))) {
				if (serverMcVersion >= reqMcVToUseAdvanceTimeConstant && serverType.equalsIgnoreCase("spigot")) { // If Spigot server 26.1.2
					w.setGameRule(GameRule.ADVANCE_TIME, false);
				} else if (MainTM.serverMcVersion >= MainTM.reqMcVToUseAdvanceTimeGamerule) { // If any other server 1.21.11+
					spawnAndExecuteCommand(w, GR_ADVANCE_TIME, false);
				} else {// If any legacy server					
					spawnAndExecuteCommand(w, GR_DO_DAYLIGHT_CYCLE, false);
				}
				MsgHandler.debugMsg(daylightFalseDebugMsg + " §e" + worldToSet + "§b."); // Console debug msg				
			} else { // If the speed of the world is increased or normal & async
				if (serverMcVersion >= reqMcVToUseAdvanceTimeConstant && serverType.equalsIgnoreCase("spigot")) { // If Spigot server 26.1.2+
					w.setGameRule(GameRule.ADVANCE_TIME, true);
				} else if (MainTM.serverMcVersion >= MainTM.reqMcVToUseAdvanceTimeGamerule) { // If any other server 1.21.11+
					spawnAndExecuteCommand(w, GR_ADVANCE_TIME, true);
				} else {// If any legacy server					
					spawnAndExecuteCommand(w, GR_DO_DAYLIGHT_CYCLE, true);
			}
				MsgHandler.debugMsg(daylightTrueDebugMsg + " §e" + worldToSet + "§b."); // Console debug msg
			}
		}
	}

	public static void spawnAndExecuteCommand(World w, String gamerule, Boolean trueOrFalse) {
		Location spawn = w.getSpawnLocation();
        ArmorStand stand = (ArmorStand) w.spawnEntity(spawn, EntityType.ARMOR_STAND);
        stand.setInvisible(true);
        stand.setGravity(false);
        stand.setMarker(true);
        stand.setInvulnerable(true);
        stand.setSilent(true);
        stand.addScoreboardTag("time_trigger");
        new BukkitRunnable() {
            @Override
            public void run() {
                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();                
                String command =
                		"execute as @e[type=armor_stand,tag=time_trigger,limit=1] " +
                		"at @s run gamerule " + gamerule + " " + trueOrFalse;                
                Bukkit.dispatchCommand(console, command);
                stand.remove();
            }
        }.runTask(MainTM.getInstance());
    }
	
};
