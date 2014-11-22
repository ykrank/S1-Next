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

    @JsonProperty("formhash")
    private String authenticityToken;

    public String getUsername() {
        return username;
    }

    /**
     * Store user info when has logged in (just now or before).
     */
    public void setUsername(String username) {
        this.username = username;

        if (Config.getUsername() == null && !TextUtils.isEmpty(username)) {
            Config.setUsername(username);
        } else if (Config.getUsername() != null && TextUtils.isEmpty(username)) {
            Config.setUsername(null);
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;

        // uid.equals("0") = true when user hasn't logged in
        if (Config.getUid() == null && !TextUtils.isEmpty(uid) && !uid.equals("0")) {
            Config.setUid(uid);
        } else if (Config.getUid() != null && (TextUtils.isEmpty(uid) || uid.equals("0"))) {
            Config.setUid(null);
        }
    }

    public String getAuthenticityToken() {
        return authenticityToken;
    }

    public void setAuthenticityToken(String authenticityToken) {
        this.authenticityToken = authenticityToken;

        if (Config.getAuthenticityToken() == null && !TextUtils.isEmpty(authenticityToken)) {
            Config.setAuthenticityToken(authenticityToken);
        } else if (Config.getAuthenticityToken() != null &&
                !authenticityToken.equals(Config.getAuthenticityToken())) {
            Config.setAuthenticityToken(null);
        }
    }
}
