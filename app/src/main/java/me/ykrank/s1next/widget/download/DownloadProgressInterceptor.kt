package me.ykrank.s1next.widget.download

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * Created by ykrank on 7/12/24
 * 
 */
class DownloadProgressInterceptor(val progressManager: ProgressManager) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val originalResponse = chain.proceed(request)
        val responseBuilder = originalResponse.newBuilder()

        val downloadResponseBody = DownloadProgressResponseBody(
            progressManager,
            DownloadTask( request.url),
            originalResponse.body,
        )

        responseBuilder.body(downloadResponseBody)


        return responseBuilder.build()
    }
}