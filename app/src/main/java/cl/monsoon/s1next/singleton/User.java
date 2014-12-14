package cl.monsoon.s1next.singleton;

import android.content.Intent;

import cl.monsoon.s1next.MyApplication;

/**
 * Current user.
 */
public enum User {
    INSTANCE;

    public static final String ACTION_USER_LOGIN = "user_login";
    public static final String ACTION_USER_LOGOUT_OR_EXPIRATION = "user_logout_or_expiration";

    private volatile String name;
    private volatile String uid;
    private volatile String authenticityToken;

    public static String getName() {
        return INSTANCE.name;
    }

    public static void setName(String name) {
        INSTANCE.name = name;
    }

    public static String getUid() {
        return INSTANCE.uid;
    }

    public static void setUid(String uid) {
        INSTANCE.uid = uid;
    }

    public static String getAuthenticityToken() {
        return INSTANCE.authenticityToken;
    }

    public static void setAuthenticityToken(String authenticityToken) {
        INSTANCE.authenticityToken = authenticityToken;
    }

    public static void clear() {
        INSTANCE.name = INSTANCE.uid = INSTANCE.authenticityToken = null;
    }

    public static void sendLoginBroadcast() {
        MyApplication.getContext().sendBroadcast(new Intent(ACTION_USER_LOGIN));
    }

    public static void sendLogoutOrExpirationBroadcast() {
        MyApplication.getContext().sendBroadcast(new Intent(ACTION_USER_LOGOUT_OR_EXPIRATION));
    }

    public static interface OnLogoutListener {

        public void onLogout();
    }
}
