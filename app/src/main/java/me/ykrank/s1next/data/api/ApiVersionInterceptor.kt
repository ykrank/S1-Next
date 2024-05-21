package me.ykrank.s1next.data.api

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.lang3.ArrayUtils
import java.io.IOException

/**
 * Add version to api
 * Created by ykrank on 2017/3/28.
 */
class ApiVersionInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        var request: Request = chain.request()
        var url = request.url
        if (ArrayUtils.contains(
                Api.HOST_LIST,
                url.host
            ) && TextUtils.isEmpty(url.queryParameter("version"))
        ) {
            url = url.newBuilder().addQueryParameter("version", Api.API_VERSION_DEFAULT).build()
            request = request.newBuilder().url(url).build()
        }
        return chain.proceed(request)
    }
}
