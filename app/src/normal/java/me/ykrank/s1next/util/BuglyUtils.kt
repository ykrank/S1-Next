package me.ykrank.s1next.util

import android.content.Context
import com.github.ykrank.androidtools.util.RxJavaUtil
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ykrank.s1next.BuildConfig
import me.ykrank.s1next.util.AppDeviceUtil.getAppFullVersionName

/**
 * Created by ykrank on 2017/8/9.
 */
object BuglyUtils {
    val isPlay: Boolean
        get() = false

    @OptIn(DelicateCoroutinesApi::class)
    fun init(context: Context) {
        val appContext = context.applicationContext
        GlobalScope.launch(Dispatchers.IO) {
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
