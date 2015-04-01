package cl.monsoon.s1next.util;

import android.content.res.Resources;
import android.support.annotation.AttrRes;
import android.util.TypedValue;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;

public final class ResourceUtil {

    private ResourceUtil() {

    }

    public static int getResourceId(Resources.Theme theme, @AttrRes int resId) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(resId, typedValue, true);
        return typedValue.resourceId;
    }

    public static int getToolbarHeight() {
        return App.getContext().getResources().getDimensionPixelSize(
                R.dimen.abc_action_bar_default_height_material);
    }
}
