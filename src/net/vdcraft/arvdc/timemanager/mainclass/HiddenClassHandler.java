package net.vdcraft.arvdc.timemanager.mainclass;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

// Avoid compatibility problems with CraftBukkit when importing some packages from Spigot API
public class HiddenClassHandler extends MsgHandler {

	/**
	 * Player action bar msg for Spigot and forks
	 */
	public static void playerActionbarMsg(Player p, String msg) {
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
	}

};
