package me.ykrank.s1next.data.api.model.search

import com.github.ykrank.androidtools.ui.adapter.StableIdModel

/**
 * Created by ykrank on 2016/10/18.
 */

class ForumSearchResult : StableIdModel, SearchResult() {

    var content: String? = null

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
