package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import me.ykrank.s1next.util.HtmlUtils.unescapeHtml

@JsonIgnoreProperties(ignoreUnknown = true)
class ForumCategoryByIds {
    @JsonProperty("name")
    var name: String? = null
        set(name) {
            field = unescapeHtml(name)
        }

    @JvmField
    @JsonProperty("forums")
    var forumIds: List<Int>? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ForumCategoryByIds

        if (name != other.name) return false
        if (forumIds != other.forumIds) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (forumIds?.hashCode() ?: 0)
        return result
    }


}
