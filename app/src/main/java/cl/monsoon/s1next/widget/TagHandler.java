package cl.monsoon.s1next.widget;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.webkit.URLUtil;

import org.xml.sax.XMLReader;

import cl.monsoon.s1next.view.activity.GalleryActivity;

/**
 * Adds {@link android.view.View.OnClickListener}
 * to {@link android.text.style.ImageSpan} and
 * handles {@literal <strike>} tag.
 */
public final class TagHandler implements Html.TagHandler {

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if ("img".equalsIgnoreCase(tag)) {
            handleImg(opening, output);
        } else if ("strike".equalsIgnoreCase(tag)) {
            handleStrike(opening, output);
        }
    }

    /**
     * Replaces {@link android.view.View.OnClickListener}
     * with {@link cl.monsoon.s1next.widget.TagHandler.ImageClickableSpan}.
     * <p>
     * See android.text.HtmlToSpannedConverter#startImg(android.text.SpannableStringBuilder, org.xml.sax.Attributes, android.text.Html.ImageGetter)
     */
    private void handleImg(boolean opening, Editable output) {
        if (!opening) {
            int end = output.length();

            // \uFFFC: OBJECT REPLACEMENT CHARACTER
            int len = "\uFFFC".length();
            ImageSpan imageSpan = output.getSpans(end - len, end, ImageSpan.class)[0];

            String url = imageSpan.getSource();
            // replace \uFFFC with ImageSpan's source
            // in order to support url copy when selected
            output.replace(end - len, end, url);

            // image from server doesn't have domain
            // skip this because we don't want to
            // make this image (emoticon or something
            // others) clickable
            if (URLUtil.isNetworkUrl(url)) {

                output.removeSpan(imageSpan);
                // make this ImageSpan clickable
                output.setSpan(new ImageClickableSpan(imageSpan.getDrawable(), url),
                        end - len, output.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * Adds {@link StrikethroughSpan} to {@literal <strike>} tag.
     * <p>
     * See android.text.HtmlToSpannedConverter#handleStartTag(java.lang.String, org.xml.sax.Attributes)
     * See android.text.HtmlToSpannedConverter#handleEndTag(java.lang.String)
     */
    private void handleStrike(boolean opening, Editable output) {
        int len = output.length();
        if (opening) {
            output.setSpan(new Strike(), len, len, Spannable.SPAN_MARK_MARK);
        } else {
            Strike strike = getLastSpan(output, Strike.class);
            int where = output.getSpanStart(strike);

            output.removeSpan(strike);

            if (where != len) {
                output.setSpan(new StrikethroughSpan(), where, len,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * See android.text.HtmlToSpannedConverter#getLast(android.text.Spanned, java.lang.Class)
     */
    private static <T> T getLastSpan(Spanned text, Class<T> kind) {
        T[] spans = text.getSpans(0, text.length(), kind);

        if (spans.length == 0) {
            return null;
        } else {
            return spans[spans.length - 1];
        }
    }

    static final class ImageClickableSpan extends ImageSpan implements View.OnClickListener {

        private ImageClickableSpan(Drawable d, String source) {
            super(d, source);
        }

        @Override
        public void onClick(View v) {
            GalleryActivity.startGalleryActivity(v.getContext(), getSource());
        }
    }

    private static final class Strike {}
}
