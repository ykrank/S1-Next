package me.ykrank.s1next.data.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ykrank.androidtools.ui.adapter.model.SameItem;
import com.google.common.base.Objects;

/**
 * Created by ykrank on 2017/1/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Friend implements SameItem {
    @JsonProperty("uid")
    private String uid;
    @JsonProperty("username")
    private String username;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friend)) return false;
        Friend friend = (Friend) o;
        return Objects.equal(uid, friend.uid) &&
                Objects.equal(username, friend.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uid, username);
    }

    @Override
    public boolean isSameItem(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friend)) return false;
        Friend friend = (Friend) o;
        return Objects.equal(uid, friend.uid);
    }
}
