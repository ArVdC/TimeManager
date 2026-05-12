package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.LockTimeHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SpeedHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SyncHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;

/**
 * /tm lock &lt;world&gt; [time]
 *
 * Freezes a world at the specified time, or at the world's current time if no
 * time argument is provided. Writes {@code lock-time} into the world's config
 * section and applies the derived speed/start values immediately. The world's
 * scheduler is restarted so the change takes effect without a full plugin
 * reload.
 *
 * Time argument accepts the same values as {@code lock-time:} in the config:
 * named presets (noon, dawn, dusk, midnight, day, night),
 * raw ticks (0–23999), HH:mm format, or {@code realtime}.
 */
public class TmLock extends MainTM {

	public static void cmdLock(CommandSender sender, String world, String timeArg) {

		// #1. Validate world exists and is in the plugin's world list
		if (Bukkit.getWorld(world) == null) {
			MsgHandler.cmdErrorMsg(sender, "§cUnknown world: §e" + world + "§c.", CMD_LOCK);
			return;
		}
		if (!MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			MsgHandler.cmdErrorMsg(sender, "§cUnknown world: §e" + world + "§c.", CMD_LOCK);
			return;
		}

		// #2. Resolve the time argument. Empty / null → use the world's current tick.
		String resolvedValue;
		if (timeArg == null || timeArg.isEmpty() || timeArg.equalsIgnoreCase("here") || timeArg.equalsIgnoreCase("now")) {
			long currentTick = Bukkit.getWorld(world).getTime();
			resolvedValue = String.valueOf(currentTick);
		} else {
			resolvedValue = timeArg;
		}

		// #3. Write lock-time + apply the derived values
		LockTimeHandler.setLockTime(world, resolvedValue);

		// #4. Re-validate / clamp the derived values like the config loader does
		ValuesConverter.restrainStart(world);
		ValuesConverter.restrainSpeed(world);
		ValuesConverter.restrainFirstStartTime(world);

		// #5. Persist + apply
		MainTM.getInstance().saveConfig();
		SyncHandler.worldSync(sender, world, ARG_START);
		SpeedHandler.speedScheduler(world);

		// #6. Notify
		MsgHandler.infoMsg("The world §e" + world + "§r is now locked (lock-time: §e" + resolvedValue + "§r).");
		MsgHandler.playerAdminMsg(sender, "Locked §e" + world + "§r at §e" + resolvedValue + "§r.");
	}
}
