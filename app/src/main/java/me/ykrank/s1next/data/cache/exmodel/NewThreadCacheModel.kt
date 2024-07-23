package me.ykrank.s1next.data.cache.exmodel

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

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
