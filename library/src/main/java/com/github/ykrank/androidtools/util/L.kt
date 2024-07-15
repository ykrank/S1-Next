package com.github.ykrank.androidtools.util

import android.content.Context
import android.util.Log
import com.github.ykrank.androidtools.BuildConfig
import com.github.ykrank.androidtools.GlobalData
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.tencent.bugly.crashreport.BuglyLog
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.coroutines.CoroutineExceptionHandler
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by AdminYkrank on 2016/4/20.
 * 对Log的包装
 */
object L {
    var init = AtomicBoolean(false)
    var showLog = false
    private var TAG = BuildConfig.LIBRARY_PACKAGE_NAME

    val print = CoroutineExceptionHandler { _, e ->
        print(e)
    }

    fun init(context: Context) {
        TAG = GlobalData.provider.logTag
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true) // (Optional) Whether to show thread info or not. Default true
            .methodCount(0) // (Optional) How many method line to show. Default 2
            .methodOffset(7) // (Optional) Hides internal method calls up to offset. Default 5
            //.logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
            .tag(TAG) // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return showLog()
            }
        })
    }

    fun setUser(id: String?, name: String?) {
        CrashReport.setUserId("id:$id,name:$name")
    }

    fun showLog(): Boolean {
        if (!init.get()) {
            init.set(true)
            showLog = GlobalData.provider.debug || "alpha" == GlobalData.provider.buildType
        }
        return showLog
    }

    fun l(msg: String?) {
        if (showLog()) {
            BuglyLog.d(TAG, msg)
        }
    }

    fun print(msg: String?) {
        if (showLog()) {
            Log.d(TAG, msg!!)
        }
    }

    @JvmStatic
    fun print(e: Throwable?) {
        if (showLog() && e != null) {
            Log.i(TAG, "", e)
        }
    }

    @JvmStatic
    fun d(msg: String?) {
        Logger.d(msg)
    }

    fun d(e: Throwable) {
        Logger.d(e.message)
    }

    fun d(tag: String?, msg: String?) {
        Logger.t(tag).d(msg)
    }

    fun i(msg: String) {
        Logger.i(msg)
    }

    fun i(tag: String?, msg: String) {
        Logger.t(tag).i(msg)
    }

    fun w(msg: String) {
        Logger.w(msg)
    }

    fun w(tag: String?, msg: String) {
        Logger.t(tag).w(msg)
    }

    fun w(e: Throwable?) {
        Logger.e(e, "")
    }

    @JvmStatic
    fun e(msg: String) {
        e(null, msg, null)
    }

    @JvmStatic
    fun e(e: Throwable?) {
        e(null, "error", e)
    }

    @JvmStatic
    fun e(msg: String, tr: Throwable?) {
        e(null, msg, tr)
    }

    @JvmOverloads
    @JvmStatic
    fun e(tag: String?, msg: String, tr: Throwable? = null) {
        BuglyLog.e(TAG + tag, msg, tr)
        if (tr != null) {
            Log.e(TAG + tag, msg, tr)
        }
        if (showLog() && tr != null) {
            CrashReport.postCatchedException(tr)
        }
    }

    @JvmOverloads
    @JvmStatic
    fun report(tr: Throwable?, severity: Int = Log.WARN) {
        if (showLog()) {
            print(tr)
        }
        val errorParser = GlobalData.provider.errorParser
        if (errorParser != null && errorParser.ignoreError(tr!!)) {
            return
        } else {
            CrashReport.postCatchedException(tr)
        }
    }

    @JvmStatic
    fun report(msg: String, tr: Throwable?) {
        leaveMsg(msg)
        report(tr)
    }

    @JvmStatic
    fun leaveMsg(msg: String) {
        leaveMsg("MSG", msg)
    }

    fun leaveMsg(tag: String?, msg: String) {
        i(tag, msg)
        BuglyLog.i(tag, msg)
    }

    fun leaveMsg(tr: Throwable?) {
        BuglyLog.e("MSG", "Error", tr)
    }

    fun test() {
        throw RuntimeException("Just test")
    }

    fun throwNewErrorIfDebug(throwable: RuntimeException?) {
        if (BuildConfig.DEBUG) {
            throw throwable!!
        } else {
            report(throwable, Log.WARN)
        }
    }
}
