package me.ykrank.s1next.data.api.app.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by ykrank on 2017/6/3.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
class AppUserInfo {
    /**
     * uid : 223963
     * username : ykrank
     * groupid : 57
     * credits : 104850
     * newprompt : 0
     * grouptitle : 圣者
     * e : 18
     * coin : 5325
     * signed : false
     * avatarurl : http://centeru.saraba1st.com/avatar.php?uid=223963&size=middle
     */

    @JsonProperty("uid")
    var uid: Int = 0
    @JsonProperty("username")
    var userName: String? = null
    @JsonProperty("groupid")
    var groupId: Int = 0
    @JsonProperty("credits")
    var credits: Int = 0
    @JsonProperty("newprompt")
    var newPrompt: Int = 0
    @JsonProperty("grouptitle")
    var groupTitle: String? = null
    @JsonProperty("e")
    var e: Int = 0
    @JsonProperty("coin")
    var coin: Int = 0
    @JsonProperty("signed")
    var isSigned: Boolean = false
    @JsonProperty("avatarurl")
    var avatarUrl: String? = null
}
