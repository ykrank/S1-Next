package me.ykrank.s1next.widget.glide

import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.github.ykrank.androidtools.widget.glide.model.ForcePassUrl
import me.ykrank.s1next.widget.glide.model.AvatarUrl
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 * A simple model loader for fetching media over HTTP/HTTPS using OkHttp.
 *
 *
 * Forked from [OkHttpUrlLoader]
 */
class AppHttpUrlLoader private constructor(
    private val mOkHttpClient: OkHttpClient,
    private val mProgressOkHttpClient: OkHttpClient
) :
    ModelLoader<GlideUrl, InputStream> {
    override fun buildLoadData(
        glideUrl: GlideUrl,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(
            glideUrl,
            buildResourceFetcher(glideUrl, width, height, options)
        )
    }

    override fun handles(glideUrl: GlideUrl): Boolean {
        return true
    }

    private fun buildResourceFetcher(
        model: GlideUrl,
        width: Int,
        height: Int,
        options: Options
    ): DataFetcher<InputStream> {
        if (model is AvatarUrl) {
            return AvatarStreamFetcher(mOkHttpClient, model)
        } else if (model is ForcePassUrl) {
            return OkHttpStreamFetcher(mProgressOkHttpClient, model)
        }
        return AppHttpStreamFetcher(mOkHttpClient, model)
    }

    /**
     * The default factory for [AppHttpUrlLoader].
     */
    class Factory(
        private val mOkHttpClient: OkHttpClient,
        private val mProgressOkHttpClient: OkHttpClient
    ) :
        ModelLoaderFactory<GlideUrl, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> {
            return AppHttpUrlLoader(mOkHttpClient, mProgressOkHttpClient)
        }

        override fun teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }
}
