package me.ykrank.s1next.data.api.model.darkroom

import com.fasterxml.jackson.annotation.JsonProperty

class DarkRoomWrapper {

    @JsonProperty("data")
    var `data`: HashMap<Int, DarkRoom>? = null
    @JsonProperty("message")
    var message: DarkRoomMessage? = null
}