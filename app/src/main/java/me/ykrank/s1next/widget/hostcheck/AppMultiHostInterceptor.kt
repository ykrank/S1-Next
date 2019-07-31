package me.ykrank.s1next.widget.hostcheck

import com.github.ykrank.androidtools.widget.hostcheck.MultiHostInterceptor
import me.ykrank.s1next.data.api.Api
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.apache.commons.lang3.ArrayUtils

/**
 * Self-adaption multi host
 * Created by ykrank on 2017/3/29.
 */

class AppMultiHostInterceptor : MultiHostInterceptor<AppHostUrl> {

    constructor(baseHostUrl: AppHostUrl)
            : super(baseHostUrl, AppMultiHostInterceptor.Companion::mergeHttpUrl)

    companion object {
        /**
         * merge complete url with base url
         */
        private fun mergeHttpUrl(originHttpUrl: HttpUrl, appHostUrl: AppHostUrl): HttpUrl {
            val baseUrl = appHostUrl.baseHttpUrl?.toString()
            if (baseUrl.isNullOrEmpty()) {
                return originHttpUrl
            }
            // s1 site
            if (ArrayUtils.contains(Api.HOST_LIST, originHttpUrl.host)) {
                val originUrl = originHttpUrl.toString()
                val originReplacedUrl = Api.parseBaseUrl(originHttpUrl)
                return originUrl.replace(originReplacedUrl, baseUrl).toHttpUrlOrNull() ?: originHttpUrl
            }
            return originHttpUrl
        }
    }
}
