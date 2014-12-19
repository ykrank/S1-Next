package cl.monsoon.s1next.singleton;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.util.TypedValue;
import android.widget.TextView;

import cl.monsoon.s1next.MyApplication;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.SettingsFragment;

public enum Config {
    INSTANCE;

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
    private volatile int colorAccent87;
    private volatile float textSize;
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
        int accentColor = typedArray.getColor(0, -1);
        if (accentColor == -1) {
            throw new IllegalStateException("Theme accent color can't be -1.");
        }

        INSTANCE.colorAccent87 =
                Color.argb(
                        (int) (0.87 * 255 + 0.5),
                        Color.red(accentColor), Color.green(accentColor), Color.blue(accentColor));
    }

    public static void changeTextColorWhenS1Theme(TextView textView) {
        if (INSTANCE.currentTheme == LIGHT_THEME_S1) {
            textView.setTextColor(
                    MyApplication.getContext()
                            .getResources().getColor(R.color.s1_theme_text_color_primary_87));
        }
    }

    @ColorRes
    public static int getColorAccent87() {
        return INSTANCE.colorAccent87;
    }

    private static float getTextSize() {
        return INSTANCE.textSize;
    }

    public static void setTextSize(SharedPreferences sharedPreferences) {
        String value =
                sharedPreferences.getString(
                        SettingsFragment.PREF_KEY_FONT_SIZE,
                        MyApplication.getContext().
                                getString(R.string.pref_font_size_default_value));

        INSTANCE.textSize = FontSize.fromString(value).getSize();
    }

    /**
     * Update TextView's font size depends on
     * {@link cl.monsoon.s1next.singleton.Config#textSize} (which same to Settings).
     *
     * @param textView works for {@link android.widget.EditText} and {@link android.widget.Button}.
     */
    public static void updateTextSize(TextView textView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getTextSize() * Config.getTextSize());
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

    public static enum FontSize {
        VERY_SMALL(0.8f), SMALL(0.9f), Medium(1f), LARGE(1.1f), VERY_LARGE(1.2f);

        private static final FontSize[] VALUES = FontSize.values();

        private final float size;

        FontSize(float size) {
            this.size = size;
        }

        public float getSize() {
            return size;
        }

        public static FontSize fromString(String value) {
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
