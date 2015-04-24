package cl.monsoon.s1next.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

/**
 * See https://code.google.com/p/android/issues/detail?id=78154
 */
@SuppressLint("Registered")
public class AppCompatActivityCompat extends AppCompatActivity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU
                && "LGE".equals(Build.BRAND)
                && Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN
                || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU
                && "LGE".equals(Build.BRAND)
                && Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            openOptionsMenu();

            return true;
        }

        return super.onKeyUp(keyCode, event);
    }
}
