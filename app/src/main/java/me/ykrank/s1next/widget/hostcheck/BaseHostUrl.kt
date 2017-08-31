package me.ykrank.s1next.widget.hostcheck

import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.pref.NetworkPreferencesManager
import okhttp3.HttpUrl

/**
 * Base url and host ip
 */

class BaseHostUrl(private val prefManager: NetworkPreferencesManager) {
    var baseHttpUrl: HttpUrl? = null
        private set
    var hostIp: String? = null
        private set

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

    companion object {

        /**
         * check whether base url is a well-formed HTTP or HTTPS URL and end with /
         *
         * @param baseUrl url eg:http://bbs.saraba1st.com/2b/
         * @return parsed HttpUrl
         */
        fun checkBaseHostUrl(baseUrl: String?): HttpUrl? {
            return if (baseUrl?.endsWith("/") == true) {
                HttpUrl.parse(baseUrl)
            } else null
        }
    }
}
