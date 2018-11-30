package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ykrank.androidtools.guava.Objects
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.SameItem
import org.apache.commons.lang3.StringEscapeUtils
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@JsonIgnoreProperties(ignoreUnknown = true)
@PaperParcel
class Forum : PaperParcelable, SameItem, StableIdModel {

    @JsonProperty("fid")
    var id: String? = null

    @JsonProperty("name")
    var name: String? = null
        set(value) {
            // unescape some basic XML entities
            field = StringEscapeUtils.unescapeXml(value)
        }

    @JsonProperty("threads")
    var threads: Int = 0

    @JsonProperty("todayposts")
    var todayPosts: Int = 0

    constructor() {}

    override val stableId: Long
        get() = id?.toLongOrNull() ?: 0

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val forum = o as Forum?
        return Objects.equal(threads, forum!!.threads) &&
                Objects.equal(todayPosts, forum.todayPosts) &&
                Objects.equal(id, forum.id) &&
                Objects.equal(name, forum.name)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(id, name, threads, todayPosts)
    }

    override fun isSameItem(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val forum = o as Forum?
        return Objects.equal(id, forum!!.id) && Objects.equal(name, forum.name)
    }

    override fun toString(): String {
        return "Forum{" +
                "id='" + id + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", threads=" + threads +
                ", todayPosts=" + todayPosts +
                '}'.toString()
    }

    companion object {

        @JvmField
        val CREATOR = PaperParcelForum.CREATOR

    }
}
