package me.ykrank.s1next.widget.hostcheck

import android.os.SystemClock

import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import com.github.ykrank.androidtools.widget.EventBus

import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.view.event.NoticeRefreshEvent

class NoticeCheckTask(private val mEventBus: EventBus, private val mS1Service: S1Service, private val mUser: User) {

    @Volatile
    private var lastCheckTime: Long = 0

    @Volatile
    private var checking = false

    fun inspectCheckNoticeTask() {
        if (checking || !mUser.isLogged) {
            return
        }
        if (lastCheckTime == 0L || SystemClock.elapsedRealtime() - lastCheckTime > periodic) {
            startCheckNotice()
        }
    }

    fun forceCheckNotice() {
        if (checking || !mUser.isLogged) {
            return
        }
        startCheckNotice()
    }

    private fun startCheckNotice() {
        checking = true
        try {
            mS1Service.getPmGroups(1)
                    .compose(RxJavaUtil.iOSingleTransformer())
                    .doAfterTerminate { lastCheckTime = SystemClock.elapsedRealtime() }
                    .subscribe({ mEventBus.post(NoticeRefreshEvent::class.java, NoticeRefreshEvent(it.data?.hasNew(), null)) }, L::e)
        } catch (e: Exception) {

        }
    }

    companion object {
        private val periodic = 300000
    }
}
