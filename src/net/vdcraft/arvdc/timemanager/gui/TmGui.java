package net.vdcraft.arvdc.timemanager.gui;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.seasons.GuiI18n;
import net.vdcraft.arvdc.timemanager.seasons.SeasonPreset;
import net.vdcraft.arvdc.timemanager.seasons.SeasonService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Localised admin settings panel. Click identification uses persistent
 * data on each ItemStack (key: tm_action) so the GUI can be translated
 * without breaking dispatch. The translated string is the display name
 * shown to the player; the action ID is what the click handler reads.
 *
 * Three pages — Server, World (current world), Seasons. Currently-active
 * options glow (enchant glint, hidden flag). Slot 0 of every page holds
 * the Reset-to-vanilla button.
 */
public class TmGui implements Listener {

    private static final String TITLE_TAG = ChatColor.DARK_AQUA + "TimeManager";

    private static final double[] SPEED_PRESETS = {0.5, 1.0, 2.0, 4.0, 8.0};
    private static final int[] YEAR_PRESETS = {8, 16, 32, 64};
    private static final long[] REFRESH_PRESETS = {5L, 20L, 100L};

    private static final NamespacedKey ACTION_KEY =
            new NamespacedKey(MainTM.getInstance(), "action");

    /** Tracks which page each viewing player is on, so nav clicks know where to go. */
    private static final Map<UUID, Integer> PAGE = new HashMap<>();

    public static void openFor(CommandSender sender) {
        if (!(sender instanceof Player p)) {
            MsgHandler.playerAdminMsg(sender, ChatColor.RED + GuiI18n.s("error-in-game-only"));
            return;
        }
        if (!p.isOp() && !p.hasPermission("timemanager.admin")) {
            p.sendMessage(ChatColor.RED + GuiI18n.s("error-admin-only"));
            return;
        }
        openPage(p, 0);
    }

    private static void openPage(Player p, int page) {
        Inventory inv = switch (page) {
            case 1 -> buildWorld(p);
            case 2 -> buildSeasons(p);
            default -> buildServer(p);
        };
        // p.openInventory fires InventoryCloseEvent for the old inv FIRST,
        // which runs onClose -> PAGE.remove(). Set PAGE after, otherwise
        // the next click sees page 0 and navigation never advances.
        p.openInventory(inv);
        PAGE.put(p.getUniqueId(), page);
    }

