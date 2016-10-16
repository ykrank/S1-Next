package me.ykrank.s1next.widget;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.view.View;

import java.util.Map;

import me.ykrank.s1next.util.L;

import static me.ykrank.s1next.widget.TagHandler.getLastSpan;

/**
 * <xmp>
 * 符合intent-filter的<a></a> tag。或者<bilibili></bilibili> tag
 * </xmp>
 * <br>
 * <xmp>
 * eg:<a href="http://www.bilibili.com/video/av6591319/" >http://www.bilibili.com/video/av6591319/</a>
 * </xmp>
 * <br>
 * <xmp>
 * or <bilibili>http://www.bilibili.com/video/av6591319/</bilibili>
 * </xmp>
 * <br>
 * Created by ykrank on 2016/10/16 0016.
 */

public class BilibiliSpan implements CustomMovementMethod.URLSpanClick {
    /**
     * intent-filter.从官方APP AndroidManifest中提取
     */
    private static ArrayMap<String, String> HOST_FILTERS = new ArrayMap<>();

    static {
        HOST_FILTERS.put("space.bilibili.com", ".*");
        HOST_FILTERS.put("bilibili.kankanews.com", "/video/av.*");
        HOST_FILTERS.put("bilibili.tv", "/video/av.*");
        HOST_FILTERS.put("bilibili.cn", "/video/av.*");
        HOST_FILTERS.put("bilibili.com", "/video/av.*");
        HOST_FILTERS.put("www.bilibili.tv", "/video/av.*");
        HOST_FILTERS.put("www.bilibili.cn", "/video/av.*");
        HOST_FILTERS.put("www.bilibili.com", "/video/av.*");
        HOST_FILTERS.put("bilibili.smgbb.cn", "/video/av.*");
        HOST_FILTERS.put("m.acg.tv", "/video/av.*");
        HOST_FILTERS.put("www.bilibili.com", "/mobile/video/av.*");
        HOST_FILTERS.put("live.bilibili.com", "/live/*.html");
        HOST_FILTERS.put("www.bilibili.com", "/bangumi/i/.*");
        HOST_FILTERS.put("www.bilibili.com", "/mobile/bangumi/i/.*");
        HOST_FILTERS.put("bangumi.bilibili.com", "/anime/.*");
        HOST_FILTERS.put("bangumi.bilibili.com", "/anime/category/.*");
        HOST_FILTERS.put("bilibili.com", "/sp/.*");
        HOST_FILTERS.put("www.bilibili.com", "/sp/.*");
        HOST_FILTERS.put("bilibili.tv", "/sp/.*");
        HOST_FILTERS.put("www.bilibili.tv", "/sp/.*");
    }

    /**
     * See android.text.HtmlToSpannedConverter#startA(android.text.SpannableStringBuilder, org.xml.sax.Attributes)
     */
    public static void startBilibiliSpan(SpannableStringBuilder text) {
        int len = text.length();
        text.setSpan(new BilibiliHref(), len, len, Spannable.SPAN_MARK_MARK);
    }

    /**
     * See android.text.HtmlToSpannedConverter#endA(android.text.SpannableStringBuilder)
     */
    public static void endBilibiliSpan(@NonNull SpannableStringBuilder text) {
        int len = text.length();
        Object obj = getLastSpan(text, BilibiliHref.class);
        int where = text.getSpanStart(obj);

        text.removeSpan(obj);

        if (where < len && obj != null) {
            char[] hrefChars = new char[len - where];
            text.getChars(where, len, hrefChars, 0);
            String href = String.valueOf(hrefChars);

            text.setSpan(new BilibiliURLSpan(href), where, len,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public boolean isMatch(Uri uri) {
        if ("http".equalsIgnoreCase(uri.getScheme())) {
            String path = uri.getEncodedPath();
            if (path == null) {
                path = "/";
            }
            for (Map.Entry<String, String> filter : HOST_FILTERS.entrySet()) {
                String host = filter.getKey();
                String pathPattern = filter.getValue();

                if (host.equalsIgnoreCase(uri.getHost())
                        && path.matches(pathPattern)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onClick(Uri uri, View view) {
        goBilibili(view.getContext(), uri);
    }

    // 对Bilibili链接进行独立处理，调用Bilibili客户端
    private static void goBilibili(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setClassName("tv.danmaku.bili", "tv.danmaku.bili.ui.IntentHandlerActivity");
        try {
            PackageManager pm = context.getPackageManager();
            ComponentName cn = intent.resolveActivity(pm);
            if (cn == null)
                throw new ActivityNotFoundException();
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            L.e("BilibiliURLSpan", "Bilibili Actvity was not found for intent, " + intent.toString());
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    private static class BilibiliHref {

    }

    private static class BilibiliURLSpan extends ClickableSpan {

        private final String mURL;

        BilibiliURLSpan(String url) {
            mURL = url;
        }

        String getURL() {
            return mURL;
        }

        @Override
        public void onClick(View widget) {
            goBilibili(widget.getContext(), Uri.parse(getURL()));
        }
    }
}
