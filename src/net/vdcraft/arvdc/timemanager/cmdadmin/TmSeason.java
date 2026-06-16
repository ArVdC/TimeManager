package net.vdcraft.arvdc.timemanager.cmdadmin;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import net.vdcraft.arvdc.timemanager.seasons.SeasonPreset;
import net.vdcraft.arvdc.timemanager.seasons.SeasonService;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

/**
 * /tm season
 *   (no args)            show engine status + current season per affected world
 *   enable / disable     master toggle
 *   preset <NAME>        switch preset (re-applies immediately)
 *   year <days>          set year-length-days (re-applies immediately)
 *   apply                force re-apply to all worlds right now
 *   list                 list all available presets
 */
public class TmSeason {

    public static void cmdSeason(CommandSender sender, String sub, String arg) {
        SeasonService svc = MainTM.getInstance().seasonService;
        if (svc == null) {
            MsgHandler.playerAdminMsg(sender, ChatColor.RED + "Seasons engine not initialised yet.");
            return;
        }

        if (sub == null || sub.equalsIgnoreCase("status")) {
            sendStatus(sender, svc);
            return;
        }

        if (sub.equalsIgnoreCase("enable")) {
            MainTM.getInstance().getConfig().set("seasons.enabled", true);
            MainTM.getInstance().saveConfig();
            MainTM.getInstance().seasonScheduler.restart();
            MsgHandler.playerAdminMsg(sender, ChatColor.GREEN + "Seasons enabled.");
            sendStatus(sender, svc);
            return;
        }
        if (sub.equalsIgnoreCase("disable")) {
            MainTM.getInstance().getConfig().set("seasons.enabled", false);
            MainTM.getInstance().saveConfig();
            MainTM.getInstance().seasonScheduler.stop();
            MsgHandler.playerAdminMsg(sender, ChatColor.YELLOW + "Seasons disabled. Existing speed values left as-is.");
            return;
        }

        if (sub.equalsIgnoreCase("preset")) {
            if (arg == null) {
                MsgHandler.playerAdminMsg(sender, ChatColor.RED + "Usage: /tm season preset <NAME>");
                sendPresetList(sender);
                return;
            }
            try {
                SeasonPreset p = SeasonPreset.valueOf(arg.toUpperCase().replace('-', '_'));
                MainTM.getInstance().getConfig().set("seasons.preset", p.name());
                MainTM.getInstance().saveConfig();
                svc.applyToAll();
                MsgHandler.playerAdminMsg(sender, ChatColor.GREEN + "Preset = " + p.name()
                        + " (" + p.label() + "). Re-applied to all affected worlds.");
            } catch (IllegalArgumentException ex) {
                MsgHandler.playerAdminMsg(sender, ChatColor.RED + "Unknown preset: " + arg);
                sendPresetList(sender);
            }
            return;
        }

        if (sub.equalsIgnoreCase("year")) {
            if (arg == null) {
                MsgHandler.playerAdminMsg(sender, ChatColor.RED + "Usage: /tm season year <days>  (min 4)");
                return;
            }
            try {
                int days = Math.max(4, Integer.parseInt(arg));
                MainTM.getInstance().getConfig().set("seasons.year-length-days", days);
                MainTM.getInstance().saveConfig();
                svc.applyToAll();
                MsgHandler.playerAdminMsg(sender, ChatColor.GREEN + "Year length = " + days + " MC days.");
            } catch (NumberFormatException ex) {
                MsgHandler.playerAdminMsg(sender, ChatColor.RED + "Not a number: " + arg);
            }
            return;
        }

        if (sub.equalsIgnoreCase("apply")) {
            svc.applyToAll();
            MsgHandler.playerAdminMsg(sender, ChatColor.GREEN + "Re-applied seasons to all affected worlds.");
            return;
        }

        if (sub.equalsIgnoreCase("list")) {
            sendPresetList(sender);
            return;
        }

        MsgHandler.playerAdminMsg(sender, ChatColor.RED + "Unknown sub: " + sub);
        MsgHandler.playerAdminMsg(sender, ChatColor.GRAY + "Try: status | enable | disable | preset <NAME> | year <days> | apply | list");
    }

    private static void sendStatus(CommandSender sender, SeasonService svc) {
        MsgHandler.playerAdminMsg(sender, ChatColor.AQUA + "─── Seasons engine ───");
        MsgHandler.playerAdminMsg(sender, ChatColor.GRAY + "enabled: " + ChatColor.WHITE + svc.enabled());
        MsgHandler.playerAdminMsg(sender, ChatColor.GRAY + "preset: " + ChatColor.WHITE + svc.preset()
                + " (" + svc.preset().label() + ")");
        MsgHandler.playerAdminMsg(sender, ChatColor.GRAY + "year-length: " + ChatColor.WHITE + svc.yearLengthDays() + " MC days");
        MsgHandler.playerAdminMsg(sender, ChatColor.GRAY + "smooth-transition: " + ChatColor.WHITE + svc.smoothTransition());

        for (String wn : svc.affectedWorlds()) {
            World w = org.bukkit.Bukkit.getWorld(wn);
            if (w == null) {
                MsgHandler.playerAdminMsg(sender, ChatColor.GRAY + "  " + wn + ": " + ChatColor.DARK_RED + "(world not loaded)");
                continue;
            }
            MsgHandler.playerAdminMsg(sender, ChatColor.GRAY + "  " + wn + ": " + ChatColor.WHITE + svc.describe(w));
        }
    }

    private static void sendPresetList(CommandSender sender) {
        MsgHandler.playerAdminMsg(sender, ChatColor.AQUA + "Available presets:");
        for (SeasonPreset p : SeasonPreset.values()) {
            MsgHandler.playerAdminMsg(sender, ChatColor.GRAY + "  " + p.name() + ChatColor.DARK_GRAY + " — " + p.label());
        }
    }
}
