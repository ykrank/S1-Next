package cl.monsoon.s1next.widget;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.ArrayList;
import java.util.List;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.Config;
import cl.monsoon.s1next.R;

/**
 * Display image from HTML string in the TextView which uses
 * Glide {@link com.bumptech.glide.request.target.ViewTarget}
 * to make an asynchronous to get to load image.
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
        // whether need download images depends settings and Wi-Fi status
        // but download Emoji at any time
        boolean download = true;

        // Append url prefix if the url is not a network url
        // because Emoji url without domain.
        if (!URLUtil.isNetworkUrl(url)) {
            url = Api.URL_S1 + url;
        } else if (!Config.isImagesDownload()) {
            download = false;
        }

        if (download) {
            UrlDrawable urlDrawable = new UrlDrawable();

            mDrawableRequestBuilder
                    .load(url)
                    .into(new ImageGetterViewTarget(mTextView, urlDrawable));

            return urlDrawable;
        }

        return null;
    }

    /**
     * Implement {@link Drawable.Callback} in order to
     * redraw the TextView that contains animated GIF.
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
            Rect rect = new Rect(
                    0, 0, resource.getIntrinsicWidth(), resource.getIntrinsicHeight());
            resource.setBounds(rect);

            mDrawable.setBounds(rect);
            mDrawable.setDrawable(resource);

            if (resource instanceof GifDrawable) {
                // set callback to drawable in oder to
                // signal its parent TextView to redraw
                // to show animated GIF
                mDrawable.setCallback((Drawable.Callback) getView().getTag(R.id.callback));
                resource.setLoopCount(GlideDrawable.LOOP_FOREVER);
                resource.start();
            }

            getView().setText(getView().getText());
            getView().invalidate();
        }

        @Override
        public Request getRequest() {
            return null;
        }

        /**
         * Override to handle request to download
         * multiple images in queue
         */
        @Override
        public void setRequest(Request request) {
            Object tag = view.getTag();
            List<Request> requestList;
            if (tag != null) {
                try {
                    //noinspection unchecked
                    requestList = (List<Request>) tag;
                } catch (ClassCastException e) {
                    throw new IllegalStateException(
                            "Must setTag(List<Request>) on a view Glide is targeting.");
                }
            } else {
                requestList = new ArrayList<>();
            }

            requestList.add(request);
        }
    }
}
