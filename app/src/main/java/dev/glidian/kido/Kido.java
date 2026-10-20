package dev.glidian.kido;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/** Shared look for KIDŌ: palette, fonts, the 1920x1080 design grid and the app catalog. */
final class Kido {
    private Kido() {}

    // Palette. The accent colors change with the theme; see applyTheme.
    static final int VOID = 0xFF040404;
    static final int PANEL = 0xDB0A0804;
    static final int DARK_TEXT = 0xFF0C0600;
    static final int WARN = 0xFFE8231E;     // alerts stay red in every theme
    static int AMBER, ORANGE, RED, GREEN, LIME, INK, CREAM, FOCUS_RIM;
    static int DIM;     // amber at 42%
    static int LINE;    // amber at 28%

    /** Theme names as shown in the settings menu: kanji, English. */
    static final String[][] THEMES = {{"標準", "STANDARD"}, {"警報", "ALERT"}, {"紫電", "VIOLET"}};

    // Per theme: amber (main text and lines), orange (focus fill), red (active tab, alerts), green (OK readouts),
    // lime (inactive tabs), ink (bright text), cream (names), focus rim.
    private static final int[][] PALETTES = {
            {0xFFFFB21E, 0xFFFF8A00, 0xFFE8231E, 0xFF3BD16F, 0xFFB6FF3C, 0xFFFFC23D, 0xFFFFF3D6, 0xFFFFD27A},
            {0xFFFF4A3A, 0xFFE8231E, 0xFFFFB21E, 0xFFFF8A00, 0xFFFF8A5A, 0xFFFF6A50, 0xFFFFE6E0, 0xFFFF9A8A},
            {0xFFA77BFF, 0xFF8FEF3C, 0xFFFF7A1E, 0xFF8FEF3C, 0xFFC8FF6A, 0xFFC3A6FF, 0xFFF0EAFF, 0xFFE2FFB0},
    };

    static int theme = -1;

    static void applyTheme(int index) {
        if (index < 0 || index >= PALETTES.length) index = 0;
        int[] p = PALETTES[index];
        AMBER = p[0];
        ORANGE = p[1];
        RED = p[2];
        GREEN = p[3];
        LIME = p[4];
        INK = p[5];
        CREAM = p[6];
        FOCUS_RIM = p[7];
        DIM = withAlpha(AMBER, 0.42f);
        LINE = withAlpha(AMBER, 0.28f);
        theme = index;
    }

    static {
        applyTheme(0);
    }

    // Fonts, loaded once
    static Typeface mincho, condBold, mono;

    static void loadFonts(Context c) {
        if (mincho != null) return;
        mincho = c.getResources().getFont(R.font.kido_mincho);
        condBold = c.getResources().getFont(R.font.kido_cond_bold);
        mono = c.getResources().getFont(R.font.kido_mono);
    }

    /** Everything is laid out on a 1920x1080 grid and scaled to the real screen. */
    static float scale = 1f;

    static int px(float designPx) {
        return Math.round(designPx * scale);
    }

    /** What KIDŌ knows about an app: its name, a category label in kanji and English, and a brand color. */
    static final class Look {
        final String name, kanji, category;
        final int glow;

        Look(String name, String kanji, String category, int glow) {
            this.name = name;
            this.kanji = kanji;
            this.category = category;
            this.glow = glow;
        }
    }

    private static final Map<String, Look> LOOKS = new HashMap<>();

    private static void look(String pkg, String name, String kanji, String category, int glow) {
        LOOKS.put(pkg, new Look(name, kanji, category, glow));
    }

    static {
        look("com.netflix.ninja", "Netflix", "映画", "FILM", 0xFFE50914);
        look("com.amazon.amazonvideo.livingroom", "Prime Video", "映画", "FILM", 0xFF00A8E1);
        look("com.wbd.stream", "HBO Max", "番組", "SERIES", 0xFF2D55FF);
        look("com.disney.disneyplus", "Disney+", "家族", "FAMILY", 0xFF3B5BFF);
        look("com.google.android.youtube.tv", "YouTube", "動画", "VIDEO", 0xFFFF2A3D);
        look("com.hulu.livingroomplus", "Hulu", "番組", "SERIES", 0xFF1CE783);
        look("com.peacocktv.peacockandroid", "Peacock", "番組", "SERIES", 0xFF7B61FF);
        look("com.crunchyroll.crunchyroid", "Crunchyroll", "アニメ", "ANIME", 0xFFF47521);
        look("com.spotify.tv.android", "Spotify", "音楽", "MUSIC", 0xFF1DB954);
        look("com.valvesoftware.steamlink", "Steam Link", "遊戯", "GAMES", 0xFF66C0F4);
        look("com.android.vending", "Play Store", "店", "STORE", 0xFF01A971);
        look("com.mediatek.wwtv.mediaplayer", "Media Player", "再生", "PLAYER", 0xFFFFB21E);
    }

    /** The main panels on first run, in order. After that, what's pinned is saved and changed from the remote. */
    static final String[] FAVORITES = {
            "com.netflix.ninja",
            "com.amazon.amazonvideo.livingroom",
            "com.wbd.stream",
            "com.disney.disneyplus",
            "com.google.android.youtube.tv",
    };

    /**
     * Installed but not shown: the manual, the updater and the input app (inputs have their own section).
     * These are the ones on a Sharp AQUOS; other TVs have their own.
     */
    static final String[] HIDDEN = {
            "jp.co.sharp.av.android.emanual",
            "rcota.android.av.sharp.co.jp.rcota",
            "com.mediatek.wwtv.tvcenter",
    };

    static Look lookFor(String pkg, String label) {
        Look l = LOOKS.get(pkg);
        return l != null ? l : new Look(label, "アプリ", "APP", AMBER);
    }

    static int withAlpha(int color, float alpha) {
        return (Math.round(alpha * 255) << 24) | (color & 0x00FFFFFF);
    }
}
