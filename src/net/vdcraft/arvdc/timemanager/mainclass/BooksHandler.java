package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;

import net.vdcraft.arvdc.timemanager.cmdplayer.PlayerLangHandler;
import net.vdcraft.arvdc.timemanager.placeholders.PlaceholdersHandler;

public class BooksHandler implements Listener {

	/**
	 * When a player achieves a book, check for {tm_placeholders}
	 */
	// Listen to book achievement in any worlds
	@EventHandler
	public void whenPlayerAchievesBook(PlayerEditBookEvent e) throws InterruptedException {
		Player p = e.getPlayer();
		World w = p.getWorld();
		String world = w.getName();
		String lang = PlayerLangHandler.setLangToUse(p);
		BookMeta book = e.getNewBookMeta();
		
		// Replace text in each book page
		if (book.hasPages()) {
			int pagesNb = book.getPageCount();			
			while (pagesNb > 0) {				
				String pg = book.getPage(pagesNb);				
				pg = PlaceholdersHandler.replaceAllPlaceholders(pg, world, lang, p);				
				book.setPage(pagesNb, pg);				
				pagesNb--;
			}			
		}
		
		// Replace text in title
		if (book.hasTitle()) {
			String t = book.getTitle();
			t = PlaceholdersHandler.replaceAllPlaceholders(t, world, lang, p);	
			book.setTitle(t);
		}
		
		// Save all changes
		e.setNewBookMeta(book);
	}
	
};