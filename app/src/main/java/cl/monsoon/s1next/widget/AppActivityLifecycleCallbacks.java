package cl.monsoon.s1next.widget;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

public final class AppActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    /**
     * Forked from http://stackoverflow.com/a/13809991
     */
    private int visibleCount;

    private WifiBroadcastReceiver mWifiBroadcastReceiver;

    public AppActivityLifecycleCallbacks(Context context) {
        mWifiBroadcastReceiver = new WifiBroadcastReceiver(context);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {
        visibleCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof WifiBroadcastReceiver.NeedMonitorWifi) {
            mWifiBroadcastReceiver.registerIfNeeded();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof WifiBroadcastReceiver.NeedMonitorWifi) {
            mWifiBroadcastReceiver.unregisterIfNeeded();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        visibleCount--;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}

    public boolean isAppVisible() {
        return visibleCount > 0;
    }
}
