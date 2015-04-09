package cl.monsoon.s1next.singleton;

import android.text.TextUtils;

import cl.monsoon.s1next.event.UserStatusEvent;

/**
 * Current user.
 */
public enum User {
    INSTANCE;

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

    public static boolean hasLoggedIn() {
        return !TextUtils.isEmpty(INSTANCE.uid) && !TextUtils.isEmpty(INSTANCE.name);
    }

    public static void reset() {
        INSTANCE.name = INSTANCE.uid = INSTANCE.authenticityToken = null;
        INSTANCE.permission = 0;
    }

    public static void postLoginEvent() {
        BusProvider.get().post(new UserStatusEvent(UserStatusEvent.USER_LOGIN));
    }

    public static void sendCookieExpirationEvent() {
        BusProvider.get().post(new UserStatusEvent(UserStatusEvent.USER_COOKIE_EXPIRATION));
    }
}
