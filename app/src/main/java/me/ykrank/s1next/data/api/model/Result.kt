package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import me.ykrank.s1next.data.api.DiscuzMessageFormatter

@JsonIgnoreProperties(ignoreUnknown = true)
class Result {
    @JvmField
    @JsonProperty("messageval")
    var status: String? = null

    @JsonProperty("messagestr")
    var messageStr: String? = null

    val message: String?
        get() = DiscuzMessageFormatter.addFullStopIfNeeded(messageStr)



    override fun toString(): String {
        return "Result{" +
                "status='" + status + '\'' +
                ", message='" + messageStr + '\'' +
                '}'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Result

        if (status != other.status) return false
        if (messageStr != other.messageStr) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status?.hashCode() ?: 0
        result = 31 * result + (messageStr?.hashCode() ?: 0)
        return result
    }

    companion object {
        val EMPTY = Result()
    }
}
