package cl.monsoon.s1next.util;

import android.content.Context;
import android.util.TypedValue;

import cl.monsoon.s1next.R;

public final class ResourceUtil {

    public static int getToolbarHeight(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true);

        return
                TypedValue.complexToDimensionPixelSize(
                        typedValue.data,
                        context.getResources().getDisplayMetrics());
    }
}
