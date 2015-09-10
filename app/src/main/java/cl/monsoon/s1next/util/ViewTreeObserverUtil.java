package cl.monsoon.s1next.util;

import android.os.Build;
import android.view.ViewTreeObserver;

public final class ViewTreeObserverUtil {

    public ViewTreeObserverUtil() {}

    @SuppressWarnings("deprecation")
    public static void removeOnGlobalLayoutListener(ViewTreeObserver viewTreeObserver,
                                                    ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener);
        } else {
            viewTreeObserver.removeGlobalOnLayoutListener(onGlobalLayoutListener);
        }
    }
}
