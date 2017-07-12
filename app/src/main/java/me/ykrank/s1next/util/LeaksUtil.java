package me.ykrank.s1next.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.AndroidExcludedRefs;
import com.squareup.leakcanary.DisplayLeakService;
import com.squareup.leakcanary.ExcludedRefs;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

import me.ykrank.s1next.BuildConfig;

public class LeaksUtil {

    public static RefWatcher install(Application application) {
        if (BuildConfig.DEBUG) {
            ExcludedRefs excludedRefs = AndroidExcludedRefs.createAppDefaults()
                    //exclude InputMethodManager
                    .clazz("android.view.inputmethod.InputMethodManager")
                    .build();
            return LeakCanary.refWatcher(application)
                    .listenerServiceClass(DisplayLeakService.class)
                    .excludedRefs(excludedRefs)
                    .buildAndInstall();
        } else {
            return LeakCanary.install(application);
        }
    }

    /**
     * leaks in huawei emui5.0
     */
    public static void releaseGestureBoostManagerLeaks(Activity activity) {
        try {
            Class clazz = Class.forName("android.gestureboost.GestureBoostManager");
            Object sGestureBoostManager = FieldUtils.readDeclaredStaticField(clazz, "sGestureBoostManager", true);
            Field contextField = FieldUtils.getField(clazz, "mContext", true);
            Object mContext = FieldUtils.readField(contextField, sGestureBoostManager, true);
            if (mContext == activity) {
                FieldUtils.writeField(contextField, sGestureBoostManager, null, true);
            }
        } catch (Exception e) {
            L.d("releaseGestureBoostManagerLeaks exception");
        }
    }

    /**
     * leaks in huawei emui5.0
     */
    public static void releaseFastgrabConfigReaderLeaks(Activity activity) {
        try {
            Class clazz = Class.forName("android.rms.iaware.FastgrabConfigReader");
            Object mFastgrabConfigReader = FieldUtils.readDeclaredStaticField(clazz, "mFastgrabConfigReader", true);
            Field contextField = FieldUtils.getField(clazz, "mContext", true);
            Context mContext = (Context) FieldUtils.readField(contextField, mFastgrabConfigReader, true);
            if (ContextUtils.getBaseContext(mContext) == activity) {
                FieldUtils.writeField(contextField, mFastgrabConfigReader, null, true);
            }
        } catch (Exception e) {
            L.d("releaseFastgrabConfigReaderLeaks exception");
        }
    }
}
