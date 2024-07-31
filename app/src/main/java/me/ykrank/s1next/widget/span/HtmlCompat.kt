package me.ykrank.s1next.widget.span

import android.text.Html
import android.text.Html.ImageGetter
import android.text.Spanned

/**
 * Created by ykrank on 2016/12/30.
 */
object HtmlCompat {

    const val FROM_HTML_MODE_LEGACY = Html.FROM_HTML_MODE_LEGACY

    /**
     * @see Html.FROM_HTML_MODE_COMPACT except {@link .FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE}
     */
    const val FROM_HTML_MODE_COMPACT_EXCLUDE_BLOCKQUOTE = (Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
            or Html.FROM_HTML_SEPARATOR_LINE_BREAK_HEADING
            or Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
            or Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST
            or Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV)

    /**
     * [Html.fromHtml]
     */
    fun fromHtml(
        source: String?,
        imageGetter: ImageGetter?,
        tagHandler: Html.TagHandler?
    ): Spanned {
        return fromHtml(source, FROM_HTML_MODE_COMPACT_EXCLUDE_BLOCKQUOTE, imageGetter, tagHandler)
    }

    /**
     * [Html.fromHtml]
     */
    @JvmOverloads
    fun fromHtml(
        source: String?,
        flags: Int = FROM_HTML_MODE_COMPACT_EXCLUDE_BLOCKQUOTE,
        imageGetter: ImageGetter? = null,
        tagHandler: Html.TagHandler? = null
    ): Spanned {
        return Html.fromHtml(source, flags, imageGetter, tagHandler)
    }
}
