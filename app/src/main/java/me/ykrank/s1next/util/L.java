package me.ykrank.s1next.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.HashMap;
import java.util.Map;

import me.ykrank.s1next.BuildConfig;

import static me.ykrank.s1next.App.LOG_TAG;

/**
 * Created by AdminYkrank on 2016/4/20.
 * 对Log的包装
 */
public class L {
    final static FixedFifoList<String> msgList = new FixedFifoList<>(9);

    public static void init(@NonNull Context context) {
        CrashReport.setIsDevelopmentDevice(context, BuildConfig.DEBUG);
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            @Override
            public Map<String, String> onCrashHandleStart(int crashType, String errorType,
                                                          String errorMessage, String errorStack) {
                HashMap<String, String> map = new HashMap<>();
                for (int i = 0; i < msgList.size(); i++) {
                    map.put("msg" + i, msgList.get(i));
                }
                return map;
            }
        });
        CrashReport.initCrashReport(context.getApplicationContext());
    }

    public static void setUser(final String id, final String name) {
        CrashReport.setUserId("id:" + id + ",name:" + name);
    }

    public static boolean showLog() {
        return BuildConfig.DEBUG;
    }

    public static void d(String msg) {
        d(LOG_TAG, msg);
    }

    public static void i(String msg) {
        i(LOG_TAG, msg);
    }

    public static void e(String msg) {
        e(LOG_TAG, msg);
    }

    public static void e(Throwable e) {
        e(LOG_TAG, e);
    }

    public static void d(String msg, Throwable tr) {
        d(LOG_TAG, msg, tr);
    }

    public static void i(String msg, Throwable tr) {
        i(LOG_TAG, msg, tr);
    }

    public static void e(String msg, Throwable tr) {
        e(LOG_TAG, msg, tr);
    }

    public static void d(String tag, String msg) {
        if (showLog()) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (showLog()) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (showLog()) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (showLog()) {
            Log.e(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (showLog()) {
            Log.d(tag, msg, tr);
        }
    }


    public static void i(String tag, String msg, Throwable tr) {
        if (showLog()) {
            Log.i(tag, msg, tr);
        }
    }


    public static void e(String tag, String msg, Throwable tr) {
        if (showLog()) {
            CrashReport.postCatchedException(tr);
        }
        BuglyLog.e(tag, msg, tr);
    }

    public static void report(Throwable tr) {
        report(tr, Log.WARN);
    }

    public static void report(Throwable tr, int severity) {
        CrashReport.postCatchedException(tr);
        BuglyLog.e(LOG_TAG, "Report error", tr);
    }

    public static void leaveMsg(String msg) {
        synchronized (msgList) {
            msgList.add(msg);
        }
    }

    public static void report(String msg, Throwable tr) {
        leaveMsg(msg);
        report(tr);
    }

    public static void test() {
        throw new RuntimeException("Just test");
    }
}
