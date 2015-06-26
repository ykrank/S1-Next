package cl.monsoon.s1next.util;

import android.content.res.Resources;
import android.support.annotation.AttrRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public final class ResourceUtil {

    private ResourceUtil() {

    }

    public static int getResourceId(Resources.Theme theme, @AttrRes int resId) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(resId, typedValue, true);
        return typedValue.resourceId;
    }

    /**
     * Set the scaling factor for fonts displayed on the display.
     *
     * @param scale the scaling factor.
     */
    public static void setScaledDensity(Resources resources, float scale) {
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        // https://android.googlesource.com/platform/packages/apps/Settings/+/master/src/com/android/settings/Display.java#99
        displayMetrics.scaledDensity = displayMetrics.density
                * resources.getConfiguration().fontScale
                * scale;
    }
}
