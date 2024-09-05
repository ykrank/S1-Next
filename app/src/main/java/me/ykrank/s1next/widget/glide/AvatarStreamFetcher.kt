package me.ykrank.s1next.widget.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.util.ContentLengthInputStream
import me.ykrank.s1next.App
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.widget.glide.model.AvatarUrl
import me.ykrank.s1next.widget.image.ImageBiz
import okhttp3.*
import okhttp3.internal.cache.CacheStrategy
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

/**
 * Fetches an [java.io.InputStream] from [AvatarUrl] using the OkHttp library.
 *
 *
 * Forked from [OkHttpStreamFetcher]
 */

class AvatarStreamFetcher(
    private val client: Call.Factory,
    private val url: AvatarUrl,
) : DataFetcher<InputStream> {
    private var stream: InputStream? = null
    private var responseBody: ResponseBody? = null
    @Volatile
    private var call: Call? = null

    @Inject
    internal lateinit var mDownloadPreferencesManager: DownloadPreferencesManager
    @Inject
    internal lateinit var avatarFailUrlsCache: AvatarFailUrlsCache

    private val imageBiz by lazy {
        ImageBiz(mDownloadPreferencesManager)
    }

    init {
        App.appComponent.inject(this)
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        if (!mDownloadPreferencesManager.isAvatarsDownload && !url.forcePass) {
            callback.onDataReady(null)
            return
        }
        val urlString = url.toStringUrl()
        //whether cached error url
        val avatarKey = imageBiz.avatarCacheKey(urlString)
        if (!url.forcePass && avatarFailUrlsCache.has(avatarKey)) {
            // already have cached this not success avatar url
            callback.onDataReady(null)
            return
        }

        val requestBuilder = Request.Builder().url(urlString)

        for ((key, value) in url.headers) {
            requestBuilder.addHeader(key, value)
        }

        val request = requestBuilder.build()
        call = client.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (avatarKey != null) {
                    avatarFailUrlsCache.put(avatarKey)
                }
                callback.onLoadFailed(e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                responseBody = response.body
                if (!response.isSuccessful) {
                    // if (this this a avatar URL) && (this URL is cacheable)
                    if (avatarKey != null && CacheStrategy.isCacheable(response, request)) {
                        avatarFailUrlsCache.put(avatarKey)
                        callback.onDataReady(null)
                        return
                    }
                    callback.onLoadFailed(IOException("Request failed with code: " + response.code))
                } else {
                    // if download success, and (this this a avatar URL) && (this URL is cacheable)
                    // remove from cache list
                    if (avatarKey != null && avatarFailUrlsCache.has(avatarKey)) {
                        avatarFailUrlsCache.remove(avatarKey)
                    }
                    responseBody?.let {
                        val contentLength = it.contentLength()
                        stream = ContentLengthInputStream.obtain(it.byteStream(), contentLength)
                    }
                    callback.onDataReady(stream)
                }
            }
        })
    }

    override fun cleanup() {
        try {
            stream?.close()
        } catch (e: IOException) {
            // Ignored
        }

        responseBody?.close()
    }

    override fun cancel() {
        call?.cancel()
    }

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }
}
