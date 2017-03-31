package me.ykrank.s1next.data.api.model;

import com.google.common.base.Objects;

/**
 * Created by ykrank on 2017/04/01.
 */

public class UserSearchResult {

    private String uid;
    private String name;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSearchResult that = (UserSearchResult) o;
        return Objects.equal(uid, that.uid) &&
                Objects.equal(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uid, name);
    }

    @Override
    public String toString() {
        return "UserSearchResult{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
