package cl.monsoon.s1next.widget;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Forked from http://stackoverflow.com/a/13809991
 */
public final class AppActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private int visibleCount;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        visibleCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        visibleCount--;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public boolean isAppVisible() {
        return visibleCount > 0;
    }
}
