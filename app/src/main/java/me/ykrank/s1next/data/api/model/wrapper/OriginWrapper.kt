package me.ykrank.s1next.data.api.model.wrapper

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Objects
import me.ykrank.s1next.data.api.model.Account

/**
 * Created by ykrank on 2017/2/3.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class OriginWrapper<T : Account> {
    @JsonProperty("Variables")
    var data: T? = null

    @JsonProperty("error")
    var error: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OriginWrapper<*>) return false
        return Objects.equal(data, other.data) &&
                Objects.equal(error, other.error)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(data, error)
    }

    override fun toString(): String {
        return "OriginWrapper{" +
                "data=" + data +
                ", error='" + error + '\'' +
                '}'
    }
}
