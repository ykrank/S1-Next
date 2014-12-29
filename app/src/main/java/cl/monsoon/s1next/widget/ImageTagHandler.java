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
        if (!opening && tag.equalsIgnoreCase("img")) {
            handleStartImg(output);
        }
    }

    /**
     * see android.text.HtmlToSpannedConverter#startImg(android.text.SpannableStringBuilder, org.xml.sax.Attributes, android.text.Html.ImageGetter)
     */
    private void handleStartImg(Editable output) {
        int end = output.length();

        int len = "\uFFFC".length();
        ImageSpan imageSpan = output.getSpans(end - len, end, ImageSpan.class)[0];

        String url = imageSpan.getSource();
        // replace \uFFFC (OBJECT REPLACEMENT CHARACTER) to its ImageSpan's source
        // in order to support url copy (when selected)
        output.replace(end - len, end, url);

        // Emoji url don't have domain
        // skip Emoji url because we don't want Emoji clickable
        if (URLUtil.isNetworkUrl(url)) {

            output.removeSpan(imageSpan);
            // make this ImageSpan clickable
            output.setSpan(
                    new ImageClickableSpan(mContext, imageSpan.getDrawable(), url),
                    end - len,
                    output.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
