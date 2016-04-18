package me.ykrank.s1next.widget;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

public final class AppActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    /**
     * Forked from http://stackoverflow.com/a/13809991
     */
    private int mVisibleCount;

    private final WifiBroadcastReceiver mWifiBroadcastReceiver;
    private int mNeedMonitorWifiActivityCount;

    public AppActivityLifecycleCallbacks(Context context) {
        mWifiBroadcastReceiver = new WifiBroadcastReceiver(context);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {
        mVisibleCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof WifiBroadcastReceiver.NeedMonitorWifi) {
            if (mNeedMonitorWifiActivityCount == 0) {
                mWifiBroadcastReceiver.registerIfNeeded();
            }
            mNeedMonitorWifiActivityCount++;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof WifiBroadcastReceiver.NeedMonitorWifi) {
            mNeedMonitorWifiActivityCount--;
            if (mNeedMonitorWifiActivityCount == 0) {
                mWifiBroadcastReceiver.unregisterIfNeeded();
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        mVisibleCount--;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}

    public boolean isAppVisible() {
        return mVisibleCount > 0;
    }
}
