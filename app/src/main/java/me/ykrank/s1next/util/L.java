package me.ykrank.s1next.util;

import android.util.Log;

import com.bugsnag.android.Bugsnag;
import com.bugsnag.android.Severity;

import me.ykrank.s1next.BuildConfig;

import static me.ykrank.s1next.App.LOG_TAG;

/**
 * Created by AdminYkrank on 2016/4/20.
 * 对Log的包装
 */
public class L {

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
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg, tr);
        }
    }


    public static void i(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg, tr);
        }
    }


    public static void e(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Bugsnag.notify(tr, Severity.INFO);
        }
        if (Log.isLoggable(tag, Log.ERROR)) {
            Log.e(tag, msg, tr);
        }
    }
    
    public static void report(Throwable tr){
        Bugsnag.notify(tr, Severity.WARNING);
    }

    public static void report(Throwable tr, Severity severity){
        Bugsnag.notify(tr, severity);
    }

    public static void leaveMsg(String msg){
        Bugsnag.leaveBreadcrumb(msg);
    }

    public static void report(String msg, Throwable tr){
        leaveMsg(msg);
        report(tr);
    }
}
