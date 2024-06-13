package me.ykrank.s1next.data.api.model.search

import android.text.Spanned
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import me.ykrank.s1next.widget.span.HtmlCompat

/**
 * Created by ykrank on 2016/10/18.
 */

class ForumSearchResult : StableIdModel, SearchResult() {

    var content: String? = null
    private var _htmlContent: Spanned? = null
    val htmlContent: Spanned?
        get() = if (_htmlContent != null || content == null)
            _htmlContent
        else
            HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_COMPACT_EXCLUDE_BLOCKQUOTE)

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val forumSearchResult = o as ForumSearchResult?

        return content == forumSearchResult!!.content

    }

    override fun hashCode(): Int {
        return content!!.hashCode()
    }

    override fun toString(): String {
        return "ForumSearchResult{" +
                "content='" + content + '\''.toString() +
                '}'.toString()
    }
}
