package me.ykrank.s1next.binding

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.net.toFile
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.isFile
import com.github.ykrank.androidtools.widget.glide.viewtarget.LargeImageViewTarget
import com.shizhefei.view.largeimage.LargeImageView
import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.widget.image.ImageBiz
import me.ykrank.s1next.widget.image.image

object LargeImageViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("url", "thumbUrl", "manager", "show")
    fun loadImage(
        largeImageView: LargeImageView,
        url: Uri?,
        thumbUrl: Uri?,
        manager: DownloadPreferencesManager,
        show: Boolean
    ) {
        if (!show || url == null) {
            return
        }
        if (url.isFile()) {
            try {
                largeImageView.setImage(FileBitmapDecoderFactory(url.toFile()))
            } catch (e: Exception) {
                L.e(e)
                if (thumbUrl != null) {
                    loadImage(largeImageView, thumbUrl, null, manager, true)
                }
            }
            return
        }

        val imageBiz = ImageBiz(manager)
        val builder = Glide.with(largeImageView)
            .downloadOnly()
            .image(imageBiz, url, forcePass = true)
        builder.into(object : LargeImageViewTarget(largeImageView) {
            override fun onLoadFailed(errorDrawable: Drawable?) {
                if (thumbUrl != null) {
                    loadImage(largeImageView, thumbUrl, null, manager, show)
                } else {
                    super.onLoadFailed(errorDrawable)
                }
            }
        })
    }
}
