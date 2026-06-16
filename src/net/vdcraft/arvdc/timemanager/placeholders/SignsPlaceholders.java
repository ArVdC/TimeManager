package net.vdcraft.arvdc.timemanager.placeholders;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.PlayerLangHandler;

public class SignsPlaceholders implements Listener {

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
		if (lines[0].equalsIgnoreCase(MainTM.getInstance().getConfig().getString(MainTM.CF_SIGNS + "." + MainTM.CF_SIGNS_MARKER))) return;
		int linesNb = lines.length;
		
		// #02. Replace text in each sign line
		while (linesNb > 0) {
			linesNb--;	
			String l = e.getLine(linesNb);				
			l = PlaceholdersHandler.replaceAllPlaceholders(l, world, lang, p, false);				
			e.setLine(linesNb, l);
		}
	}
	
};