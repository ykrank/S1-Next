package com.github.ykrank.androidtools.util;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public final class ContextUtils {

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
        if (activity instanceof FragmentActivity) {
            FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
            return fm == null || fm.isDestroyed();
        }
        return false;
    }

    /**
     * get base context (FragmentActivity, Activity, ApplicationContext) ContextWrapper <br>
     * fork from {@link com.bumptech.glide.manager.RequestManagerRetriever#get(Context)}
     */
    public static Context getBaseContext(@NonNull Context context) {
        if (LooperUtil.isOnMainThread() && !(context instanceof Application)) {
            if (context instanceof Activity) {
                return context;
            } else if (context instanceof ContextWrapper) {
                return getBaseContext(((ContextWrapper) context).getBaseContext());
            }
        }
        return context.getApplicationContext();
    }

    /**
     * Returns class name for this fragment with the package prefix removed.
     * This is the default name used to read and write settings.
     *
     * @return The local class name.
     */
    public static String getLocalClassName(Fragment fragment) {
        final String pkg = fragment.getContext().getPackageName();
        final String cls = fragment.getClass().getName();
        int packageLen = pkg.length();
        if (!cls.startsWith(pkg) || cls.length() <= packageLen
                || cls.charAt(packageLen) != '.') {
            return cls;
        }
        return cls.substring(packageLen + 1);
    }

    /**
     * Returns class name for this fragment with the package prefix removed.
     * This is the default name used to read and write settings.
     *
     * @return The local class name.
     */
    public static String getLocalClassName(android.app.Fragment fragment) {
        final String pkg = fragment.getActivity().getPackageName();
        final String cls = fragment.getClass().getName();
        int packageLen = pkg.length();
        if (!cls.startsWith(pkg) || cls.length() <= packageLen
                || cls.charAt(packageLen) != '.') {
            return cls;
        }
        return cls.substring(packageLen + 1);
    }
}
