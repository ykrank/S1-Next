package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.DiffSameItem

@JsonIgnoreProperties(ignoreUnknown = true)
class Pm : Cloneable, DiffSameItem, StableIdModel {
    @JsonProperty("plid")
    var plId: String? = null
    @JsonProperty("pmid")
    var pmId: String? = null
    @JsonProperty("pmtype")
    var pmType: String? = null
    @JsonProperty("authorid")
    var authorId: String? = null
    var author: String? = null
    var subject: String? = null
    var message: String? = null
    var dateline: Long = 0
    @JsonProperty("msgfromid")
    var msgFromId: String? = null
    @JsonProperty("msgfrom")
    var msgFrom: String? = null
    @JsonProperty("msgtoid")
    var msgToId: String? = null
    //no this data in api, should set it manual
    @JsonIgnore
    var msgTo: String? = null

    override val stableId: Long
        get() = pmId?.toLongOrNull() ?: 0


    override fun isSameItem(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pm

        if (plId != other.plId) return false
        if (pmId != other.pmId) return false

        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pm

        if (plId != other.plId) return false
        if (pmId != other.pmId) return false
        if (pmType != other.pmType) return false
        if (authorId != other.authorId) return false
        if (author != other.author) return false
        if (subject != other.subject) return false
        if (message != other.message) return false
        if (dateline != other.dateline) return false
        if (msgFromId != other.msgFromId) return false
        if (msgFrom != other.msgFrom) return false
        if (msgToId != other.msgToId) return false
        if (msgTo != other.msgTo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = plId?.hashCode() ?: 0
        result = 31 * result + (pmId?.hashCode() ?: 0)
        result = 31 * result + (pmType?.hashCode() ?: 0)
        result = 31 * result + (authorId?.hashCode() ?: 0)
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (subject?.hashCode() ?: 0)
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + dateline.hashCode()
        result = 31 * result + (msgFromId?.hashCode() ?: 0)
        result = 31 * result + (msgFrom?.hashCode() ?: 0)
        result = 31 * result + (msgToId?.hashCode() ?: 0)
        result = 31 * result + (msgTo?.hashCode() ?: 0)
        return result
    }
}
