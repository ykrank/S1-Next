package me.ykrank.s1next.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.ykrank.androidtools.util.RxJavaUtil;
import com.tencent.bugly.crashreport.CrashReport;

import me.ykrank.s1next.BuildConfig;

import static me.ykrank.s1next.util.ErrorUtil.BUGLY_APP_ID;

/**
 * Created by ykrank on 2017/8/9.
 */

public class BuglyUtils {

    public static void init(@NonNull Context context) {
        final Context appContext = context.getApplicationContext();
        RxJavaUtil.workInRxIoThread(() -> {
            CrashReport.UserStrategy userStrategy = new CrashReport.UserStrategy(appContext);
            userStrategy.setAppVersion(BuildConfig.VERSION_NAME + "-" + BuildConfig.VERSION_CODE);
            CrashReport.initCrashReport(appContext, BUGLY_APP_ID, BuildConfig.DEBUG, userStrategy);
            CrashReport.setIsDevelopmentDevice(appContext, BuildConfig.DEBUG);
        });
    }
}
