package me.ykrank.s1next.data.api.app

import me.ykrank.s1next.data.api.ApiException
import me.ykrank.s1next.view.event.AppNotLoginEvent
import me.ykrank.s1next.widget.RxBus

/**
 * Created by ykrank on 2017/7/25.
 */
object AppApiUtil {

    fun appLoginIfNeed(rxBus: RxBus, throwable: Throwable): Boolean {
        if (throwable is ApiException.AppServerException) {
            if (throwable.code == 503) {
                rxBus.post(AppNotLoginEvent())
                return true
            }
        }

        return false
    }
}