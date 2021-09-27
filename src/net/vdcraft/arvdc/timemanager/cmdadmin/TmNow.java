package net.vdcraft.arvdc.timemanager.cmdadmin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdplayer.PlayerLangHandler;
import net.vdcraft.arvdc.timemanager.mainclass.CfgFileHandler;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.placeholders.PlaceholdersHandler;

public class TmNow extends MainTM {

	/**
	 * CMD /tm now [msg|title|actionbar] [player|all|world]
	 */
	public static void cmdNow(CommandSender sender, String display, String target) {
		
		// #1. Don't launch the command if there is no players (empty server, empty targeted world or unknown target)
		// #1.A. Stop if the server is empty
		if (Bukkit.getServer().getOnlinePlayers().size() == 0) {
			MsgHandler.debugMsg(tmNowEmptyServerMsg);
			if (sender instanceof CommandBlock) MsgHandler.playerAdminMsg(sender, tmNowEmptyServerMsg);
			return;
		}
		// #1.B. Stop if the targeted world is empty
		List<String> playersList = new ArrayList<>(); // Get the online players list
		for (Player listedPlayer : Bukkit.getServer().getOnlinePlayers()) playersList.add(listedPlayer.getName());
		List<String> worldsList = CfgFileHandler.setAnyListFromConfig(CF_WORLDSLIST); // Get the worlds list
		if (target.equalsIgnoreCase(ARG_ALL) || playersList.contains(target)) { // If target is "all" or is an online player, don't do nothing special
		} else if (worldsList.contains(target)) { // Else, if target is a world
			List<Player> players = Bukkit.getServer().getWorld(target).getPlayers();			
			if (players.size() == 0) {
				MsgHandler.debugMsg("World  §e" + target + " §b " + tmNowEmptyWorldMsg);
				MsgHandler.playerAdminMsg(sender, "World §e" + target + "§r " + tmNowEmptyWorldMsg);
				return;
			}
		// #1.C. Else, stop if the target is unknown
		} else {
			MsgHandler.cmdErrorMsg(sender, "§cThe §e" + target + "§c " + tmNowUnknownArgMsg, CMD_TMNOW);
			return;
		}
		
		// #2. Get all he players
		for (Player p : Bukkit.getOnlinePlayers()) {
			String world = p.getWorld().getName();
			String player = p.getName();
			
			// #3. Check who will receive the msg
			if (player.equalsIgnoreCase(target) || target.equalsIgnoreCase(ARG_ALL) || target.equalsIgnoreCase(world)) {
				String lang = PlayerLangHandler.setLangToUse(p);
				
				// #4. Get the prefix from the lang.yml file
				String prefix = MainTM.getInstance().langConf.getString(CF_LANGUAGES + "." + lang + "." + CF_PREFIX);
				prefix = prefix.replace("&", "§");		
				
				// #5. Avoid showing actual time if player is in a nether or the_end world
				if (world.contains(ARG_NETHER) || world.contains(ARG_THEEND)) {
					return;
				}
				
				// #6. Configure message content
				String msg = null;
				String subtitle = null;
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
				
				// #7. Replace placeholders
				msg = msg.replace("&", "§");
				msg = msg.replace("{" + PH_PREFIX + PH_PLAYER + "}", player);
				msg = PlaceholdersHandler.replaceAllPlaceholders(msg, world, lang, p);
				if (display.equalsIgnoreCase(ARG_TITLE)) {
					subtitle = subtitle.replace("&", "§");
					subtitle = subtitle.replace("{" + PH_PREFIX + PH_PLAYER + "}", player);
					subtitle = PlaceholdersHandler.replaceAllPlaceholders(subtitle, world, lang, p);		
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
			}
		}
	}

};