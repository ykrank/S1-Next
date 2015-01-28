package cl.monsoon.s1next.model;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.monsoon.s1next.singleton.MyAccount;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    @JsonIgnore
    private String uid;

    @JsonIgnore
    private String username;

    @JsonIgnore
    private String authenticityToken;

    @JsonIgnore
    private int permission;

    public Account() {

    }

    @JsonCreator
    public Account(
            @JsonProperty("member_uid") String uid,
            @JsonProperty("member_username") String username,
            @JsonProperty("formhash") String authenticityToken,
            @JsonProperty("readaccess") int permission) {
        this.username = username;
        this.uid = uid;
        this.authenticityToken = authenticityToken;
        this.permission = permission;

        final boolean isUserLoggedInPrevious = MyAccount.isLoggedIn();
        final boolean hasValidUidNow = !TextUtils.isEmpty(uid);
        if (isUserLoggedInPrevious) {
            // if user's cookie has expired
            if (!hasValidUidNow) {
                MyAccount.sendCookieExpirationBroadcast();
            }
        } else {
            if (hasValidUidNow) {
                MyAccount.sendLoginBroadcast();
            }
        }

        MyAccount.setUid(uid);
        MyAccount.setName(username);
        MyAccount.setAuthenticityToken(authenticityToken);
        MyAccount.setPermission(permission);
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthenticityToken() {
        return authenticityToken;
    }

    public int getPermission() {
        return permission;
    }
}
