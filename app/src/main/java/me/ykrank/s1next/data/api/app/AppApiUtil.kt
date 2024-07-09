package me.ykrank.s1next.data.api.app

import com.github.ykrank.androidtools.widget.RxBus
import me.ykrank.s1next.data.api.ApiException
import me.ykrank.s1next.view.event.AppNotLoginEvent

/**
 * Created by ykrank on 2017/7/25.
 */
object AppApiUtil {

    fun appLoginIfNeed(rxBus: RxBus, throwable: Throwable): Boolean {
        if (throwable is ApiException.AppServerException) {
            if (throwable.message?.contains("重新登录")==true) {
                rxBus.post(AppNotLoginEvent())
                return true
            }
        }

        return false
    }
}