package cl.monsoon.s1next.singleton;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.StringSignature;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import cl.monsoon.s1next.MyApplication;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.DownloadSettingsFragment;
import cl.monsoon.s1next.fragment.SettingsFragment;
import cl.monsoon.s1next.util.ColorUtil;
import cl.monsoon.s1next.util.DateUtil;

public enum Config {
    INSTANCE;

    public static final int OKHTTP_CLIENT_CONNECT_TIMEOUT = 20;
    public static final int OKHTTP_CLIENT_WRITE_TIMEOUT = 20;
    public static final int OKHTTP_CLIENT_READ_TIMEOUT = 60;

    public static final long COOKIES_MAX_AGE = TimeUnit.DAYS.toSeconds(30);

    // 1MB
    static final long AVATAR_URLS_DISK_CACHE_MAX_SIZE = 1000 * 1000;

    static final int AVATAR_URLS_MEMORY_CACHE_MAX_NUMBER = 1000;
    static final int AVATAR_URL_KEYS_MEMORY_CACHE_MAX_NUMBER = 1000;

    public static final int THREADS_PER_PAGE = 50;
    public static final int POSTS_PER_PAGE = 30;

    public static final int REPLY_NOTIFICATION_MAX_LENGTH = 100;

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
    private volatile boolean hasWifi;
    private volatile DownloadStrategy avatarsDownloadStrategy;
    private volatile AvatarResolutionStrategy avatarResolutionStrategy;
    private volatile AvatarCacheInvalidationInterval avatarCacheInvalidationInterval;
    private volatile DownloadStrategy imagesDownloadStrategy;

    public static boolean isDefaultApplicationTheme() {
        // default theme in AndroidManifest.xml is DarkTheme
        return INSTANCE.currentTheme == DARK_THEME;
    }

    public static boolean isS1Theme() {
        return INSTANCE.currentTheme == LIGHT_THEME_S1;
    }

    public static boolean isDarkTheme() {
        return INSTANCE.currentTheme == DARK_THEME;
    }

    public static int getCurrentTheme() {
        return INSTANCE.currentTheme;
    }

    public static void setCurrentTheme(SharedPreferences sharedPreferences) {
        INSTANCE.currentTheme =
                THEMES[Integer.parseInt(
                        getString(
                                sharedPreferences,
                                SettingsFragment.PREF_KEY_THEME,
                                R.string.pref_theme_default_value))];

        // get current theme's accent color
        TypedArray typedArray =
                MyApplication.getContext()
                        .obtainStyledAttributes(
                                INSTANCE.currentTheme, new int[]{R.attr.colorAccent});
        INSTANCE.colorAccent = typedArray.getColor(0, -1);
        typedArray.recycle();

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
                getString(
                        sharedPreferences,
                        SettingsFragment.PREF_KEY_FONT_SIZE,
                        R.string.pref_font_size_default_value);

        INSTANCE.textScale = TextScale.fromString(value).getSize();
    }

    public static void setWifi(boolean hasWifi) {
        INSTANCE.hasWifi = hasWifi;
    }

    public static int getCacheSize(SharedPreferences sharedPreferences) {
        String value =
                getString(
                        sharedPreferences,
                        DownloadSettingsFragment.PREF_DOWNLOAD_CACHE_SIZE,
                        R.string.pref_download_cache_size_default_value);

        return CacheSize.fromString(value).size;
    }

    public static void setAvatarsDownloadStrategy(SharedPreferences sharedPreferences) {
        String value =
                getString(
                        sharedPreferences,
                        DownloadSettingsFragment.PREF_KEY_DOWNLOAD_AVATARS,
                        R.string.pref_download_avatars_default_value);

        INSTANCE.avatarsDownloadStrategy = DownloadStrategy.fromString(value);
    }

    public static boolean isAvatarsDownload() {
        return INSTANCE.avatarsDownloadStrategy.isDownload(INSTANCE.hasWifi);
    }

