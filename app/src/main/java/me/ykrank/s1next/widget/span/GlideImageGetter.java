package me.ykrank.s1next.widget.span;

import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.ykrank.androidautodispose.AndroidRxDispose;
import com.github.ykrank.androidlifecycle.AndroidLifeCycle;
import com.github.ykrank.androidlifecycle.event.ViewEvent;
import com.uber.autodispose.SingleScoper;

import java.util.WeakHashMap;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.LooperUtil;
import me.ykrank.s1next.widget.EmoticonFactory;
import me.ykrank.s1next.widget.glide.downsamplestrategy.SizeDownSampleStrategy;
import me.ykrank.s1next.widget.glide.transformations.FitOutWidthBitmapTransformation;
import me.ykrank.s1next.widget.glide.transformations.SizeMultiplierBitmapTransformation;
import me.ykrank.s1next.widget.track.DataTrackAgent;
import me.ykrank.s1next.widget.track.event.EmoticonNotFoundTrackEvent;

/**
 * Implements {@link android.text.Html.ImageGetter}
 * in order to show images in the TextView.
 * <p>
 * Uses {@link com.bumptech.glide.request.target.ViewTarget}
 * to make an asynchronous HTTP GET to load the image.
 * <p>
 * Forked from https://github.com/goofyz/testGlide/pull/1
 * See https://github.com/bumptech/glide/issues/550
 */
