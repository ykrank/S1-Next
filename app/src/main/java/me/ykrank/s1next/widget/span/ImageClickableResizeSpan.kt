package me.ykrank.s1next.widget.span

import android.graphics.drawable.Drawable
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.View
import android.webkit.URLUtil

import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.view.activity.GalleryActivity

/**
 * Clickable and resize after drawable invalidate
 */
internal class ImageClickableResizeSpan(d: Drawable, source: String, private val images: ArrayList<String>)
    : ImageSpan(d, source, DynamicDrawableSpan.ALIGN_BOTTOM), View.OnClickListener {

    private val url: String?

    init {
        if (d is UrlDrawable) {
            d.imageSpan = this
        }
        // we don't want to
        // make this image (emoticon or something
        // others) clickable
        if (Api.isEmoticonName(source)) {
            url = null
        } else if (!URLUtil.isNetworkUrl(source)) {
            url = Api.BASE_URL + source
        } else {
            url = source
        }
        url?.let { images.add(url) }
    }

    override fun onClick(v: View) {
        url?.let { GalleryActivity.start(v.context, images, images.indexOf(url)) }
    }

    override fun toString(): String {
        return "ImageClickableResizeSpan(url=$url)"
    }
}
