package me.ykrank.s1next.widget.span

import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.*
import android.view.View
import android.webkit.URLUtil
import android.widget.TextView
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.ViewEvent
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.LooperUtil
import com.github.ykrank.androidtools.widget.glide.downsamplestrategy.*
import com.github.ykrank.androidtools.widget.glide.transformations.FitOutWidthBitmapTransformation
import com.github.ykrank.androidtools.widget.glide.viewtarget.CustomViewTarget
import com.github.ykrank.androidtools.widget.track.DataTrackAgent
import com.uber.autodispose.SingleScoper
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.widget.EmoticonFactory
import me.ykrank.s1next.widget.track.event.EmoticonNotFoundTrackEvent
import java.util.*
import kotlin.collections.ArrayList

/**
 * Implements [android.text.Html.ImageGetter]
 * in order to show images in the TextView.
 *
 *
 * Uses [com.bumptech.glide.request.target.ViewTarget]
 * to make an asynchronous HTTP GET to load the image.
 *
 *
 * Forked from https://github.com/goofyz/testGlide/pull/1
 * See https://github.com/bumptech/glide/issues/550
 */
class GlideImageGetter protected constructor(private val mTextView: TextView) : Html.ImageGetter, View.OnAttachStateChangeListener, Drawable.Callback {
    private val requestManager: RequestManager
    private val imageGetterScoper: SingleScoper<RequestBuilder<Drawable>>
    private val trackAgent: DataTrackAgent

    /**
     * Manage target to clear target if textview re bind
     */
    private val animatableTargetHashMap = WeakHashMap<Animatable, ImageGetterViewTarget>()
    private var serial = 0

    private val density: Float by lazy {
        mTextView.context.resources.displayMetrics.density
    }
    private val emoticonRequestOptions by lazy {
        RequestOptions()
                .error(R.mipmap.unknown_image)
                //Only cache data before decode, because we change drawable bounds
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                //Original size because gif could not downSample
                .downsample(SizeMultiplierDownSampleStrategy(1.0f))
    }
    private val glideRequestOptions by lazy {
        RequestOptions()
                .placeholder(R.mipmap.unknown_image)
                .error(R.mipmap.unknown_image)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .downsample(MultiDownSampleStrategy(GlMaxTextureSizeDownSampleStrategy(),
                        FitOutWidthDownSampleStrategy(),
                        SizeDownSampleStrategy(MaxImageSize)))
                .transform(FitOutWidthBitmapTransformation())
    }


    init {
        LooperUtil.enforceOnMainThread()
        this.requestManager = Glide.with(mTextView)
        this.imageGetterScoper = AndroidRxDispose.withSingle(mTextView, ViewEvent.DESTROY)
        this.trackAgent = App.preAppComponent.dataTrackAgent

        // save Drawable.Callback in TextView
        // and get back when finish fetching image
        // see https://github.com/goofyz/testGlide/pull/1 for more details
        mTextView.setTag(R.id.tag_drawable_callback, this)
        // add this listener in order to clean any pending images loading
        // and set drawable callback tag to null when detached from window
        mTextView.addOnAttachStateChangeListener(this)
    }

    @MainThread
    private fun invalidate() {
        LooperUtil.enforceOnMainThread()
        serial += 1
        for (anim in animatableTargetHashMap.keys) {
            // Perhaps this gif could not recycle immediate
            anim.stop()
            requestManager.clear(animatableTargetHashMap[anim])
        }
        animatableTargetHashMap.clear()
    }

    /**
     * We display image depends on settings and Wi-Fi status,
     * but display emoticons at any time.
     */
    @AnyThread
    override fun getDrawable(url: String?): Drawable? {
        var url = url
        if (TextUtils.isEmpty(url)) {
            return null
        }

        val urlDrawable: UrlDrawable

        val emoticonName = Api.parseEmoticonName(url)
        // url has no domain if it comes from server.
        if (emoticonName == null && !URLUtil.isNetworkUrl(url)) {
            url = Api.BASE_URL + url
        }
        if (emoticonName != null) {
            //Scale
            urlDrawable = UrlDrawable(url, density)
            val imageGetterViewTarget = ImageGetterViewTarget(this, mTextView,
                    urlDrawable, serial)

            val finalUrl = if (URLUtil.isNetworkUrl(url)) url else Api.BASE_URL + url

            val glideRequestBuilder = requestManager
                    .load(Uri.parse(EmoticonFactory.ASSET_PATH_EMOTICON + emoticonName))
                    .apply(emoticonRequestOptions)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            L.leaveMsg("Exception in emoticon uri:$model")
                            trackAgent.post(EmoticonNotFoundTrackEvent(model.toString()))

                            // append domain to this url
                            val emoticonNetRequestBuilder = requestManager
                                    .load(finalUrl)
                                    .apply(emoticonRequestOptions)
                            startImageGetterViewTarget(emoticonNetRequestBuilder, imageGetterViewTarget, true)
                            return true
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
            startImageGetterViewTarget(glideRequestBuilder, imageGetterViewTarget, true)

            return urlDrawable
        }

        urlDrawable = UrlDrawable(url)

        val imageGetterViewTarget = ImageGetterViewTarget(this, mTextView,
                urlDrawable, serial)

        val glideRequestBuilder = requestManager
                .load(url)
                .apply(glideRequestOptions)
        startImageGetterViewTarget(glideRequestBuilder, imageGetterViewTarget, false)

        return urlDrawable
    }

