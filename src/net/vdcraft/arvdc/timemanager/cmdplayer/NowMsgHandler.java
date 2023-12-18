package net.vdcraft.arvdc.timemanager.cmdplayer;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.mainclass.ValuesConverter;
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
		//String player = p.getName();
		String world = w.getName();
		String msg = null;
		String subtitle = null;
		
		// #2. Get the language to use
		String lang = PlayerLangHandler.setLangToUse(sender);
		
		// #3. Get and format the prefix from the lang.yml file
		String prefix = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lang + "." + CF_PREFIX);
		prefix = prefix.replace("&", "§");
		
		// #4. Configure message content
		switch (display) {
		case ARG_MSG :		
			if (world.contains(ARG_NETHER)) {
				msg = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lang + "." + CF_NETHERMSG);
			} else if (world.contains(ARG_THEEND)) {
				msg = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lang + "." + CF_ENDMSG);
			} else {
				msg = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lang + "." + CF_MSG);	
			}
			break;
		case ARG_TITLE :
			msg = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lang + "." + CF_TITLE);
			subtitle = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lang + "." + CF_SUBTITLE);
			break;
		case ARG_ACTIONBAR :
			msg = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lang + "." + CF_ACTIONBAR);
			break;
		}
		// If the value in lang.yml file is empty, nothing will be send to the player
		if (msg.equalsIgnoreCase("")) {
			return true;
		}
		
		// #5. Replace placeholders
		msg = msg.replace("&", "§");
		//msg = msg.replace("{tm_player}", player);
		msg = PlaceholdersHandler.replaceAllPlaceholders(msg, world, lang, p);
		if (display.equalsIgnoreCase("title")) {
			subtitle = subtitle.replace("&", "§");
			//subtitle = subtitle.replace("{tm_player}", player);
			subtitle = PlaceholdersHandler.replaceAllPlaceholders(subtitle, world, lang, p);		
		}
		
		// #6. Replace hexadecimal colors by ChatColors
		if (serverMcVersion >= reqMcVForHexColors){ // Check if MC version is at least 1.16.0
			msg = ValuesConverter.replaceAllHexColors(msg);
			switch (display) {
			case ARG_MSG :
				prefix = ValuesConverter.replaceAllHexColors(prefix);
				break;
			case ARG_TITLE :
				subtitle = ValuesConverter.replaceAllHexColors(subtitle);
				break;
			}
		}
		
		// #7. Configure and send command
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