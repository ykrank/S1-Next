package me.ykrank.s1next.widget.span

import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.text.*
import android.text.style.ImageSpan
import android.widget.TextView
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.transition.Transition
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.glide.viewtarget.CustomViewTarget
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import java.util.*

internal class ImageGetterViewTarget constructor(
    private val mGlideImageGetter: GlideImageGetter,
    view: TextView,
    val mDrawable: UrlDrawable,
    val serial: Int
) : CustomViewTarget<TextView, Drawable>(view) {

    private val mDownloadPreferencesManager: DownloadPreferencesManager = App.preAppComponent.downloadPreferencesManager

    private var mRequest: Request? = null

    override fun onResourceLoading(placeholder: Drawable?) {
        super.onResourceLoading(placeholder)
        L.l("onResourceLoading:" + mDrawable.url)
        if (placeholder != null) {
            setDrawable(placeholder, 1)
        }
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        L.l("onResourceReady:" + mDrawable.url)
        if (!checkTextViewValidate()) {
            return
        }
        val textView = getView()

        val images = (textView.getTag(R.id.tag_text_view_span_images)
            ?: arrayListOf<String>()) as ArrayList<String>
        if (images.indexOf(mDrawable.url) >= mDownloadPreferencesManager.postMaxImageShow) {
            return
        }

        setDrawable(resource, 10)
        if (resource is Animatable) {
            val callback = textView.getTag(
                com.github.ykrank.androidtools.R.id.tag_drawable_callback
            ) as Drawable.Callback?
            // note: not sure whether callback would be null sometimes
            // when this Drawable' host view is detached from View
            if (callback != null) {
                mGlideImageGetter.animateTargetHashMap[resource] = this
                // set callback to drawable in order to
                // signal its container to be redrawn
                // to show the animated GIF
                mDrawable.callback = callback
                (resource as Animatable).start()
            }
        }
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        L.l("onLoadFailed:" + mDrawable.url)
        if (checkTextViewValidate()) {
            return
        }
        if (errorDrawable != null) {
            setDrawable(errorDrawable, 0)
        }
    }

    override fun onResourceCleared(placeholder: Drawable?) {
        mDrawable.drawable?.let {
            if (it is Animatable) {
                it.stop()
            }
        }
        mDrawable.drawable = null
    }

    override fun onStart() {
        mDrawable.drawable?.let {
            if (it is Animatable) {
                it.start()
            }
        }
    }

    override fun onStop() {
        mDrawable.drawable?.let {
            if (it is Animatable) {
                it.stop()
            }
        }
    }

    override fun onDestroy() {

    }

    private fun checkTextViewValidate(): Boolean {
        if (serial != mGlideImageGetter.serial) {
            L.l("serial:" + serial + ",GlideImageGetter serial:" + mGlideImageGetter.serial)
            return false
        }
        return true
    }

    private fun setDrawable(resource: Drawable, priority: Int) {
        L.l("setDrawable")
        // resize this drawable's width & height to fit its container
        val resWidth = resource.intrinsicWidth
        val resHeight = resource.intrinsicHeight
        val width: Int
        val height: Int
        val textView = getView()
        if (textView.width >= resWidth) {
            width = resWidth
            height = resHeight
        } else {
            width = textView.width
            height = (resHeight / (resWidth.toFloat() / width)).toInt()
        }

        val rect = Rect(0, 0, width, height)
        mDrawable.drawable = resource
        mDrawable.bounds = rect

        mDrawable.imageSpan?.apply {
            mGlideImageGetter.sendSpanChangedMsg(this, priority)
        }
    }


    /**
     * See https://github.com/bumptech/glide/issues/550#issuecomment-123693051
     */
    override fun getRequest(): Request? {
        return mRequest
    }

    override fun setRequest(request: Request?) {
        this.mRequest = request
    }

    companion object {

        private fun isSpanValid(start: Int, end: Int): Boolean {
            return start >= 0 && end >= 0
        }

        /**
         * refresh textView layout after drawable invalidate
         */
        fun refreshLayout(imageSpanChangedMsg: GlideImageGetter.ImageSpanChangedMsg?, textView: TextView) {
            val imageSpan = imageSpanChangedMsg?.imageSpan
            L.l("refreshLayout start $imageSpanChangedMsg")
            if (imageSpan == null) {
                //onResourceReady run before imageSpan init. do nothing
                L.l("refreshLayout run before imageSpan init $imageSpanChangedMsg")
                return
            }
            val text = textView.text
            if (text is Spannable) {
                val start = text.getSpanStart(imageSpan)
                val end = text.getSpanEnd(imageSpan)
                if (!isSpanValid(start, end)) {
                    //onResourceReady run before imageSpan add to textView. do nothing
                    L.l("refreshLayout run before imageSpan add to textView $imageSpanChangedMsg")
                    return
                }
                //sendSpanChanged
//                val spanWatchers: Array<SpanWatcher> = text.getSpans<SpanWatcher>(
//                    start,
//                    end, SpanWatcher::class.java
//                )
//                val n = spanWatchers.size
//                for (i in 0 until n) {
//                    spanWatchers[i].onSpanChanged(text, imageSpan, start, end, start, end)
//                }
//                Or image overlapping error
//                text.removeSpan(imageSpan)
//                text.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                textView.text = text
            }

            L.l("refreshLayout end $imageSpanChangedMsg")
        }
    }
}