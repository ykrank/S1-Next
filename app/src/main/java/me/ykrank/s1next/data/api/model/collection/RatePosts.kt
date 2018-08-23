package me.ykrank.s1next.data.api.model.collection

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import me.ykrank.s1next.App
import me.ykrank.s1next.data.api.model.Account
import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.util.JsonUtil

/**
 * Just use for load rates info because only new api could get this
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class RatePosts : Account {

    @JsonProperty("postlist")
    var postList: List<Post>? = null

    @JsonIgnore
    var commentCountMap: Map<Int, Int?>? = null

    constructor() {}

    @JsonCreator
    constructor(@JsonProperty("commentcount") commentCount: JsonNode?) {
        if (commentCount != null && commentCount.isObject) {
            commentCountMap = JsonUtil.readJsonNode(App.preAppComponent.jsonMapper, commentCount, object : TypeReference<Map<Int, Int?>>() {})
        }
    }

}
