package me.ykrank.s1next.data.api.model.darkroom

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import me.ykrank.s1next.App
import me.ykrank.s1next.util.JsonUtil

class DarkRoomWrapper {

    @JsonIgnore
    var darkRooms: List<DarkRoom> = listOf()
    @JsonProperty("message")
    var message: DarkRoomMessage? = null
    @JsonIgnore
    var last: Boolean = false

    constructor()

    @JsonCreator
    constructor(@JsonProperty("data") d: JsonNode?) {
        if (d != null) {
            val rooms = mutableListOf<DarkRoom>()
            d.elements().forEach {
                rooms.add(JsonUtil.readJsonNode(App.preAppComponent.jsonMapper, it, DarkRoom::class.java))
            }
            darkRooms = rooms
        }
    }
}