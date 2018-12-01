package me.ykrank.s1next.data.api.model.darkroom

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ykrank.androidtools.ui.adapter.StableIdModel

class DarkRoom : StableIdModel {
    @JsonProperty("action")
    var action: String? = null  // 禁止发言
    @JsonProperty("cid")
    var cid: String? = null  // 40648
    @JsonProperty("dateline")
    var dateline: String? = null  // 2018-11-24 06:48
    @JsonProperty("groupexpiry")
    var groupExpiry: String? = null  // 2018-12-24 06:48
    @JsonProperty("operator")
    var `operator`: String? = null  // 索兰塔.织姬
    @JsonProperty("operatorid")
    var operatorId: String? = null  // 245
    @JsonProperty("reason")
    var reason: String? = null  // 学好普通话，少谈政治
    @JsonProperty("uid")
    var uid: String? = null  // 223056
    @JsonProperty("username")
    var username: String? = null // c933103

    override val stableId: Long
        get() = cid?.toLongOrNull() ?: 0
}