package com.github.ykrank.androidtools.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.github.ykrank.androidtools.util.LooperUtil.isOnMainThread


object ContextUtils {
    fun isActivityDestroyedForGlide(context: Context?): Boolean {
        if (context is Activity) {
            return isActivityDestroyedForGlide(
                context
            )
        }
        if (context is ContextWrapper) {
            return isActivityDestroyedForGlide(
                context.baseContext
            )
        }
        return false
    }

    fun isActivityDestroyedForGlide(activity: Activity): Boolean {
        //in device from 4.2
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.isDestroyed
        }
        //in device before 4.2
        if (activity is FragmentActivity) {
            val fm = activity.supportFragmentManager
            return fm == null || fm.isDestroyed
        }
        return false
    }

    /**
     * get base context (FragmentActivity, Activity, ApplicationContext) ContextWrapper <br></br>
     * fork from [com.bumptech.glide.manager.RequestManagerRetriever.get]
     */
    fun getBaseContext(context: Context): Context {
        if (isOnMainThread && context !is Application) {
            if (context is Activity) {
                return context
            } else if (context is ContextWrapper) {
                return getBaseContext(context.baseContext)
            }
        }
        return context.applicationContext
    }

    /**
     * Returns class name for this fragment with the package prefix removed.
     * This is the default name used to read and write settings.
     *
     * @return The local class name.
     */
    @JvmStatic
    fun getLocalClassName(fragment: Fragment): String {
        val pkg = fragment.context?.packageName ?: ""
        val cls = fragment.javaClass.name
        val packageLen = pkg.length
        if (!cls.startsWith(pkg) || cls.length <= packageLen || cls[packageLen] != '.') {
            return cls
        }
        return cls.substring(packageLen + 1)
    }

    /**
     * Returns class name for this fragment with the package prefix removed.
     * This is the default name used to read and write settings.
     *
     * @return The local class name.
     */
    fun getLocalClassName(fragment: android.app.Fragment): String {
        val pkg = fragment.activity.packageName
        val cls = fragment.javaClass.name
        val packageLen = pkg.length
        if (!cls.startsWith(pkg) || cls.length <= packageLen || cls[packageLen] != '.') {
            return cls
        }
        return cls.substring(packageLen + 1)
    }
}
