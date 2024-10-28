package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.event.world.TimeSkipEvent.SkipReason;

public class HiddenListenerHandler implements Listener {	// extends SleepHandler.java, only works with MC version is at least 1.15.0

	/**
	 * When a player finishes sleeping, prohibit the natural automatic night shift
	 */
	@EventHandler
	private void whenNightIsSkipped(TimeSkipEvent e) throws InterruptedException {
		World w = e.getWorld();
		Enum<SkipReason> reason = e.getSkipReason();
		long t = w.getTime();
		if (reason.equals(SkipReason.NIGHT_SKIP)) {
			MsgHandler.debugMsg("Night skip in world §e" + w.getName() + " §bwas cancelled at tick §e#" + t + "§b.");
			e.setCancelled(true);
		}
	}

};