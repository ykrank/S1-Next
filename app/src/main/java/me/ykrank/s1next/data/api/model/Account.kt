package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
open class Account {
    @JsonProperty("member_uid")
    var uid: String? = null

    @JsonProperty("member_username")
    var username: String? = null

    @JsonProperty("formhash")
    var authenticityToken: String? = null

    @JsonProperty("readaccess")
    var permission = 0
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Account) return false

        if (uid != other.uid) return false
        if (username != other.username) return false
        if (authenticityToken != other.authenticityToken) return false
        if (permission != other.permission) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid?.hashCode() ?: 0
        result = 31 * result + (username?.hashCode() ?: 0)
        result = 31 * result + (authenticityToken?.hashCode() ?: 0)
        result = 31 * result + permission
        return result
    }

    override fun toString(): String {
        return "Account(uid=$uid, username=$username, authenticityToken=$authenticityToken, permission=$permission)"
    }

}