    public static void setAvatarResolutionStrategy(SharedPreferences sharedPreferences) {
        String value =
                getString(
                        sharedPreferences,
                        DownloadSettingsFragment.PREF_KEY_AVATAR_RESOLUTION,
                        R.string.pref_avatar_resolution_default_value);

        INSTANCE.avatarResolutionStrategy = AvatarResolutionStrategy.fromString(value);
    }

    public static boolean isHighResolutionAvatarsDownload() {
        return INSTANCE.avatarResolutionStrategy.isHigherResolution(Config.INSTANCE.hasWifi);
    }

    public static void setAvatarCacheInvalidationInterval(SharedPreferences sharedPreferences) {
        String value =
                getString(
                        sharedPreferences,
                        DownloadSettingsFragment.PREF_KEY_AVATAR_CACHE_INVALIDATION_INTERVAL,
                        R.string.pref_avatar_cache_invalidation_interval_default_value);

        INSTANCE.avatarCacheInvalidationInterval = AvatarCacheInvalidationInterval.fromString(value);
    }

    public static Key getAvatarCacheInvalidationIntervalSignature() {
        return INSTANCE.avatarCacheInvalidationInterval.getSignature();
    }

    public static void setImagesDownloadStrategy(SharedPreferences sharedPreferences) {
        String value =
                getString(
                        sharedPreferences,
                        DownloadSettingsFragment.PREF_KEY_DOWNLOAD_IMAGES,
                        R.string.pref_download_images_default_value);

        INSTANCE.imagesDownloadStrategy = DownloadStrategy.fromString(value);
    }

    public static boolean isImagesDownload() {
        return INSTANCE.imagesDownloadStrategy.isDownload(INSTANCE.hasWifi);
    }

    public static boolean needToTurnWifiOn() {
        return
                INSTANCE.avatarsDownloadStrategy != DownloadStrategy.NOT
                        || INSTANCE.imagesDownloadStrategy != DownloadStrategy.NOT;
    }

    private static String getString(SharedPreferences sharedPreferences, String key, @StringRes int defValueResId) {
        return
                sharedPreferences.getString(
                        key, MyApplication.getContext().getString(defValueResId));
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

    private static enum CacheSize {
        // 64MB, 128MB, 256MB
        LOW(64), NORMAL(128), HIGH(256);

        private static final CacheSize[] VALUES = CacheSize.values();

        private int size;

        private CacheSize(int size) {
            this.size = size * 1000 * 1000;
        }

        public static CacheSize fromString(String value) {
            return VALUES[Integer.parseInt(value)];
        }
    }

    private static enum DownloadStrategy {
        NOT, WIFI, ALWAYS;

        private static final DownloadStrategy[] VALUES = DownloadStrategy.values();

        public static DownloadStrategy fromString(String value) {
            return VALUES[Integer.parseInt(value)];
        }

        public boolean isDownload(boolean hasWifi) {
            return
                    equals(WIFI) && hasWifi
                            || equals(ALWAYS);
        }
    }

    private static enum AvatarResolutionStrategy {
        LOW, HIGH_WIFI, HIGH;

        private static final AvatarResolutionStrategy[] VALUES = AvatarResolutionStrategy.values();

        public static AvatarResolutionStrategy fromString(String value) {
            return VALUES[Integer.parseInt(value)];
        }

        public boolean isHigherResolution(boolean hasWifi) {
            return
                    equals(HIGH_WIFI) && hasWifi
                            || equals(HIGH);
        }
    }

    private static enum AvatarCacheInvalidationInterval {
        EVERY_DAY(DateUtil::today),
        EVERY_WEEK(DateUtil::dayOfWeek),
        EVERY_MONTH(DateUtil::dayOfMonth);

        private static final AvatarCacheInvalidationInterval[] VALUES = AvatarCacheInvalidationInterval.values();

        public static AvatarCacheInvalidationInterval fromString(String value) {
            return VALUES[Integer.parseInt(value)];
        }

        private final Callable<String> callable;

        private AvatarCacheInvalidationInterval(Callable<String> callable) {
            this.callable = callable;
        }

        public Key getSignature() {
            try {
                return new StringSignature(callable.call());
            } catch (Exception e) {
                throw new RuntimeException("Unknown exception occurs.", e);
            }
        }
    }
}
