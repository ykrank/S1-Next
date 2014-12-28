package cl.monsoon.s1next.util;

import android.support.annotation.IntDef;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cl.monsoon.s1next.MyApplication;

public final class ToastHelper {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    public @interface Duration {

    }

    public static void showByText(CharSequence text, @Duration int duration) {
        Toast.makeText(MyApplication.getContext(), text, duration).show();
    }

    public static void showByResId(int resId, @Duration int duration) {
        Toast.makeText(MyApplication.getContext(), resId, duration).show();
    }
}
