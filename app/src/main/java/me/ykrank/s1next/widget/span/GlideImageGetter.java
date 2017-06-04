package me.ykrank.s1next.widget.span;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.widget.EmoticonFactory;
import me.ykrank.s1next.widget.glide.transformations.FitOutWidthDownSampleStrategy;
import me.ykrank.s1next.widget.glide.transformations.GlMaxTextureSizeDownSampleStrategy;
import me.ykrank.s1next.widget.glide.transformations.MultiDownSampleStrategy;
import me.ykrank.s1next.widget.glide.transformations.SizeMultiplierDownSampleStrategy;
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

    private final Context mContext;
    private final TextView mTextView;
    private final DataTrackAgent trackAgent;

    /**
     * Weak {@link java.util.HashSet}.
     */
    private final Set<ViewTarget<TextView, Drawable>> mViewTargetSet = Collections.newSetFromMap(new WeakHashMap<>());

    private float density;
    private Rect emoticonInitRect, unknownImageInitRect;

    protected GlideImageGetter(Context context, TextView textView) {
        this.mContext = context;
        this.mTextView = textView;
        this.trackAgent = App.getAppComponent().getDataTrackAgent();

        // save Drawable.Callback in TextView
        // and get back when finish fetching image
        // see https://github.com/goofyz/testGlide/pull/1 for more details
        mTextView.setTag(R.id.tag_drawable_callback, this);
        // add this listener in order to clean any pending images loading
        // and set drawable callback tag to null when detached from window
        mTextView.addOnAttachStateChangeListener(this);

        initRectHolder();
    }

    public static GlideImageGetter get(TextView textView) {
        Object object = textView.getTag(R.id.tag_drawable_callback);
        if (object == null) {
            return new GlideImageGetter(textView.getContext(), textView);
        }
        return (GlideImageGetter) object;
    }

    private void initRectHolder() {
        density = mContext.getResources().getDisplayMetrics().density;
        //init bounds
        emoticonInitRect = new Rect(0, 0, (int) (Api.EMOTICON_INIT_WIDTH * density), (int) (Api.EMOTICON_INIT_HEIGHT * density));

        Drawable unknownDrawable = ContextCompat.getDrawable(mContext, R.mipmap.unknown_image);
        unknownImageInitRect = new Rect(0, 0, unknownDrawable.getIntrinsicWidth(), unknownDrawable.getIntrinsicHeight());
    }

    /**
     * We display image depends on settings and Wi-Fi status,
     * but display emoticons at any time.
     */
    @Override
    @WorkerThread
    public Drawable getDrawable(String url) {
        UrlDrawable urlDrawable;

        String emoticonName = Api.parseEmoticonName(url);
        // url has no domain if it comes from server.
        if (emoticonName == null && !URLUtil.isNetworkUrl(url)) {
            url = Api.BASE_URL + url;
        }
        if (emoticonName != null) {
            urlDrawable = new UrlDrawable(url, emoticonInitRect);
            ImageGetterViewTarget imageGetterViewTarget = new ImageGetterViewTarget(mTextView,
                    urlDrawable);

            SizeMultiplierDownSampleStrategy sizeMultiplierDownsampleStrategy = new SizeMultiplierDownSampleStrategy(density);
            String finalUrl = url;

            RequestBuilder<Drawable> glideRequestBuilder = Glide.with(mContext)
                    .load(Uri.parse(EmoticonFactory.ASSET_PATH_EMOTICON + emoticonName))
                    .apply(RequestOptions.downsampleOf(sizeMultiplierDownsampleStrategy))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            L.leaveMsg("Exception in emoticon uri:" + model);
                            trackAgent.post(new EmoticonNotFoundTrackEvent(model.toString()));
                            // append domain to this url
                            Glide.with(mContext)
                                    .load(Api.BASE_URL + Api.URL_EMOTICON_IMAGE_PREFIX + finalUrl)
                                    .apply(RequestOptions.downsampleOf(sizeMultiplierDownsampleStrategy))
                                    .into(imageGetterViewTarget);

                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    });
            RxJavaUtil.workInMainThread(glideRequestBuilder, builder -> builder.into(imageGetterViewTarget));

            mViewTargetSet.add(imageGetterViewTarget);
            return urlDrawable;
        }

        urlDrawable = new UrlDrawable(url, unknownImageInitRect);
        ImageGetterViewTarget imageGetterViewTarget = new ImageGetterViewTarget(mTextView,
                urlDrawable);

        RequestBuilder<Drawable> glideRequestBuilder = Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.unknown_image)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .downsample(new MultiDownSampleStrategy(new GlMaxTextureSizeDownSampleStrategy(), new FitOutWidthDownSampleStrategy())));
        RxJavaUtil.workInMainThread(glideRequestBuilder, builder -> builder.into(imageGetterViewTarget));

        mViewTargetSet.add(imageGetterViewTarget);
        return urlDrawable;
    }

    @Override
    public void onViewAttachedToWindow(View v) {
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        mViewTargetSet.clear();
        v.removeOnAttachStateChangeListener(this);

        v.setTag(R.id.tag_drawable_callback, null);
    }

    /**
     * Implements {@link Drawable.Callback} in order to
     * redraw the TextView which contains the animated GIFs.
     */
    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        mTextView.invalidate();
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
    }

    private static final class ImageGetterViewTarget extends ViewTarget<TextView, Drawable> {

        private final UrlDrawable mDrawable;

        private Request mRequest;

        private ImageGetterViewTarget(TextView view, UrlDrawable drawable) {
            super(view);

            this.mDrawable = drawable;
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            if (placeholder != null) {
                setDrawable(placeholder);
            }
        }

        @Override
        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
            setDrawable(resource);

            TextView textView = getView();
            if (resource instanceof Animatable) {
                Drawable.Callback callback = (Drawable.Callback) textView.getTag(
                        R.id.tag_drawable_callback);
                // note: not sure whether callback would be null sometimes
                // when this Drawable' host view is detached from View
                if (callback != null) {
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
            if (errorDrawable != null) {
                setDrawable(errorDrawable);
            }
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
                span.removeSpan(imageSpan);
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
                span.removeSpan(imageSpan);
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
