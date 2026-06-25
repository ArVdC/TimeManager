package net.vdcraft.arvdc.timemanager.seasons;

import net.vdcraft.arvdc.timemanager.MainTM;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Polls affected worlds and triggers a SeasonService.applyToWorld when
 * an MC day boundary has been crossed. Polling beats listening to a
 * specific event because TimeManager already plays games with time
 * progression (frozen worlds, real-time worlds, etc.) and a polling
 * tick is cheap.
 */
public class SeasonScheduler {

    private static final long POLL_TICKS = 200L; // every 10 real seconds

    private final SeasonService svc;
    private final Map<String, Long> lastMcDay = new HashMap<>();
    private int taskId = -1;

    public SeasonScheduler(SeasonService svc) {
        this.svc = svc;
    }

    public void start() {
        if (taskId != -1) return;
        if (!svc.enabled()) {
            return;
        }
        // First pass on enable so the world starts with correct values.
        svc.applyToAll();
        for (String w : svc.affectedWorlds()) {
            World world = Bukkit.getWorld(w);
            if (world != null) lastMcDay.put(w, svc.mcDayOfWorld(world));
        }
        taskId = new BukkitRunnable() {
            @Override public void run() {
                if (!svc.enabled()) { cancel(); taskId = -1; return; }
                for (String w : svc.affectedWorlds()) {
                    World world = Bukkit.getWorld(w);
                    if (world == null) continue;
                    long now = svc.mcDayOfWorld(world);
                    Long prev = lastMcDay.get(w);
                    if (prev == null || now != prev) {
                        svc.applyToWorld(w);
                        lastMcDay.put(w, now);
                    }
                }
            }
        }.runTaskTimer(MainTM.getInstance(), POLL_TICKS, POLL_TICKS).getTaskId();
    }

    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        lastMcDay.clear();
    }

    /** Restart picking up the latest config (used after /tm reload or season changes). */
    public void restart() {
        stop();
        start();
    }
}
