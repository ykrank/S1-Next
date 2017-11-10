package me.ykrank.s1next.widget.hostcheck

import com.github.ykrank.androidtools.widget.hostcheck.BaseHostUrl
import com.github.ykrank.androidtools.widget.hostcheck.BaseHostUrl.Companion.checkBaseHostUrl
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.pref.NetworkPreferencesManager
import okhttp3.HttpUrl

/**
 * Base url and host ip
 */

class AppHostUrl(private val prefManager: NetworkPreferencesManager) : BaseHostUrl {
    override var baseHttpUrl: HttpUrl? = null
    override var hostIp: String? = null

    init {
        refreshBaseHostUrl()
        refreshForceHostIp()
    }

    fun refreshBaseHostUrl() {
        val url: String? = if (prefManager.isForceBaseUrlEnable) {
            prefManager.forceBaseUrl
        } else {
            Api.BASE_URL
        }

        if (url != baseHttpUrl?.toString()) {
            baseHttpUrl = checkBaseHostUrl(url)
        }
    }

    fun refreshForceHostIp() {
        hostIp = if (prefManager.isForceHostIpEnable) {
            prefManager.forceHostIp
        } else {
            null
        }
    }
}
