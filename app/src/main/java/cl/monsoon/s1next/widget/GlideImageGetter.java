package cl.monsoon.s1next.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.singleton.Config;

/**
 * Implements {@link android.text.Html.ImageGetter}
 * in order to show images in the TextView.
 * <p>
 * Uses {@link com.bumptech.glide.request.target.ViewTarget}
 * to make an asynchronous HTTP GET to load the image.
 */
public final class GlideImageGetter implements Html.ImageGetter, Drawable.Callback {

    private final Context mContext;

    private final TextView mTextView;

    public GlideImageGetter(Context context, TextView textView) {
        this.mContext = context;
        this.mTextView = textView;
        // save Drawable.Callback in TextView
        // and get back when finish fetching image form Internet
        mTextView.setTag(R.id.callback, this);
    }

    /**
     * We download image depends on settings and
     * Wi-Fi status, but download image from server
     * (emoticons or something others) at any time
     */
    @Override
    public Drawable getDrawable(String url) {
        UrlDrawable urlDrawable = new UrlDrawable();

        // url has no domain if it comes from server.
        if (!URLUtil.isNetworkUrl(url)) {
            // We may have this image in assets if this is emoticon.
            if (url.startsWith(Api.URL_EMOTICON_IMAGE_PREFIX)) {
                String assetSuffix = url.substring(Api.URL_EMOTICON_IMAGE_PREFIX.length());
                Glide.with(mContext)
                        .load(Uri.parse(Config.PREFIX_EMOTICON_ASSET + assetSuffix))
                        .listener(new RequestListener<Uri, GlideDrawable>() {

                            /**
                             * Occurs If we don't have this image (maybe a new emoticon) in assets.
                             */
                            @Override
                            public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                // append domain to this url
                                Glide.with(mContext)
                                        .load(Api.URL_S1 + url)
                                        .into(new ImageGetterViewTarget(mTextView, urlDrawable));

                                return true;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(new ImageGetterViewTarget(mTextView, urlDrawable));
            } else {
                Glide.with(mContext)
                        .load(Api.URL_S1 + url)
                        .into(new ImageGetterViewTarget(mTextView, urlDrawable));
            }

            return urlDrawable;
        }

        if (Config.isImagesDownload()) {
            Glide.with(mContext)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new ImageGetterViewTarget(mTextView, urlDrawable));

            return urlDrawable;
        } else {
            return null;
        }
    }

    /**
     * Implements {@link Drawable.Callback} in order to
     * redraw the TextView which contains the animated GIFs.
     */
    @Override
    public void invalidateDrawable(Drawable who) {
        mTextView.invalidate();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {

    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {

    }

    private static class ImageGetterViewTarget extends ViewTarget<TextView, GlideDrawable> {

        private final UrlDrawable mDrawable;

        private ImageGetterViewTarget(TextView view, UrlDrawable drawable) {
            super(view);

            this.mDrawable = drawable;
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            // resize this drawable's width & height to fit its container
            final int resWidth = resource.getIntrinsicWidth();
            final int resHeight = resource.getIntrinsicHeight();
            int width, height;
            if (getView().getWidth() >= resWidth) {
                width = resWidth;
                height = resHeight;
            } else {
                width = getView().getWidth();
                height = (int) (resHeight / (1.0 * resWidth / width));
            }

            Rect rect = new Rect(0, 0, width, height);
            resource.setBounds(rect);

            mDrawable.setBounds(rect);
            mDrawable.setDrawable(resource);

            if (resource.isAnimated()) {
                // set callback to drawable in order to
                // signal its container to be redrawn
                // to show the animated GIF
                mDrawable.setCallback((Drawable.Callback) getView().getTag(R.id.callback));
                resource.setLoopCount(GlideDrawable.LOOP_FOREVER);
                resource.start();
            }

            getView().setText(getView().getText());
            getView().invalidate();
        }

        /**
         * See https://github.com/bumptech/glide/issues/256
         *
         * @see com.bumptech.glide.GenericRequestBuilder#into(com.bumptech.glide.request.target.Target)
         */
        @Override
        public Request getRequest() {
            return null;
        }

        @Override
        public void setRequest(Request request) {

        }
    }
}
