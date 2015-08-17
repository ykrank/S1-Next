package cl.monsoon.s1next.util;

import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.AnyRes;
import android.support.annotation.AttrRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public final class ResourceUtil {

    private ResourceUtil() {}

    /**
     * Retrieves the resource id in the Theme.
     *
     * @param theme The theme we want to retrieve attribute.
     * @param resId The resource id of of the desired theme attribute.
     * @return The corresponding resource id.
     */
    @AnyRes
    public static int getResourceId(Resources.Theme theme, @AttrRes int resId) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(resId, typedValue, true);
        return typedValue.resourceId;
    }

    /**
     * Sets the scaling factor for fonts displayed on the display.
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

    /**
     * Whether current layout direction is from Right to Left.
     */
    public static boolean isRTL(Resources resources) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }
}
