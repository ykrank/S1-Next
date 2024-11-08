package com.github.ykrank.androidtools.binding

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.net.Uri
import android.text.TextUtils
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
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
import com.github.ykrank.androidtools.GlobalData.recycleViewErrorId
import com.github.ykrank.androidtools.GlobalData.recycleViewLoadingId
import com.github.ykrank.androidtools.R
import com.github.ykrank.androidtools.util.L.e
import com.github.ykrank.androidtools.widget.glide.downsamplestrategy.GlMaxTextureSizeDownSampleStrategy

object LibImageViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("imageDrawable")
    fun setImageDrawable(imageView: ImageView, drawable: Drawable?) {
        @SuppressLint("PrivateResource") @ColorInt val rippleColor = ContextCompat.getColor(
            imageView.context, R.color.ripple_material_dark
        )
        // add ripple effect if API >= 21
        val rippleDrawable = RippleDrawable(
            ColorStateList.valueOf(rippleColor),
            drawable, null
        )
        imageView.setImageDrawable(rippleDrawable)
    }

    @JvmStatic
    @BindingAdapter("url", "localUri")
    fun loadImageNetLocal(imageView: ImageView, url: String?, localUri: Uri?) {
        if (localUri == null) {
            loadImage(imageView, url)
            return
        }
        val localRequest = Glide.with(imageView)
            .load(localUri)
            .downsample(GlMaxTextureSizeDownSampleStrategy())
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
        if (url == null) {
            localRequest.into(imageView)
        } else {
            val requestOptions = RequestOptions()
                .diskCacheStrategy(
                    if (URLUtil.isNetworkUrl(url)) {
                        DiskCacheStrategy.DATA
                    } else {
                        DiskCacheStrategy.NONE
                    }
                )
                .downsample(GlMaxTextureSizeDownSampleStrategy())
                .fitCenter()
                .priority(Priority.HIGH)

            Glide.with(imageView)
                .load(url)
                .apply(requestOptions)
                .thumbnail(localRequest)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                })
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("url")
    fun loadImage(imageView: ImageView, url: String?) {
        loadImage(imageView, url, null)
    }

    @BindingAdapter("url", "thumbUrl")
    fun loadImage(imageView: ImageView, url: String?, thumbUrl: String?) {
        loadImage(imageView, url, thumbUrl, recycleViewLoadingId, recycleViewErrorId)
    }

    @BindingAdapter("url", "thumbUrl", "loading", "error")
    fun loadImage(
        imageView: ImageView,
        url: String?,
        thumbUrl: String?,
        @DrawableRes loading: Int,
        @DrawableRes error: Int
    ) {
        if (TextUtils.isEmpty(url)) {
            imageView.setImageResource(error)
            return
        }
        val requestOptions = RequestOptions()
            .diskCacheStrategy(
                if (URLUtil.isNetworkUrl(url)) {
                    DiskCacheStrategy.DATA
                } else {
                    DiskCacheStrategy.NONE
                }
            )
            .downsample(GlMaxTextureSizeDownSampleStrategy())
            .error(error)
            .fitCenter()
            .priority(Priority.HIGH)
        val thumbnailRequest: RequestBuilder<Drawable> = if (!TextUtils.isEmpty(thumbUrl)) {
            val thumbRequestOptions = RequestOptions()
                .diskCacheStrategy(
                    if (URLUtil.isNetworkUrl(thumbUrl)) {
                        DiskCacheStrategy.DATA
                    } else {
                        DiskCacheStrategy.NONE
                    }
                )
            Glide.with(imageView)
                .load(thumbUrl)
                .apply(thumbRequestOptions)
        } else {
            Glide.with(imageView).load(loading).diskCacheStrategy(DiskCacheStrategy.NONE)
        }
        val builder = Glide.with(imageView)
            .load(url)
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
                    e(e)
                    target.onStop()
                    target.onLoadFailed(ContextCompat.getDrawable(imageView.context, error))
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
        builder.into(imageView)
    }
}
