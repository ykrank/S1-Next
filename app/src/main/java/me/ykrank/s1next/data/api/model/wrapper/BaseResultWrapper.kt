package me.ykrank.s1next.data.api.model.wrapper

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Objects
import me.ykrank.s1next.data.api.model.Account
import me.ykrank.s1next.data.api.model.Result

@JsonIgnoreProperties(ignoreUnknown = true)
open class BaseResultWrapper<T : Account> : OriginWrapper<T>() {
    @JsonProperty("Message")
    var result: Result = Result.EMPTY
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseResultWrapper<*>) return false
        if (!super.equals(other)) return false
        return Objects.equal(result, other.result)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(super.hashCode(), result)
    }

    override fun toString(): String {
        return "BaseResultWrapper{" +
                "result=" + result +
                '}'
    }
}