    /* =========================================================
       PAGE 1: SERVER
       ========================================================= */
    private static Inventory buildServer(Player p) {
        Inventory inv = Bukkit.createInventory(p, 54,
                TITLE_TAG + " — " + ChatColor.stripColor(GuiI18n.s("page-server")));

        boolean debug    = MainTM.getInstance().getConfig().getBoolean("debugMode", false);
        boolean multi    = "true".equalsIgnoreCase(MainTM.getInstance().getConfig().getString("multiLang", "false"));
        boolean useCmdsB = "true".equalsIgnoreCase(MainTM.getInstance().getConfig().getString("useCmds", "false"));
        long refreshRate = MainTM.getInstance().getConfig().getLong(MainTM.CF_REFRESHRATE, 5);

        // Header
        inv.setItem(4, action(item(Material.CLOCK,
                GuiI18n.f("header-title", MainTM.getInstance().getDescription().getVersion()),
                List.of(GuiI18n.s("header-lore-1"),
                        "",
                        GuiI18n.f("header-lore-refresh", refreshRate),
                        GuiI18n.f("header-lore-debug", debug ? GuiI18n.s("on-text") : GuiI18n.s("off-text")),
                        GuiI18n.f("header-lore-multilang", multi ? GuiI18n.s("on-text") : GuiI18n.s("off-text")),
                        GuiI18n.f("header-lore-usecmds", useCmdsB ? GuiI18n.s("on-text") : GuiI18n.s("off-text")))), "header"));

        // Row 1: Reload
        inv.setItem(10, action(item(Material.LECTERN,        GuiI18n.s("section-reload"),    List.of()), "section"));
        inv.setItem(11, action(item(Material.LECTERN,        GuiI18n.s("reload-all"),
                List.of("&8/tm reload all")), "reload-all"));
        inv.setItem(12, action(item(Material.WRITABLE_BOOK,  GuiI18n.s("reload-config"),
                List.of("&8/tm reload config")), "reload-config"));
        inv.setItem(13, action(item(Material.BOOK,           GuiI18n.s("reload-lang"),
                List.of("&8/tm reload lang")), "reload-lang"));
        inv.setItem(14, action(item(Material.WRITTEN_BOOK,   GuiI18n.s("reload-cmds"),
                List.of("&8/tm reload cmds")), "reload-cmds"));

        // Row 2: Checks
        inv.setItem(19, action(item(Material.SPYGLASS,    GuiI18n.s("section-checks"),     List.of()), "section"));
        inv.setItem(20, action(item(Material.SPYGLASS,    GuiI18n.s("check-config"),
                List.of("&8/tm checkConfig")), "check-config"));
        inv.setItem(21, action(item(Material.COMPASS,     GuiI18n.s("check-time"),
                List.of("&8/tm checkTime all")), "check-time"));
        inv.setItem(22, action(item(Material.ENDER_EYE,   GuiI18n.s("check-update"),
                List.of("&8/tm checkUpdate")), "check-update"));
        inv.setItem(23, action(item(Material.NAME_TAG,    GuiI18n.s("show-placeholders"),
                List.of("&7%tm_*%", "&8/tm placeholders")), "show-placeholders"));

        // Row 3: Toggles
        inv.setItem(28, action(item(Material.LEVER, GuiI18n.s("section-toggles"), List.of()), "section"));
        ItemStack debugItem = item(Material.REDSTONE_TORCH,
                debug ? GuiI18n.s("debug-on") : GuiI18n.s("debug-off"),
                List.of("", GuiI18n.s("click-toggle")));
        if (debug) glow(debugItem);
        inv.setItem(29, action(debugItem, "toggle-debug"));

        ItemStack multiItem = item(Material.OAK_SIGN,
                multi ? GuiI18n.s("multilang-on") : GuiI18n.s("multilang-off"),
                List.of("", GuiI18n.s("click-toggle")));
        if (multi) glow(multiItem);
        inv.setItem(30, action(multiItem, "toggle-multilang"));

        ItemStack cmdsItem = item(Material.COMMAND_BLOCK,
                useCmdsB ? GuiI18n.s("usecmds-on") : GuiI18n.s("usecmds-off"),
                List.of("", GuiI18n.s("click-toggle")));
        if (useCmdsB) glow(cmdsItem);
        inv.setItem(31, action(cmdsItem, "toggle-usecmds"));

        // Row 4: Refresh rate
        inv.setItem(37, action(item(Material.CLOCK, GuiI18n.s("section-refresh-rate"), List.of()), "section"));
        int slot = 38;
        for (long ticks : REFRESH_PRESETS) {
            ItemStack it = item(refreshIcon(ticks), "&f" + ticks + "t",
                    List.of("", GuiI18n.s("click-apply")));
            if (refreshRate == ticks) glow(it);
            inv.setItem(slot++, action(it, "refresh-" + ticks));
        }

        // Row 5: Misc
        inv.setItem(46, action(item(Material.CLOCK, GuiI18n.s("give-pocket-watch"),
                List.of("&8/tm nowitem")), "give-nowitem"));
        inv.setItem(52, action(item(Material.PAINTING, GuiI18n.s("broadcast-time"),
                List.of("&8/tm now msg all")), "broadcast-time"));

        inv.setItem(0, resetButton());
        inv.setItem(8, previewItem(p));
        navRow(inv, 0);
        return inv;
    }

    /**
     * Top-right summary on Page 1. Non-clickable (action = "header" so the
     * dispatch ignores it). Shows real time, world time, day/night speed,
     * and current season in one glance.
     */
    private static ItemStack previewItem(Player p) {
        String world = p.getWorld().getName();
        long tick = p.getWorld().getTime() % 24000;
        long ticksFromMidnight = (tick + 6000) % 24000;
        long hh = (ticksFromMidnight * 24L) / 24000L;
        long mm = ((ticksFromMidnight * 24L * 60L) / 24000L) % 60L;
        String mcClock = String.format("%02d:%02d", hh, mm);

        java.time.LocalTime now = java.time.LocalTime.now();
        String realClock = String.format("%02d:%02d", now.getHour(), now.getMinute());

        SeasonService svc = MainTM.getInstance().seasonService;
        String seasonLine;
        if (svc != null && svc.enabled()) {
            seasonLine = GuiI18n.f("overview-season-on",
                    svc.currentSeason(p.getWorld()).name(),
                    Math.round(svc.daylightFraction(p.getWorld()) * 100));
        } else {
            seasonLine = GuiI18n.s("overview-season-off");
        }

        double daySpeed = MainTM.getInstance().getConfig().getDouble(
                MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_D_SPEED, 1.0);
        double nightSpeed = MainTM.getInstance().getConfig().getDouble(
                MainTM.CF_WORLDSLIST + "." + world + "." + MainTM.CF_N_SPEED, 1.0);

        List<String> lore = new ArrayList<>();
        lore.add(GuiI18n.f("overview-real-time", realClock));
        lore.add(GuiI18n.f("overview-world-time", world, mcClock));
        lore.add(GuiI18n.f("overview-speeds", daySpeed, nightSpeed));
        lore.add(seasonLine);
        lore.add("");
        lore.add(GuiI18n.s("overview-info-line"));
        return action(item(Material.RECOVERY_COMPASS, GuiI18n.s("overview-title"), lore), "header");
    }

