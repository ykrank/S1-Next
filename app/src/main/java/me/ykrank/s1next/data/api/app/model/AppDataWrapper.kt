package me.ykrank.s1next.data.api.app.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by ykrank on 2017/2/3.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
open class AppDataWrapper<T> : AppResult() {
    @JsonProperty("data")
    var data: T? = null
}
