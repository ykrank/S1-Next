package me.ykrank.s1next.widget.span;

import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

/**
 * Created by ykrank on 2016/12/30.
 */

public class HtmlCompat {
    /**
     * Flag indicating that texts inside &lt;p&gt; elements will be separated from other texts with
     * one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH = 0x00000001;

    /**
     * Flag indicating that texts inside &lt;h1&gt;~&lt;h6&gt; elements will be separated from
     * other texts with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_HEADING = 0x00000002;

    /**
     * Flag indicating that texts inside &lt;li&gt; elements will be separated from other texts
     * with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM = 0x00000004;

    /**
     * Flag indicating that texts inside &lt;ul&gt; elements will be separated from other texts
     * with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_LIST = 0x00000008;

    /**
     * Flag indicating that texts inside &lt;div&gt; elements will be separated from other texts
     * with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_DIV = 0x00000010;

    /**
     * Flag indicating that texts inside &lt;blockquote&gt; elements will be separated from other
     * texts with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE = 0x00000020;

    /**
     * Flag indicating that CSS color values should be used instead of those defined in
     * {@link Color}.
     */
    public static final int FROM_HTML_OPTION_USE_CSS_COLORS = 0x00000100;

    /**
     * Flags for {@link Html#fromHtml(String, int, Html.ImageGetter, Html.TagHandler)}: Separate block-level
     * elements with blank lines (two newline characters) in between. This is the legacy behavior
     * prior to N.
     */
    public static final int FROM_HTML_MODE_LEGACY = 0x00000000;

    /**
     * Flags for {@link Html#fromHtml(String, int, Html.ImageGetter, Html.TagHandler)}: Separate block-level
     * elements with line breaks (single newline character) in between. This inverts the
     * {@link Spanned} to HTML string conversion done with the option
     * {@link Html#TO_HTML_PARAGRAPH_LINES_INDIVIDUAL}.
     */
    public static final int FROM_HTML_MODE_COMPACT =
            FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                    | FROM_HTML_SEPARATOR_LINE_BREAK_HEADING
                    | FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
                    | FROM_HTML_SEPARATOR_LINE_BREAK_LIST
                    | FROM_HTML_SEPARATOR_LINE_BREAK_DIV
                    | FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE;

    /**
     * {@linkplain Html#fromHtml(String)}
     */
    @Deprecated
    public static Spanned fromHtml(String source) {
        return fromHtml(source, FROM_HTML_MODE_LEGACY, null, null);
    }

    /**
     * {@linkplain Html#fromHtml(String, int)}
     */
    public static Spanned fromHtml(String source, int flags) {
        return fromHtml(source, flags, null, null);
    }

    /**
     * {@linkplain Html#fromHtml(String, Html.ImageGetter, Html.TagHandler)}
     */
    @Deprecated
    public static Spanned fromHtml(String source, Html.ImageGetter imageGetter, Html.TagHandler tagHandler) {
        return fromHtml(source, FROM_HTML_MODE_LEGACY, imageGetter, tagHandler);
    }

    /**
     * {@linkplain Html#fromHtml(String, int, Html.ImageGetter, Html.TagHandler)}
     */
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source, int flags, Html.ImageGetter imageGetter,
                                   Html.TagHandler tagHandler) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, flags, imageGetter, new HtmlTagHandlerCompat(tagHandler, flags));
        } else {
            return Html.fromHtml(source, imageGetter, new HtmlTagHandlerCompat(tagHandler, flags));
        }
    }
}
