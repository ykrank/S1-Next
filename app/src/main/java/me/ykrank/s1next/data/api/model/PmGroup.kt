package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ykrank.androidtools.guava.Objects
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.SameItem

/**
 * Created by ykrank on 2016/11/12 0012.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class PmGroup : Cloneable, SameItem, StableIdModel {
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

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val pmGroup = o as PmGroup?
        return Objects.equal(authorId, pmGroup!!.authorId) &&
                Objects.equal(plId, pmGroup.plId) &&
                Objects.equal(isNew, pmGroup.isNew) &&
                Objects.equal(lastAuthorid, pmGroup.lastAuthorid) &&
                Objects.equal(lastAuthor, pmGroup.lastAuthor) &&
                Objects.equal(lastSummary, pmGroup.lastSummary) &&
                Objects.equal(lastDateline, pmGroup.lastDateline) &&
                Objects.equal(pmNum, pmGroup.pmNum) &&
                Objects.equal(toUid, pmGroup.toUid) &&
                Objects.equal(toUsername, pmGroup.toUsername)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(authorId, plId, isNew, lastAuthorid, lastAuthor, lastSummary, lastDateline, pmNum, toUid, toUsername)
    }

    override fun isSameItem(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val pmGroup = o as PmGroup?
        return Objects.equal(plId, pmGroup!!.plId)
    }
}
