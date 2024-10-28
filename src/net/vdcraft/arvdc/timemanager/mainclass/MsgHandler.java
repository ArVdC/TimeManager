package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;

public class MsgHandler extends MainTM {

	/**
	 * Info msg
	 */
	public static void infoMsg(String msg) {
		Bukkit.getLogger().info(MainTM.prefixTM + " " + msg);
	}

	/**
	 * Warning msg
	 */
	public static void warnMsg(String msg) {
		Bukkit.getLogger().warning(MainTM.prefixTM + " " + msg);
	}

	/**
	 * Error msg
	 */
	public static void errorMsg(String msg) {
		Bukkit.getLogger().severe(MainTM.prefixTM + " " + msg);
	}

	/**
	 * Colored msg
	 */
	public static void colorMsg(String msg) {
		Bukkit.getServer().getConsoleSender().sendMessage(MainTM.prefixTM + " " + msg);
	}

	/**
	 * Command error message and its associated help message
	 */
	public static void cmdErrorMsg(CommandSender sender, String msgError, String cmdHelp) {
		playerAdminMsg(sender, ChatColor.RED + msgError); // Player error msg (in case is player)
		warnMsg(msgError); // Console error msg (always)
		Bukkit.dispatchCommand(sender, CMD_TM + " " + CMD_HELP + cmdHelp); // Sender help msg (always)
	}

	/**
	 * Debug msg
	 */
	public static void debugMsg(String msg) {
		if (debugMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + " " + msg);
	}

	/**
	 * Dev msg
	 */
	public static void devMsg(String msg) {
		if (devMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + ChatColor.BLUE + " " + msg);
	}

	/**
	 * Timer msg
	 */
	public static void timerMsg(String msg) {
		if (timerMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + ChatColor.DARK_PURPLE + " " + msg);
	}

	/**
	 * Admin msg
	 */
	public static void playerAdminMsg(CommandSender sender, String msg) {
		if (sender instanceof Player) sender.sendMessage(prefixTMColor + " " + msg);
		else if (sender instanceof CommandBlock) sender.sendMessage(prefixTM + " " + msg);
	}

	/**
	 * Player chat msg
	 */
	public static void playerChatMsg(Player p, String prefix, String msg) {
		p.sendMessage(prefix + ChatColor.RESET + " " + msg);
	}

	/**
	 * Player title msg (with config.yml values)
	 */
	public static void playerTitleMsg(Player p, String title, String subtitle) {
		int fadeIn = MainTM.getInstance().langConf.getInt(LG_TITLES + "." + LG_FADEIN);
		int stay = MainTM.getInstance().langConf.getInt(LG_TITLES + "." + LG_STAY);
		int fadeOut = MainTM.getInstance().langConf.getInt(LG_TITLES + "." + LG_FADEOUT);
		playerTitleMsg(p, title, subtitle, fadeIn, stay, fadeOut);
	}

	/**
	 * Player title msg (with custom values)
	 */
	@SuppressWarnings("deprecation")
	public static void playerTitleMsg(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {		
		if (serverMcVersion >= reqMcVForNewSendTitleMsg) { // Check if MC version is at least 1.16.0
			p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		} else {
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			String commandTitleTimes = "title " + p.getName() + " times " + fadeIn + " " + stay + " " + fadeOut;
			String scfb = p.getWorld().getGameRuleValue(GR_SEND_COMMAND_FEEDBACK);
			if (scfb.equalsIgnoreCase(ARG_TRUE)) p.getWorld().setGameRuleValue(GR_SEND_COMMAND_FEEDBACK, ARG_FALSE);
			Bukkit.dispatchCommand(console, commandTitleTimes);
			p.sendTitle(title, subtitle);
			if (scfb.equalsIgnoreCase(ARG_TRUE)) p.getWorld().setGameRuleValue(GR_SEND_COMMAND_FEEDBACK, ARG_TRUE);
		}
	}
	
	/**
	 * Player action bar msg
	 */
	public static void playerActionbarMsg(Player p, String msg) {
		if (serverMcVersion < reqMcVForActionbarMsg) { // Check if MC version is at least 1.8.0
			MsgHandler.infoMsg(noActionbarMsg);
		
		} else if (serverType.equalsIgnoreCase(ARG_BUKKIT)) { // ... or CraftBukkit since MC 1.9
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			String command = ARG_TITLE + " " + p.getName() + " " + ARG_ACTIONBAR + " \"" + msg + "\"";
			Bukkit.dispatchCommand(console, command);
		} else { // ... or Spigot and forks since MC 1.9
			HiddenClassHandler.playerActionbarMsg(p, msg);
		}
	}

};