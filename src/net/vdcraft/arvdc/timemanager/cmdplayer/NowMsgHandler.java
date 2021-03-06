package net.vdcraft.arvdc.timemanager.cmdplayer;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.placeholders.PlaceholdersHandler;

public class NowMsgHandler extends MainTM {

	/**
	 * CMD /now [<msg|title|actionbar>] [<world>] 
	 */
	public static boolean sendNowMsg(CommandSender sender) {
		String display = MainTM.getInstance().langConf.getString(CF_DEFAULTDISPLAY);
		World w = ((Player) sender).getWorld();
		sendNowMsg(sender, display, w);
		return true;
	}
	public static boolean sendNowMsg(CommandSender sender, String display) {
		World w = ((Player) sender).getWorld();
		sendNowMsg(sender, display, w);
		return true;
	}
	public static boolean sendNowMsg(CommandSender sender, World w) {
		String display = MainTM.getInstance().langConf.getString(CF_DEFAULTDISPLAY);
		sendNowMsg(sender, display, w);
		return true;
	}
	public static boolean sendNowMsg(CommandSender sender, String display, World w) {
		// #1. Set basic variables
		Player p = ((Player) sender);
		String player = p.getName();
		String world = w.getName();
		String msg = null;
		String subtitle = null;
		
		// #2. Get the language to use
		String lang = PlayerLangHandler.setLangToUse(sender);
		
		// #3. Get and format the prefix from the lang.yml file
		String prefix = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lang + "." + CF_PREFIX);
		prefix = prefix.replace("&", "§");
		
		// #4.Avoid showing actual time if player is in a nether or the_end world
		if (world.contains(ARG_NETHER) || world.contains(ARG_THEEND)) {
			msg = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lang + "." + CF_NOMSG);
			// #4.A. If the noMsg in lang.yml file is empty, nothing will be send to the player
			if (msg.equalsIgnoreCase("")) {
				return true;
			// #4.B. If the noMsg in lang.yml file exists, send it as msg and return
			} else {
				msg = msg.replace("&", "§");
				MsgHandler.playerChatMsg(p, prefix, msg);
				return true;
			}
		}
		
		// #5. Configure message content
		switch (display) {
		case ARG_MSG :
			msg = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MSG);
			break;
		case ARG_TITLE :
			msg = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lang + "." + CF_TITLE);
			subtitle = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lang + "." + CF_SUBTITLE);
			break;
		case ARG_ACTIONBAR :
			msg = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lang + "." + CF_ACTIONBAR);
			break;
		}
		
		// #6. Replace placeholders
		msg = msg.replace("&", "§");
		msg = msg.replace("{tm_player}", player);
		msg = PlaceholdersHandler.replaceAllPlaceholders(msg, world, lang);
		if (display.equalsIgnoreCase("title")) {
			subtitle = subtitle.replace("&", "§");
			subtitle = subtitle.replace("{tm_player}", player);
			subtitle = PlaceholdersHandler.replaceAllPlaceholders(subtitle, world, lang);		
		}
				
		// #8. Configure and send command
		switch (display) {
		case ARG_MSG :
			MsgHandler.playerChatMsg(p, prefix, msg);
			break;
		case ARG_TITLE :
			MsgHandler.playerTitleMsg(p, msg, subtitle);
			break; 
		case ARG_ACTIONBAR :
			MsgHandler.playerActionbarMsg(p, msg);
			break;
		}
		return true;
	}

};