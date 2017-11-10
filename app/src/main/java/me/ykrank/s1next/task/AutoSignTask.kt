package me.ykrank.s1next.task

import android.os.SystemClock
import com.github.ykrank.androidtools.extension.toast
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import io.reactivex.Single
import me.ykrank.s1next.App
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.model.AutoSignResult

/**
 * Created by ykrank on 2017/6/4.
 */
class AutoSignTask(val s1Service: S1Service, val user: User) {
    val Check_Interval = 30L
    var lastCheck = 0L

    fun silentCheck() {
        if (SystemClock.elapsedRealtime() - lastCheck < Check_Interval) {
            return
        }
        autoSign().compose(RxJavaUtil.iOSingleTransformer())
                .doOnSuccess { lastCheck = SystemClock.elapsedRealtime() }
                .subscribe({
                    user.isSigned = it.signed
                    if (it.success) {
                        App.get()?.toast(it.msg)
                    }
                }, L::report)
    }

    fun autoSign(): Single<AutoSignResult> {
        return s1Service.autoSign(user.authenticityToken)
                .map { AutoSignResult.fromHtml(it) }
    }
}