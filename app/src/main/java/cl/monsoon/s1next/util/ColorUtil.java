package cl.monsoon.s1next.util;

import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ColorUtil {

    public static final int WHITE_BACKGROUND_DISABLED_OR_HINT_TEXT_ALPHA = 26;
    public static final int WHITE_BACKGROUND_SECONDARY_TEXT_OR_ICONS_ALPHA = 54;

    public static final int BLACK_BACKGROUND_DISABLED_OR_HINT_TEXT_ALPHA = 30;
    public static final int BLACK_BACKGROUND_SECONDARY_TEXT_ALPHA = 70;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            WHITE_BACKGROUND_DISABLED_OR_HINT_TEXT_ALPHA,
            WHITE_BACKGROUND_SECONDARY_TEXT_OR_ICONS_ALPHA,
            BLACK_BACKGROUND_DISABLED_OR_HINT_TEXT_ALPHA,
            BLACK_BACKGROUND_SECONDARY_TEXT_ALPHA
    })
    public @interface Alpha {

    }

    private ColorUtil() {

    }

    /**
     * Returns a color-int with specific alpha.
     */
    @ColorRes
    public static int a(int color, @Alpha int alpha) {
        if (alpha < 0 || alpha > 100) {
            throw new IllegalArgumentException("Alpha must be between 0 and 100 inclusive.");
        }

        return Color.argb(
                (int) (alpha / 100.0 * 255 + 0.5),
                Color.red(color), Color.green(color), Color.blue(color));
    }
}
