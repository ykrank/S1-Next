package me.ykrank.s1next.data.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ykrank.androidtools.guava.Objects;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

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

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthenticityToken() {
        return authenticityToken;
    }

    public void setAuthenticityToken(String authenticityToken) {
        this.authenticityToken = authenticityToken;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equal(permission, account.permission) &&
                Objects.equal(uid, account.uid) &&
                Objects.equal(username, account.username) &&
                Objects.equal(authenticityToken, account.authenticityToken);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uid, username, authenticityToken, permission);
    }

    @Override
    public String toString() {
        return "Account{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", authenticityToken='" + authenticityToken + '\'' +
                ", permission=" + permission +
                '}';
    }
}
