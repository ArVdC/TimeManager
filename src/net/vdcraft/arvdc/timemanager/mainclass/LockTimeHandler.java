package net.vdcraft.arvdc.timemanager.mainclass;

import net.vdcraft.arvdc.timemanager.MainTM;

/**
 * Convenience single-key shortcut for locking or speeding up a world.
 *
 * Admins set {@code lock-time:} on a world inside {@code worldsList}, and on
 * config load the plugin translates that single key into the equivalent
 * (start, daySpeed, nightSpeed, firstStartTime) combination that the rest of
 * the plugin already understands. The original {@code lock-time} key stays in
 * the config as the source of truth — the derived values are written back so
 * older parts of the plugin (and human readers) can still see what the world
 * is doing.
 *
 * Supported values:
 * <ul>
 *   <li>{@code noon} / {@code midday} – tick 6000, frozen</li>
 *   <li>{@code dawn} / {@code sunrise} / {@code morning} – tick 0, frozen</li>
 *   <li>{@code dusk} / {@code sunset} / {@code evening} – tick 12000, frozen</li>
 *   <li>{@code day} – tick 1000, frozen</li>
 *   <li>{@code night} – tick 13000, frozen</li>
 *   <li>{@code midnight} – tick 18000, frozen</li>
 *   <li>{@code 0}–{@code 23999} – exact tick, frozen</li>
 *   <li>{@code HH:mm} (e.g. {@code 13:30}) – frozen at the matching tick</li>
 *   <li>{@code realtime} – follow UTC clock (speed = 24.0)</li>
 *   <li>{@code false} / {@code off} / unset – no override</li>
 * </ul>
 */
public class LockTimeHandler extends MainTM {

	/**
	 * If a world has {@code lock-time} set, expand it into the equivalent
	 * speed/start combination and persist the derived values. Called once
	 * per world during {@link CfgFileHandler#loadConfig(String)}.
	 */
	public static void applyLockTime(String world) {
		String key = CF_WORLDSLIST + "." + world + "." + CF_LOCKTIME;
		if (!MainTM.getInstance().getConfig().contains(key)) return;

		String raw = MainTM.getInstance().getConfig().getString(key);
		if (raw == null || raw.isEmpty()
				|| raw.equalsIgnoreCase(ARG_FALSE)
				|| raw.equalsIgnoreCase("off")
				|| raw.equalsIgnoreCase("none")) {
			return;
		}

		String base = CF_WORLDSLIST + "." + world + ".";

		// "realtime" — sync world clock to UTC (24h cycle in 24h real time).
		if (raw.equalsIgnoreCase("realtime")) {
			MainTM.getInstance().getConfig().set(base + CF_D_SPEED, realtimeSpeed);
			MainTM.getInstance().getConfig().set(base + CF_N_SPEED, realtimeSpeed);
			MsgHandler.debugMsg("lock-time on §e" + world + "§b: realtime (speed=24.0).");
			return;
		}

		// Frozen at a specific tick.
		Long tick = ValuesConverter.tickFromString(raw);
		if (tick == null) tick = 6000L; // shouldn't happen — tickFromString never returns null

		MainTM.getInstance().getConfig().set(base + CF_START, tick);
		MainTM.getInstance().getConfig().set(base + CF_D_SPEED, 0.0);
		MainTM.getInstance().getConfig().set(base + CF_N_SPEED, 0.0);
		MainTM.getInstance().getConfig().set(base + CF_FIRSTSTARTTIME, ARG_START);

		MsgHandler.debugMsg("lock-time on §e" + world + "§b: locked at tick " + tick + ".");
	}

	/**
	 * Set or update the lock-time entry for a world at runtime (e.g. from
	 * {@code /tm lock <world> <time>} command). Writes config without saving;
	 * caller should follow with {@code MainTM.getInstance().saveConfig()}.
	 */
	public static void setLockTime(String world, String value) {
		MainTM.getInstance().getConfig().set(
				CF_WORLDSLIST + "." + world + "." + CF_LOCKTIME, value);
		applyLockTime(world);
	}

	/**
	 * Remove lock-time on a world. Does NOT restore previous speeds — caller
	 * (e.g. /tm unlock) decides what defaults to write back, since we don't
	 * keep a snapshot.
	 */
	public static void clearLockTime(String world) {
		MainTM.getInstance().getConfig().set(
				CF_WORLDSLIST + "." + world + "." + CF_LOCKTIME, null);
	}
}
