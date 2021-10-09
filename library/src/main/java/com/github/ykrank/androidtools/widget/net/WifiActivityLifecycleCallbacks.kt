package com.github.ykrank.androidtools.widget.net

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.annotation.CallSuper


open abstract class WifiActivityLifecycleCallbacks(val context: Context) : Application.ActivityLifecycleCallbacks {

    private var mWifiBroadcastReceiver: WifiBroadcastReceiver? = null
    /**
     * Forked from http://stackoverflow.com/a/13809991
     */
    private var mVisibleCount: Int = 0
    private var mNeedMonitorWifiActivityCount: Int = 0
    private var mExistCount: Int = 0

    val isAppVisible: Boolean
        get() = mVisibleCount > 0

    open val wifiStateChangedCallback: ((Boolean) -> Unit)?
        get() = null

    @CallSuper
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mExistCount++
    }

    @CallSuper
    override fun onActivityStarted(activity: Activity) {
        mVisibleCount++
    }

    @CallSuper
    override fun onActivityResumed(activity: Activity) {
        if (activity is WifiBroadcastReceiver.NeedMonitorWifi) {
            if (mNeedMonitorWifiActivityCount == 0) {
                check()
                mWifiBroadcastReceiver?.registerIfNeeded()
            }
            mNeedMonitorWifiActivityCount++
        }
    }

    @CallSuper
    override fun onActivityPaused(activity: Activity) {
        if (activity is WifiBroadcastReceiver.NeedMonitorWifi) {
            mNeedMonitorWifiActivityCount--
            if (mNeedMonitorWifiActivityCount == 0) {
                check()
                mWifiBroadcastReceiver?.unregisterIfNeeded()
            }
        }
    }

    @CallSuper
    override fun onActivityStopped(activity: Activity) {
        mVisibleCount--
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {}

    @CallSuper
    override fun onActivityDestroyed(activity: Activity) {
        mExistCount--
    }

    private fun check() {
        if (mWifiBroadcastReceiver == null) {
            mWifiBroadcastReceiver = WifiBroadcastReceiver(context.applicationContext, wifiStateChangedCallback)
        }
    }
}
