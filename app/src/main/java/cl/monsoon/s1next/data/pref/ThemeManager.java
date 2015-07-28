package cl.monsoon.s1next.data.pref;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.StyleRes;
import android.support.v4.graphics.ColorUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import cl.monsoon.s1next.R;

/**
 * A manager manage the theme preference that is associated with settings.
 */
public final class ThemeManager {

    /**
     * Default theme in AndroidManifest.xml is dark theme.
     */
    private static final Theme DEFAULT_THEME = Theme.DARK_THEME;

    public static final int TRANSLUCENT_THEME_LIGHT = R.style.Theme_Translucent_Light;

    // https://www.google.com/design/spec/style/color.html#color-ui-color-application
    private static final int WHITE_BACKGROUND_HINT_OR_DISABLED_TEXT_ALPHA = (int) (0.26 * 255);
    private static final int WHITE_BACKGROUND_SECONDARY_TEXT_ALPHA = (int) (0.54 * 255);
    private static final int BLACK_BACKGROUND_HINT_OR_DISABLED_TEXT_ALPHA = (int) (0.30 * 255);
    private static final int BLACK_BACKGROUND_SECONDARY_TEXT_ALPHA = (int) (0.70 * 255);

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            WHITE_BACKGROUND_HINT_OR_DISABLED_TEXT_ALPHA,
            WHITE_BACKGROUND_SECONDARY_TEXT_ALPHA,
            BLACK_BACKGROUND_HINT_OR_DISABLED_TEXT_ALPHA,
            BLACK_BACKGROUND_SECONDARY_TEXT_ALPHA
    })
    private @interface BackgroundAlphaDef {

    }

    private final Context mContext;
    private final GeneralPreferencesRepository mGeneralPreferencesProvider;

    /**
     * Lazy Initialization.
     */
    private final Supplier<Theme> mThemeSupplier = new Supplier<Theme>() {

        @Override
        public Theme get() {
            Theme theme = Theme.VALUES.get(Integer.parseInt(
                    mGeneralPreferencesProvider.getThemeString()));
            invalidateAccentColor(theme);

            return theme;
        }
    };

    private volatile Supplier<Theme> mThemeMemorized = Suppliers.memoize(mThemeSupplier);
    private volatile int mColorAccent;

    public ThemeManager(Context context, GeneralPreferencesRepository generalPreferencesProvider) {
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
     * Commit theme preference change for settings.
     *
     * @param index The theme index.
     */
    public void applyTheme(int index) {
        mGeneralPreferencesProvider.applyThemeString(String.valueOf(index));
    }

    public Theme getTheme() {
        return mThemeMemorized.get();
    }

    @StyleRes
    public int getThemeStyle() {
        return getTheme().style;
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
                || theme == Theme.DARK_THEME_IMPERFECT
                || theme == Theme.DARK_THEME_IMPERFECT_BLACK;
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
        LIGHT_THEME(R.style.Theme_Light),
        LIGHT_THEME_INVERSE_AMBER(R.style.Theme_Light_Inverse_Amber),
        LIGHT_THEME_INVERSE_GREEN(R.style.Theme_Light_Inverse_Green),
        LIGHT_THEME_INVERSE_LIGHT_BLUE(R.style.Theme_Light_Inverse_LightBlue),
        DARK_THEME(R.style.Theme_Dark),
        DARK_THEME_IMPERFECT(R.style.Theme_Dark_Imperfect),
        DARK_THEME_IMPERFECT_BLACK(R.style.Theme_Dark_Imperfect_Black);

        private static final List<Theme> VALUES = Arrays.asList(Theme.values());

        @StyleRes
        private final int style;

        Theme(@StyleRes int style) {
            this.style = style;
        }
    }
}
