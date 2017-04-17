package me.ykrank.s1next.util;

import android.app.Activity;
import android.content.Context;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

public class LeaksUtil {

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
            L.d(e);
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
            L.d(e);
        }
    }
}
