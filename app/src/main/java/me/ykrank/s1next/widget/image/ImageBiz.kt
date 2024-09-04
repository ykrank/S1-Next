package me.ykrank.s1next.widget.image

import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.ykrank.androidtools.widget.glide.model.ForcePassUrl
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.widget.glide.model.AvatarUrl

/**
 * Created by ykrank on 9/4/24
 */
class ImageBiz(private val downloadPrefManager: DownloadPreferencesManager?) {

    val avatarRequestOptions by lazy {
        RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .circleCrop()
            .let {
                if (downloadPrefManager != null) {
                    it.signature(downloadPrefManager.avatarCacheInvalidationIntervalSignature)
                } else {
                    it
                }
            }
    }

    fun imageModelFromUrl(url: String?, forcePass: Boolean = false): Any {
        if (Api.isAvatarUrl(url)) {
            return AvatarUrl(url, forcePass = forcePass)
        }
        return if (forcePass) {
            ForcePassUrl(url)
        } else {
            url ?: ""
        }
    }

    fun requestOptions(url: String?): RequestOptions {
        if (Api.isAvatarUrl(url)) {
            return avatarRequestOptions
        }

        return RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    }
}

fun RequestManager.image(
    imageBiz: ImageBiz,
    url: String?,
    forcePass: Boolean = false
): RequestBuilder<Drawable> {
    return this.load(imageBiz.imageModelFromUrl(url, forcePass))
        .apply(imageBiz.requestOptions(url))
}

fun <T> RequestBuilder<T>.image(
    imageBiz: ImageBiz,
    url: String?,
    forcePass: Boolean = false
): RequestBuilder<T> {
    return this.load(imageBiz.imageModelFromUrl(url, forcePass))
        .apply(imageBiz.requestOptions(url))
}

fun RequestManager.avatar(imageBiz: ImageBiz, url: String?): RequestBuilder<Drawable> {
    return this.load(AvatarUrl(url)).apply(imageBiz.avatarRequestOptions)
}

fun <T> RequestBuilder<T>.avatar(imageBiz: ImageBiz, url: String?): RequestBuilder<T> {
    return this.load(AvatarUrl(url)).apply(imageBiz.avatarRequestOptions)
}

fun RequestManager.avatarUid(imageBiz: ImageBiz, uid: String?): RequestBuilder<Drawable> {
    return avatar(imageBiz, Api.getAvatarBigUrl(uid))
}