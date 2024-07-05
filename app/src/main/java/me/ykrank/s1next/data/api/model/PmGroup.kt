package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.DiffSameItem

/**
 * Created by ykrank on 2016/11/12 0012.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class PmGroup : Cloneable, DiffSameItem, StableIdModel {
    /**
     * who create this pm list
     */
    @JsonProperty("authorid")
    var authorId: String? = null
    /**
     * pm list id
     */
    @JsonProperty("plid")
    var plId: String? = null
    @JsonIgnore
    var isNew: Boolean = false
    @JsonProperty("lastauthorid")
    var lastAuthorid: String? = null
    @JsonProperty("lastauthor")
    var lastAuthor: String? = null
    @JsonProperty("lastsummary")
    var lastSummary: String? = null
    @JsonProperty("lastdateline")
    var lastDateline: Long = 0
    @JsonProperty("pmnum")
    var pmNum: String? = null
    @JsonProperty("touid")
    var toUid: String? = null
    @JsonProperty("tousername")
    var toUsername: String? = null

    constructor() {}

    @JsonCreator
    constructor(@JsonProperty("isnew") isNew: String) {
        this.isNew = "1" == isNew
    }

    override val stableId: Long
        get() = plId?.toLongOrNull() ?: 0


    override fun isSameItem(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PmGroup
        if (plId != other.plId) return false

        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PmGroup

        if (authorId != other.authorId) return false
        if (plId != other.plId) return false
        if (isNew != other.isNew) return false
        if (lastAuthorid != other.lastAuthorid) return false
        if (lastAuthor != other.lastAuthor) return false
        if (lastSummary != other.lastSummary) return false
        if (lastDateline != other.lastDateline) return false
        if (pmNum != other.pmNum) return false
        if (toUid != other.toUid) return false
        if (toUsername != other.toUsername) return false

        return true
    }

    override fun hashCode(): Int {
        var result = authorId?.hashCode() ?: 0
        result = 31 * result + (plId?.hashCode() ?: 0)
        result = 31 * result + isNew.hashCode()
        result = 31 * result + (lastAuthorid?.hashCode() ?: 0)
        result = 31 * result + (lastAuthor?.hashCode() ?: 0)
        result = 31 * result + (lastSummary?.hashCode() ?: 0)
        result = 31 * result + lastDateline.hashCode()
        result = 31 * result + (pmNum?.hashCode() ?: 0)
        result = 31 * result + (toUid?.hashCode() ?: 0)
        result = 31 * result + (toUsername?.hashCode() ?: 0)
        return result
    }
}
