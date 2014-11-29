package cl.monsoon.s1next.singleton;

/**
 * Current user.
 */
public enum User {
    INSTANCE;

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

    public static interface OnLogoutListener {

        public void onLogout();
    }
}
