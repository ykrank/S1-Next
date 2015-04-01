package cl.monsoon.s1next.util;

import android.support.annotation.IntDef;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cl.monsoon.s1next.App;

public final class ToastUtil {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    private @interface Duration {

    }

    private ToastUtil() {

    }

    public static void showByText(String text, @Duration int duration) {
        Toast.makeText(App.getContext(), text, duration).show();
    }

    public static void showByResId(int resId, @Duration int duration) {
        Toast.makeText(App.getContext(), resId, duration).show();
    }
}
