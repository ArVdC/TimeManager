package net.vdcraft.arvdc.timemanager.seasons;

import net.vdcraft.arvdc.timemanager.MainTM;

/**
 * Real-world-inspired day-length profiles. Each preset declares the
 * solstice-day daylight fraction (0.0–1.0) for the NORTHERN hemisphere
 * calendar. Use {@code seasons.hemisphere: south} in config to flip the
 * calendar (summer in December, winter in June).
 *
 * Polar presets clamp values to 5%/95% so the speed math stays finite.
 * The {@code CUSTOM} preset reads {@code seasons.custom.winter-daylight}
 * and {@code seasons.custom.summer-daylight} from config at runtime.
 */
public enum SeasonPreset {

    /** Equator — no seasonal swing. Always 50/50. */
    EQUATORIAL(0.50, 0.50, "Equatorial / Tropical"),

    /** ~30-40° latitude — mild swing. Summer 14h day, winter 10h. */
    MEDITERRANEAN(0.42, 0.58, "Mediterranean / Subtropical"),

    /** ~45-50° latitude (Pacific Northwest, Central Europe).
     *  Summer 16h day, winter 8h. */
    TEMPERATE(0.33, 0.67, "Temperate / Pacific Northwest"),

    /** ~60-65° latitude (Scandinavia, southern Alaska).
     *  Summer 20h day, winter 4h. */
    SUBARCTIC(0.17, 0.83, "Subarctic / Scandinavian"),

    /** >66° latitude — polar day / polar night.
     *  Clamped to 5% / 95% so the speed math stays finite. */
    ARCTIC(0.05, 0.95, "Arctic / Polar"),

    /** User-supplied winter/summer daylight fractions from config.yml. */
    CUSTOM(0.33, 0.67, "Custom");

    private final double winterDaylightStatic;
    private final double summerDaylightStatic;
    private final String label;

    SeasonPreset(double winter, double summer, String label) {
        this.winterDaylightStatic = winter;
        this.summerDaylightStatic = summer;
        this.label = label;
    }

    public double winterDaylight() {
        if (this == CUSTOM) {
            return MainTM.getInstance().getConfig().getDouble(
                    "seasons.custom.winter-daylight", winterDaylightStatic);
        }
        return winterDaylightStatic;
    }

    public double summerDaylight() {
        if (this == CUSTOM) {
            return MainTM.getInstance().getConfig().getDouble(
                    "seasons.custom.summer-daylight", summerDaylightStatic);
        }
        return summerDaylightStatic;
    }

    public String label() { return label; }

    public static SeasonPreset fromString(String s) {
        if (s == null) return TEMPERATE;
        try {
            return SeasonPreset.valueOf(s.toUpperCase().replace('-', '_').replace(' ', '_'));
        } catch (IllegalArgumentException ex) {
            return TEMPERATE;
        }
    }
}
