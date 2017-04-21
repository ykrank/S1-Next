package me.ykrank.s1next.widget.span;

import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import android.view.View;
import android.webkit.URLUtil;

import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.view.activity.GalleryActivity;

/**
 * Clickable and resize after drawable invalidate
 */
final class ImageClickableResizeSpan extends ImageSpan implements View.OnClickListener {

    public ImageClickableResizeSpan(Drawable d, String source) {
        super(d, source);

        if (d instanceof UrlDrawable) {
            ((UrlDrawable) d).setImageSpan(this);
        }
    }

    @Override
    public void onClick(View v) {
        String url = getSource();
        //// image from server doesn't have domain
        // skip this because we don't want to
        // make this image (emoticon or something
        // others) clickable
        if (Api.isEmoticonName(url)) {
            return;
        }
        if (!URLUtil.isNetworkUrl(url)) {
            url = Api.BASE_URL + url;
        }
        GalleryActivity.startGalleryActivity(v.getContext(), url);
    }
}
