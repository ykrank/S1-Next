package me.ykrank.s1next.data.api.model.collection

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import me.ykrank.s1next.data.api.model.Account

/**
 * Created by yuanke on 6/4/24
 * @author yuanke.ykrank@bytedance.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
open class BaseAccountCollection<T> : Account() {
    @JsonProperty("count")
    var count = 0

    @JsonProperty("page")
    var page = 0

    @JsonProperty("perpage")
    var perPage = 0

    @JsonProperty("list")
    var list: List<T>? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseAccountCollection<*>) return false
        if (!super.equals(other)) return false

        if (count != other.count) return false
        if (page != other.page) return false
        if (perPage != other.perPage) return false
        if (list != other.list) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + count
        result = 31 * result + page
        result = 31 * result + perPage
        result = 31 * result + (list?.hashCode() ?: 0)
        return result
    }
}