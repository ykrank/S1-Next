package me.ykrank.s1next.binding

import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.ykrank.androidtools.R
import com.github.ykrank.androidtools.util.ContextUtils
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.glide.model.ImageInfo
import me.ykrank.s1next.App.Companion.preAppComponent
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.model.Emoticon
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.widget.glide.AvatarUrlsCache
import me.ykrank.s1next.widget.glide.model.AvatarUrl
import me.ykrank.s1next.widget.image.ImageBiz
import me.ykrank.s1next.widget.image.avatar
import me.ykrank.s1next.widget.image.avatarUid

object ImageViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("emoticonRequestManager", "emoticon")
    fun loadEmoticon(
        imageView: ImageView,
        requestManager: RequestManager,
        emoticon: Emoticon
    ) {
        val imageType = emoticon.imageType
        if (imageType != null) {
            val uri = Uri.parse(emoticon.imagePath + imageType)
            requestManager.load(uri).into(imageView)
        } else {
            val pngUri = Uri.parse(emoticon.imagePath + Emoticon.TYPE_PNG)
            requestManager.load(pngUri)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        emoticon.imageType = Emoticon.TYPE_GIF
                        val uri = Uri.parse(emoticon.imagePath + Emoticon.TYPE_GIF)
                        imageView.post {
                            requestManager.load(uri).into(imageView)
                        }
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        // 成功加载png，则保存到原对象
                        emoticon.imageType = Emoticon.TYPE_PNG
                        return false
                    }
                })
                .into(imageView)
        }
    }

    /**
     * Show default avatar if user hasn't logged in,
     * otherwise show user's avatar.
     */
    @JvmStatic
    @BindingAdapter("user")
    fun loadUserAvatar(bezelImageView: ImageView, user: User) {
        //in device before 4.4, destroyed activity will cause glide error
        if (ContextUtils.isActivityDestroyedForGlide(bezelImageView.context)) {
            return
        }
        val downloadPreferencesManager = preAppComponent
            .downloadPreferencesManager
        val imageBiz = ImageBiz(downloadPreferencesManager)
        if (user.isLogged) {
            val requestManager = Glide.with(bezelImageView)
            bezelImageView.setTag(R.id.tag_drawable_info, null)
            AvatarUrlsCache.clearUserAvatarCache(user.uid)
            // setup user's avatar
            requestManager.avatarUid(imageBiz, user.uid)
                .error(
                    requestManager
                        .load(me.ykrank.s1next.R.drawable.ic_drawer_avatar_placeholder)
                        .apply(RequestOptions.circleCropTransform())
                )
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        //You can't start or clear loads in RequestListener or Target callbacks.
                        bezelImageView.post {
                            bezelImageView.setTag(R.id.tag_drawable_info, null)
                        }
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        bezelImageView.setTag(
                            R.id.tag_drawable_info,
                            ImageInfo(
                                (model as AvatarUrl).toStringUrl(),
                                resource.intrinsicWidth,
                                resource.intrinsicHeight
                            )
                        )
                        return false
                    }
                })
                .into(bezelImageView)
        } else {
            // setup default avatar
            loadPlaceHolderAvatar(bezelImageView)
        }
    }

    @JvmStatic
    @BindingAdapter("uid")
    fun loadAvatar(bezelImageView: ImageView, oldUid: Int, newUid: Int) {
        loadAvatar(bezelImageView, oldUid.toString(), newUid.toString())
    }

    @JvmStatic
    @BindingAdapter("uid")
    fun loadAvatar(bezelImageView: ImageView, oldUid: String?, newUid: String?) {
        if (TextUtils.equals(oldUid, newUid)) {
            return
        }
        val downloadPreferencesManager = preAppComponent
            .downloadPreferencesManager
        loadAvatar(
            bezelImageView,
            null,
            null,
            null,
            downloadPreferencesManager,
            newUid,
            null
        )
    }

    @JvmStatic
    @BindingAdapter("downloadPreferencesManager", "uid", "thumb")
    fun loadAvatar(
        bezelImageView: ImageView,
        oldManager: DownloadPreferencesManager?,
        oldUid: String?,
        oldThumbUrl: String?,
        newManager: DownloadPreferencesManager,
        newUid: String?,
        newThumbUrl: String?
    ) {
        if (oldManager == newManager && oldUid == newUid &&
            oldThumbUrl == newThumbUrl
        ) {
            return
        }
        //in device before 4.4, destroyed activity will cause glide error
        if (ContextUtils.isActivityDestroyedForGlide(bezelImageView.context)) {
            return
        }
        if (TextUtils.isEmpty(newUid)) {
            loadPlaceHolderAvatar(bezelImageView)
        } else {
            loadRoundAvatar(bezelImageView, newManager, newUid, newThumbUrl)
        }
    }

    private fun loadPlaceHolderAvatar(imageView: ImageView) {
        imageView.setTag(R.id.tag_drawable_info, null)
        Glide.with(imageView)
            .load(me.ykrank.s1next.R.drawable.ic_drawer_avatar_placeholder)
            .apply(RequestOptions.circleCropTransform())
            .into(imageView)
    }

    private fun loadRoundAvatar(
        imageView: ImageView, downloadPreferencesManager: DownloadPreferencesManager,
        uid: String?, thumbUrl: String?
    ) {
        val urls = Api.getAvatarUrls(uid)
        loadRoundAvatar(imageView, downloadPreferencesManager, urls, thumbUrl, false, 0)
    }

    private fun loadRoundAvatar(
        imageView: ImageView, downloadPreferencesManager: DownloadPreferencesManager,
        urls: List<String?>?, thumbUrl: String?, fade: Boolean, loadIndex: Int,
    ) {
        val url = urls?.getOrNull(loadIndex)
        if (url.isNullOrEmpty()) {
            if (loadIndex == 0) {
                loadPlaceHolderAvatar(imageView)
            }
            return
        }
        val imageBiz = ImageBiz(downloadPreferencesManager)
        var listener = Glide.with(imageView)
            .avatar(imageBiz, url)
            .priority(Priority.HIGH)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    imageView.post {
                        loadRoundAvatar(
                            imageView,
                            downloadPreferencesManager,
                            urls,
                            thumbUrl,
                            fade,
                            loadIndex + 1,
                        )
                    }
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    val avatarUrl = model as AvatarUrl
                    L.l("Load avatar:" + avatarUrl.toStringUrl())
                    imageView.setTag(
                        R.id.tag_drawable_info,
                        ImageInfo(
                            avatarUrl.toStringUrl(),
                            resource.intrinsicWidth,
                            resource.intrinsicHeight
                        )
                    )
                    return false
                }
            })
        listener = if (thumbUrl.isNullOrEmpty() || thumbUrl == url) {
            listener.thumbnail(
                Glide.with(imageView).load(me.ykrank.s1next.R.drawable.ic_drawer_avatar_placeholder)
                    .apply(RequestOptions.circleCropTransform())
            )
        } else {
            listener.thumbnail(
                Glide.with(imageView)
                    .avatar(imageBiz, thumbUrl)
            )
        }
        if (fade) {
            listener = listener.transition(DrawableTransitionOptions.withCrossFade(300))
        }
        listener.into(imageView)
    }
}
