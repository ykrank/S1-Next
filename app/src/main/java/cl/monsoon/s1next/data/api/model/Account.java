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
     * then change user's login status.
     */
    public void setUid(String uid) {
        this.uid = uid;

        final boolean isLogged = mUser.isLogged();
        final boolean hasSetUsername = !TextUtils.isEmpty(mUser.getName());
        if (INVALID_UID.equals(uid) || TextUtils.isEmpty(uid)) {
            // if user's cookie has expired
            if (isLogged) {
                if (hasSetUsername) {
                    mUser.setUid(null);
                    mUser.setName(null);
                    mUser.setLogged(false);
                }
            }
        } else {
            mUser.setUid(uid);
            if (!isLogged && hasSetUsername) {
                mUser.setLogged(true);
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

        final boolean isLogged = mUser.isLogged();
        final boolean hasSetUid = !TextUtils.isEmpty(mUser.getUid());
        if (TextUtils.isEmpty(username)) {
            if (isLogged) {
                if (hasSetUid) {
                    mUser.setUid(null);
                    mUser.setName(null);
                    mUser.setLogged(false);
                }
            }
        } else {
            mUser.setName(username);
            if (!isLogged && hasSetUid) {
                mUser.setLogged(true);
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
