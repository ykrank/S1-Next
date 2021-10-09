package com.github.ykrank.androidtools.widget.hostcheck

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.ProtocolException
import java.util.concurrent.TimeUnit

/**
 * Self-adaption multi host
 * Created by ykrank on 2017/3/29.
 */

open class MultiHostInterceptor<T : BaseHostUrl>(private val baseHostUrl: T, private val mergeHttpUrl: (originHttpUrl: HttpUrl, baseHostUrl: T) -> HttpUrl) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val originHttpUrl = originRequest.url

        val newHttpUrl = mergeHttpUrl.invoke(originHttpUrl, baseHostUrl)

        var newRequest = originRequest
        if (originHttpUrl !== newHttpUrl) {
            val builder = originRequest.newBuilder()
            builder.url(newHttpUrl)
            builder.header("host", newHttpUrl.host)
            newRequest = builder.build()
        }

        val response: Response = proceedRequest(chain, newRequest) {
            if (newRequest != originRequest) {
                return proceedRequest(chain.withReadTimeout(chain.readTimeoutMillis(), TimeUnit.MILLISECONDS), originRequest) { throw it }
            } else {
                throw it
            }
        }

        return response
    }

    private inline fun proceedRequest(chain: Interceptor.Chain, request: Request, except: (IOException) -> Response): Response {
        return try {
            chain.proceed(request)
        } catch (e: Exception) {
            if (e is IOException) {
                except.invoke(e)
            } else {
                except.invoke(OkHttpException(request.url.host, e))
            }
        }
    }
}

/**
 * Must not extends IOException but ProtocolException because  okhttp3 catch RouteException and IOException to retry.
 * But only some exception
 *
 * @see [okhttp3.internal.http.RetryAndFollowUpInterceptor.recover]
 */
class OkHttpException(val host: String?, override val cause: Throwable?) : ProtocolException(host)