package cl.monsoon.s1next.util;

import android.content.Context;

import cl.monsoon.s1next.R;

public final class ResourceUtil {

    private ResourceUtil() {

    }

    public static int getToolbarHeight(Context context) {
        return
                context.getResources()
                        .getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
    }
}
