package me.ykrank.s1next.util;


import android.app.Activity;
import android.app.Application;
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

    /**
     * get base context (FragmentActivity, Activity, ApplicationContext) ContextWrapper <br>
     * fork from {@link com.bumptech.glide.manager.RequestManagerRetriever#get(Context)}
     */
    public static Context getBaseContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("You cannot start a load on a null Context");
        } else if (LooperUtil.isOnMainThread() && !(context instanceof Application)) {
            if (context instanceof FragmentActivity) {
                return context;
            } else if (context instanceof Activity) {
                return context;
            } else if (context instanceof ContextWrapper) {
                return getBaseContext(((ContextWrapper) context).getBaseContext());
            }
        }
        return context.getApplicationContext();
    }
}
