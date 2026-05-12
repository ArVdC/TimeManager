package net.vdcraft.arvdc.timemanager.cmdadmin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.LockTimeHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SpeedHandler;
import net.vdcraft.arvdc.timemanager.mainclass.SyncHandler;

/**
 * /tm unlock &lt;world&gt;
 *
 * Removes a {@code lock-time} entry from a world. The world keeps whatever
 * (start, daySpeed, nightSpeed, firstStartTime) the lock had written to the
 * config — admins are expected to set the desired speed afterwards if they
 * want the world to resume normal day/night progression.
 */
public class TmUnlock extends MainTM {

	public static void cmdUnlock(CommandSender sender, String world) {

		// #1. Validate
		if (Bukkit.getWorld(world) == null) {
			MsgHandler.cmdErrorMsg(sender, "§cUnknown world: §e" + world + "§c.", CMD_UNLOCK);
			return;
		}
		if (!MainTM.getInstance().getConfig().getConfigurationSection(CF_WORLDSLIST).getKeys(false).contains(world)) {
			MsgHandler.cmdErrorMsg(sender, "§cUnknown world: §e" + world + "§c.", CMD_UNLOCK);
			return;
		}

		String lockKey = CF_WORLDSLIST + "." + world + "." + CF_LOCKTIME;
		if (!MainTM.getInstance().getConfig().contains(lockKey)) {
			MsgHandler.playerAdminMsg(sender, "World §e" + world + "§r has no lock-time set.");
			return;
		}

		// #2. Clear the lock-time key
		LockTimeHandler.clearLockTime(world);
		MainTM.getInstance().saveConfig();

		// #3. Re-apply scheduler / sync so the speed change takes effect
		SyncHandler.worldSync(sender, world, ARG_START);
		SpeedHandler.speedScheduler(world);

		// #4. Notify
		MsgHandler.infoMsg("The world §e" + world + "§r has been unlocked.");
		MsgHandler.playerAdminMsg(sender,
				"Unlocked §e" + world + "§r. Use §e/tm set daySpeed/nightSpeed §rto restore normal time flow.");
	}
}
