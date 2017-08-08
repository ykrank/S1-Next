package me.ykrank.s1next.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import me.ykrank.s1next.App;
import me.ykrank.s1next.BuildConfig;

import static me.ykrank.s1next.App.LOG_TAG;

/**
 * Created by AdminYkrank on 2016/4/20.
 * 对Log的包装
 */
public class L {
    static final String BUGLY_APP_ID = "eae39d8732";

    public static void init(@NonNull Context context) {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                //.logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag(LOG_TAG)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
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
        return BuildConfig.DEBUG || "alpha".equals(BuildConfig.BUILD_TYPE);
    }

    public static void l(String msg) {
        if (BuildConfig.DEBUG) {
            Log.e("msg", msg);
        }
    }

    public static void print(String msg) {
        if (showLog()) {
            Log.d(LOG_TAG, msg);
        }
    }

    public static void print(Throwable e) {
        if (showLog() && e != null) {
            e.printStackTrace();
        }
    }

    public static void d(String msg) {
        Logger.d(msg);
    }

    public static void d(Throwable e) {
        Logger.d(e.getMessage());
    }

    public static void d(String tag, String msg) {
        Logger.t(tag).d(msg);
    }

    public static void i(String msg) {
        Logger.i(msg);
    }

    public static void i(String tag, String msg) {
        Logger.t(tag).i(msg);
    }

    public static void w(String msg) {
        Logger.w(msg);
    }

    public static void w(String tag, String msg) {
        Logger.t(tag).w(msg);
    }

    public static void w(Throwable e) {
        Logger.e(e, null);
    }

    public static void e(String msg) {
        e(null, msg, null);
    }

    public static void e(Throwable e) {
        e(null, "error", e);
    }

    public static void e(String msg, Throwable tr) {
        e(null, msg, tr);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable tr) {
        BuglyLog.e(LOG_TAG + tag, msg, tr);
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
        CrashReport.postCatchedException(tr);
        BuglyLog.e(LOG_TAG, "Report error", tr);
    }

    public static void report(String msg, Throwable tr) {
        leaveMsg(msg);
        report(tr);
    }

    public static void leaveMsg(String msg) {
        d(msg);
        BuglyLog.i("MSG", msg);
    }

    public static void leaveMsg(Throwable tr) {
        w(tr);
        BuglyLog.e("MSG", "Error", tr);
    }

    public static void test() {
        throw new RuntimeException("Just test");
    }

    public static void toast(String msg) {
        Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void toast(@StringRes int msg) {
        Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT).show();
    }
}
