package net.vdcraft.arvdc.timemanager.seasons;

import net.vdcraft.arvdc.timemanager.MainTM;
import net.vdcraft.arvdc.timemanager.mainclass.MsgHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Seasonal modulation of day/night durations. Tracks an MC-time-based
 * calendar (server uptime as MC days) and applies preset-derived
 * daySpeed/nightSpeed values to the world's TimeManager config on every
 * MC sunrise.
 *
 * Calendar model:
 *   year-length-days  = configurable (default 32 MC days)
 *   4 seasons of equal length: WINTER → SPRING → SUMMER → FALL → WINTER
 *   Day-of-year drives a sinusoidal swing between solstice extremes,
 *   so consecutive days differ by a small step, not a hard cutoff.
 *
 * Speed math: vanilla MC day = 12000 ticks day + 12000 ticks night.
 * Real-time at speed=1.0 = 600 sec daytime + 600 sec nighttime.
 * To make daytime LONGER in real time, daySpeed is LOWER (the world
 * progresses through day ticks slower). Conversely nightSpeed goes UP
 * to compress night into less real time.
 *
 *   daylightFraction = preset.minDaylight + amplitude * (sin(angle) + 1) / 2
 *   daySpeed = 0.5 / daylightFraction        (so 0.5 → 1.0; 0.67 → 0.75)
 *   nightSpeed = 0.5 / (1 - daylightFraction)
 */
public class SeasonService {

    public enum Season { WINTER, SPRING, SUMMER, FALL }

    /** Last-applied (season, speedDay, speedNight) per world; used to skip no-op updates. */
    private final java.util.Map<String, String> lastApplied = new java.util.HashMap<>();

    public boolean enabled() {
        return MainTM.getInstance().getConfig().getBoolean("seasons.enabled", false);
    }

    public int yearLengthDays() {
        int n = MainTM.getInstance().getConfig().getInt("seasons.year-length-days", 32);
        return Math.max(4, n);
    }

    public SeasonPreset preset() {
        return SeasonPreset.fromString(
                MainTM.getInstance().getConfig().getString("seasons.preset", "TEMPERATE"));
    }

    public boolean smoothTransition() {
        return MainTM.getInstance().getConfig().getBoolean("seasons.smooth-transition", true);
    }

    /** True if the configured calendar runs on the southern hemisphere
     *  (summer in December, winter in June). */
    public boolean isSouthernHemisphere() {
        return "south".equalsIgnoreCase(
                MainTM.getInstance().getConfig().getString("seasons.hemisphere", "north"));
    }

    public List<String> affectedWorlds() {
        List<String> out = new ArrayList<>(
                MainTM.getInstance().getConfig().getStringList("seasons.affected-worlds"));
        if (out.isEmpty()) {
            // Default: only overworld-environment worlds. Nether and End
            // have no daylight cycle, so applying speeds to them just
            // produces "Cannot set time" warnings every refresh.
            var list = MainTM.getInstance().getConfig().getConfigurationSection(MainTM.CF_WORLDSLIST);
            if (list != null) {
                for (String w : list.getKeys(false)) {
                    World wo = Bukkit.getWorld(w);
                    if (wo == null || wo.getEnvironment() == Environment.NORMAL) {
                        out.add(w);
                    }
                }
            }
        }
        return out;
    }

    /** MC days elapsed since the world's first tick — read directly from the world clock. */
    public long mcDayOfWorld(World w) {
        return w.getFullTime() / 24000L;
    }

    public int dayOfYear(World w) {
        long mcDay = mcDayOfWorld(w);
        int year = yearLengthDays();
        return (int) ((mcDay % year + year) % year);
    }

    public Season currentSeason(World w) {
        int year = yearLengthDays();
        int doy = dayOfYear(w);
        int seasonLen = Math.max(1, year / 4);
        int idx = Math.min(3, doy / seasonLen);
        return Season.values()[idx];
    }

    /**
     * Daylight fraction for this MC day in this world, 0..1. Sinusoidal
     * across the year: winter solstice = preset.winterDaylight, summer
     * solstice = preset.summerDaylight, equinoxes = midpoint (~0.5).
     *
     * Northern hemisphere: winter solstice at day 0 (year start), summer at
     * year/2. Southern presets shift by half a year.
     */
    public double daylightFraction(World w) {
        SeasonPreset p = preset();
        int year = yearLengthDays();
        int doy = dayOfYear(w);
        double frac = (double) doy / (double) year;
        double phase = isSouthernHemisphere() ? frac + 0.5 : frac;
        // -cos(2πphase): -1 at phase 0 (winter solstice), +1 at phase 0.5 (summer solstice)
        double sinusoid = -Math.cos(2.0 * Math.PI * phase);
        double mid = 0.5 * (p.winterDaylight() + p.summerDaylight());
        double amp = 0.5 * (p.summerDaylight() - p.winterDaylight());

        if (!smoothTransition()) {
            // Pin to the solstice value of whatever quarter we're in.
            Season s = currentSeason(w);
            return switch (s) {
                case WINTER -> p.winterDaylight();
                case SUMMER -> p.summerDaylight();
                default -> mid;
            };
        }

        return mid + amp * sinusoid;
    }

    /** Computed daySpeed for the current MC day in this world. */
    public double computeDaySpeed(World w) {
        double daylight = clamp(daylightFraction(w), 0.05, 0.95);
        return 0.5 / daylight;
    }

    public double computeNightSpeed(World w) {
        double daylight = clamp(daylightFraction(w), 0.05, 0.95);
        return 0.5 / (1.0 - daylight);
    }

    /**
     * Apply the season's day/night speeds to a single world's config.
     * Called at sunrise of every MC day. No-op if the resulting values
     * match what was applied yesterday.
     */
    public void applyToWorld(String worldName) {
        if (!enabled()) return;
        if (!affectedWorlds().contains(worldName)) return;
        World w = Bukkit.getWorld(worldName);
        if (w == null) return;

        double daySpeed = round2(computeDaySpeed(w));
        double nightSpeed = round2(computeNightSpeed(w));
        Season s = currentSeason(w);
        String stamp = s.name() + ":" + daySpeed + ":" + nightSpeed;

        String prev = lastApplied.get(worldName);
        if (stamp.equals(prev)) return;

        var cfg = MainTM.getInstance().getConfig();
        String base = MainTM.CF_WORLDSLIST + "." + worldName + ".";
        cfg.set(base + MainTM.CF_D_SPEED, daySpeed);
        cfg.set(base + MainTM.CF_N_SPEED, nightSpeed);
        cfg.set(base + MainTM.CF_SPEED, daySpeed);  // generic speed = day's, for non-sync worlds
        MainTM.getInstance().saveConfig();
        lastApplied.put(worldName, stamp);

        MsgHandler.infoMsg("[seasons] " + worldName + " → " + s + " (day " + dayOfYear(w) + "/"
                + yearLengthDays() + "), daySpeed=" + daySpeed + ", nightSpeed=" + nightSpeed
                + " (daylight=" + Math.round(daylightFraction(w) * 100) + "%)");
    }

    public void applyToAll() {
        for (String w : affectedWorlds()) applyToWorld(w);
    }

    /** Human-readable summary for /tm season + GUI. */
    public String describe(World w) {
        if (!enabled()) return "seasons disabled";
        int year = yearLengthDays();
        return preset().label().toLowerCase(Locale.ROOT)
                + " — " + currentSeason(w).name().toLowerCase(Locale.ROOT)
                + " (MC day " + dayOfYear(w) + "/" + year + ", "
                + Math.round(daylightFraction(w) * 100) + "% daylight)";
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
