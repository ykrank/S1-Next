package me.ykrank.s1next.widget

import android.app.Activity
import android.content.Context
import com.github.ykrank.androidtools.widget.net.WifiActivityLifecycleCallbacks
import me.ykrank.s1next.App
import me.ykrank.s1next.data.Wifi
import me.ykrank.s1next.widget.hostcheck.NoticeCheckTask
import javax.inject.Inject

class AppActivityLifecycleCallbacks(private val noticeCheckTask: NoticeCheckTask) :
    WifiActivityLifecycleCallbacks() {

    @Inject
    lateinit var wifi: Wifi

    init {
        App.appComponent.inject(this)
    }

    override val wifiStateChangedCallback: ((Boolean) -> Unit)?
        get() = { wifi.isWifiEnabled = it }


    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        noticeCheckTask.inspectCheckNoticeTask()
    }
}
