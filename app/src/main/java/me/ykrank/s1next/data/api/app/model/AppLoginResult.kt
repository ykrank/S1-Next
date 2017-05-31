package me.ykrank.s1next.data.api.app.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class AppLoginResult {
    var uid: String? = null
    @JsonProperty("sid")
    var secureToken: String? = null
    @JsonProperty("username")
    var name: String? = null
}