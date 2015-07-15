package cl.monsoon.s1next.data;

public class User {

    private volatile String uid;

    private volatile String name;

    private volatile int permission;

    private volatile String authenticityToken;

    private volatile boolean logged;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getAuthenticityToken() {
        return authenticityToken;
    }

    public void setAuthenticityToken(String authenticityToken) {
        this.authenticityToken = authenticityToken;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }
}
