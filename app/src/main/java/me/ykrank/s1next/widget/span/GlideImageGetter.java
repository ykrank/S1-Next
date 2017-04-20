package me.ykrank.s1next.widget.span;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.widget.EmoticonFactory;
import me.ykrank.s1next.widget.glide.transformations.FitOutWidthBitmapTransformation;
import me.ykrank.s1next.widget.glide.transformations.GlMaxTextureSizeBitmapTransformation;
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

    private final Context mContext;
    private final TextView mTextView;
    private final DataTrackAgent trackAgent;

    /**
     * Weak {@link java.util.HashSet}.
     */
    private final Set<ViewTarget<TextView, GlideDrawable>> mViewTargetSet = Collections.newSetFromMap(new WeakHashMap<>());

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
    }

    public static GlideImageGetter get(TextView textView) {
        Object object = textView.getTag(R.id.tag_drawable_callback);
        if (object == null) {
            return new GlideImageGetter(textView.getContext(), textView);
        }
        return (GlideImageGetter) object;
    }

    /**
     * We display image depends on settings and Wi-Fi status,
     * but display emoticons at any time.
     */
    @Override
    @WorkerThread
    public Drawable getDrawable(String url) {
        UrlDrawable urlDrawable = new UrlDrawable(url, null);

        String emoticonName = Api.parseEmoticonName(url);
        // url has no domain if it comes from server.
        if (emoticonName == null && !URLUtil.isNetworkUrl(url)) {
            url = Api.BASE_URL + url;
        }
        if (emoticonName != null) {
            ImageGetterViewTarget imageGetterViewTarget = new ImageGetterViewTarget(mTextView,
                    urlDrawable);
            float density = mContext.getResources().getDisplayMetrics().density;
            //init bounds
            urlDrawable.setBounds(new Rect(0, 0, (int) (Api.EMOTICON_INIT_WIDTH * density), (int) (Api.EMOTICON_INIT_HEIGHT * density)));

            SizeMultiplierBitmapTransformation sizeMultiplierBitmapTransformation =
                    new SizeMultiplierBitmapTransformation(mContext, density);
            String finalUrl = url;

            DrawableRequestBuilder<Uri> glideRequestBuilder = Glide.with(mContext)
                    .load(Uri.parse(EmoticonFactory.ASSET_PATH_EMOTICON + emoticonName))
                    .transform(sizeMultiplierBitmapTransformation)
                    .listener(new RequestListener<Uri, GlideDrawable>() {

                        /**
                         * Occurs If we don't have this image (maybe a new emoticon) in assets.
                         */
                        @Override
                        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                            L.leaveMsg("Exception in emoticon uri:" + model);
                            trackAgent.post(new EmoticonNotFoundTrackEvent(model.toString()));
                            // append domain to this url
                            Glide.with(mContext)
                                    .load(Api.BASE_URL + Api.URL_EMOTICON_IMAGE_PREFIX + finalUrl)
                                    .transform(sizeMultiplierBitmapTransformation)
                                    .into(imageGetterViewTarget);

                            return true;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    });
            RxJavaUtil.workInMainThread(glideRequestBuilder, builder -> builder.into(imageGetterViewTarget));

            mViewTargetSet.add(imageGetterViewTarget);
            return urlDrawable;
        }

        ImageGetterViewTarget imageGetterViewTarget = new ImageGetterViewTarget(mTextView,
                urlDrawable);

        Drawable unknownDrawable = ContextCompat.getDrawable(mContext, R.mipmap.unknown_image);
        urlDrawable.setInitDrawable(unknownDrawable);
        urlDrawable.setBounds(new Rect(0, 0, unknownDrawable.getIntrinsicWidth(), unknownDrawable.getIntrinsicHeight()));

        DrawableRequestBuilder<String> glideRequestBuilder = Glide.with(mContext)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .transform(new GlMaxTextureSizeBitmapTransformation(mContext), new FitOutWidthBitmapTransformation(mContext));
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

    private static final class ImageGetterViewTarget extends ViewTarget<TextView, GlideDrawable> {

        private final UrlDrawable mDrawable;

        private Request mRequest;

        private ImageGetterViewTarget(TextView view, UrlDrawable drawable) {
            super(view);

            this.mDrawable = drawable;
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            setDrawable(resource);

            TextView textView = getView();
            if (resource.isAnimated()) {
                Drawable.Callback callback = (Drawable.Callback) textView.getTag(
                        R.id.tag_drawable_callback);
                // note: not sure whether callback would be null sometimes
                // when this Drawable' host view is detached from View
                if (callback != null) {
                    // set callback to drawable in order to
                    // signal its container to be redrawn
                    // to show the animated GIF
                    mDrawable.setCallback(callback);
                    resource.setLoopCount(GlideDrawable.LOOP_FOREVER);
                    resource.start();
                }
            }
        }

        private void setDrawable(@NonNull Drawable resource) {
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

            Rect initRect = mDrawable.getInitRect();
            if (width > initRect.width() || height > initRect.height()) {

                Rect rect = new Rect(0, 0, width, height);
                resource.setBounds(rect);
                mDrawable.setBounds(rect);
                mDrawable.setDrawable(resource);

                // see http://stackoverflow.com/questions/7870312/android-imagegetter-images-overlapping-text#comment-22289166
                textView.setText(textView.getText());
            }
        }

        /**
         * See https://github.com/bumptech/glide/issues/550#issuecomment-123693051
         *
         * @see com.bumptech.glide.GenericRequestBuilder#into(com.bumptech.glide.request.target.Target)
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
