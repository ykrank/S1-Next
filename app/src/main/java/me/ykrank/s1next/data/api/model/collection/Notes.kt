package me.ykrank.s1next.data.api.model.collection

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import me.ykrank.s1next.data.api.model.Note

/**
 * Created by ykrank on 2017/1/5.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Notes : BaseAccountCollection<Note>() {

    @JsonProperty("groupid")
    var groupId = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Notes) return false
        if (!super.equals(other)) return false

        if (groupId != other.groupId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + groupId
        return result
    }


}
