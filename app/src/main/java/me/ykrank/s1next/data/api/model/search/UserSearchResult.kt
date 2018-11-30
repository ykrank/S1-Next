package me.ykrank.s1next.data.api.model.search

import com.github.ykrank.androidtools.ui.adapter.StableIdModel

/**
 * Created by ykrank on 2017/04/01.
 */

class UserSearchResult : StableIdModel, SearchResult() {

    var uid: String? = null
    var name: String? = null

    override fun toString(): String {
        return "UserSearchResult{" +
                "uid='" + uid + '\''.toString() +
                ", name='" + name + '\''.toString() +
                '}'.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserSearchResult

        if (uid != other.uid) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }
}
