package net.vdcraft.arvdc.timemanager.placeholders;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.PlayerLangHandler;

public class ChatHandler implements Listener {

	/**
	 * When a player send a message, check for {tm_placeholders} and replace them
	 */
	@EventHandler
	public void onMessageSend(AsyncPlayerChatEvent e) {
		
		// #01. Check in the config file if function is activated
		String chat = MainTM.getInstance().getConfig().getString(MainTM.CF_PLACEHOLDERS + "." + MainTM.CF_PLACEHOLDER_CHAT);
		if (chat.equalsIgnoreCase(MainTM.ARG_TRUE)) {
		
			// #02. Check if the message contains some placeholder
			String msg = e.getMessage();
			if (msg.contains("{" + MainTM.PH_PREFIX)) {
			
				// #03. Retrieve information from the sent message
				Player p = e.getPlayer();	
				World w = p.getWorld();
				String world = w.getName();
				String lang = PlayerLangHandler.setLangToUse(p);
				
				// #04. Replace placeholders in the message
				String newMsg = PlaceholdersHandler.replaceAllPlaceholders(msg, world, lang, p);
				
				// #05. Send the modified message
				e.setMessage(newMsg);
			}
		}
	}
	
};