package net.vdcraft.arvdc.timemanager.gui;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.seasons.GuiI18n;
import net.vdcraft.arvdc.timemanager.seasons.SeasonPreset;
import net.vdcraft.arvdc.timemanager.seasons.SeasonService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
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
 *
 * Java 8 source level — no var, no switch-expressions, no pattern-matching
 * instanceof, no List.of (the latter replaced by Arrays.asList / lst()).
 */
public class TmGui implements Listener {

    private static final String TITLE_TAG = ChatColor.DARK_AQUA + "TimeManager";

    /** Marker holder so the click/close handlers can identify our GUI on any
     *  MC version. InventoryView.getTitle() only landed in 1.13; holders work
     *  back to 1.4, which is the entire range this unified jar targets. */
    private static final class TmGuiHolder implements InventoryHolder {
        @Override public Inventory getInventory() { return null; }
    }

    private static final double[] SPEED_PRESETS = {0.5, 1.0, 2.0, 4.0, 8.0};
    private static final int[] YEAR_PRESETS = {8, 16, 32, 64};
    private static final long[] REFRESH_PRESETS = {5L, 20L, 100L};

    /** Tracks which page each viewing player is on, so nav clicks know where to go. */
    private static final Map<UUID, Integer> PAGE = new HashMap<UUID, Integer>();

    /** Per-player slot → action-ID map. Built when a page is constructed and
     *  consulted by the click handler. Replaces the old PersistentDataContainer
     *  approach so the GUI works on every MC version (1.9.4 through 26.x);
     *  PDC + NamespacedKey only landed in MC 1.14. */
    private static final Map<UUID, Map<Integer, String>> SLOT_ACTIONS =
            new HashMap<UUID, Map<Integer, String>>();

    /** action() stashes its id here; the very next set() call consumes it and
     *  records it against the slot in SLOT_ACTIONS. Lets the existing
     *  set(inv, N, action(item, "id")) shape stay intact across the file. */
    private static final ThreadLocal<String> PENDING_ACTION = new ThreadLocal<String>();
    private static final ThreadLocal<Player>  BUILDING_FOR  = new ThreadLocal<Player>();

    /** Set during openPage() while we are swapping the player from one TM
     *  page to another. The InventoryCloseEvent for the old page fires
     *  synchronously inside p.openInventory() — without this guard, onClose
     *  would wipe the slot map for the page we just built. */
    private static final ThreadLocal<Boolean> NAVIGATING = new ThreadLocal<Boolean>();

    /**
     * Pick the first Material name in the list that exists on the runtime
     * server. Lets the same compiled jar render fitting icons across MC
     * versions — newer name first, older fallback after. Returns PAPER if
     * none exist (unreachable for the lists we use here).
     */
    private static Material pickMat(String... names) {
        for (String n : names) {
            try { return Material.valueOf(n); }
            catch (IllegalArgumentException ignored) {}
        }
        return Material.PAPER;
    }

    /** Java 8-friendly replacement for List.of(...). Returns a mutable list
     *  (the GUI never mutates lore lists once built, so the immutability of
     *  Java 9's List.of isn't load-bearing here). */
    private static List<String> lst(String... items) {
        List<String> out = new ArrayList<String>(items.length);
        for (String s : items) out.add(s);
        return out;
    }

