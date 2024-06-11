package me.ykrank.s1next.util

import android.content.Context
import com.github.ykrank.androidtools.util.RxJavaUtil
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import me.ykrank.s1next.BuildConfig
import me.ykrank.s1next.util.AppDeviceUtil.getAppFullVersionName

/**
 * Created by ykrank on 2017/8/9.
 */
object BuglyUtils {
    val isPlay: Boolean
        get() = true

    fun init(context: Context) {
        val appContext = context.applicationContext
        RxJavaUtil.workInRxIoThread {
            val userStrategy = UserStrategy(appContext)
            userStrategy.setAppVersion(getAppFullVersionName())
            CrashReport.initCrashReport(
                appContext,
                ErrorUtil.BUGLY_APP_ID,
                BuildConfig.DEBUG,
                userStrategy
            )
            CrashReport.setIsDevelopmentDevice(appContext, BuildConfig.DEBUG)
        }
    }
}
