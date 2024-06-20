package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import me.ykrank.s1next.util.HtmlUtils

@JsonIgnoreProperties(ignoreUnknown = true)
class Favourite : StableIdModel {

    @JsonProperty("id")
    var id: String? = null

    @JsonProperty("favid")
    var favId: String? = null

    @JsonProperty("title")
    // unescape some basic XML entities
    var title: String? = null
        set(title) {
            field = HtmlUtils.unescapeHtml(title)
        }

    override val stableId: Long
        get() = favId?.toLongOrNull() ?: 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Favourite

        if (id != other.id) return false
        if (favId != other.favId) return false
        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (favId?.hashCode() ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        return result
    }

}
