package cl.monsoon.s1next.model;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.monsoon.s1next.singleton.MyAccount;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    private static final String INVALID_UID = "0";

    @JsonProperty("member_uid")
    private String uid;

    @JsonProperty("member_username")
    private String username;

    @JsonProperty("formhash")
    private String authenticityToken;

    @JsonProperty("readaccess")
    private int permission;

    public String getUid() {
        return uid;
    }

    /**
     * We should confirm that both uid and username have been set,
     * then send {@link cl.monsoon.s1next.singleton.MyAccount#sendCookieExpirationBroadcast()}
     * or {@link MyAccount#sendLoginBroadcast()}.
     */
    public void setUid(String uid) {
        this.uid = uid;

        final boolean hasUserLoggedIn = MyAccount.hasLoggedIn();
        final boolean hasSetUsername = this.username != null;
        if (TextUtils.isEmpty(uid) || INVALID_UID.equals(uid)) {
            // if user's cookie has expired
            if (hasUserLoggedIn) {
                if (hasSetUsername) {
                    MyAccount.setUid(null);
                    MyAccount.setName(null);
                    MyAccount.sendCookieExpirationBroadcast();
                }
            }
        } else {
            MyAccount.setUid(uid);
            if (!hasUserLoggedIn && hasSetUsername) {
                MyAccount.sendLoginBroadcast();
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

        final boolean hasUserLoggedIn = MyAccount.hasLoggedIn();
        final boolean hasSetUid = this.uid != null;
        if (TextUtils.isEmpty(username)) {
            if (hasUserLoggedIn) {
                if (hasSetUid) {
                    MyAccount.setUid(null);
                    MyAccount.setName(null);
                    MyAccount.sendCookieExpirationBroadcast();
                }
            }
        } else {
            MyAccount.setName(username);
            if (!hasUserLoggedIn && hasSetUid) {
                MyAccount.sendLoginBroadcast();
            }
        }
    }

    public String getAuthenticityToken() {
        return authenticityToken;
    }

    public void setAuthenticityToken(String authenticityToken) {
        this.authenticityToken = authenticityToken;

        MyAccount.setAuthenticityToken(authenticityToken);
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;

        MyAccount.setPermission(permission);
    }
}
