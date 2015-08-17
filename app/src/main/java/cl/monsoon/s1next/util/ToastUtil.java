package cl.monsoon.s1next.util;

import android.support.annotation.IntDef;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cl.monsoon.s1next.App;

public final class ToastUtil {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    private @interface DurationDef {}

    private ToastUtil() {}

    /**
     * Show a standard toast that just contains a text view.
     *
     * @param text     The text to show.
     * @param duration How long to display the message. Either {@link Toast#LENGTH_SHORT} or
     *                 {@link Toast#LENGTH_LONG}.
     */
    public static void showByText(String text, @DurationDef int duration) {
        Toast.makeText(App.get(), text, duration).show();
    }

    /**
     * Show a standard toast that just contains a text view.
     *
     * @param resId    The resource id of the string resource to use.
     * @param duration How long to display the message. Either {@link Toast#LENGTH_SHORT} or
     *                 {@link Toast#LENGTH_LONG}.
     */
    public static void showByResId(int resId, @DurationDef int duration) {
        Toast.makeText(App.get(), resId, duration).show();
    }
}
