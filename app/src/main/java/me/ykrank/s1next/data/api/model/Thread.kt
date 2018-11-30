package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ykrank.androidtools.guava.Objects
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.SameItem
import me.ykrank.s1next.data.db.dbmodel.History
import org.apache.commons.lang3.StringEscapeUtils
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Ambiguity in naming due to [java.lang.Thread].
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@PaperParcel
class Thread : PaperParcelable, Cloneable, SameItem, StableIdModel {
    @JsonProperty("tid")
    var id: String? = null

    @JsonProperty("subject")
    var title: String? = null
        set(value) {
            // unescape some basic XML entities
            field = StringEscapeUtils.unescapeXml(value)
        }

    /**
     * perhaps '-'
     */
    @JsonProperty("replies")
    var replies: String? = null

    @JsonProperty("readperm")
    var permission: Int = 0

    @JsonProperty("author")
    var author: String? = null

    @JsonProperty("authorId")
    var authorId: Int = 0

    @JsonProperty("displayorder")
    var displayOrder: Int = 0

    @JsonProperty("typeid")
    var typeId: String? = null

    @JsonProperty("fid")
    var fid: String? = null

    @JsonIgnore
    var typeName: String? = null

    @JsonIgnore
    var isHide = false
    /**
     * reply count when last view
     */
    @JsonIgnore
    var lastReplyCount: Int = 0

    val reliesCount: Int
        get() {
            return replies?.toIntOrNull() ?: 0
        }

    constructor() {}

    constructor(history: History) {
        this.id = history.threadId.toString()
        this.title = history.title
    }

    override val stableId: Long
        get() = id?.toLongOrNull() ?: 0

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val thread = o as Thread?
        return permission == thread!!.permission &&
                authorId == thread.authorId &&
                displayOrder == thread.displayOrder &&
                isHide == thread.isHide &&
                lastReplyCount == thread.lastReplyCount &&
                Objects.equal(id, thread.id) &&
                Objects.equal(title, thread.title) &&
                Objects.equal(replies, thread.replies) &&
                Objects.equal(author, thread.author) &&
                Objects.equal(typeId, thread.typeId) &&
                Objects.equal(fid, thread.fid) &&
                Objects.equal(typeName, thread.typeName)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(id, title, replies, permission, author, authorId, displayOrder, typeId, fid, typeName, isHide, lastReplyCount)
    }

    public override fun clone(): Any {
        return super.clone() as Thread
    }

    override fun isSameItem(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val thread = o as Thread?
        return Objects.equal(id, thread!!.id) &&
                Objects.equal(title, thread.title) &&
                Objects.equal(author, thread.author) &&
                Objects.equal(authorId, thread.authorId)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class ThreadListInfo {

        @JsonProperty("threads")
        var threads: Int = 0

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ThreadListInfo

            if (threads != other.threads) return false

            return true
        }

        override fun hashCode(): Int {
            return threads
        }

    }

    companion object {

        private val TAG = Thread::class.java.simpleName

        @JvmField
        val CREATOR = PaperParcelThread.CREATOR
    }
}