public final class GlideImageGetter
        implements Html.ImageGetter, View.OnAttachStateChangeListener, Drawable.Callback {

    private final TextView mTextView;
    private final RequestManager requestManager;
    private final SingleScoper<RequestBuilder<Drawable>> imageGetterScoper;
    private final DataTrackAgent trackAgent;

    /**
     * Manage target to clear target if textview re bind
     */
    private WeakHashMap<Animatable, ImageGetterViewTarget> animatableTargetHashMap = new WeakHashMap<>();
    private int serial = 0;

    private float density;
    private Rect emoticonInitRect, unknownImageInitRect;

    protected GlideImageGetter(TextView textView) {
        LooperUtil.enforceOnMainThread();

        this.mTextView = textView;
        this.requestManager = Glide.with(mTextView);
        this.imageGetterScoper = AndroidRxDispose.withSingle(mTextView, ViewEvent.DESTROY);
        this.trackAgent = App.getAppComponent().getDataTrackAgent();

        // save Drawable.Callback in TextView
        // and get back when finish fetching image
        // see https://github.com/goofyz/testGlide/pull/1 for more details
        mTextView.setTag(R.id.tag_drawable_callback, this);
        // add this listener in order to clean any pending images loading
        // and set drawable callback tag to null when detached from window
        mTextView.addOnAttachStateChangeListener(this);

        initRectHolder();

        AndroidLifeCycle.with(mTextView)
                .listen(ViewEvent.RESUME, () -> {
                    if (ViewCompat.isAttachedToWindow(mTextView)) {
                        for (Animatable anim : animatableTargetHashMap.keySet()) {
                            anim.start();
                        }
                    }
                })
                .listen(ViewEvent.PAUSE, () -> {
                    for (Animatable anim : animatableTargetHashMap.keySet()) {
                        anim.stop();
                    }
                })
                .listen(ViewEvent.DESTROY, this::invalidate);
    }

    @MainThread
    public static GlideImageGetter get(TextView textView) {

        Object object = textView.getTag(R.id.tag_drawable_callback);
        if (object == null) {
            return new GlideImageGetter(textView);
        } else {
            GlideImageGetter glideImageGetter = (GlideImageGetter) object;
            glideImageGetter.invalidate();
            return glideImageGetter;
        }
    }

    @MainThread
    private void invalidate() {
        LooperUtil.enforceOnMainThread();
        serial = serial + 1;
        for (Animatable anim : animatableTargetHashMap.keySet()) {
            // Perhaps this gif could not recycle immediate
            anim.stop();
            requestManager.clear(animatableTargetHashMap.get(anim));
        }
        animatableTargetHashMap.clear();
    }

    private void initRectHolder() {
        density = mTextView.getContext().getResources().getDisplayMetrics().density;
        //init bounds
        emoticonInitRect = new Rect(0, 0, (int) (Api.EMOTICON_INIT_WIDTH * density), (int) (Api.EMOTICON_INIT_HEIGHT * density));

        Drawable unknownDrawable = ContextCompat.getDrawable(mTextView.getContext(), R.mipmap.unknown_image);
        unknownImageInitRect = new Rect(0, 0, unknownDrawable.getIntrinsicWidth(), unknownDrawable.getIntrinsicHeight());
    }

    /**
     * We display image depends on settings and Wi-Fi status,
     * but display emoticons at any time.
     */
    @Override
    @WorkerThread
    public Drawable getDrawable(@Nullable String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        UrlDrawable urlDrawable;

        String emoticonName = Api.parseEmoticonName(url);
        // url has no domain if it comes from server.
        if (emoticonName == null && !URLUtil.isNetworkUrl(url)) {
            url = Api.BASE_URL + url;
        }
        if (emoticonName != null) {
            urlDrawable = new UrlDrawable(url, emoticonInitRect);
            ImageGetterViewTarget imageGetterViewTarget = new ImageGetterViewTarget(this, mTextView,
                    urlDrawable, serial);

            SizeMultiplierBitmapTransformation transformation = new SizeMultiplierBitmapTransformation(density);
            String finalUrl = url;

            RequestBuilder<Drawable> glideRequestBuilder = requestManager
                    .load(Uri.parse(EmoticonFactory.ASSET_PATH_EMOTICON + emoticonName))
                    // TODO: 2017/10/10 Use correct downsample 
                    .apply(RequestOptions.downsampleOf(new SizeDownSampleStrategy(density * Api.EMOTICON_INIT_WIDTH)))
//                    .apply(RequestOptions.bitmapTransform(transformation))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            L.leaveMsg("Exception in emoticon uri:" + model);
                            trackAgent.post(new EmoticonNotFoundTrackEvent(model.toString()));

                            // append domain to this url
                            Single.just(requestManager
                                    .load(Api.BASE_URL + Api.URL_EMOTICON_IMAGE_PREFIX + finalUrl)
//                                    .apply(RequestOptions.bitmapTransform(transformation))
                                            .apply(RequestOptions.downsampleOf(new SizeDownSampleStrategy(density * Api.EMOTICON_INIT_WIDTH)))
                            )
                                    .subscribeOn(AndroidSchedulers.mainThread())
                                    .to(imageGetterScoper)
                                    .subscribe(builder -> builder.into(imageGetterViewTarget), L::report);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    });
            startImageGetterViewTarget(glideRequestBuilder, imageGetterViewTarget);

            return urlDrawable;
        }

        urlDrawable = new UrlDrawable(url, unknownImageInitRect);
        ImageGetterViewTarget imageGetterViewTarget = new ImageGetterViewTarget(this, mTextView,
                urlDrawable, serial);

        RequestBuilder<Drawable> glideRequestBuilder = requestManager
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.unknown_image)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .transform(new FitOutWidthBitmapTransformation()));
        startImageGetterViewTarget(glideRequestBuilder, imageGetterViewTarget);

        return urlDrawable;
    }

    private void startImageGetterViewTarget(RequestBuilder<Drawable> glideRequestBuilder,
                                            ImageGetterViewTarget imageGetterViewTarget) {
        Single.just(glideRequestBuilder)
                .subscribeOn(AndroidSchedulers.mainThread())
                .to(imageGetterScoper)
                .subscribe(builder -> builder.into(imageGetterViewTarget), L::report);
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        for (Animatable anim : animatableTargetHashMap.keySet()) {
            anim.start();
        }
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        for (Animatable anim : animatableTargetHashMap.keySet()) {
            anim.stop();
        }
    }

    /**
     * Implements {@link Drawable.Callback} in order to
     * redraw the TextView which contains the animated GIFs.
     */
    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        if (who instanceof Animatable) {
            ImageGetterViewTarget target = animatableTargetHashMap.get(who);
            if (target == null) {
                return;
            }
            if (target.serial == serial) {
                if (ViewCompat.isAttachedToWindow(mTextView)) {
                    mTextView.invalidate();
                } else {
                    ((Animatable) who).stop();
                }
            } else {
                requestManager.clear(target);
                animatableTargetHashMap.remove(who);
            }
        }
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
    }

    private static final class ImageGetterViewTarget extends ViewTarget<TextView, Drawable> {
        private final GlideImageGetter mGlideImageGetter;
        private final UrlDrawable mDrawable;
        private final int serial;

        private Request mRequest;

        private ImageGetterViewTarget(GlideImageGetter glideImageGetter, TextView view, UrlDrawable drawable, int serial) {
            super(view);

            this.mGlideImageGetter = glideImageGetter;
            this.mDrawable = drawable;
            this.serial = serial;
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            if (placeholder != null) {
                setDrawable(placeholder);
            }
        }

        @Override
        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
            if (!checkTextViewValidate()) {
                return;
            }
            TextView textView = getView();

            setDrawable(resource);
            if (resource instanceof Animatable) {
                Drawable.Callback callback = (Drawable.Callback) textView.getTag(
                        R.id.tag_drawable_callback);
                // note: not sure whether callback would be null sometimes
                // when this Drawable' host view is detached from View
                if (callback != null) {
                    mGlideImageGetter.animatableTargetHashMap.put((Animatable) resource, this);
                    // set callback to drawable in order to
                    // signal its container to be redrawn
                    // to show the animated GIF
                    mDrawable.setCallback(callback);
                    ((Animatable) resource).start();
                }
            }
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            if (checkTextViewValidate()) {
                return;
            }
            if (errorDrawable != null) {
                setDrawable(errorDrawable);
            }
        }

        private boolean checkTextViewValidate() {
            if (serial != mGlideImageGetter.serial) {
                L.l("serial:" + serial + ",GlideImageGetter serial:" + mGlideImageGetter.serial);
                return false;
            }
            return true;
        }

        private void setDrawable(@NonNull Drawable resource) {
            L.l("setDrawable");
            // resize this drawable's width & height to fit its container
            final int resWidth = resource.getIntrinsicWidth();
            final int resHeight = resource.getIntrinsicHeight();
            int width, height;
            TextView textView = getView();
            if (textView.getWidth() >= resWidth) {
                width = resWidth;
                height = resHeight;
            } else {
                width = textView.getWidth();
                height = (int) (resHeight / ((float) resWidth / width));
            }

            Rect rect = new Rect(0, 0, width, height);
            resource.setBounds(rect);
            mDrawable.setBounds(rect);
            mDrawable.setDrawable(resource);

            refreshLayout();
        }

        /**
         * refresh textView layout after drawable invalidate
         */
        private void refreshLayout() {
            L.l("refreshLayout start");
            ImageSpan imageSpan = mDrawable.getImageSpan();
            if (imageSpan == null) {
                //onResourceReady run before imageSpan init. do nothing
                L.l("onResourceReady run before imageSpan init");
                return;
            }
            CharSequence text = getView().getText();
            if (text instanceof SpannableString) {
                SpannableString span = (SpannableString) text;
                int start = span.getSpanStart(imageSpan);
                int end = span.getSpanEnd(imageSpan);
                if (!isSpanValid(start, end)) {
                    //onResourceReady run before imageSpan add to textView. do nothing
                    L.l("onResourceReady run before imageSpan add to textView");
                    return;
                }
                span.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                SpannableStringBuilder span = (SpannableStringBuilder) text;
                int start = span.getSpanStart(imageSpan);
                int end = span.getSpanEnd(imageSpan);
                if (!isSpanValid(start, end)) {
                    //onResourceReady run before imageSpan add to textView. do nothing
                    L.d("onResourceReady run before imageSpan add to textView");
                    return;
                }
                span.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            L.l("refreshLayout end");
        }


        private boolean isSpanValid(int start, int end) {
            return start >= 0 && end >= 0;
        }

        /**
         * See https://github.com/bumptech/glide/issues/550#issuecomment-123693051
         */
        @Override
        public Request getRequest() {
            return mRequest;
        }

        @Override
        public void setRequest(Request request) {
            this.mRequest = request;
        }
    }
}