    /* =========================================================
       PAGE 2: WORLD
       ========================================================= */
    private static Inventory buildWorld(Player p) {
        Inventory inv = Bukkit.createInventory(p, 54,
                TITLE_TAG + " — " + ChatColor.stripColor(GuiI18n.s("page-world")));
        String world = p.getWorld().getName();
        String base = MainTM.CF_WORLDSLIST + "." + world + ".";

        double curSpeed = MainTM.getInstance().getConfig().getDouble(base + MainTM.CF_D_SPEED, 1.0);
        double curNight = MainTM.getInstance().getConfig().getDouble(base + MainTM.CF_N_SPEED, 1.0);
        boolean sync    = "true".equalsIgnoreCase(MainTM.getInstance().getConfig().getString(base + MainTM.CF_SYNC, "true"));
        boolean sleep   = "true".equalsIgnoreCase(MainTM.getInstance().getConfig().getString(base + MainTM.CF_SLEEP, "true"));
        String locked   = MainTM.getInstance().getConfig().getString(base + MainTM.CF_LOCKTIME, "");
        boolean isLocked = locked != null && !locked.isEmpty();
        String nightSkipMode = MainTM.getInstance().getConfig().getString(base + MainTM.CF_NIGHTSKIP_MODE, "default");
        boolean animOn = "animation".equalsIgnoreCase(nightSkipMode);

        // Header
        inv.setItem(4, action(item(Material.GRASS_BLOCK, GuiI18n.f("world-header", world),
                List.of("",
                        GuiI18n.f("world-day-speed", curSpeed),
                        GuiI18n.f("world-night-speed", curNight),
                        GuiI18n.f("world-sync-line", sync ? GuiI18n.s("on-text") : GuiI18n.s("off-text")),
                        GuiI18n.f("world-sleep-line", sleep ? GuiI18n.s("on-text") : GuiI18n.s("off-text")),
                        GuiI18n.f("world-lock-line", isLocked ? GuiI18n.s("lock-active") : GuiI18n.s("lock-off")),
                        GuiI18n.f("world-anim-line", animOn ? GuiI18n.s("on-text") : GuiI18n.s("anim-vanilla")))), "header"));

        // Speed presets
        inv.setItem(9, action(item(Material.SUNFLOWER, GuiI18n.s("section-speed"), List.of()), "section"));
        int slot = 10;
        for (double speed : SPEED_PRESETS) {
            ItemStack it = item(speedIcon(speed), "&f" + formatSpeed(speed) + GuiI18n.s("speed-suffix"),
                    List.of(speedLabel(speed), "", GuiI18n.s("click-apply")));
            if (Math.abs(curSpeed - speed) < 0.001 && Math.abs(curNight - speed) < 0.001) glow(it);
            inv.setItem(slot++, action(it, "speed-" + speed));
        }

        // Toggles
        ItemStack syncItem = item(Material.COMPASS,
                sync ? GuiI18n.s("sync-on") : GuiI18n.s("sync-off"),
                List.of("", GuiI18n.s("click-toggle")));
        if (sync) glow(syncItem);
        inv.setItem(16, action(syncItem, "toggle-sync"));

        ItemStack sleepItem = item(Material.RED_BED,
                sleep ? GuiI18n.s("sleep-on") : GuiI18n.s("sleep-off"),
                List.of("", GuiI18n.s("click-toggle")));
        if (sleep) glow(sleepItem);
        inv.setItem(17, action(sleepItem, "toggle-sleep"));

        // Time jumps
        inv.setItem(18, action(item(Material.RECOVERY_COMPASS, GuiI18n.s("quick-jumps"), List.of()), "section"));
        inv.setItem(19, action(item(Material.ORANGE_DYE,  GuiI18n.s("time-dawn"),
                List.of("&7t=23000", "", GuiI18n.s("click-jump"))),     "time-23000"));
        inv.setItem(20, action(item(Material.YELLOW_DYE,  GuiI18n.s("time-morning"),
                List.of("&7t=1000", "", GuiI18n.s("click-jump"))),       "time-1000"));
        inv.setItem(21, action(item(Material.GLOWSTONE,   GuiI18n.s("time-noon"),
                List.of("&7t=6000", "", GuiI18n.s("click-jump"))),       "time-6000"));
        inv.setItem(22, action(item(Material.CAMPFIRE,    GuiI18n.s("time-sunset"),
                List.of("&7t=12000", "", GuiI18n.s("click-jump"))),     "time-12000"));
        inv.setItem(23, action(item(Material.BLACK_DYE,   GuiI18n.s("time-night"),
                List.of("&7t=14000", "", GuiI18n.s("click-jump"))),     "time-14000"));
        inv.setItem(24, action(item(Material.OBSIDIAN,    GuiI18n.s("time-midnight"),
                List.of("&7t=18000", "", GuiI18n.s("click-jump"))),     "time-18000"));

        // Lock / animation / date / elapsed / resync
        ItemStack lockItem = item(Material.IRON_BARS,
                isLocked ? GuiI18n.s("unlock-world") : GuiI18n.s("lock-world"),
                List.of("", GuiI18n.s("click-toggle")));
        if (isLocked) glow(lockItem);
        inv.setItem(27, action(lockItem, "toggle-lock"));

        ItemStack animItem = item(Material.FIREWORK_ROCKET,
                animOn ? GuiI18n.s("anim-on") : GuiI18n.s("anim-off"),
                List.of("", GuiI18n.s("click-toggle")));
        if (animOn) glow(animItem);
        inv.setItem(28, action(animItem, "toggle-anim"));

        inv.setItem(29, action(item(Material.WRITABLE_BOOK, GuiI18n.s("set-date-today"),
                List.of("&8/tm set date today")), "set-date-today"));
        inv.setItem(30, action(item(Material.SOUL_LANTERN, GuiI18n.s("reset-elapsed"),
                List.of("&8/tm set elapsedDays 0")), "reset-elapsed"));
        inv.setItem(31, action(item(Material.HOPPER, GuiI18n.s("resync-world"),
                List.of("&8/tm resync")), "resync-world"));
        inv.setItem(32, action(item(Material.NETHER_STAR, GuiI18n.s("check-this-world"),
                List.of("&8/tm checkTime")), "check-this-world"));

        inv.setItem(40, action(item(Material.RECOVERY_COMPASS, GuiI18n.s("now-button"),
                List.of("&8/now")), "now-button"));

        inv.setItem(0, resetButton());
        navRow(inv, 1);
        return inv;
    }

