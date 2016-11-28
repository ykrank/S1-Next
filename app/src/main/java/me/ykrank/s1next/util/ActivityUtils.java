package me.ykrank.s1next.util;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public final class ActivityUtils {

    public static boolean isActivityDestroyedForGlide(Context context) {
        if (context instanceof Activity) {
            return isActivityDestroyedForGlide((Activity) context);
        }
        if (context instanceof ContextWrapper) {
            return isActivityDestroyedForGlide(((ContextWrapper) context).getBaseContext());
        }
        return false;
    }

    public static boolean isActivityDestroyedForGlide(Activity activity) {
        //in device from 4.2
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.isDestroyed();
        }
        //in device before 4.2
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity instanceof FragmentActivity) {
                FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
                return fm == null || fm.isDestroyed();
            }
        }
        return false;
    }
}
