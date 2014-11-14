package cl.monsoon.s1next.util;

import android.widget.Toast;

import cl.monsoon.s1next.MyApplication;

public final class ToastHelper {

    private ToastHelper() {
    }

    public static void showByText(CharSequence text) {
        Toast.makeText(MyApplication.getContext(), text, Toast.LENGTH_LONG).show();
    }

    public static void showByResId(int resId) {
        Toast.makeText(MyApplication.getContext(), resId, Toast.LENGTH_SHORT).show();
    }
}