    /* =========================================================
       PAGE 3: SEASONS
       ========================================================= */
    private static Inventory buildSeasons(Player p) {
        Inventory inv = Bukkit.createInventory(p, 54,
                TITLE_TAG + " — " + ChatColor.stripColor(GuiI18n.s("page-seasons")));
        SeasonService svc = MainTM.getInstance().seasonService;
        boolean enabled = svc != null && svc.enabled();
        SeasonPreset cur = svc == null ? SeasonPreset.TEMPERATE : svc.preset();
        int year = svc == null ? 32 : svc.yearLengthDays();

        String state = enabled ? GuiI18n.s("seasons-active") : GuiI18n.s("seasons-disabled");
        inv.setItem(4, action(item(Material.WHEAT, GuiI18n.f("seasons-header", state),
                List.of(GuiI18n.f("season-current-preset", cur.label()),
                        GuiI18n.f("season-year-length", year),
                        enabled ? GuiI18n.f("season-status-line", svc.describe(p.getWorld())) : "")), "header"));

        SeasonPreset[] presets = {
                SeasonPreset.EQUATORIAL, SeasonPreset.MEDITERRANEAN,
                SeasonPreset.TEMPERATE, SeasonPreset.SUBARCTIC, SeasonPreset.ARCTIC,
                SeasonPreset.CUSTOM
        };
        int[] slots = {19, 20, 21, 22, 23, 24};
        for (int i = 0; i < presets.length && i < slots.length; i++) {
            SeasonPreset preset = presets[i];
            ItemStack it = item(iconFor(preset), "&f" + preset.label(),
                    List.of(GuiI18n.f("preset-winter-daylight", Math.round(preset.winterDaylight() * 100)),
                            GuiI18n.f("preset-summer-daylight", Math.round(preset.summerDaylight() * 100)),
                            "",
                            GuiI18n.s("click-apply")));
            if (preset == cur) glow(it);
            inv.setItem(slots[i], action(it, "preset-" + preset.name()));
        }

        // Hemisphere toggle (replaces _SOUTH preset variants)
        boolean south = svc != null && svc.isSouthernHemisphere();
        ItemStack hemi = item(
                south ? Material.PRISMARINE_CRYSTALS : Material.SUNFLOWER,
                south ? GuiI18n.s("hemisphere-south") : GuiI18n.s("hemisphere-north"),
                List.of("",
                        south ? "&7" + GuiI18n.s("hemisphere-south-desc")
                              : "&7" + GuiI18n.s("hemisphere-north-desc"),
                        "",
                        GuiI18n.s("click-toggle")));
        if (south) glow(hemi);
        inv.setItem(16, action(hemi, "toggle-hemisphere"));

        int yearSlot = 37;
        for (int yp : YEAR_PRESETS) {
            ItemStack it = item(Material.MAP, GuiI18n.f("year-button", yp),
                    List.of("", GuiI18n.s("click-set")));
            if (yp == year) glow(it);
            inv.setItem(yearSlot++, action(it, "year-" + yp));
        }

        ItemStack toggle = item(enabled ? Material.REDSTONE_BLOCK : Material.EMERALD_BLOCK,
                enabled ? GuiI18n.s("disable-seasons") : GuiI18n.s("enable-seasons"),
                List.of());
        if (enabled) glow(toggle);
        inv.setItem(25, action(toggle, enabled ? "seasons-disable" : "seasons-enable"));

        inv.setItem(34, action(item(Material.PAPER, GuiI18n.s("force-apply"), List.of()), "seasons-apply"));

        inv.setItem(0, resetButton());
        navRow(inv, 2);
        return inv;
    }

