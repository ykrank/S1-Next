package cl.monsoon.s1next.activity;

import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;

/**
 * See https://code.google.com/p/android/issues/detail?id=78154
 */
public class ActionBarActivityCompat extends ActionBarActivity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU
                && "LGE".equals(Build.BRAND)
                && Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN
                || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU
                && "LGE".equals(Build.BRAND)
                && Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            openOptionsMenu();

            return true;
        }

        return super.onKeyUp(keyCode, event);
    }
}
