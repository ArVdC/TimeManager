package net.vdcraft.arvdc.timemanager.mainclass;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.cmdadmin.TmNow;

/**
 * Custom "pocket watch" item that runs the {@code /now} action on right-click.
 *
 * Implementation:
 * <ul>
 *   <li>Item is a {@link Material#CLOCK} with a PersistentDataContainer flag
 *       under namespace {@code timemanager:now-item}.</li>
 *   <li>{@link #createItem()} builds a fresh stack with display name + lore.</li>
 *   <li>{@link #onUse(PlayerInteractEvent)} catches right-click on this item
 *       and calls {@link TmNow} so the player gets the same chat/title output
 *       as {@code /tm now}.</li>
 * </ul>
 *
 * Config keys (under root):
 * <pre>
 * now-item:
 *   enabled: true
 *   display-name: "&#FFD700&l✦ Pocket Watch"
 *   lore:
 *     - "&7Right-click to check the current time"
 *   cooldown-ticks: 20   # client cooldown after each use
 * </pre>
 *
 * Admin command: {@code /tm nowitem} gives one to the executing player. The
 * command lives inside {@link net.vdcraft.arvdc.timemanager.cmdadmin.TmNowItem}.
 */
public class NowItemHandler implements Listener {

	private static final String CF_NOW_ITEM_ENABLED = "now-item.enabled";
	private static final String CF_NOW_ITEM_NAME    = "now-item.display-name";
	private static final String CF_NOW_ITEM_LORE    = "now-item.lore";
	private static final String CF_NOW_ITEM_CD      = "now-item.cooldown-ticks";

	/** PDC key on the item to identify it as a TimeManager now-item. */
	public static NamespacedKey markerKey() {
		return new NamespacedKey(MainTM.getInstance(), "now-item");
	}

	public static void ensureDefaults() {
		if (!MainTM.getInstance().getConfig().contains(CF_NOW_ITEM_ENABLED)) {
			MainTM.getInstance().getConfig().set(CF_NOW_ITEM_ENABLED, true);
		}
		if (!MainTM.getInstance().getConfig().contains(CF_NOW_ITEM_NAME)) {
			MainTM.getInstance().getConfig().set(CF_NOW_ITEM_NAME, "&6&l✦ Pocket Watch");
		}
		if (!MainTM.getInstance().getConfig().contains(CF_NOW_ITEM_LORE)) {
			List<String> defLore = new ArrayList<>();
			defLore.add("&7Right-click to check the time.");
			MainTM.getInstance().getConfig().set(CF_NOW_ITEM_LORE, defLore);
		}
		if (!MainTM.getInstance().getConfig().contains(CF_NOW_ITEM_CD)) {
			MainTM.getInstance().getConfig().set(CF_NOW_ITEM_CD, 20);
		}
	}

	/** Build a fresh pocket-watch ItemStack. */
	public static ItemStack createItem() {
		ItemStack stack = new ItemStack(Material.CLOCK, 1);
		ItemMeta meta = stack.getItemMeta();
		if (meta != null) {
			String name = MainTM.getInstance().getConfig().getString(CF_NOW_ITEM_NAME,
					"&6&l✦ Pocket Watch");
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

			List<String> loreRaw = MainTM.getInstance().getConfig().getStringList(CF_NOW_ITEM_LORE);
			List<String> lore = new ArrayList<>(loreRaw.size());
			for (String l : loreRaw) lore.add(ChatColor.translateAlternateColorCodes('&', l));
			meta.setLore(lore);

			PersistentDataContainer pdc = meta.getPersistentDataContainer();
			pdc.set(markerKey(), PersistentDataType.BYTE, (byte) 1);

			stack.setItemMeta(meta);
		}
		return stack;
	}

	/** Check whether a stack is a TimeManager now-item. */
	public static boolean isNowItem(ItemStack stack) {
		if (stack == null || stack.getType() != Material.CLOCK) return false;
		ItemMeta meta = stack.getItemMeta();
		if (meta == null) return false;
		PersistentDataContainer pdc = meta.getPersistentDataContainer();
		return pdc.has(markerKey(), PersistentDataType.BYTE);
	}

	@EventHandler
	public void onUse(PlayerInteractEvent e) {
		if (!MainTM.getInstance().getConfig().getBoolean(CF_NOW_ITEM_ENABLED, true)) return;
		if (e.getHand() == null) return;
		if (!e.getAction().name().startsWith("RIGHT_CLICK")) return;

		Player p = e.getPlayer();
		ItemStack stack = p.getInventory().getItem(e.getHand());
		if (!isNowItem(stack)) return;

		if (!p.hasPermission("timemanager.now-item.use")) {
			p.sendMessage(ChatColor.RED + "You don't have permission to use this item.");
			return;
		}

		// Apply a short cooldown on the CLOCK material so the player can't
		// spam right-click. Cooldown is per-player on the Material.
		int cd = MainTM.getInstance().getConfig().getInt(CF_NOW_ITEM_CD, 20);
		if (p.hasCooldown(Material.CLOCK)) return;
		if (cd > 0) p.setCooldown(Material.CLOCK, cd);

		// Trigger the same output as /tm now msg <player> (chat message)
		try {
			TmNow.cmdNow((CommandSender) p, "msg", p.getName());
		} catch (Throwable t) {
			// Fallback: a minimal direct line if TmNow throws (e.g. missing
			// language file key).
			p.sendMessage(ChatColor.GOLD + "Tick: " + ChatColor.YELLOW + p.getWorld().getTime());
		}
	}
}