    private fun startImageGetterViewTarget(glideRequestBuilder: RequestBuilder<Drawable>,
                                           imageGetterViewTarget: ImageGetterViewTarget, emoticon: Boolean) {
        Single.just(glideRequestBuilder)
                .subscribeOn(AndroidSchedulers.mainThread())
                .to(imageGetterScoper)
                .subscribe({ builder ->
                    if (emoticon) {
                        imageGetterViewTarget.mDrawable.let {
                            it.setKeepScaleRatio(true)
//                            it.setWidthTargetSize(emoticonSize)
//                            it.setHeightTargetSize(emoticonSize)
                        }
                    } else {
                        //Big image scale to fit width
                        imageGetterViewTarget.mDrawable.setTriggerSize(TriggerSize)
                        if (mTextView.width > 0) {
                            imageGetterViewTarget.mDrawable.setWidthTargetSize(mTextView.width)
                        }
                    }
                    builder.into(imageGetterViewTarget)
                }, { L.report(it) })
    }

    override fun onViewAttachedToWindow(v: View) {
        for (anim in animatableTargetHashMap.keys) {
            anim.start()
        }
    }

    override fun onViewDetachedFromWindow(v: View) {
        for (anim in animatableTargetHashMap.keys) {
            anim.stop()
        }
    }

    /**
     * Implements [Drawable.Callback] in order to
     * redraw the TextView which contains the animated GIFs.
     */
    override fun invalidateDrawable(who: Drawable) {
        if (who is Animatable) {
            val target = animatableTargetHashMap[who] ?: return
            if (target.serial == serial) {
                if (ViewCompat.isAttachedToWindow(mTextView)) {
                    mTextView.invalidate()
                } else {
                    (who as Animatable).stop()
                }
            } else {
                requestManager.clear(target)
                animatableTargetHashMap.remove(who)
            }
        }
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {}

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {}

    private class ImageGetterViewTarget constructor(private val mGlideImageGetter: GlideImageGetter, view: TextView, val mDrawable: UrlDrawable, val serial: Int)
        : CustomViewTarget<TextView, Drawable>(view) {

        private var mRequest: Request? = null

        override fun onResourceLoading(placeholder: Drawable?) {
            super.onResourceLoading(placeholder)
            L.l("onResourceLoading:" + mDrawable.url)
            if (placeholder != null) {
                setDrawable(placeholder)
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
            if (images.indexOf(mDrawable.url) >=  MaxImageShow) {
                return
            }

            setDrawable(resource)
            if (resource is Animatable) {
                val callback = textView.getTag(
                        R.id.tag_drawable_callback) as Drawable.Callback?
                // note: not sure whether callback would be null sometimes
                // when this Drawable' host view is detached from View
                if (callback != null) {
                    mGlideImageGetter.animatableTargetHashMap[resource] = this
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
                setDrawable(errorDrawable)
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
            mGlideImageGetter.invalidate()
        }

        private fun checkTextViewValidate(): Boolean {
            if (serial != mGlideImageGetter.serial) {
                L.l("serial:" + serial + ",GlideImageGetter serial:" + mGlideImageGetter.serial)
                return false
            }
            return true
        }

        private fun setDrawable(resource: Drawable) {
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

            refreshLayout()
        }

        /**
         * refresh textView layout after drawable invalidate
         */
        private fun refreshLayout() {
            L.l("refreshLayout start")
            val imageSpan = mDrawable.imageSpan
            if (imageSpan == null) {
                //onResourceReady run before imageSpan init. do nothing
                L.l("refreshLayout run before imageSpan init")
                return
            }
            val textView = getView()
            val text = textView.text
            if (text is SpannableString) {
                val start = text.getSpanStart(imageSpan)
                val end = text.getSpanEnd(imageSpan)
                if (!isSpanValid(start, end)) {
                    //onResourceReady run before imageSpan add to textView. do nothing
                    L.l("refreshLayout run before imageSpan add to textView")
                    return
                }
                //Or image overlapping error
                text.removeSpan(imageSpan)
                text.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                val span = text as SpannableStringBuilder
                val start = span.getSpanStart(imageSpan)
                val end = span.getSpanEnd(imageSpan)
                if (!isSpanValid(start, end)) {
                    //onResourceReady run before imageSpan add to textView. do nothing
                    L.l("refreshLayout run before imageSpan add to textView")
                    return
                }
                //Or image overlapping error
                span.removeSpan(imageSpan)
                span.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            L.l("refreshLayout end")
        }


        private fun isSpanValid(start: Int, end: Int): Boolean {
            return start >= 0 && end >= 0
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
    }

    companion object {
        /**
         * Image bigger then this will fit width
         */
        private const val TriggerSize = 200
        /**
         * Too big image make app looks like blocked
         */
        private const val MaxImageSize = 6400
        /**
         * Too many images make list slow, so hide it default
         */
        private const val MaxImageShow = 5

        @MainThread
        operator fun get(textView: TextView): GlideImageGetter {

            val obj = textView.getTag(R.id.tag_drawable_callback)
            if (obj == null) {
                return GlideImageGetter(textView)
            } else {
                val glideImageGetter = obj as GlideImageGetter
                glideImageGetter.invalidate()
                return glideImageGetter
            }
        }
    }
}
