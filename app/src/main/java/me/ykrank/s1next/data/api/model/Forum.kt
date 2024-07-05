package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.DiffSameItem
import me.ykrank.s1next.util.HtmlUtils
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@JsonIgnoreProperties(ignoreUnknown = true)
@PaperParcel
class Forum : PaperParcelable, DiffSameItem, StableIdModel {

    @JsonProperty("fid")
    var id: String? = null

    @JsonProperty("name")
    var name: String? = null
        set(value) {
            field = HtmlUtils.unescapeHtml(value)
        }

    @JsonProperty("threads")
    var threads: Int = 0

    @JsonProperty("todayposts")
    var todayPosts: Int = 0

    constructor() {}

    override val stableId: Long
        get() = id?.toLongOrNull() ?: 0

    override fun isSameItem(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Forum

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun toString(): String {
        return "Forum{" +
                "id='" + id + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", threads=" + threads +
                ", todayPosts=" + todayPosts +
                '}'.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Forum

        if (id != other.id) return false
        if (name != other.name) return false
        if (threads != other.threads) return false
        if (todayPosts != other.todayPosts) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + threads
        result = 31 * result + todayPosts
        return result
    }

    companion object {

        @JvmField
        val CREATOR = PaperParcelForum.CREATOR

    }
}
