package cl.monsoon.s1next.widget;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
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

    private final TextView mTextView;
    private final DrawableRequestBuilder<String> mDrawableRequestBuilder;

    public GlideImageGetter(TextView textView, DrawableRequestBuilder<String> builder) {
        this.mTextView = textView;
        // save Drawable.Callback in TextView
        // and get back when finish fetching image form Internet
        mTextView.setTag(R.id.callback, this);

        mDrawableRequestBuilder = builder;
    }

    @Override
    public Drawable getDrawable(String url) {
        // whether need to download the image
        // depends on settings and Wi-Fi status
        // but download emoticon at any time
        boolean download = true;

        // Appends url prefix if this url is not a network url
        // because emoticon urls haven't domain.
        if (!URLUtil.isNetworkUrl(url)) {
            url = Api.URL_S1 + url;
        } else if (!Config.isImagesDownload()) {
            download = false;
        }

        if (download) {
            UrlDrawable urlDrawable = new UrlDrawable();

            mDrawableRequestBuilder
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new ImageGetterViewTarget(mTextView, urlDrawable));

            return urlDrawable;
        }

        return null;
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
