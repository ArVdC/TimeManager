package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

import net.vdcraft.arvdc.timemanager.MainTM;

public class MsgHandler extends MainTM {

	/**
	 * Info msg
	 */
	public static void infoMsg(String msg) {
		Bukkit.getLogger().info(prefixTM + " " + msg);
	}

	/**
	 * Warning msg
	 */
	public static void warnMsg(String msg) {
		Bukkit.getLogger().warning(prefixTM + " " + msg);
	}

	/**
	 * Error msg
	 */
	public static void errorMsg(String msg) {
		Bukkit.getLogger().severe(prefixTM + " " + msg);
	}

	/**
	 * Colored msg
	 */
	public static void colorMsg(String msg) {
		Bukkit.getServer().getConsoleSender().sendMessage(prefixTM + " " + msg);
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
		if (devMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + "§9 " + msg);
	}

	/**
	 * Timer msg
	 */
	public static void timerMsg(String msg) {
		if (timerMode) Bukkit.getServer().getConsoleSender().sendMessage(prefixDebugMode + "§5 " + msg);
	}

	/**
	 * Player msg
	 */
	public static void playerMsg(CommandSender sender, String msg) {
		if (sender instanceof Player) sender.sendMessage(prefixTMColor + " " + msg);
	}

};