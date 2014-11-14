package cl.monsoon.s1next.model;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.monsoon.s1next.Config;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    @JsonProperty("member_username")
    private String username;

    @JsonProperty("member_uid")
    private String uid;

    public String getUsername() {
        return username;
    }

    /**
     * Store user info when logged in.
     */
    public void setUsername(String username) {
        this.username = username;

        if (Config.getUsername() == null && !TextUtils.isEmpty(username)) {
            Config.setUsername(username);
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;

        // uid.equals("0") = true when user hasn't logged
        if (Config.getUid() == null && !TextUtils.isEmpty(uid) && !uid.equals("0")) {
            Config.setUid(uid);
        }
    }
}
