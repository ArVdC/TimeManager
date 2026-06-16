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
	 * Configure the gamerule advance_time in targeted world(s), based on actual speed
	 */
	public static void adjustAdvanceTime(String worldToSet) {
		// For all listed worlds
		if (worldToSet.equalsIgnoreCase(ARG_ALL)) {
			for (String w : MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false)) {
				adjustAdvanceTime(w);
			}
			// For a single world
		} else {
			World w = Bukkit.getWorld(worldToSet);
			long t = w.getTime();
			double speedModifier = MainTM.getInstance().getConfig().getDouble(CF_WORLDSLIST +"." + worldToSet + "." + ValuesConverter.wichSpeedParam(t));
			// If the speed of the world is freeze, decreased or normal & sync 
			if (speedModifier == realtimeSpeed || speedModifier < 1.0 || (speedModifier == 1.0 && MainTM.getInstance().getConfig().getString(CF_WORLDSLIST + "." + worldToSet + "." + CF_SYNC).equalsIgnoreCase(ARG_TRUE))) {
				if (serverMcVersion >= reqMcVToUseAdvanceTimeConstant && serverType.equalsIgnoreCase("spigot")) { // If Spigot server 1.21.11+
					w.setGameRule(GameRule.ADVANCE_TIME, false);
					MsgHandler.devMsg("[AdvanceTimeHandler] GameRule.ADVANCE_TIME false : Spigot server 1.21.11+");
				} else if (MainTM.serverMcVersion >= MainTM.reqMcVToUseAdvanceTimeGamerule) { // If any other server 1.21.11+
					spawnAndExecuteCommand(w, GR_ADVANCE_TIME, false);
					MsgHandler.devMsg("[AdvanceTimeHandler] /gamerule advance_time false : server 1.21.11+");
				} else {// If any legacy server					
					spawnAndExecuteCommand(w, GR_DO_DAYLIGHT_CYCLE, false);
					MsgHandler.devMsg("[AdvanceTimeHandler] /gamerule doDaylightCycle false : server 1.21.11-");
				}
				MsgHandler.debugMsg(daylightFalseDebugMsg + " §e" + worldToSet + "§b."); // Console debug msg				
			} else { // If the speed of the world is increased or normal & async
				if (serverMcVersion >= reqMcVToUseAdvanceTimeConstant && serverType.equalsIgnoreCase("spigot")) { // If Spigot server 1.21.11+
					w.setGameRule(GameRule.ADVANCE_TIME, true);
					MsgHandler.devMsg("[AdvanceTimeHandler] GameRule.ADVANCE_TIME true : Spigot server 1.21.11+");
				} else if (MainTM.serverMcVersion >= MainTM.reqMcVToUseAdvanceTimeGamerule) { // If any other server 1.21.11+
					spawnAndExecuteCommand(w, GR_ADVANCE_TIME, true);
					MsgHandler.devMsg("[AdvanceTimeHandler] /gamerule advance_time true : server 1.21.11+");
				} else {// If any legacy server					
					spawnAndExecuteCommand(w, GR_DO_DAYLIGHT_CYCLE, true);
					MsgHandler.devMsg("[AdvanceTimeHandler] /gamerule doDaylightCycle true : server 1.21.11-");
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
