package cl.monsoon.s1next.widget;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.HashMap;

import cl.monsoon.s1next.view.activity.GalleryActivity;

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
            if (opening) {
                processAttributes(xmlReader);
                startAcfun((SpannableStringBuilder) output, attributes);
            } else
                endAcfun((SpannableStringBuilder) output);
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
            Log.d(TAG, "Exception: " + e);
        }
    }

    /**
     * See android.text.HtmlToSpannedConverter#startA(android.text.SpannableStringBuilder, org.xml.sax.Attributes)
     */
    private static void startAcfun(SpannableStringBuilder text, HashMap<String, String> attributes) {
        String href = attributes.get("href");

        int len = text.length();
        text.setSpan(new AcfunHref(href), len, len, Spannable.SPAN_MARK_MARK);
    }

    /**
     * See android.text.HtmlToSpannedConverter#endA(android.text.SpannableStringBuilder)
     */
    private static void endAcfun(@NonNull SpannableStringBuilder text) {
        int len = text.length();
        Object obj = getLastSpan(text, AcfunHref.class);
        int where = text.getSpanStart(obj);

        text.removeSpan(obj);

        if (where != len) {
            AcfunHref h = (AcfunHref) obj;

            if (h.mHref != null) {
                text.setSpan(new AcfunURLSpan(h.mHref), where, len,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
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

    private static class AcfunHref {
        public String mHref;

        public AcfunHref(String href) {
            mHref = href;
        }
    }

    private static class AcfunURLSpan extends ClickableSpan {

        private final String mURL;

        public AcfunURLSpan(String url) {
            mURL = url;
        }

        public String getURL() {
            return mURL;
        }

        @Override
        public void onClick(View widget) {
            // 对Acfun链接进行独立处理，调用acfun客户端
            Uri uri = Uri.parse(getURL());
            Context context = widget.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            try {
                PackageManager pm = context.getPackageManager();
                ComponentName cn = intent.resolveActivity(pm);
                if (cn == null)
                    throw new ActivityNotFoundException();
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.w("AcfunURLSpan", "Acfun Actvity was not found for intent, " + intent.toString());
            }
        }
    }
}
