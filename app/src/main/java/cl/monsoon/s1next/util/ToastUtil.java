package cl.monsoon.s1next.util;

import android.content.Context;
import android.widget.Toast;

public final class ToastUtil {

    private ToastUtil() {}

    /**
     * Show a long toast that just contains a text view.
     *
     * @param context The context to use.  Usually your {@link android.app.Application}
     *                or {@link android.app.Activity} object.
     * @param text    The text to show.
     */
    public static void showLongToastByText(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    /**
     * Show a long toast that just contains a text view with the text from a resource.
     *
     * @param context The context to use.  Usually your {@link android.app.Application}
     *                or {@link android.app.Activity} object.
     * @param resId   The resource id of the string resource to use.
     */
    public static void showLongToastByResId(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }
}
