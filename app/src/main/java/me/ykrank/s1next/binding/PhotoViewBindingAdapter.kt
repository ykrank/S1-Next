package me.ykrank.s1next.binding

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import com.github.ykrank.androidtools.widget.glide.transformations.GlMaxTextureSizeBitmapTransformation
import me.ykrank.s1next.R
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.widget.image.ImageBiz
import me.ykrank.s1next.widget.image.image

object PhotoViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("url", "thumbUrl", "manager")
    fun loadImage(
        photoView: PhotoView,
        url: Uri?,
        thumbUrl: Uri?,
        manager: DownloadPreferencesManager
    ) {
        if (url == null) {
            return
        }
        val imageBiz = ImageBiz(manager)

        val thumbnailRequest: RequestBuilder<Drawable> = if (thumbUrl != null) {
            Glide.with(photoView)
                .image(imageBiz, thumbUrl)
        } else {
            Glide.with(photoView).load(R.drawable.loading).diskCacheStrategy(DiskCacheStrategy.NONE)
        }

        val requestOptions = RequestOptions()
            .optionalTransform(GlMaxTextureSizeBitmapTransformation())
            .error(R.mipmap.error_symbol)
            .optionalFitCenter()
            .priority(Priority.HIGH)

        val builder = Glide.with(photoView)
            .image(imageBiz, url, forcePass = true)
            .apply(requestOptions)
            .thumbnail(thumbnailRequest)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    //stop thumnal animatable like gif
                    target.onStop()
                    target.onLoadFailed(
                        ContextCompat.getDrawable(
                            photoView.context,
                            R.mipmap.error_symbol
                        )
                    )
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
        builder.into(photoView)
    }
}
