package me.ykrank.s1next.widget.span;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.webkit.URLUtil;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.HashMap;

import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.activity.GalleryActivity;

/**
 * Adds {@link android.view.View.OnClickListener}
 * to {@link android.text.style.ImageSpan} and
 * handles {@literal <strike>} tag.
 */
public final class TagHandler implements Html.TagHandler {
    private static final String TAG = TagHandler.class.getCanonicalName();

    private final HashMap<String, String> attributes = new HashMap<>(8);

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if ("img".equalsIgnoreCase(tag)) {
            handleImg(opening, output);
        } else if ("strike".equalsIgnoreCase(tag)) {
            handleStrike(opening, output);
        } else if ("acfun".equalsIgnoreCase(tag)) {
            handleAcfun(opening, output, xmlReader);
        } else if ("bilibili".equalsIgnoreCase(tag)) {
            handleBilibili(opening, output, xmlReader);
        }
    }

    /**
     * Replaces {@link android.view.View.OnClickListener}
     * with {@link TagHandler.ImageClickableSpan}.
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
            // in order to support url copyFrom when selected
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

    private void handleAcfun(boolean opening, Editable output, XMLReader xmlReader){
        if (opening) {
            processAttributes(xmlReader);
            AcfunSpan.startAcfun((SpannableStringBuilder) output, attributes);
        } else
            AcfunSpan.endAcfun((SpannableStringBuilder) output);
    }

    private void handleBilibili(boolean opening, Editable output, XMLReader xmlReader){
        if (opening) {
            BilibiliSpan.startBilibiliSpan((SpannableStringBuilder) output);
        } else
            BilibiliSpan.endBilibiliSpan((SpannableStringBuilder) output);
    }

    /**
     * See android.text.HtmlToSpannedConverter#getLast(android.text.Spanned, java.lang.Class)
     */
    public static <T> T getLastSpan(Spanned text, Class<T> kind) {
        T[] spans = text.getSpans(0, text.length(), kind);

        if (spans.length == 0) {
            return null;
        } else {
            return spans[spans.length - 1];
        }
    }

    /**
     * See http://stackoverflow.com/questions/6952243/how-to-get-an-attribute-from-an-xmlreader
     *
     * @param xmlReader
     */
    private void processAttributes(final XMLReader xmlReader) {
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[]) dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = (Integer) lengthField.get(atts);

            /**
             * MSH: Look for supported attributes and add to hash map.
             * This is as tight as things can get :)
             * The data index is "just" where the keys and values are stored. 
             */
            for (int i = 0; i < len; i++)
                attributes.put(data[i * 5 + 1], data[i * 5 + 4]);
        } catch (Exception e) {
            L.d(TAG, e);
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

    private static final class Strike {
    }
}
