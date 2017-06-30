package me.ykrank.s1next.widget.hostcheck

import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.util.L
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.lang3.ArrayUtils
import java.io.IOException

/**
 * Self-adaption multi host
 * Created by ykrank on 2017/3/29.
 */

class MultiHostInterceptor(private val baseHostUrl: BaseHostUrl) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val originHttpUrl = originRequest.url()

        val newHttpUrl = mergeHttpUrl(originHttpUrl, baseHostUrl.baseUrl)

        var newRequest = originRequest
        if (originHttpUrl !== newHttpUrl) {
            val builder = originRequest.newBuilder()
            builder.url(newHttpUrl)
            builder.header("host", newHttpUrl.host())
            newRequest = builder.build()
        }

        val response: Response = proceedRequest(chain, newRequest, {
            if (newRequest != originRequest) {
                return proceedRequest(chain, originRequest, { throw OkHttpException(it) })
            } else {
                throw OkHttpException(it)
            }
        })

        return response
    }

    @Throws(IOException::class)
    private inline fun proceedRequest(chain: Interceptor.Chain, request: Request, except: (Exception) -> Response): Response {
        try {
            return chain.proceed(request)
        } catch (e: Exception) {
            if (e is IOException) {
                //Normal exception
                throw e
            } else {
                //Route error or other
                L.leaveMsg("request:" + request)
                L.report(e)
                return except.invoke(e)
            }
        }
    }

    companion object {
        /**
         * merge complete url with base url
         */
        private fun mergeHttpUrl(originHttpUrl: HttpUrl, baseUrl: String?): HttpUrl {
            if (baseUrl.isNullOrEmpty()) {
                return originHttpUrl
            }
            // s1 site
            if (ArrayUtils.contains(Api.HOST_LIST, originHttpUrl.host())) {
                val originUrl = originHttpUrl.toString()
                val originReplacedUrl = Api.parseBaseUrl(originHttpUrl)
                return HttpUrl.parse(originUrl.replace(originReplacedUrl, baseUrl!!)) ?: originHttpUrl
            }
            return originHttpUrl
        }
    }
}

class OkHttpException : IOException {
    constructor() : super()

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable) : super(cause)
}