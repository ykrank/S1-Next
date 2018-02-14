package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

/**
 * Created by ykrank on 2017/1/8.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Profile : Account {

    var homeUsername: String? = null
    var homeUid: String? = null
    var groupTitle: String? = null
    var friends: Int = 0
    var threads: Int = 0
    var replies: Int = 0
    var signHtml: String? = null
    var onlineHour: Int = 0
    var regDate: String? = null
    var lastVisitDate: String? = null
    var lastActiveDate: String? = null
    var lastPostDate: String? = null
    var credits: Int = 0
    var combatEffectiveness: Int = 0
    var gold: Int = 0
    var rp: Int = 0
    var shameSense: Int = 0

    constructor()

    @JsonCreator
    constructor(@JsonProperty("extcredits") extCredits: JsonNode, @JsonProperty("space") space: JsonNode) {
        this.homeUsername = space.get("username")?.asText()
        this.homeUid = space.get("uid")?.asText()
        this.groupTitle = space.get("group").get("grouptitle")?.asText()
        this.friends = space.get("friends")?.asInt() ?: -1
        val posts = space.get("posts")?.asInt() ?: -1
        this.threads = space.get("threads")?.asInt() ?: -1
        this.replies = posts - threads
        this.signHtml = space.get("sightml")?.asText()
        this.onlineHour = space.get("oltime")?.asInt() ?: -1
        this.regDate = space.get("regdate")?.asText()
        this.lastVisitDate = space.get("lastvisit")?.asText()
        this.lastActiveDate = space.get("lastactivity")?.asText()
        this.lastPostDate = space.get("lastpost")?.asText()
        this.credits = space.get("credits")?.asInt() ?: -1
        this.combatEffectiveness = space.get("extcredits1")?.asInt() ?: -1
        this.gold = space.get("extcredits2")?.asInt() ?: -1
        this.rp = space.get("extcredits4")?.asInt() ?: -1
        this.shameSense = space.get("extcredits7")?.asInt() ?: -1
    }
}
