package me.ykrank.s1next.data.api.app.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by ykrank on 2017/2/3.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
open class AppResult {
    @JsonProperty("code")
    var code: Int = 0
    @JsonProperty("message")
    var message: String? = null
    @JsonProperty("success")
    var isSuccess: Boolean = false
}
