
package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;

import net.vdcraft.arvdc.timemanager.MainTM;

public class DoDaylightCycleHandler extends MainTM {

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
				setDoDaylightCycle(w, false);
				MsgHandler.debugMsg(daylightFalseDebugMsg + " §e" + worldToSet + "§b."); // Console debug msg
			} else { // If the speed of the world is increased or normal & async
				setDoDaylightCycle(w, true);
				MsgHandler.debugMsg(daylightTrueDebugMsg + " §e" + worldToSet + "§b."); // Console debug msg
			}
		}
	}

	/**
	 * GameRule API bridge. Pre-1.13 servers don't have the org.bukkit.GameRule
	 * class at all (gamerules were string-keyed), so we fall back to the
	 * legacy setGameRuleValue(String, String) overload — still present on
	 * modern Paper for back-compat, just deprecated.
	 */
	@SuppressWarnings("deprecation")
	private static void setDoDaylightCycle(World w, boolean value) {
		if (serverMcVersion >= reqMcVForGamerules) {
			w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, value);
		} else {
			w.setGameRuleValue("doDaylightCycle", String.valueOf(value));
		}
	}

};