    /* =========================================================
       NAV + HELPERS
       ========================================================= */
    private static void navRow(Inventory inv, int currentPage) {
        int base = inv.getSize() - 9;
        String[] tabKeys = {"page-server", "page-world", "page-seasons"};
        for (int i = 0; i < 3; i++) {
            Material m = (i == currentPage) ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE;
            ItemStack pane = item(m, "&7" + GuiI18n.s(tabKeys[i]), List.of());
            if (i == currentPage) glow(pane);
            inv.setItem(base + 3 + i, action(pane, "nav-tab-" + i));
        }
        if (currentPage > 0) {
            inv.setItem(base, action(item(Material.ARROW, GuiI18n.s("nav-prev"), List.of()), "nav-prev"));
        }
        if (currentPage < 2) {
            inv.setItem(base + 8, action(item(Material.ARROW, GuiI18n.s("nav-next"), List.of()), "nav-next"));
        }
    }

    private static ItemStack resetButton() {
        ItemStack it = item(Material.BARRIER, GuiI18n.s("reset-button"), GuiI18n.l("reset-lore"));
        return action(it, "reset-vanilla");
    }

    private static ItemStack item(Material mat, String name, List<String> lore) {
        Material safe = mat;
        try { new ItemStack(safe).getType(); } catch (Throwable t) { safe = Material.PAPER; }
        ItemStack it = new ItemStack(safe);
        ItemMeta meta = it.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> out = new ArrayList<>(lore.size());
            for (String l : lore) {
                if (l == null || l.isEmpty()) { out.add(""); continue; }
                out.add(ChatColor.translateAlternateColorCodes('&', l));
            }
            meta.setLore(out);
            it.setItemMeta(meta);
        }
        return it;
    }

    private static ItemStack action(ItemStack it, String actionId) {
        if (it == null) return null;
        ItemMeta meta = it.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(ACTION_KEY, PersistentDataType.STRING, actionId);
            it.setItemMeta(meta);
        }
        return it;
    }

    /** Chat feedback + UI click sound after a successful action. Lang key
     *  + a single string value substituted into the template via {0}. */
    private static void chatConfirm(Player p, String key, Object value) {
        String prefix = ChatColor.GRAY + "[TM] ";
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                prefix + GuiI18n.f(key, value)));
        try {
            p.playSound(p.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 0.5f, 1.4f);
        } catch (Throwable ignored) {}
    }

    private static String actionOf(ItemStack it) {
        if (it == null) return null;
        ItemMeta meta = it.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(ACTION_KEY, PersistentDataType.STRING);
    }

    private static void glow(ItemStack it) {
        ItemMeta meta = it.getItemMeta();
        if (meta == null) return;
        try { meta.addEnchant(Enchantment.UNBREAKING, 1, true); } catch (Throwable t) {}
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        it.setItemMeta(meta);
    }

    private static String formatSpeed(double s) {
        return s == (long) s ? String.valueOf((long) s) : String.valueOf(s);
    }

    private static String speedLabel(double s) {
        String key = switch ((int) Math.round(s * 10)) {
            case 5  -> "speed-label-half";
            case 10 -> "speed-label-vanilla";
            case 20 -> "speed-label-double";
            case 40 -> "speed-label-quad";
            case 80 -> "speed-label-octuple";
            default -> "speed-label-custom";
        };
        return GuiI18n.s(key);
    }

    private static Material speedIcon(double s) {
        if (s < 1.0) return Material.LIGHT_BLUE_DYE;
        if (s == 1.0) return Material.WHITE_DYE;
        if (s <= 2.0) return Material.YELLOW_DYE;
        if (s <= 4.0) return Material.ORANGE_DYE;
        return Material.RED_DYE;
    }

    private static Material refreshIcon(long t) {
        if (t <= 1) return Material.GUNPOWDER;
        if (t <= 5) return Material.SUGAR;
        if (t <= 20) return Material.WHEAT_SEEDS;
        return Material.STONE;
    }

    private static Material iconFor(SeasonPreset p) {
        return switch (p) {
            case EQUATORIAL -> Material.JUNGLE_LEAVES;
            case MEDITERRANEAN -> Material.BIRCH_LEAVES;
            case TEMPERATE -> Material.OAK_LEAVES;
            case SUBARCTIC -> Material.SPRUCE_LEAVES;
            case ARCTIC -> Material.SNOW_BLOCK;
            case CUSTOM -> Material.WRITABLE_BOOK;
        };
    }

    private static void resetToVanilla() {
        var cfg = MainTM.getInstance().getConfig();
        var wl = cfg.getConfigurationSection(MainTM.CF_WORLDSLIST);
        if (wl != null) {
            for (String w : wl.getKeys(false)) {
                String base = MainTM.CF_WORLDSLIST + "." + w + ".";
                cfg.set(base + MainTM.CF_D_SPEED, 1.0);
                cfg.set(base + MainTM.CF_N_SPEED, 1.0);
                cfg.set(base + MainTM.CF_SPEED, 1.0);
                cfg.set(base + MainTM.CF_SYNC, "true");
                cfg.set(base + MainTM.CF_SLEEP, "true");
                cfg.set(base + MainTM.CF_LOCKTIME, "");
                cfg.set(base + MainTM.CF_NIGHTSKIP_MODE, "default");
                cfg.set(base + MainTM.CF_FIRSTSTARTTIME, "default");
                cfg.set(base + MainTM.CF_START, 0);
            }
        }
        cfg.set("debugMode", false);
        cfg.set("multiLang", "false");
        cfg.set("useCmds", "false");
        cfg.set(MainTM.CF_REFRESHRATE, 5);
        cfg.set("seasons.enabled", false);
        MainTM.getInstance().saveConfig();
        if (MainTM.getInstance().seasonScheduler != null) {
            MainTM.getInstance().seasonScheduler.stop();
        }
    }

    /* =========================================================
       CLICK HANDLER  (action-ID based, language-agnostic)
       ========================================================= */
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (!title.startsWith(TITLE_TAG)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!p.isOp() && !p.hasPermission("timemanager.admin")) return;

        String act = actionOf(e.getCurrentItem());
        if (act == null || "section".equals(act) || "header".equals(act)) return;

        // Nav
        if ("nav-prev".equals(act)) {
            int cur = PAGE.getOrDefault(p.getUniqueId(), 0);
            openPage(p, Math.max(0, cur - 1));
            return;
        }
        if ("nav-next".equals(act)) {
            int cur = PAGE.getOrDefault(p.getUniqueId(), 0);
            openPage(p, Math.min(2, cur + 1));
            return;
        }
        if (act.startsWith("nav-tab-")) {
            int idx = Integer.parseInt(act.substring("nav-tab-".length()));
            openPage(p, idx);
            return;
        }

        // Reset (works on every page)
        if ("reset-vanilla".equals(act)) {
            resetToVanilla();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', GuiI18n.s("reset-msg")));
            int cur = PAGE.getOrDefault(p.getUniqueId(), 0);
            openPage(p, cur);
            return;
        }

        String world = p.getWorld().getName();
        String base = MainTM.CF_WORLDSLIST + "." + world + ".";

        switch (act) {
            // Page 1
            case "reload-all"        -> { p.performCommand("tm reload all"); chatConfirm(p, "chat-reloaded", "all"); openPage(p, 0); return; }
            case "reload-config"     -> { p.performCommand("tm reload config"); chatConfirm(p, "chat-reloaded", "config"); openPage(p, 0); return; }
            case "reload-lang"       -> { p.performCommand("tm reload lang"); chatConfirm(p, "chat-reloaded", "lang"); openPage(p, 0); return; }
            case "reload-cmds"       -> { p.performCommand("tm reload cmds"); chatConfirm(p, "chat-reloaded", "cmds"); openPage(p, 0); return; }
            case "check-config"      -> { p.performCommand("tm checkConfig"); return; }
            case "check-time"        -> { p.performCommand("tm checkTime all"); return; }
            case "check-update"      -> { p.performCommand("tm checkUpdate"); return; }
            case "show-placeholders" -> { p.performCommand("tm placeholders"); return; }
            case "give-nowitem"      -> { p.performCommand("tm nowitem"); return; }
            case "broadcast-time"    -> { p.performCommand("tm now msg all"); return; }
            case "toggle-debug" -> {
                boolean cur = MainTM.getInstance().getConfig().getBoolean("debugMode", false);
                p.performCommand("tm set debugMode " + (!cur));
                chatConfirm(p, "chat-toggled", "Debug = " + (!cur));
                openPage(p, 0); return;
            }
            case "toggle-multilang" -> {
                boolean cur = "true".equalsIgnoreCase(MainTM.getInstance().getConfig().getString("multiLang", "false"));
                p.performCommand("tm set multiLang " + (!cur));
                chatConfirm(p, "chat-toggled", "Multi-language = " + (!cur));
                openPage(p, 0); return;
            }
            case "toggle-usecmds" -> {
                boolean cur = "true".equalsIgnoreCase(MainTM.getInstance().getConfig().getString("useCmds", "false"));
                p.performCommand("tm set useCmds " + (!cur));
                chatConfirm(p, "chat-toggled", "Scheduled cmds = " + (!cur));
                openPage(p, 0); return;
            }
            // Page 2
            case "toggle-sync" -> {
                boolean cur = "true".equalsIgnoreCase(MainTM.getInstance().getConfig().getString(base + MainTM.CF_SYNC, "true"));
                p.performCommand("tm set sync " + (!cur) + " " + world);
                chatConfirm(p, "chat-toggled", "Sync = " + (!cur));
                openPage(p, 1); return;
            }
            case "toggle-sleep" -> {
                boolean cur = "true".equalsIgnoreCase(MainTM.getInstance().getConfig().getString(base + MainTM.CF_SLEEP, "true"));
                p.performCommand("tm set sleep " + (!cur) + " " + world);
                chatConfirm(p, "chat-toggled", "Sleep = " + (!cur));
                openPage(p, 1); return;
            }
            case "toggle-anim" -> {
                p.performCommand("tm animation " + world + " toggle");
                chatConfirm(p, "chat-toggled", "Sleep animation");
                openPage(p, 1); return;
            }
            case "toggle-lock" -> {
                String locked = MainTM.getInstance().getConfig().getString(base + MainTM.CF_LOCKTIME, "");
                if (locked == null || locked.isEmpty()) {
                    p.performCommand("tm lock " + world);
                    chatConfirm(p, "chat-applied", "Locked " + world);
                } else {
                    p.performCommand("tm unlock " + world);
                    chatConfirm(p, "chat-applied", "Unlocked " + world);
                }
                openPage(p, 1); return;
            }
            case "set-date-today"  -> { p.performCommand("tm set date today " + world); chatConfirm(p, "chat-applied", "Date → today"); openPage(p, 1); return; }
            case "reset-elapsed"   -> { p.performCommand("tm set elapsedDays 0 " + world); chatConfirm(p, "chat-applied", "Elapsed days = 0"); openPage(p, 1); return; }
            case "resync-world"    -> { p.performCommand("tm resync " + world); chatConfirm(p, "chat-applied", "Resynced " + world); openPage(p, 1); return; }
            case "check-this-world"-> { p.performCommand("tm checkTime " + world); return; }
            case "now-button"      -> { p.performCommand("now"); return; }
            // Page 3
            case "seasons-enable" -> {
                MainTM.getInstance().getConfig().set("seasons.enabled", true);
                MainTM.getInstance().saveConfig();
                if (MainTM.getInstance().seasonScheduler != null) MainTM.getInstance().seasonScheduler.restart();
                chatConfirm(p, "chat-toggled", "Seasons = ON");
                openPage(p, 2); return;
            }
            case "seasons-disable" -> {
                MainTM.getInstance().getConfig().set("seasons.enabled", false);
                MainTM.getInstance().saveConfig();
                if (MainTM.getInstance().seasonScheduler != null) MainTM.getInstance().seasonScheduler.stop();
                chatConfirm(p, "chat-toggled", "Seasons = OFF");
                openPage(p, 2); return;
            }
            case "seasons-apply" -> {
                if (MainTM.getInstance().seasonService != null) MainTM.getInstance().seasonService.applyToAll();
                chatConfirm(p, "chat-applied", "Seasons re-applied");
                return;
            }
            case "toggle-hemisphere" -> {
                String cur = MainTM.getInstance().getConfig().getString("seasons.hemisphere", "north");
                String next = "south".equalsIgnoreCase(cur) ? "north" : "south";
                MainTM.getInstance().getConfig().set("seasons.hemisphere", next);
                MainTM.getInstance().saveConfig();
                if (MainTM.getInstance().seasonService != null) MainTM.getInstance().seasonService.applyToAll();
                chatConfirm(p, "chat-applied", "Hemisphere = " + next);
                openPage(p, 2); return;
            }
            default -> {}
        }

        // Parameterised actions
        if (act.startsWith("refresh-")) {
            try {
                long t = Long.parseLong(act.substring("refresh-".length()));
                p.performCommand("tm set refreshRate " + t);
                chatConfirm(p, "chat-applied", "Refresh rate = " + t + "t");
                openPage(p, 0);
            } catch (NumberFormatException ignored) {}
            return;
        }
        if (act.startsWith("speed-")) {
            try {
                double s = Double.parseDouble(act.substring("speed-".length()));
                p.performCommand("tm set speed " + s + " " + world);
                chatConfirm(p, "chat-applied", "Speed = " + s + "× (" + world + ")");
                openPage(p, 1);
            } catch (NumberFormatException ignored) {}
            return;
        }
        if (act.startsWith("time-")) {
            try {
                int t = Integer.parseInt(act.substring("time-".length()));
                p.performCommand("tm set time " + t + " " + world);
                chatConfirm(p, "chat-applied", "Time = tick " + t + " (" + world + ")");
                openPage(p, 1);
            } catch (NumberFormatException ignored) {}
            return;
        }
        if (act.startsWith("year-")) {
            try {
                int y = Integer.parseInt(act.substring("year-".length()));
                MainTM.getInstance().getConfig().set("seasons.year-length-days", y);
                MainTM.getInstance().saveConfig();
                if (MainTM.getInstance().seasonService != null) MainTM.getInstance().seasonService.applyToAll();
                chatConfirm(p, "chat-applied", "Year length = " + y + " MC days");
                openPage(p, 2);
            } catch (NumberFormatException ignored) {}
            return;
        }
        if (act.startsWith("preset-")) {
            try {
                SeasonPreset preset = SeasonPreset.valueOf(act.substring("preset-".length()));
                MainTM.getInstance().getConfig().set("seasons.preset", preset.name());
                MainTM.getInstance().saveConfig();
                if (MainTM.getInstance().seasonService != null) MainTM.getInstance().seasonService.applyToAll();
                chatConfirm(p, "chat-applied", "Preset = " + preset.label());
                openPage(p, 2);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        String title = e.getView().getTitle();
        if (!title.startsWith(TITLE_TAG)) return;
        PAGE.remove(e.getPlayer().getUniqueId());
    }
    
};
