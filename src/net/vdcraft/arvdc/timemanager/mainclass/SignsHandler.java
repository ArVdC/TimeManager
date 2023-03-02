package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitScheduler;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.PlayerLangHandler;
import net.vdcraft.arvdc.timemanager.placeholders.PlaceholdersHandler;

public class SignsHandler implements Listener {

	/**
	 * When a player create or modify a sign, check for {tm_placeholders}
	 */
	@EventHandler
	// #01. Listen to sign changes in any worlds
	public void whenPlayerChangeSign(SignChangeEvent e) throws InterruptedException {
		Player p = e.getPlayer();
		World w = p.getWorld();
		String world = w.getName();
		String lang = PlayerLangHandler.setLangToUse(p);
		String [] lines = e.getLines();
		int linesNb = lines.length;
		
		// #02. Replace text in each sign line
		while (linesNb > 0) {	
			linesNb--;	
			String l = e.getLine(linesNb);				
			l = PlaceholdersHandler.replaceAllPlaceholders(l, world, lang, p);				
			e.setLine(linesNb, l);
		}
	}
	
	/**
	 * Create auto-updated signs TODO 1.8
	 */
	// On server load -> create 'signs.yml'
	
	// On command /tm giveSign <player> -> create and give a special item
	
	// When using this item -> command /tm placeSign <x, y, z> <world>
	// & add location reference + text WITH original placeholders in 'signs.yml'
	
	// Loop and actualize every known signs with existing refreshRate
	public static void actualizeSigns(String world) {

		BukkitScheduler signsSheduler = MainTM.getInstance().getServer().getScheduler();
		signsSheduler.scheduleSyncDelayedTask(MainTM.getInstance(), new Runnable() {
			@Override
			public void run() {
				

			}
		}, MainTM.refreshRateLong);
	}
	
};