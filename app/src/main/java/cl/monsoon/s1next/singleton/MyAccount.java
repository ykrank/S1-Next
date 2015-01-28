package cl.monsoon.s1next.singleton;

import android.content.Intent;
import android.text.TextUtils;

import cl.monsoon.s1next.MyApplication;

/**
 * Current user.
 */
public enum MyAccount {
    INSTANCE;

    public static final String ACTION_USER_LOGIN = "user_login";
    public static final String ACTION_USER_COOKIE_EXPIRATION = "user_cookie_expiration";

    private volatile String uid;
    private volatile String name;
    private volatile String authenticityToken;
    private volatile int permission;

    public static String getUid() {
        return INSTANCE.uid;
    }

    public static void setUid(String uid) {
        INSTANCE.uid = uid;
    }

    public static String getName() {
        return INSTANCE.name;
    }

    public static void setName(String name) {
        INSTANCE.name = name;
    }

    public static String getAuthenticityToken() {
        return INSTANCE.authenticityToken;
    }

    public static void setAuthenticityToken(String authenticityToken) {
        INSTANCE.authenticityToken = authenticityToken;
    }

    public static int getPermission() {
        return INSTANCE.permission;
    }

    public static void setPermission(int permission) {
        INSTANCE.permission = permission;
    }

    public static boolean isLoggedIn() {
        return !TextUtils.isEmpty(INSTANCE.name);
    }

    public static void clear() {
        INSTANCE.name = INSTANCE.uid = INSTANCE.authenticityToken = null;
        INSTANCE.permission = 0;
    }

    public static void sendLoginBroadcast() {
        MyApplication.getContext().sendBroadcast(new Intent(ACTION_USER_LOGIN));
    }

    public static void sendCookieExpirationBroadcast() {
        MyApplication.getContext().sendBroadcast(new Intent(ACTION_USER_COOKIE_EXPIRATION));
    }

    public static interface OnLogoutListener {

        public void onLogout();
    }
}
