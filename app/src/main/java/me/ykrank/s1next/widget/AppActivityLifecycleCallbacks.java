package me.ykrank.s1next.widget;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import me.ykrank.s1next.widget.hostcheck.HostUrlCheckTask;

public final class AppActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private final WifiBroadcastReceiver mWifiBroadcastReceiver;
    private final HostUrlCheckTask hostUrlCheckTask;
    private final NoticeCheckTask noticeCheckTask;
    /**
     * Forked from http://stackoverflow.com/a/13809991
     */
    private int mVisibleCount;
    private int mNeedMonitorWifiActivityCount;
    private int mExistCount;

    public AppActivityLifecycleCallbacks(Context context) {
        mWifiBroadcastReceiver = new WifiBroadcastReceiver(context);
        hostUrlCheckTask = HostUrlCheckTask.INSTANCE;
        noticeCheckTask = new NoticeCheckTask();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (mExistCount == 0) {
            hostUrlCheckTask.startCheckHostTask(activity);
        } else {
            hostUrlCheckTask.inspectCheckHostTask();
        }
        mExistCount++;
    }

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
        noticeCheckTask.inspectCheckNoticeTask();
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
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mExistCount--;
        if (mExistCount == 0) {
            hostUrlCheckTask.stopCheckHostTask(activity);
        }
    }

    public boolean isAppVisible() {
        return mVisibleCount > 0;
    }
}
