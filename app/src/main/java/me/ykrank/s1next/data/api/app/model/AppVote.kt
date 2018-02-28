package me.ykrank.s1next.data.api.app.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by ykrank on 2017/9/29.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
class AppVote {

    /**
     * tid : 1550205
     * overt : 1
     * multiple : 0
     * visible : 1
     * maxchoices : 1
     * isimage : 0
     * expiration : 0
     * voters : 14
     * expired : false
     * voted : false
     */

    @JsonProperty("tid")
    var tid: Int? = null
    /**
     * 公开投票，可见投票人
     */
    @JsonIgnore
    var isOvert: Boolean = false
    /**
     * 是否多选
     */
    @JsonIgnore
    var isMultiple: Boolean = false
    /**
     * 比例是否可见
     */
    @JsonIgnore
    var isVisible: Boolean = false
    /**
     * 最多选择数量
     */
    @JsonProperty("maxchoices")
    var maxChoices: Int = 0
    @JsonIgnore
    var isImage: Boolean = false
    /**
     * 过期时间
     */
    @JsonProperty("expiration")
    var expiration: Long = 0
    /**
     * 投票的人数
     */
    @JsonProperty("voters")
    var voters: Int = 0
    /**
     * 是否已过期
     */
    @JsonProperty("expired")
    var isExpired: Boolean = false
    /**
     * 是否已投票
     */
    @JsonProperty("voted")
    var isVoted: Boolean = false

    constructor()

    @JsonCreator
    constructor(@JsonProperty("overt") overt: Int, @JsonProperty("multiple") multiple: Int,
                @JsonProperty("visible") visible: Int, @JsonProperty("isimage") isImage: Int) {
        this.isOvert = 1 == overt
        this.isMultiple = 1 == multiple
        this.isVisible = 1 == visible
        this.isImage = 1 == isImage
    }
}
