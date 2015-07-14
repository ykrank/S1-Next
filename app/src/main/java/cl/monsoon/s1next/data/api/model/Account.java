package cl.monsoon.s1next.data.api.model;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.data.User;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    private static final String INVALID_UID = "0";

    User mUser;

    @JsonProperty("member_uid")
    private String uid;

    @JsonProperty("member_username")
    private String username;

    @JsonProperty("formhash")
    private String authenticityToken;

    @JsonProperty("readaccess")
    private int permission;

    public Account() {
        mUser = App.get().getAppComponent().getUser();
    }

    public String getUid() {
        return uid;
    }

    /**
     * We should confirm that both uid and username have been set,
     * then send {@link User#sendCookieExpirationEvent()}
     * or {@link User#postLoginEvent()}.
     */
    public void setUid(String uid) {
        this.uid = uid;

        final boolean hasUserLoggedIn = mUser.isLogged();
        final boolean hasSetUsername = this.username != null;
        if (TextUtils.isEmpty(uid) || INVALID_UID.equals(uid)) {
            // if user's cookie has expired
            if (hasUserLoggedIn) {
                if (hasSetUsername) {
                    mUser.setUid(null);
                    mUser.setName(null);
                    User.sendCookieExpirationEvent();
                }
            }
        } else {
            mUser.setUid(uid);
            if (!hasUserLoggedIn && hasSetUsername) {
                User.postLoginEvent();
            }
        }
    }

    public String getUsername() {
        return username;
    }

    /**
     * Similar to {@link #setUid(String)}.
     */
    public void setUsername(String username) {
        this.username = username;

        final boolean hasUserLoggedIn = mUser.isLogged();
        final boolean hasSetUid = this.uid != null;
        if (TextUtils.isEmpty(username)) {
            if (hasUserLoggedIn) {
                if (hasSetUid) {
                    mUser.setUid(null);
                    mUser.setName(null);
                    User.sendCookieExpirationEvent();
                }
            }
        } else {
            mUser.setName(username);
            if (!hasUserLoggedIn && hasSetUid) {
                User.postLoginEvent();
            }
        }
    }

    public String getAuthenticityToken() {
        return authenticityToken;
    }

    public void setAuthenticityToken(String authenticityToken) {
        this.authenticityToken = authenticityToken;

        mUser.setAuthenticityToken(authenticityToken);
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;

        mUser.setPermission(permission);
    }
}
