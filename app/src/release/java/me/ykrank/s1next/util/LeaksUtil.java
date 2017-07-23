package me.ykrank.s1next.util;

import android.app.Activity;
import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class LeaksUtil {

    public static RefWatcher install(Application application) {
        return LeakCanary.install(application);
    }

    /**
     * leaks in huawei emui5.0
     */
    public static void releaseGestureBoostManagerLeaks(Activity activity) {

    }

    /**
     * leaks in huawei emui5.0
     */
    public static void releaseFastgrabConfigReaderLeaks(Activity activity) {

    }
}
