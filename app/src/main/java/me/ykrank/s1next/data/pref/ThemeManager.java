package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.StyleRes;
import androidx.core.graphics.ColorUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import me.ykrank.s1next.R;

/**
 * A manager manage the theme preference that is associated with settings.
 */
public final class ThemeManager {

    public static final int TRANSLUCENT_THEME_DARK = R.style.Theme_Translucent_Dark;

    /**
     * Default theme in AndroidManifest.xml is light theme.
     */
    private static final Theme DEFAULT_THEME = Theme.AFTERNOON_TEA;

    /**
     * https://www.google.com/design/spec/style/color.html#color-ui-color-application
     */
    private static final int WHITE_BACKGROUND_SECONDARY_TEXT_ALPHA = (int) (0.54 * 255);
    private static final int WHITE_BACKGROUND_HINT_OR_DISABLED_TEXT_ALPHA = (int) (0.38 * 255);
    private static final int BLACK_BACKGROUND_SECONDARY_TEXT_ALPHA = (int) (0.70 * 255);
    private static final int BLACK_BACKGROUND_HINT_OR_DISABLED_TEXT_ALPHA = (int) (0.30 * 255);
    private final Context mContext;
    private final GeneralPreferences mGeneralPreferencesProvider;

    @ColorInt
    private volatile int mColorAccent;
    /**
     * Lazy Initialization.
     */
    private final Supplier<Theme> mThemeSupplier = new Supplier<Theme>() {

        @Override
        public Theme get() {
            Theme theme;

            int currentNightMode = mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                theme = Theme.VALUES.get(mGeneralPreferencesProvider.getDarkThemeIndex());
            } else {
                theme = Theme.VALUES.get(mGeneralPreferencesProvider.getThemeIndex());
            }
            invalidateAccentColor(theme);

            return theme;
        }
    };
    private volatile Supplier<Theme> mThemeMemorized = Suppliers.memoize(mThemeSupplier);

    public ThemeManager(Context context, GeneralPreferences generalPreferencesProvider) {
        this.mContext = context;
        this.mGeneralPreferencesProvider = generalPreferencesProvider;
    }

    /**
     * Used for invalidating the theme preference if settings change.
     */
    public void invalidateTheme() {
        mThemeMemorized = Suppliers.memoize(mThemeSupplier);
    }

    /**
     * Commits theme preference change for settings.
     *
     * @param index The theme index.
     */
    public void applyTheme(int index) {
        mGeneralPreferencesProvider.setThemeIndex(index);
    }

    public Theme getTheme() {
        return mThemeMemorized.get();
    }

    @StyleRes
    public int getThemeStyle() {
        return getTheme().style;
    }

    @StyleRes
    public int getThemeTranslucentStyle() {
        return getTheme().translucentStyle;
    }

    public int getThemeIndex() {
        return Theme.VALUES.indexOf(getTheme());
    }

    public void setThemeByIndex(int i) {
        Theme theme = Theme.VALUES.get(i);
        mThemeMemorized = Suppliers.ofInstance(theme);
        invalidateAccentColor(theme);
    }

    public boolean isDefaultTheme() {
        return getTheme() == DEFAULT_THEME;
    }

    public boolean isDarkTheme() {
        Theme theme = getTheme();
        return theme == Theme.DARK_THEME
                || theme == Theme.DARK_THEME_NIGHT_MODE
                || theme == Theme.DARK_THEME_NIGHT_MODE_AMOLED;
    }

    /**
     * Used for invalidating the accent color if theme changes.
     */
    private void invalidateAccentColor(Theme theme) {
        // get current theme's accent color
        TypedArray typedArray = mContext.obtainStyledAttributes(theme.style,
                new int[]{R.attr.colorAccent});
        mColorAccent = typedArray.getColor(0, -1);
        typedArray.recycle();
        Preconditions.checkState(mColorAccent != -1);
    }

    @ColorInt
    public int getGentleAccentColor() {
        return ColorUtils.setAlphaComponent(mColorAccent, getSecondaryTextAlpha());
    }

    @ColorInt
    public int getHintOrDisabledGentleAccentColor() {
        return ColorUtils.setAlphaComponent(mColorAccent, getHintOrDisabledTextAlpha());
    }

    @BackgroundAlphaDef
    private int getSecondaryTextAlpha() {
        if (isDarkTheme()) {
            return BLACK_BACKGROUND_SECONDARY_TEXT_ALPHA;
        } else {
            return WHITE_BACKGROUND_SECONDARY_TEXT_ALPHA;
        }
    }

    @BackgroundAlphaDef
    private int getHintOrDisabledTextAlpha() {
        if (isDarkTheme()) {
            return BLACK_BACKGROUND_HINT_OR_DISABLED_TEXT_ALPHA;
        } else {
            return WHITE_BACKGROUND_HINT_OR_DISABLED_TEXT_ALPHA;
        }
    }

    public enum Theme {
        AFTERNOON_TEA(R.style.Theme_Light_AfternoonTea, R.style.Theme_Translucent_AfternoonTea),
        LIGHT_THEME(R.style.Theme_Light, R.style.Theme_Translucent_Light),
        LIGHT_THEME_AMBER(R.style.Theme_Light_Amber, R.style.Theme_Translucent_Amber),
        LIGHT_THEME_GREEN(R.style.Theme_Light_Green, R.style.Theme_Translucent_Green),
        LIGHT_THEME_LIGHT_BLUE(R.style.Theme_Light_LightBlue, R.style.Theme_Translucent_LightBlue),
        LIGHT_THEME_PURPLE(R.style.Theme_Light_Purple, R.style.Theme_Translucent_Purple),
        DARK_THEME(R.style.Theme_Dark, R.style.Theme_Translucent_Dark),
        DARK_THEME_NIGHT_MODE(R.style.Theme_Dark_NightMode, R.style.Theme_Translucent_Dark_NightMode),
        DARK_THEME_NIGHT_MODE_AMOLED(R.style.Theme_Dark_NightMode_Amoled, R.style.Theme_Translucent_Dark_NightMode_Amoled);

        private static final List<Theme> VALUES = Arrays.asList(Theme.values());

        @StyleRes
        private final int style;

        @StyleRes
        private final int translucentStyle;

        Theme(@StyleRes int style, @StyleRes int translucentStyle) {
            this.style = style;
            this.translucentStyle = translucentStyle;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            WHITE_BACKGROUND_SECONDARY_TEXT_ALPHA,
            WHITE_BACKGROUND_HINT_OR_DISABLED_TEXT_ALPHA,
            BLACK_BACKGROUND_SECONDARY_TEXT_ALPHA,
            BLACK_BACKGROUND_HINT_OR_DISABLED_TEXT_ALPHA
    })
    private @interface BackgroundAlphaDef {
    }
}
