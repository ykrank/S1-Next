package me.ykrank.s1next.widget.span

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Html
import android.text.TextUtils
import android.text.style.ImageSpan
import android.view.View
import android.webkit.URLUtil
import android.widget.TextView
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.ViewEvent
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.LooperUtil
import com.github.ykrank.androidtools.widget.glide.downsamplestrategy.FitOutWidthDownSampleStrategy
import com.github.ykrank.androidtools.widget.glide.downsamplestrategy.GlMaxTextureSizeDownSampleStrategy
import com.github.ykrank.androidtools.widget.glide.downsamplestrategy.MultiDownSampleStrategy
import com.github.ykrank.androidtools.widget.glide.downsamplestrategy.SizeDownSampleStrategy
import com.github.ykrank.androidtools.widget.glide.transformations.FitOutWidthBitmapTransformation
import com.github.ykrank.androidtools.widget.track.DataTrackAgent
import com.uber.autodispose.SingleScoper
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.widget.EmoticonFactory
import me.ykrank.s1next.widget.track.event.EmoticonNotFoundTrackEvent
import java.util.TreeSet
import java.util.WeakHashMap

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
    private val handler: Handler
    private var lastValidateSpanTime = 0L

    /**
     * Manage target to clear target if textview re bind
     */
    internal val animateTargetHashMap = WeakHashMap<Animatable, ImageGetterViewTarget>()
    internal var serial = 0
    private val cachedImageSpanRefreshMsg = TreeSet<ImageSpanChangedMsg>()

    private val density: Float by lazy {
        mTextView.context.resources.displayMetrics.density
    }
    private val emoticonAssetRequestOptions by lazy {
        RequestOptions()
                .error(R.mipmap.unknown_image)
                //Do not cache asset
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                //Original size because gif could not downSample
                .downsample(DownsampleStrategy.NONE)
    }
    private val emoticonRequestOptions by lazy {
        RequestOptions()
                .error(R.mipmap.unknown_image)
                //Only cache data before decode, because we change drawable bounds
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                //Original size because gif could not downSample
                .downsample(DownsampleStrategy.NONE)
    }
    private val glideRequestOptions by lazy {
        RequestOptions()
                .placeholder(R.mipmap.unknown_image)
                .error(R.mipmap.unknown_image)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .downsample(MultiDownSampleStrategy(GlMaxTextureSizeDownSampleStrategy(),
                        FitOutWidthDownSampleStrategy(),
                        SizeDownSampleStrategy(MaxImageSize)))
                .optionalTransform(FitOutWidthBitmapTransformation())
    }


    init {
        LooperUtil.enforceOnMainThread()
        this.requestManager = Glide.with(mTextView)
        this.imageGetterScoper = AndroidRxDispose.withSingle(mTextView, ViewEvent.DESTROY)
        this.trackAgent = App.preAppComponent.dataTrackAgent

        // save Drawable.Callback in TextView
        // and get back when finish fetching image
        // see https://github.com/goofyz/testGlide/pull/1 for more details
        mTextView.setTag(com.github.ykrank.androidtools.R.id.tag_drawable_callback, this)
        // add this listener in order to clean any pending images loading
        // and set drawable callback tag to null when detached from window
        mTextView.addOnAttachStateChangeListener(this)

        handler = Handler(Looper.getMainLooper(), Handler.Callback {
            when (it.what) {
                Msg_Invalidate_Span -> {
                    val time = System.currentTimeMillis()
                    val imageSpanChangedMsg: ImageSpanChangedMsg? = it.obj as ImageSpanChangedMsg?
                    if (imageSpanChangedMsg != null) {
                        cachedImageSpanRefreshMsg.add(imageSpanChangedMsg)
                        it.target.removeMessages(Msg_Invalidate_Span_Late)
                        it.target.sendEmptyMessageDelayed(Msg_Invalidate_Span_Late,
                                lastValidateSpanTime + SpanInvalidateColdTime - time)
                    }

                    return@Callback true
                }
                Msg_Invalidate_Span_Late -> {
                    lastValidateSpanTime = System.currentTimeMillis()

                    if (cachedImageSpanRefreshMsg.isNotEmpty()) {
                        val imageSpanChangedMsg: ImageSpanChangedMsg = cachedImageSpanRefreshMsg.pollFirst()
                        ImageGetterViewTarget.refreshLayout(imageSpanChangedMsg, mTextView)
                    }
                    if (cachedImageSpanRefreshMsg.isNotEmpty()) {
                        it.target.sendEmptyMessageDelayed(Msg_Invalidate_Span_Late,
                                lastValidateSpanTime + SpanInvalidateColdTime - System.currentTimeMillis())
                    }
                    return@Callback true
                }
            }
            false
        })

    }

    @MainThread
    private fun invalidate() {
        LooperUtil.enforceOnMainThread()
        serial += 1
        for (anim in animateTargetHashMap.keys) {
            // Perhaps this gif could not recycle immediate
            anim.stop()
            requestManager.clear(animateTargetHashMap[anim])
        }
        animateTargetHashMap.clear()
    }

    /**
     * We display image depends on settings and Wi-Fi status,
     * but display emoticons at any time.
     */
    @AnyThread
    override fun getDrawable(source: String?): Drawable? {
        var url = source
        if (TextUtils.isEmpty(url)) {
            return null
        }

        val urlDrawable: UrlDrawable

        val emoticonName = Api.parseEmoticonName(url)
        // url has no domain if it comes from server.
        if (emoticonName == null && !URLUtil.isNetworkUrl(url)) {
            url = Api.BASE_URL + url
        }
        // 图片兜底，http链接替换为https
        if(url?.startsWith("http://") == true){
            url = url.replaceFirst("http://", "https://")
        }
        if (emoticonName != null) {
            //Scale
            urlDrawable = UrlDrawable(url, density)
            val imageGetterViewTarget = ImageGetterViewTarget(this, mTextView,
                    urlDrawable, serial)

            val finalUrl = if (URLUtil.isNetworkUrl(url)) url else Api.BASE_URL + url

            val glideRequestBuilder = requestManager
                    .load(Uri.parse(EmoticonFactory.ASSET_PATH_EMOTICON + emoticonName))
                    .apply(emoticonAssetRequestOptions)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
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

    fun sendSpanChangedMsg(imageSpan: ImageSpan, priority: Int) {
        Message.obtain(handler, Msg_Invalidate_Span, ImageSpanChangedMsg(imageSpan, priority)).sendToTarget()
    }

    override fun onViewAttachedToWindow(v: View) {
        for (anim in animateTargetHashMap.keys) {
            anim.start()
        }
    }

    override fun onViewDetachedFromWindow(v: View) {
        handler.removeCallbacksAndMessages(null)
        for (anim in animateTargetHashMap.keys) {
            anim.stop()
        }
    }

    /**
     * Implements [Drawable.Callback] in order to
     * redraw the TextView which contains the animated GIFs.
     */
    override fun invalidateDrawable(who: Drawable) {
        if (who is Animatable) {
            val target = animateTargetHashMap[who] ?: return
            if (target.serial == serial) {
                if (ViewCompat.isAttachedToWindow(mTextView)) {
//                    Log.d("GlideImage", "invalidate ${Integer.toHexString(mTextView.hashCode())}")
                    mTextView.invalidate()
                } else {
                    (who as Animatable).stop()
                }
            } else {
                requestManager.clear(target)
                animateTargetHashMap.remove(who)
            }
        }
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {}

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {}

    companion object {
        /**
         * Image bigger then this will fit width
         */
        private const val TriggerSize = 200
        /**
         * Too big image make app looks like blocked
         */
        private const val MaxImageSize = 6400
        private const val Msg_Invalidate_Span = 2
        private const val Msg_Invalidate_Span_Late = 3
        const val SpanInvalidateColdTime = 100

        @MainThread
        operator fun get(textView: TextView): GlideImageGetter {

            val obj = textView.getTag(com.github.ykrank.androidtools.R.id.tag_drawable_callback)
            if (obj == null) {
                return GlideImageGetter(textView)
            } else {
                val glideImageGetter = obj as GlideImageGetter
                glideImageGetter.invalidate()
                return glideImageGetter
            }
        }
    }

    /**
     * priority越大越优先
     */
    class ImageSpanChangedMsg(val imageSpan: ImageSpan, private val priority: Int) : Comparable<ImageSpanChangedMsg> {

        override fun compareTo(other: ImageSpanChangedMsg): Int {
            if (imageSpan === other.imageSpan){
                return 0
            }
            val compare = other.priority - priority
            if (compare != 0) {
                return compare
            }
            return other.hashCode() - imageSpan.hashCode()
        }

        override fun toString(): String {
            return "ImageSpanChangedMsg(imageSpan=$imageSpan, priority=$priority)"
        }
    }
}
