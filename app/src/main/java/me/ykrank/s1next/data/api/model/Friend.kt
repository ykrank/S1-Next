package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ykrank.androidtools.guava.Objects
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.DiffSameItem

/**
 * Created by ykrank on 2017/1/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Friend : DiffSameItem, StableIdModel {
    @JsonProperty("uid")
    var uid: String? = null
    @JsonProperty("username")
    var username: String? = null

    override val stableId: Long
        get() = uid?.toLongOrNull() ?: 0

    override fun isSameItem(o: Any): Boolean {
        if (this === o) return true
        return if (o !is Friend) false else Objects.equal(uid, o.uid)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Friend

        if (uid != other.uid) return false
        if (username != other.username) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid?.hashCode() ?: 0
        result = 31 * result + (username?.hashCode() ?: 0)
        return result
    }
}
