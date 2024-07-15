package me.ykrank.s1next.widget.glide

import android.content.Context
import android.os.Build
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.github.ykrank.androidtools.R
import me.ykrank.s1next.App.Companion.appComponent
import me.ykrank.s1next.App.Companion.preAppComponent
import java.io.InputStream

/**
 * Lazily configures Glide.
 */
@GlideModule
class S1NextGlideModule : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // set max size of the disk cache for images
        builder.setDiskCache(
            InternalCacheDiskCacheFactory(
                context, preAppComponent.downloadPreferencesManager.totalImageCacheSize
            )
        )
        ViewTarget.setTagId(R.id.tag_glide)
        builder.setLogLevel(Log.ERROR)
        var requestOptions = RequestOptions()

        //Change default RGB_565 to ARGB_8888, show image with transparent
        requestOptions = requestOptions.format(DecodeFormat.PREFER_ARGB_8888)

        //shared element transition crash in version O, fix in O MR1
        //https://muyangmin.github.io/glide-docs-cn/doc/hardwarebitmaps.html
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            requestOptions = requestOptions.disallowHardwareConfig()
        }
        builder.setDefaultRequestOptions(requestOptions)

//        int bitmapPoolSizeBytes = 1024 * 1024 * 0; // 0mb
//        int memoryCacheSizeBytes = 1024 * 1024 * 0; // 0mb
//        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
//        builder.setBitmapPool(new LruBitmapPool(bitmapPoolSizeBytes));

        //兼容了华为机型上，Register too many Broadcast Receivers 的问题
        if (NoConnectivityMonitorFactory.needDisableNetCheck()) {
            builder.setConnectivityMonitorFactory(NoConnectivityMonitorFactory())
        }
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry.replace(
            GlideUrl::class.java, InputStream::class.java, AppHttpUrlLoader.Factory(
                appComponent.imageOkHttpClient,
            )
        )
    }
}
