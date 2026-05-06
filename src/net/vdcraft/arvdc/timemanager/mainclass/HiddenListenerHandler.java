package net.vdcraft.arvdc.timemanager.mainclass;

import java.lang.reflect.Method;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;

public class HiddenListenerHandler implements Listener {

	/**
	 * When a player finishes sleeping, prohibit the natural automatic night shift
	 */
	@EventHandler
	private void whenNightIsSkipped(TimeSkipEvent e) throws InterruptedException {
		String reasonName;
		try {
			Method m = e.getClass().getMethod("getSkipReason");
			Object reason = m.invoke(e);
			if (reason == null) return;
			reasonName = reason.toString();
		} catch (ReflectiveOperationException ex) {
			return;
		}
		if (!"NIGHT_SKIP".equals(reasonName)) return;
		World w = e.getWorld();
		long t = w.getTime();
		MsgHandler.debugMsg("Night skip in world §e" + w.getName() + " §bwas cancelled at tick §e#" + t + "§b.");
		e.setCancelled(true);
	}

};
