package me.ykrank.s1next.widget.span;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import androidx.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.view.View;

import com.github.ykrank.androidtools.util.L;

import org.xml.sax.Attributes;

/**
 * Created by ykrank on 2016/10/16 0016.
 */

public class AcfunSpan implements PostMovementMethod.URLSpanClick {

    /**
     * See android.text.HtmlToSpannedConverter#startA(android.text.SpannableStringBuilder, org.xml.sax.Attributes)
     */
    public static void startAcfun(SpannableStringBuilder text, Attributes attributes) {
        String href = attributes.getValue("href");

        int len = text.length();
        text.setSpan(new AcfunHref(href), len, len, Spannable.SPAN_MARK_MARK);
    }

    /**
     * See android.text.HtmlToSpannedConverter#endA(android.text.SpannableStringBuilder)
     */
    public static void endAcfun(@NonNull SpannableStringBuilder text) {
        int len = text.length();
        Object obj = TagHandler.getLastSpan(text, AcfunHref.class);
        int where = text.getSpanStart(obj);

        text.removeSpan(obj);

        if (where != len && obj != null) {
            AcfunHref h = (AcfunHref) obj;

            if (h.mHref != null) {
                text.setSpan(new AcfunURLSpan(h.mHref), where, len,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    // 对Acfun链接进行独立处理，调用acfun客户端
    //TODO 目前Acfun客户端似乎不支持直接intent-filter调用
    private static void goAcfun(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (Throwable e) {
            L.report("AcfunURLSpan, Acfun Actvity was not found for intent, " + intent.toString(), e);
        }
    }

    @Override
    public boolean isMatch(Uri uri) {
        return false;
    }

    @Override
    public void onClick(Uri uri, View view) {
        goAcfun(view.getContext(), uri);
    }

    private static class AcfunHref {
        String mHref;

        AcfunHref(String href) {
            mHref = href;
        }
    }

    private static class AcfunURLSpan extends ClickableSpan {

        private final String mURL;

        AcfunURLSpan(String url) {
            mURL = url;
        }

        String getURL() {
            return mURL;
        }

        @Override
        public void onClick(View widget) {
            goAcfun(widget.getContext(), Uri.parse(getURL()));
        }
    }
}
