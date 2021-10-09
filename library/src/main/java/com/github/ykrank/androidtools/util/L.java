package com.github.ykrank.androidtools.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.ykrank.androidtools.BuildConfig;
import com.github.ykrank.androidtools.GlobalData;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by AdminYkrank on 2016/4/20.
 * 对Log的包装
 */
public class L {
    static AtomicBoolean init = new AtomicBoolean(false);
    static boolean showLog = false;
    private static String TAG = BuildConfig.LIBRARY_PACKAGE_NAME;

    public static void init(@NonNull Context context) {
        TAG = GlobalData.provider.getLogTag();

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                //.logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag(TAG)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return showLog();
            }
        });
    }

    public static void setUser(final String id, final String name) {
        CrashReport.setUserId("id:" + id + ",name:" + name);
    }

    public static boolean showLog() {
        if (!init.get()) {
            init.set(true);
            showLog = GlobalData.provider.getDebug() || "alpha".equals(GlobalData.provider.getBuildType());
        }
        return showLog;
    }

    public static void l(String msg) {
        if (showLog()) {
            BuglyLog.d(TAG, msg);
        }
    }

    public static void print(String msg) {
        if (showLog()) {
            Log.d(TAG, msg);
        }
    }

    public static void print(Throwable e) {
        if (showLog() && e != null) {
            e.printStackTrace();
        }
    }

    public static void d(@Nullable String msg) {
        Logger.d(msg);
    }

    public static void d(@NonNull Throwable e) {
        Logger.d(e.getMessage());
    }

    public static void d(String tag, @Nullable String msg) {
        Logger.t(tag).d(msg);
    }

    public static void i(@NonNull String msg) {
        Logger.i(msg);
    }

    public static void i(String tag, @NonNull String msg) {
        Logger.t(tag).i(msg);
    }

    public static void w(@NonNull String msg) {
        Logger.w(msg);
    }

    public static void w(String tag, @NonNull String msg) {
        Logger.t(tag).w(msg);
    }

    public static void w(Throwable e) {
        Logger.e(e, "");
    }

    public static void e(@NonNull String msg) {
        e(null, msg, null);
    }

    public static void e(Throwable e) {
        e(null, "error", e);
    }

    public static void e(String msg, Throwable tr) {
        e(null, msg, tr);
    }

    public static void e(String tag, @NonNull String msg) {
        e(tag, msg, null);
    }

    public static void e(String tag, @NonNull String msg, Throwable tr) {
        BuglyLog.e(TAG + tag, msg, tr);
        if (tr != null) {
            tr.printStackTrace();
        }
        if (showLog() && tr != null) {
            CrashReport.postCatchedException(tr);
        }
    }

    public static void report(Throwable tr) {
        report(tr, Log.WARN);
    }

    public static void report(Throwable tr, int severity) {
        if (showLog()) {
            tr.printStackTrace();
        }

        ErrorParser errorParser = GlobalData.provider.getErrorParser();
        if (errorParser != null && errorParser.ignoreError(tr)) {
            return;
        } else {
            CrashReport.postCatchedException(tr);
        }
    }

    public static void report(String msg, Throwable tr) {
        leaveMsg(msg);
        report(tr);
    }

    public static void leaveMsg(String msg) {
        leaveMsg("MSG", msg);
    }

    public static void leaveMsg(String tag, String msg) {
        i(tag, msg);
        BuglyLog.i(tag, msg);
    }

    public static void leaveMsg(Throwable tr) {
        BuglyLog.e("MSG", "Error", tr);
    }

    public static void test() {
        throw new RuntimeException("Just test");
    }

    public static void throwNewErrorIfDebug(RuntimeException throwable) {
        if (BuildConfig.DEBUG) {
            throw throwable;
        } else {
            L.report(throwable, Log.WARN);
        }
    }
}
