package cl.monsoon.s1next.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.webkit.URLUtil;

import org.xml.sax.XMLReader;

import cl.monsoon.s1next.activity.GalleryActivity;

/**
 * Make ImageSpan clickable.
 */
public final class ImageTagHandler implements Html.TagHandler {

    private final Context mContext;

    public ImageTagHandler(Context context) {
        this.mContext = context;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (tag.toLowerCase().equals("img")) {
            // get all ImageSpans
            int length = output.length();
            // ImageSpan's length = 1
            ImageSpan imageSpan = output.getSpans(length - 1, length, ImageSpan.class)[0];

            String url = imageSpan.getSource();
            // Emoji url don't have domain
            // skip Emoji url because we don't want Emoji clickable
            if (URLUtil.isNetworkUrl(url)) {

                // make this ImageSpan clickable
                output.setSpan(
                        new ImageClickableSpan(mContext, imageSpan.getDrawable(), url),
                        length - 1,
                        length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public static class ImageClickableSpan extends ImageSpan implements View.OnClickListener {

        private final Context mContext;

        ImageClickableSpan(Context context, Drawable d, String source) {
            super(d, source);

            this.mContext = context;
        }

        public Context getContext() {
            return mContext;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), GalleryActivity.class);
            intent.putExtra(GalleryActivity.ARG_IMAGE_URL, getSource());

            getContext().startActivity(intent);
        }

    }
}
