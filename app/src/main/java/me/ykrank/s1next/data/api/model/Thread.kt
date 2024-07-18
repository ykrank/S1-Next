package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.DiffSameItem
import me.ykrank.s1next.data.db.dbmodel.History
import me.ykrank.s1next.util.HtmlUtils
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Ambiguity in naming due to [java.lang.Thread].
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@PaperParcel
class Thread : PaperParcelable, Cloneable, DiffSameItem, StableIdModel {
    @JsonProperty("tid")
    var id: String? = null

    @JsonProperty("subject")
    var title: String? = null
        set(value) {
            field = HtmlUtils.unescapeHtml(value)
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

    @JsonProperty("_typeName")
    var typeName: String? = null

    @JsonProperty("_isHide")
    var isHide = false
    /**
     * reply count when last view
     */
    @JsonProperty("_lastReplyCount")
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

    public override fun clone(): Any {
        return super.clone() as Thread
    }

    override fun isSameItem(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Thread

        if (id != other.id) return false
        if (title != other.title) return false
        if (author != other.author) return false
        if (authorId != other.authorId) return false

        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Thread

        if (id != other.id) return false
        if (title != other.title) return false
        if (replies != other.replies) return false
        if (permission != other.permission) return false
        if (author != other.author) return false
        if (authorId != other.authorId) return false
        if (displayOrder != other.displayOrder) return false
        if (typeId != other.typeId) return false
        if (fid != other.fid) return false
        if (typeName != other.typeName) return false
        if (isHide != other.isHide) return false
        if (lastReplyCount != other.lastReplyCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (replies?.hashCode() ?: 0)
        result = 31 * result + permission
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + authorId
        result = 31 * result + displayOrder
        result = 31 * result + (typeId?.hashCode() ?: 0)
        result = 31 * result + (fid?.hashCode() ?: 0)
        result = 31 * result + (typeName?.hashCode() ?: 0)
        result = 31 * result + isHide.hashCode()
        result = 31 * result + lastReplyCount
        return result
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
