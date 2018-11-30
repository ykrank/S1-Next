package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ykrank.androidtools.guava.Objects
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.SameItem

@JsonIgnoreProperties(ignoreUnknown = true)
class Pm : Cloneable, SameItem, StableIdModel {
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

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val pm = o as Pm?
        return Objects.equal(plId, pm!!.plId) &&
                Objects.equal(pmId, pm.pmId) &&
                Objects.equal(pmType, pm.pmType) &&
                Objects.equal(authorId, pm.authorId) &&
                Objects.equal(author, pm.author) &&
                Objects.equal(subject, pm.subject) &&
                Objects.equal(message, pm.message) &&
                Objects.equal(dateline, pm.dateline) &&
                Objects.equal(msgFromId, pm.msgFromId) &&
                Objects.equal(msgFrom, pm.msgFrom) &&
                Objects.equal(msgToId, pm.msgToId) &&
                Objects.equal(msgTo, pm.msgTo)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(plId, pmId, pmType, authorId, author, subject, message, dateline, msgFromId, msgFrom, msgToId, msgTo)
    }

    override fun isSameItem(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val pm = o as Pm?
        return Objects.equal(plId, pm!!.plId) && Objects.equal(pmId, pm.pmId)
    }
}
