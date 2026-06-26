
package net.vdcraft.arvdc.timemanager.mainclass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
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
	 * GameRule API bridge — done entirely through reflection so the same
	 * compiled jar runs on every MC version from 1.9.4 through Paper 26.x:
	 *
	 *   - Pre-1.13:  the org.bukkit.GameRule enum doesn't exist; the
	 *                world has setGameRuleValue(String, String).
	 *   - 1.13–1.21: GameRule.DO_DAYLIGHT_CYCLE + setGameRule(GameRule, T).
	 *   - 26.x+:     the same enum constant was renamed to ADVANCE_TIME;
	 *                setGameRuleValue(String, String) is gone.
	 *
	 * Reflection sidesteps Material-style compile-time enum lookups so
	 * the absent fields don't error out at jar-load time.
	 */
	private static void setDoDaylightCycle(World w, boolean value) {
		if (serverMcVersion >= reqMcVForGamerules) {
			try {
				Class<?> gameRuleCls = Class.forName("org.bukkit.GameRule");
				Object rule = null;
				// Try modern (renamed) name first so 26.x picks ADVANCE_TIME,
				// then fall back to the historical DO_DAYLIGHT_CYCLE.
				for (String name : new String[]{"ADVANCE_TIME", "DO_DAYLIGHT_CYCLE"}) {
					try {
						Field f = gameRuleCls.getField(name);
						rule = f.get(null);
						if (rule != null) break;
					} catch (NoSuchFieldException ignored) {}
				}
				if (rule != null) {
					Method m = World.class.getMethod("setGameRule", gameRuleCls, Object.class);
					m.invoke(w, rule, Boolean.valueOf(value));
					return;
				}
			} catch (Throwable t) {
				MsgHandler.debugMsg("[gamerule] reflective setGameRule failed: " + t.getMessage());
			}
		}
		// Legacy string-keyed setter — still present on pre-26 builds, removed
		// on 26.x but we wouldn't reach here on a 26.x server since the
		// modern reflection branch above handles it.
		try {
			Method legacy = World.class.getMethod("setGameRuleValue", String.class, String.class);
			legacy.invoke(w, "doDaylightCycle", String.valueOf(value));
		} catch (Throwable t) {
			MsgHandler.debugMsg("[gamerule] legacy setGameRuleValue unavailable: " + t.getMessage());
		}
	}

};