    public static void openFor(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MsgHandler.playerAdminMsg(sender, ChatColor.RED + GuiI18n.s("error-in-game-only"));
            return;
        }
        Player p = (Player) sender;
        if (!p.isOp() && !p.hasPermission("timemanager.admin")) {
            p.sendMessage(ChatColor.RED + GuiI18n.s("error-admin-only"));
            return;
        }
        openPage(p, 0);
    }

    private static void openPage(Player p, int page) {
        // Reset the slot map and pending-action stash for this fresh build.
        SLOT_ACTIONS.put(p.getUniqueId(), new HashMap<Integer, String>());
        BUILDING_FOR.set(p);
        PENDING_ACTION.remove();
        Inventory inv;
        try {
            switch (page) {
                case 1:  inv = buildWorld(p);   break;
                case 2:  inv = buildSeasons(p); break;
                default: inv = buildServer(p);  break;
            }
        } finally {
            BUILDING_FOR.remove();
            PENDING_ACTION.remove();
        }
        // p.openInventory fires InventoryCloseEvent for the old inv FIRST,
        // which runs onClose. The NAVIGATING flag prevents that handler from
        // wiping the slot map / page index we just built for the new page.
        NAVIGATING.set(Boolean.TRUE);
        try {
            p.openInventory(inv);
        } finally {
            NAVIGATING.remove();
        }
        PAGE.put(p.getUniqueId(), page);
    }

    /** Wrapper for inv.setItem that consumes the PENDING_ACTION stash (set by
     *  the wrapping action() call) and records it as the slot's action-id. */
    private static void set(Inventory inv, int slot, ItemStack it) {
        inv.setItem(slot, it);
        String id = PENDING_ACTION.get();
        if (id == null) return;
        PENDING_ACTION.remove();
        Player p = BUILDING_FOR.get();
        if (p == null) return;
        Map<Integer, String> m = SLOT_ACTIONS.get(p.getUniqueId());
        if (m != null) m.put(slot, id);
    }

    /* =========================================================
       PAGE 1: SERVER
       ========================================================= */
    private static Inventory buildServer(Player p) {
        Inventory inv = Bukkit.createInventory(new TmGuiHolder(), 54,
                TITLE_TAG + " — " + ChatColor.stripColor(GuiI18n.s("page-server")));

        // The three toggles live in different YAML files, all stored as the
        // literal string "true"/"false" (not YAML booleans). Reading from
        // the wrong file or via getBoolean leaves the button stuck on OFF.
        //   debugMode  → config.yml
        //   multiLang  → lang.yml   (key: useMultiLang)
        //   useCmds    → cmds.yml   (key: useCmds)
        boolean debug    = "true".equalsIgnoreCase(MainTM.getInstance().getConfig().getString("debugMode", "false"));
        boolean multi    = "true".equalsIgnoreCase(MainTM.getInstance().langConf.getString("useMultiLang", "false"));
        boolean useCmdsB = "true".equalsIgnoreCase(MainTM.getInstance().cmdsConf.getString("useCmds", "false"));
        long refreshRate = MainTM.getInstance().getConfig().getLong(MainTM.CF_REFRESHRATE, 5);

        // Header
        set(inv, 4, action(item(pickMat("CLOCK", "WATCH"),
                GuiI18n.f("header-title", MainTM.getInstance().getDescription().getVersion()),
                lst(GuiI18n.s("header-lore-1"),
                        "",
                        GuiI18n.f("header-lore-refresh", refreshRate),
                        GuiI18n.f("header-lore-debug", debug ? GuiI18n.s("on-text") : GuiI18n.s("off-text")),
                        GuiI18n.f("header-lore-multilang", multi ? GuiI18n.s("on-text") : GuiI18n.s("off-text")),
                        GuiI18n.f("header-lore-usecmds", useCmdsB ? GuiI18n.s("on-text") : GuiI18n.s("off-text")))), "header"));

        // Row 1: Reload
        set(inv, 10, action(item(pickMat("LECTERN", "BOOKSHELF"),        GuiI18n.s("section-reload"),    lst()), "section"));
        set(inv, 11, action(item(pickMat("LECTERN", "BOOKSHELF"),        GuiI18n.s("reload-all"),
                lst("&8/tm reload all")), "reload-all"));
        set(inv, 12, action(item(pickMat("WRITABLE_BOOK", "BOOK_AND_QUILL"),  GuiI18n.s("reload-config"),
                lst("&8/tm reload config")), "reload-config"));
        set(inv, 13, action(item(pickMat("BOOK"),           GuiI18n.s("reload-lang"),
                lst("&8/tm reload lang")), "reload-lang"));
        set(inv, 14, action(item(pickMat("WRITTEN_BOOK"),   GuiI18n.s("reload-cmds"),
                lst("&8/tm reload cmds")), "reload-cmds"));

        // Row 2: Checks
        set(inv, 19, action(item(pickMat("SPYGLASS", "COMPASS"),    GuiI18n.s("section-checks"),     lst()), "section"));
        set(inv, 20, action(item(pickMat("SPYGLASS", "COMPASS"),    GuiI18n.s("check-config"),
                lst("&8/tm checkConfig")), "check-config"));
        set(inv, 21, action(item(pickMat("COMPASS"),     GuiI18n.s("check-time"),
                lst("&8/tm checkTime all")), "check-time"));
        set(inv, 22, action(item(pickMat("ENDER_EYE"),   GuiI18n.s("check-update"),
                lst("&8/tm checkUpdate")), "check-update"));
        set(inv, 23, action(item(pickMat("NAME_TAG"),    GuiI18n.s("show-placeholders"),
                lst("&7%tm_*%", "&8/tm placeholders")), "show-placeholders"));

        // Row 3: Toggles
        set(inv, 28, action(item(pickMat("LEVER"), GuiI18n.s("section-toggles"), lst()), "section"));
        ItemStack debugItem = item(pickMat("REDSTONE_TORCH"),
                debug ? GuiI18n.s("debug-on") : GuiI18n.s("debug-off"),
                lst("", GuiI18n.s("click-toggle")));
        if (debug) glow(debugItem);
        set(inv, 29, action(debugItem, "toggle-debug"));

        ItemStack multiItem = item(pickMat("OAK_SIGN", "SIGN", "SIGN_POST"),
                multi ? GuiI18n.s("multilang-on") : GuiI18n.s("multilang-off"),
                lst("", GuiI18n.s("click-toggle")));
        if (multi) glow(multiItem);
        set(inv, 30, action(multiItem, "toggle-multilang"));

        ItemStack cmdsItem = item(pickMat("COMMAND_BLOCK"),
                useCmdsB ? GuiI18n.s("usecmds-on") : GuiI18n.s("usecmds-off"),
                lst("", GuiI18n.s("click-toggle")));
        if (useCmdsB) glow(cmdsItem);
        set(inv, 31, action(cmdsItem, "toggle-usecmds"));

        // Row 4: Refresh rate
        set(inv, 37, action(item(pickMat("CLOCK", "WATCH"), GuiI18n.s("section-refresh-rate"), lst()), "section"));
        int slot = 38;
        for (long ticks : REFRESH_PRESETS) {
            ItemStack it = item(refreshIcon(ticks), "&f" + ticks + "t",
                    lst("", GuiI18n.s("click-apply")));
            if (refreshRate == ticks) glow(it);
            set(inv, slot++, action(it, "refresh-" + ticks));
        }

        // Row 5: Misc
        // Pocket-watch needs PersistentDataContainer for right-click detection
        // (1.13+); on legacy servers MainTM skips NowItemHandler registration
        // entirely, so the button would just hand out an inert clock.
        if (MainTM.serverMcVersion >= MainTM.reqMcVForGamerules) {
            set(inv, 46, action(item(pickMat("CLOCK", "WATCH"), GuiI18n.s("give-pocket-watch"),
                    lst("&8/tm nowitem")), "give-nowitem"));
        }
        set(inv, 52, action(item(pickMat("PAINTING"), GuiI18n.s("broadcast-time"),
                lst("&8/tm now msg all")), "broadcast-time"));

        set(inv, 0, resetButton());
        set(inv, 8, previewItem(p));
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

        List<String> lore = new ArrayList<String>();
        lore.add(GuiI18n.f("overview-real-time", realClock));
        lore.add(GuiI18n.f("overview-world-time", world, mcClock));
        lore.add(GuiI18n.f("overview-speeds", daySpeed, nightSpeed));
        lore.add(seasonLine);
        lore.add("");
        lore.add(GuiI18n.s("overview-info-line"));
        return action(item(pickMat("RECOVERY_COMPASS", "COMPASS"), GuiI18n.s("overview-title"), lore), "header");
    }

    /* =========================================================
       PAGE 2: WORLD
       ========================================================= */
    private static Inventory buildWorld(Player p) {
        Inventory inv = Bukkit.createInventory(new TmGuiHolder(), 54,
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
        set(inv, 4, action(item(pickMat("GRASS_BLOCK", "GRASS"), GuiI18n.f("world-header", world),
                lst("",
                        GuiI18n.f("world-day-speed", curSpeed),
                        GuiI18n.f("world-night-speed", curNight),
                        GuiI18n.f("world-sync-line", sync ? GuiI18n.s("on-text") : GuiI18n.s("off-text")),
                        GuiI18n.f("world-sleep-line", sleep ? GuiI18n.s("on-text") : GuiI18n.s("off-text")),
                        GuiI18n.f("world-lock-line", isLocked ? GuiI18n.s("lock-active") : GuiI18n.s("lock-off")),
                        GuiI18n.f("world-anim-line", animOn ? GuiI18n.s("on-text") : GuiI18n.s("anim-vanilla")))), "header"));

        // Speed presets
        set(inv, 9, action(item(pickMat("SUNFLOWER", "DOUBLE_PLANT", "YELLOW_FLOWER"), GuiI18n.s("section-speed"), lst()), "section"));
        int slot = 10;
        for (double speed : SPEED_PRESETS) {
            ItemStack it = item(speedIcon(speed), "&f" + formatSpeed(speed) + GuiI18n.s("speed-suffix"),
                    lst(speedLabel(speed), "", GuiI18n.s("click-apply")));
            if (Math.abs(curSpeed - speed) < 0.001 && Math.abs(curNight - speed) < 0.001) glow(it);
            set(inv, slot++, action(it, "speed-" + speed));
        }

        // Toggles
        ItemStack syncItem = item(pickMat("COMPASS"),
                sync ? GuiI18n.s("sync-on") : GuiI18n.s("sync-off"),
                lst("", GuiI18n.s("click-toggle")));
        if (sync) glow(syncItem);
        set(inv, 16, action(syncItem, "toggle-sync"));

        ItemStack sleepItem = item(pickMat("RED_BED", "BED"),
                sleep ? GuiI18n.s("sleep-on") : GuiI18n.s("sleep-off"),
                lst("", GuiI18n.s("click-toggle")));
        if (sleep) glow(sleepItem);
        set(inv, 17, action(sleepItem, "toggle-sleep"));

        // Time jumps
        set(inv, 18, action(item(pickMat("RECOVERY_COMPASS", "COMPASS"), GuiI18n.s("quick-jumps"), lst()), "section"));
        set(inv, 19, action(item(pickMat("ORANGE_DYE", "ORANGE_WOOL"),  GuiI18n.s("time-dawn"),
                lst("&7t=23000", "", GuiI18n.s("click-jump"))),     "time-23000"));
        set(inv, 20, action(item(pickMat("YELLOW_DYE", "DANDELION_YELLOW", "YELLOW_WOOL"),  GuiI18n.s("time-morning"),
                lst("&7t=1000", "", GuiI18n.s("click-jump"))),       "time-1000"));
        set(inv, 21, action(item(pickMat("GLOWSTONE"),   GuiI18n.s("time-noon"),
                lst("&7t=6000", "", GuiI18n.s("click-jump"))),       "time-6000"));
        set(inv, 22, action(item(pickMat("CAMPFIRE", "TORCH"),    GuiI18n.s("time-sunset"),
                lst("&7t=12000", "", GuiI18n.s("click-jump"))),     "time-12000"));
        set(inv, 23, action(item(pickMat("BLACK_DYE", "INK_SACK"),   GuiI18n.s("time-night"),
                lst("&7t=14000", "", GuiI18n.s("click-jump"))),     "time-14000"));
        set(inv, 24, action(item(pickMat("OBSIDIAN"),    GuiI18n.s("time-midnight"),
                lst("&7t=18000", "", GuiI18n.s("click-jump"))),     "time-18000"));

        // Lock / animation / date / elapsed / resync
        ItemStack lockItem = item(pickMat("IRON_BARS"),
                isLocked ? GuiI18n.s("unlock-world") : GuiI18n.s("lock-world"),
                lst("", GuiI18n.s("click-toggle")));
        if (isLocked) glow(lockItem);
        set(inv, 27, action(lockItem, "toggle-lock"));

        // Night-skip animation needs the Particle / modern Sound enums introduced
        // in 1.9; the underlying feature is gated to MC 1.9+ in SleepHandler, so
        // we hide the GUI toggle on older servers to avoid setting a flag that
        // would never take effect.
        if (MainTM.serverMcVersion >= MainTM.reqMcVForSleepAnimation) {
            ItemStack animItem = item(pickMat("FIREWORK_ROCKET", "FIREWORK"),
                    animOn ? GuiI18n.s("anim-on") : GuiI18n.s("anim-off"),
                    lst("", GuiI18n.s("click-toggle")));
            if (animOn) glow(animItem);
            set(inv, 28, action(animItem, "toggle-anim"));
        }

        set(inv, 29, action(item(pickMat("WRITABLE_BOOK", "BOOK_AND_QUILL"), GuiI18n.s("set-date-today"),
                lst("&8/tm set date today")), "set-date-today"));
        set(inv, 30, action(item(pickMat("SOUL_LANTERN", "LANTERN", "REDSTONE_LAMP"), GuiI18n.s("reset-elapsed"),
                lst("&8/tm set elapsedDays 0")), "reset-elapsed"));
        set(inv, 31, action(item(pickMat("HOPPER"), GuiI18n.s("resync-world"),
                lst("&8/tm resync")), "resync-world"));
        set(inv, 32, action(item(pickMat("NETHER_STAR"), GuiI18n.s("check-this-world"),
                lst("&8/tm checkTime")), "check-this-world"));

        set(inv, 40, action(item(pickMat("RECOVERY_COMPASS", "COMPASS"), GuiI18n.s("now-button"),
                lst("&8/now")), "now-button"));

        set(inv, 0, resetButton());
        navRow(inv, 1);
        return inv;
    }

    /* =========================================================
       PAGE 3: SEASONS
       ========================================================= */
    private static Inventory buildSeasons(Player p) {
        Inventory inv = Bukkit.createInventory(new TmGuiHolder(), 54,
                TITLE_TAG + " — " + ChatColor.stripColor(GuiI18n.s("page-seasons")));
        SeasonService svc = MainTM.getInstance().seasonService;
        boolean enabled = svc != null && svc.enabled();
        SeasonPreset cur = svc == null ? SeasonPreset.TEMPERATE : svc.preset();
        int year = svc == null ? 32 : svc.yearLengthDays();

        String state = enabled ? GuiI18n.s("seasons-active") : GuiI18n.s("seasons-disabled");
        set(inv, 4, action(item(pickMat("WHEAT"), GuiI18n.f("seasons-header", state),
                lst(GuiI18n.f("season-current-preset", cur.label()),
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
                    lst(GuiI18n.f("preset-winter-daylight", Math.round(preset.winterDaylight() * 100)),
                            GuiI18n.f("preset-summer-daylight", Math.round(preset.summerDaylight() * 100)),
                            "",
                            GuiI18n.s("click-apply")));
            if (preset == cur) glow(it);
            set(inv, slots[i], action(it, "preset-" + preset.name()));
        }

        // Hemisphere toggle (replaces _SOUTH preset variants)
        boolean south = svc != null && svc.isSouthernHemisphere();
        ItemStack hemi = item(
                south ? pickMat("PRISMARINE_CRYSTALS") : pickMat("SUNFLOWER", "DOUBLE_PLANT", "YELLOW_FLOWER"),
                south ? GuiI18n.s("hemisphere-south") : GuiI18n.s("hemisphere-north"),
                lst("",
                        south ? "&7" + GuiI18n.s("hemisphere-south-desc")
                              : "&7" + GuiI18n.s("hemisphere-north-desc"),
                        "",
                        GuiI18n.s("click-toggle")));
        if (south) glow(hemi);
        set(inv, 16, action(hemi, "toggle-hemisphere"));

        int yearSlot = 37;
        for (int yp : YEAR_PRESETS) {
            ItemStack it = item(pickMat("MAP"), GuiI18n.f("year-button", yp),
                    lst("", GuiI18n.s("click-set")));
            if (yp == year) glow(it);
            set(inv, yearSlot++, action(it, "year-" + yp));
        }

        ItemStack toggle = item(enabled ? pickMat("REDSTONE_BLOCK") : pickMat("EMERALD_BLOCK"),
                enabled ? GuiI18n.s("disable-seasons") : GuiI18n.s("enable-seasons"),
                lst());
        if (enabled) glow(toggle);
        set(inv, 25, action(toggle, enabled ? "seasons-disable" : "seasons-enable"));

        set(inv, 34, action(item(Material.PAPER, GuiI18n.s("force-apply"), lst()), "seasons-apply"));

        set(inv, 0, resetButton());
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
            Material m = (i == currentPage) ? pickMat("LIME_STAINED_GLASS_PANE", "STAINED_GLASS_PANE") : pickMat("GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE");
            ItemStack pane = item(m, "&7" + GuiI18n.s(tabKeys[i]), lst());
            if (i == currentPage) glow(pane);
            set(inv, base + 3 + i, action(pane, "nav-tab-" + i));
        }
        if (currentPage > 0) {
            set(inv, base, action(item(pickMat("ARROW"), GuiI18n.s("nav-prev"), lst()), "nav-prev"));
        }
        if (currentPage < 2) {
            set(inv, base + 8, action(item(pickMat("ARROW"), GuiI18n.s("nav-next"), lst()), "nav-next"));
        }
    }

    private static ItemStack resetButton() {
        ItemStack it = item(pickMat("BARRIER"), GuiI18n.s("reset-button"), GuiI18n.l("reset-lore"));
        return action(it, "reset-vanilla");
    }

    private static ItemStack item(Material mat, String name, List<String> lore) {
        Material safe = mat;
        try { new ItemStack(safe).getType(); } catch (Throwable t) { safe = Material.PAPER; }
        ItemStack it = new ItemStack(safe);
        ItemMeta meta = it.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> out = new ArrayList<String>(lore.size());
            for (String l : lore) {
                if (l == null || l.isEmpty()) { out.add(""); continue; }
                out.add(ChatColor.translateAlternateColorCodes('&', l));
            }
            meta.setLore(out);
            it.setItemMeta(meta);
        }
        return it;
    }

    /** Stash the action-id for the immediately following set() call. The
     *  ItemStack is returned untouched so call sites can stay shaped as
     *  set(inv, slot, action(item, "id")). */
    private static ItemStack action(ItemStack it, String actionId) {
        if (it == null) return null;
        PENDING_ACTION.set(actionId);
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

    /** Returns the action-id recorded against the given slot for this player,
     *  or null if the slot has no registered button. */
    private static String actionAt(Player p, int slot) {
        if (p == null) return null;
        Map<Integer, String> m = SLOT_ACTIONS.get(p.getUniqueId());
        return m == null ? null : m.get(slot);
    }

    private static void glow(ItemStack it) {
        ItemMeta meta = it.getItemMeta();
        if (meta == null) return;
        Enchantment ench = null;
        // UNBREAKING is the 1.13+ name, DURABILITY the legacy one. getByName
        // resolves whichever the runtime server provides; both render glint.
        try { ench = Enchantment.getByName("UNBREAKING"); } catch (Throwable ignored) {}
        if (ench == null) {
            try { ench = Enchantment.getByName("DURABILITY"); } catch (Throwable ignored) {}
        }
        if (ench != null) {
            try { meta.addEnchant(ench, 1, true); } catch (Throwable ignored) {}
        }
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        it.setItemMeta(meta);
    }

    private static String formatSpeed(double s) {
        return s == (long) s ? String.valueOf((long) s) : String.valueOf(s);
    }

    private static String speedLabel(double s) {
        String key;
        switch ((int) Math.round(s * 10)) {
            case 5:  key = "speed-label-half";     break;
            case 10: key = "speed-label-vanilla";  break;
            case 20: key = "speed-label-double";   break;
            case 40: key = "speed-label-quad";     break;
            case 80: key = "speed-label-octuple";  break;
            default: key = "speed-label-custom";   break;
        }
        return GuiI18n.s(key);
    }

    private static Material speedIcon(double s) {
        if (s < 1.0)  return pickMat("LIGHT_BLUE_DYE", "LIGHT_BLUE_WOOL");
        if (s == 1.0) return pickMat("WHITE_DYE", "BONE_MEAL");
        if (s <= 2.0) return pickMat("YELLOW_DYE", "DANDELION_YELLOW", "YELLOW_WOOL");
        if (s <= 4.0) return pickMat("ORANGE_DYE", "ORANGE_WOOL");
        return pickMat("RED_DYE", "ROSE_RED", "INK_SACK");
    }

    private static Material refreshIcon(long t) {
        if (t <= 1) return pickMat("GUNPOWDER", "SULPHUR");
        if (t <= 5) return pickMat("SUGAR");
        if (t <= 20) return pickMat("WHEAT_SEEDS");
        return pickMat("STONE");
    }

    private static Material iconFor(SeasonPreset p) {
        switch (p) {
            case EQUATORIAL:    return pickMat("JUNGLE_LEAVES", "LEAVES");
            case MEDITERRANEAN: return pickMat("BIRCH_LEAVES", "LEAVES");
            case TEMPERATE:     return pickMat("OAK_LEAVES", "LEAVES");
            case SUBARCTIC:     return pickMat("SPRUCE_LEAVES", "LEAVES");
            case ARCTIC:        return pickMat("SNOW_BLOCK");
            case CUSTOM:        return pickMat("WRITABLE_BOOK", "BOOK_AND_QUILL");
            default:            return Material.PAPER;
        }
    }

    private static void resetToVanilla() {
        org.bukkit.configuration.file.FileConfiguration cfg = MainTM.getInstance().getConfig();
        org.bukkit.configuration.ConfigurationSection wl = cfg.getConfigurationSection(MainTM.CF_WORLDSLIST);
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
        Inventory top = e.getInventory();
        if (top == null || !(top.getHolder() instanceof TmGuiHolder)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        if (!p.isOp() && !p.hasPermission("timemanager.admin")) return;

        String act = actionAt(p, e.getRawSlot());
        if (act == null || "section".equals(act) || "header".equals(act)) return;

        // Nav
        if ("nav-prev".equals(act)) {
            int cur = PAGE.containsKey(p.getUniqueId()) ? PAGE.get(p.getUniqueId()) : 0;
            openPage(p, Math.max(0, cur - 1));
            return;
        }
        if ("nav-next".equals(act)) {
            int cur = PAGE.containsKey(p.getUniqueId()) ? PAGE.get(p.getUniqueId()) : 0;
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
            int cur = PAGE.containsKey(p.getUniqueId()) ? PAGE.get(p.getUniqueId()) : 0;
            openPage(p, cur);
            return;
        }

        String world = p.getWorld().getName();
        String base = MainTM.CF_WORLDSLIST + "." + world + ".";

        // Page 1
        if ("reload-all".equals(act))       { p.performCommand("tm reload all"); chatConfirm(p, "chat-reloaded", "all"); openPage(p, 0); return; }
        if ("reload-config".equals(act))    { p.performCommand("tm reload config"); chatConfirm(p, "chat-reloaded", "config"); openPage(p, 0); return; }
        if ("reload-lang".equals(act))      { p.performCommand("tm reload lang"); chatConfirm(p, "chat-reloaded", "lang"); openPage(p, 0); return; }
        if ("reload-cmds".equals(act))      { p.performCommand("tm reload cmds"); chatConfirm(p, "chat-reloaded", "cmds"); openPage(p, 0); return; }
        if ("check-config".equals(act))     { p.performCommand("tm checkConfig"); return; }
        if ("check-time".equals(act))       { p.performCommand("tm checkTime all"); return; }
        if ("check-update".equals(act))     { p.performCommand("tm checkUpdate"); return; }
        if ("show-placeholders".equals(act)){ p.performCommand("tm placeholders"); return; }
        if ("give-nowitem".equals(act))     { p.performCommand("tm nowitem"); return; }
        if ("broadcast-time".equals(act))   { p.performCommand("tm now msg all"); return; }
        if ("toggle-debug".equals(act)) {
            boolean cur = "true".equalsIgnoreCase(MainTM.getInstance().getConfig().getString("debugMode", "false"));
            p.performCommand("tm set debugMode " + (!cur));
            chatConfirm(p, "chat-toggled", "Debug = " + (!cur));
            openPage(p, 0); return;
        }
        if ("toggle-multilang".equals(act)) {
            boolean cur = "true".equalsIgnoreCase(MainTM.getInstance().langConf.getString("useMultiLang", "false"));
            p.performCommand("tm set multiLang " + (!cur));
            chatConfirm(p, "chat-toggled", "Multi-language = " + (!cur));
            openPage(p, 0); return;
        }
        if ("toggle-usecmds".equals(act)) {
            boolean cur = "true".equalsIgnoreCase(MainTM.getInstance().cmdsConf.getString("useCmds", "false"));
            p.performCommand("tm set useCmds " + (!cur));
            chatConfirm(p, "chat-toggled", "Scheduled cmds = " + (!cur));
            openPage(p, 0); return;
        }
        // Page 2
        if ("toggle-sync".equals(act)) {
            boolean cur = "true".equalsIgnoreCase(MainTM.getInstance().getConfig().getString(base + MainTM.CF_SYNC, "true"));
            p.performCommand("tm set sync " + (!cur) + " " + world);
            chatConfirm(p, "chat-toggled", "Sync = " + (!cur));
            openPage(p, 1); return;
        }
        if ("toggle-sleep".equals(act)) {
            boolean cur = "true".equalsIgnoreCase(MainTM.getInstance().getConfig().getString(base + MainTM.CF_SLEEP, "true"));
            p.performCommand("tm set sleep " + (!cur) + " " + world);
            chatConfirm(p, "chat-toggled", "Sleep = " + (!cur));
            openPage(p, 1); return;
        }
        if ("toggle-anim".equals(act)) {
            p.performCommand("tm animation " + world + " toggle");
            chatConfirm(p, "chat-toggled", "Sleep animation");
            openPage(p, 1); return;
        }
        if ("toggle-lock".equals(act)) {
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
        if ("set-date-today".equals(act))   { p.performCommand("tm set date today " + world); chatConfirm(p, "chat-applied", "Date → today"); openPage(p, 1); return; }
        if ("reset-elapsed".equals(act))    { p.performCommand("tm set elapsedDays 0 " + world); chatConfirm(p, "chat-applied", "Elapsed days = 0"); openPage(p, 1); return; }
        if ("resync-world".equals(act))     { p.performCommand("tm resync " + world); chatConfirm(p, "chat-applied", "Resynced " + world); openPage(p, 1); return; }
        if ("check-this-world".equals(act)) { p.performCommand("tm checkTime " + world); return; }
        if ("now-button".equals(act))       { p.performCommand("now"); return; }
        // Page 3
        if ("seasons-enable".equals(act)) {
            MainTM.getInstance().getConfig().set("seasons.enabled", true);
            MainTM.getInstance().saveConfig();
            if (MainTM.getInstance().seasonScheduler != null) MainTM.getInstance().seasonScheduler.restart();
            chatConfirm(p, "chat-toggled", "Seasons = ON");
            openPage(p, 2); return;
        }
        if ("seasons-disable".equals(act)) {
            MainTM.getInstance().getConfig().set("seasons.enabled", false);
            MainTM.getInstance().saveConfig();
            if (MainTM.getInstance().seasonScheduler != null) MainTM.getInstance().seasonScheduler.stop();
            chatConfirm(p, "chat-toggled", "Seasons = OFF");
            openPage(p, 2); return;
        }
        if ("seasons-apply".equals(act)) {
            if (MainTM.getInstance().seasonService != null) MainTM.getInstance().seasonService.applyToAll();
            chatConfirm(p, "chat-applied", "Seasons re-applied");
            return;
        }
        if ("toggle-hemisphere".equals(act)) {
            String cur = MainTM.getInstance().getConfig().getString("seasons.hemisphere", "north");
            String next = "south".equalsIgnoreCase(cur) ? "north" : "south";
            MainTM.getInstance().getConfig().set("seasons.hemisphere", next);
            MainTM.getInstance().saveConfig();
            if (MainTM.getInstance().seasonService != null) MainTM.getInstance().seasonService.applyToAll();
            chatConfirm(p, "chat-applied", "Hemisphere = " + next);
            openPage(p, 2); return;
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
        Inventory top = e.getInventory();
        if (top == null || !(top.getHolder() instanceof TmGuiHolder)) return;
        // Mid-navigation close: another TM page is opening in the same call.
        // Wiping state here would clear the slot map of the page we just
        // built and the click handler would treat every button as null.
        if (Boolean.TRUE.equals(NAVIGATING.get())) return;
        UUID id = e.getPlayer().getUniqueId();
        PAGE.remove(id);
        SLOT_ACTIONS.remove(id);
    }
}
