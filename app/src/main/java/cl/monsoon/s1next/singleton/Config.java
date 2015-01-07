package cl.monsoon.s1next.singleton;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;

import cl.monsoon.s1next.MyApplication;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.SettingsFragment;
import cl.monsoon.s1next.util.ColorUtil;

public enum Config {
    INSTANCE;

    public static final int OKHTTP_CLIENT_CONNECT_TIMEOUT = 20;
    public static final int OKHTTP_CLIENT_WRITE_TIMEOUT = 20;
    public static final int OKHTTP_CLIENT_READ_TIMEOUT = 60;

    /**
     * Take care of Menu ~ open in browser ~
     * when these numbers are not default.
     */
    public static final int THREADS_PER_PAGE = 50;
    public static final int POSTS_PER_PAGE = 30;

    private static final int LIGHT_THEME_S1 = R.style.LightTheme_S1;
    private static final int LIGHT_THEME_LIGHT_BLUE = R.style.LightTheme_Inverse_LightBlue;
    private static final int LIGHT_THEME_GREEN = R.style.LightTheme_Inverse_Green;
    private static final int DARK_THEME = R.style.DarkTheme;

    private static final int[] THEMES = {
            LIGHT_THEME_S1,
            LIGHT_THEME_LIGHT_BLUE,
            LIGHT_THEME_GREEN,
            DARK_THEME
    };

    private volatile int currentTheme;
    private volatile int colorAccent;
    private volatile float textScale;
    private volatile boolean wifi;
    private volatile DownloadStrategy avatarsDownloadStrategy;
    private volatile DownloadStrategy imagesDownloadStrategy;

    public static boolean isDefaultApplicationTheme() {
        // default theme in AndroidManifest.xml is DarkTheme
        return INSTANCE.currentTheme == DARK_THEME;
    }

    public static boolean isS1Theme() {
        return INSTANCE.currentTheme == LIGHT_THEME_S1;
    }

    public static int getCurrentTheme() {
        return INSTANCE.currentTheme;
    }

    public static void setCurrentTheme(SharedPreferences sharedPreferences) {
        INSTANCE.currentTheme =
                THEMES[Integer.parseInt(
                        sharedPreferences.getString(
                                SettingsFragment.PREF_KEY_THEME,
                                MyApplication.getContext()
                                        .getString(R.string.pref_theme_default_value)))];

        // get current theme's accent color
        TypedArray typedArray =
                MyApplication.getContext()
                        .obtainStyledAttributes(
                                INSTANCE.currentTheme, new int[]{R.attr.colorAccent});
        INSTANCE.colorAccent = typedArray.getColor(0, -1);
        if (INSTANCE.colorAccent == -1) {
            throw new IllegalStateException("Theme accent color can't be -1.");
        }
    }

    @ColorRes
    public static int getColorAccent() {
        return INSTANCE.colorAccent;
    }

    @ColorUtil.Alpha
    public static int getSecondaryTextAlpha() {
        if (INSTANCE.currentTheme == DARK_THEME) {
            return ColorUtil.BLACK_BACKGROUND_SECONDARY_TEXT_ALPHA;
        } else {
            return ColorUtil.WHITE_BACKGROUND_SECONDARY_TEXT_OR_ICONS_ALPHA;
        }
    }

    @ColorUtil.Alpha
    public static int getDisabledOrHintTextAlpha() {
        if (INSTANCE.currentTheme == DARK_THEME) {
            return ColorUtil.BLACK_BACKGROUND_DISABLED_OR_HINT_TEXT_ALPHA;
        } else {
            return ColorUtil.WHITE_BACKGROUND_DISABLED_OR_HINT_TEXT_ALPHA;
        }
    }

    public static float getTextScale() {
        return INSTANCE.textScale;
    }

    public static void setTextScale(SharedPreferences sharedPreferences) {
        String value =
                sharedPreferences.getString(
                        SettingsFragment.PREF_KEY_FONT_SIZE,
                        MyApplication.getContext().
                                getString(R.string.pref_font_size_default_value));

        INSTANCE.textScale = TextScale.fromString(value).getSize();
    }

    public static void setWifi(boolean wifi) {
        INSTANCE.wifi = wifi;
    }

    public static DownloadStrategy getAvatarsDownloadStrategy() {
        return INSTANCE.avatarsDownloadStrategy;
    }

    public static void setAvatarsDownloadStrategy(SharedPreferences sharedPreferences) {
        String value =
                sharedPreferences.getString(
                        SettingsFragment.PREF_KEY_DOWNLOAD_AVATARS,
                        MyApplication.getContext()
                                .getString(R.string.pref_download_avatars_default_value));

        INSTANCE.avatarsDownloadStrategy = DownloadStrategy.fromString(value);
    }

    public static boolean isAvatarsDownload() {
        return isDownload(INSTANCE.avatarsDownloadStrategy);
    }

    public static DownloadStrategy getImagesDownloadStrategy() {
        return INSTANCE.imagesDownloadStrategy;
    }

    public static void setImagesDownloadStrategy(SharedPreferences sharedPreferences) {
        String value =
                sharedPreferences.getString(
                        SettingsFragment.PREF_KEY_DOWNLOAD_IMAGES,
                        MyApplication.getContext().
                                getString(R.string.pref_download_images_default_value));

        INSTANCE.imagesDownloadStrategy = DownloadStrategy.fromString(value);
    }

    public static boolean isImagesDownload() {
        return isDownload(INSTANCE.imagesDownloadStrategy);
    }

    private static boolean isDownload(DownloadStrategy downloadStrategy) {
        return
                downloadStrategy ==
                        DownloadStrategy.WIFI
                        && INSTANCE.wifi
                        || downloadStrategy == DownloadStrategy.ALWAYS;
    }

    public static enum TextScale {
        VERY_SMALL(0.8f), SMALL(0.9f), MEDIUM(1f), LARGE(1.1f), VERY_LARGE(1.2f);

        private static final TextScale[] VALUES = TextScale.values();

        private final float size;

        TextScale(float size) {
            this.size = size;
        }

        public float getSize() {
            return size;
        }

        public static TextScale fromString(String value) {
            return VALUES[Integer.parseInt(value)];
        }
    }

    public static enum DownloadStrategy {
        NOT, WIFI, ALWAYS;

        private static final DownloadStrategy[] VALUES = DownloadStrategy.values();

        public static DownloadStrategy fromString(String value) {
            return VALUES[Integer.parseInt(value)];
        }
    }
}
