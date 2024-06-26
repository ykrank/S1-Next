package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Objects
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val result = other as Result
        return Objects.equal(status, result.status) &&
                Objects.equal(messageStr, result.messageStr)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(status, messageStr)
    }

    override fun toString(): String {
        return "Result{" +
                "status='" + status + '\'' +
                ", message='" + messageStr + '\'' +
                '}'
    }

    companion object {
        val EMPTY = Result()
    }
}
