package cl.monsoon.s1next.util;

import android.support.design.widget.Snackbar;
import android.view.View;

public final class SnackBarUtil {

    private SnackBarUtil() {}

    /**
     * Makes a long {@link Snackbar} to display a message.
     * <p>
     * Having a {@link android.support.design.widget.CoordinatorLayout}
     * in your view hierarchy allows Snackbar to enable certain features,
     * such as swipe-to-dismiss and automatically moving of widgets like
     * {@link android.support.design.widget.FloatingActionButton}.
     *
     * @param view The view to find a parent from.
     * @param text The text to show. Can be formatted text.
     * @return The displayed {@link Snackbar}.
     */
    public static Snackbar showLongSnackBar(View view, CharSequence text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        snackbar.show();
        return snackbar;
    }
}
