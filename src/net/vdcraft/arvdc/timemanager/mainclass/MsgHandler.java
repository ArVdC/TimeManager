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
	 * Player title msg
	 */
	@SuppressWarnings("deprecation")
	public static void playerTitleMsg(Player p, String title, String subtitle) {
		int fadeIn = MainTM.getInstance().langConf.getInt(CF_TITLES + "." + CF_FADEIN);
		int stay = MainTM.getInstance().langConf.getInt(CF_TITLES + "." + CF_STAY);
		int fadeOut = MainTM.getInstance().langConf.getInt(CF_TITLES + "." + CF_FADEOUT);
		if (serverMcVersion >= reqMcVForNewSendTitleMsg) p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		else p.sendTitle(title, subtitle);
	}
	
	/**
	 * Player action bar msg
	 */
	public static void playerActionbarMsg(Player p, String msg) {
		// Up to MC 1.8
		if (serverMcVersion < reqMcVForActionbarMsg) {
			MsgHandler.infoMsg(noActionbarMsg);
		// CraftBukkit since MC 1.9
		} else if (serverType.equalsIgnoreCase(ARG_BUKKIT)) {
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			String command = ARG_TITLE + " " + p.getName() + " " + ARG_ACTIONBAR + " \"" + msg + "\"";
			Bukkit.dispatchCommand(console, command);
		// Spigot and forks since MC 1.9
		} else {
			HiddenClassHandler.playerActionbarMsg(p, msg);
		}
	}

};