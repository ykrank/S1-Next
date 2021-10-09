package com.github.ykrank.androidtools.widget.hostcheck

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

/**
 * Base url and host ip
 */

interface BaseHostUrl {
    var baseHttpUrl: HttpUrl?
    var hostIp: String?

    companion object {

        /**
         * check whether base url is a well-formed HTTP or HTTPS URL and end with /
         *
         * @param baseUrl url eg:http://bbs.saraba1st.com/2b/
         * @return parsed HttpUrl
         */
        fun checkBaseHostUrl(baseUrl: String?): HttpUrl? {
            return if (baseUrl?.endsWith("/") == true)
                baseUrl.toHttpUrlOrNull()
            else null
        }
    }
}
