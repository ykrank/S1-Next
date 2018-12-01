package me.ykrank.s1next.data.api.model.darkroom

import com.fasterxml.jackson.annotation.JsonProperty

class DarkRoomMessage {

    @JsonProperty("cid")
    var cid: String? = null // 40561
    @JsonProperty("dataexist")
    var dataExist: String? = null // 1
}