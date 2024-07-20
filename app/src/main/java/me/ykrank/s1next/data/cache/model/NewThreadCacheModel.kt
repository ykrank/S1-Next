package me.ykrank.s1next.data.cache.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Objects

@JsonIgnoreProperties(ignoreUnknown = true)
data class NewThreadCacheModel(
    @JsonProperty("selectPosition")
    var selectPosition: Int = 0,
    @JsonProperty("title")
    var title: String? = null,
    @JsonProperty("message")
    var message: String? = null,
) {

}
