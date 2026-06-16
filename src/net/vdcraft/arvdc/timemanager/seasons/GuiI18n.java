package net.vdcraft.arvdc.timemanager.seasons;

import net.vdcraft.arvdc.timemanager.MainTM;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads GUI-translatable strings from {@code lang.yml} under
 * {@code languages.<currentLang>.gui.*}. Falls back to en_US, then to the
 * literal key name if a translation is missing.
 *
 * The "current language" is always the global {@code defaultLang} setting —
 * we don't honour per-player language overrides inside the admin GUI
 * because admin panels should stay predictable across sessions.
 */
public final class GuiI18n {

    private GuiI18n() {}

    private static String currentLang() {
        try {
            String lg = MainTM.getInstance().langConf.getString(MainTM.LG_DEFAULTLANG);
            return (lg == null || lg.isEmpty()) ? "en_US" : lg;
        } catch (Throwable t) {
            return "en_US";
        }
    }

    /** Get a single string with en_US fallback and finally the literal key. */
    public static String s(String key) {
        String lang = currentLang();
        String v = readString("languages." + lang + ".gui." + key);
        if (v != null) return v;
        if (!"en_US".equals(lang)) {
            v = readString("languages.en_US.gui." + key);
            if (v != null) return v;
        }
        return key; // literal fallback so the gap is obvious in-game
    }

    /** Format a translated string with positional {0}, {1} placeholders. */
    public static String f(String key, Object... args) {
        String pattern = s(key);
        if (args == null) return pattern;
        for (int i = 0; i < args.length; i++) {
            pattern = pattern.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return pattern;
    }

    /** Get a string list (for lore blocks) with the same fallback rules. */
    public static List<String> l(String key) {
        String lang = currentLang();
        List<String> v = readList("languages." + lang + ".gui." + key);
        if (v != null && !v.isEmpty()) return v;
        if (!"en_US".equals(lang)) {
            v = readList("languages.en_US.gui." + key);
            if (v != null && !v.isEmpty()) return v;
        }
        return new ArrayList<>();
    }

    /** Lore list with positional placeholder substitution per line. */
    public static List<String> lf(String key, Object... args) {
        List<String> base = l(key);
        if (args == null || args.length == 0) return base;
        List<String> out = new ArrayList<>(base.size());
        for (String line : base) {
            String v = line;
            for (int i = 0; i < args.length; i++) {
                v = v.replace("{" + i + "}", String.valueOf(args[i]));
            }
            out.add(v);
        }
        return out;
    }

    private static String readString(String path) {
        try {
            return MainTM.getInstance().langConf.getString(path);
        } catch (Throwable t) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> readList(String path) {
        try {
            Object v = MainTM.getInstance().langConf.get(path);
            if (v instanceof List<?>) return (List<String>) v;
            return null;
        } catch (Throwable t) {
            return null;
        }
    }
}
