package cl.monsoon.s1next.model;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.monsoon.s1next.singleton.User;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    @JsonProperty("member_username")
    private String username;

    @JsonProperty("member_uid")
    private String uid;

    @JsonProperty("formhash")
    private String authenticityToken;

    @JsonProperty("readaccess")
    private int permission;

    public String getUsername() {
        return username;
    }

    /**
     * Store user info when has logged in (just now or before).
     */
    public void setUsername(String username) {
        this.username = username;

        final boolean isUserExisted = !TextUtils.isEmpty(User.getName());
        if (TextUtils.isEmpty(username)) {
            if (isUserExisted) {
                User.setName(null);
                // user's cookie has expired
                User.sendLogoutOrExpirationBroadcast();
            }
        } else {
            if (!isUserExisted) {
                User.setName(username);

                // we should confirm both username and uid are exist
                // then send login Broadcast
                if (uid != null) {
                    // login in
                    User.sendLoginBroadcast();
                }
            }
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;

        // uid.equals("0") = true when user hasn't logged in
        if (TextUtils.isEmpty(uid) || uid.equals("0")) {
            User.setUid(null);
        } else {
            User.setUid(uid);

            // we don't send login Broadcast twice actually
            if (username != null) {
                User.sendLoginBroadcast();
            }
        }
    }

    public String getAuthenticityToken() {
        return authenticityToken;
    }

    public void setAuthenticityToken(String authenticityToken) {
        this.authenticityToken = authenticityToken;

        if (TextUtils.isEmpty(authenticityToken)) {
            User.setAuthenticityToken(null);
        } else {
            User.setAuthenticityToken(authenticityToken);
        }
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;

        User.setPermission(permission);
    }
}
