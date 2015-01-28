package cl.monsoon.s1next.util;

import cl.monsoon.s1next.MyApplication;
import cl.monsoon.s1next.R;

public final class ResourceUtil {

    private ResourceUtil() {

    }

    public static int getToolbarHeight() {
        return
                MyApplication.getContext().getResources()
                        .getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
    }
}
