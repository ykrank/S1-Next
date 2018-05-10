package me.ykrank.s1next.data.api.model.collection;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ykrank.androidtools.guava.Objects;

import java.util.List;

import me.ykrank.s1next.data.api.model.Account;
import me.ykrank.s1next.data.api.model.Friend;

/**
 * Created by ykrank on 2017/1/16.
 */

public class Friends extends Account {
    @Nullable
    @JsonProperty("list")
    private List<Friend> friendList;

    @Nullable
    public List<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(@Nullable List<Friend> friendList) {
        this.friendList = friendList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friends)) return false;
        if (!super.equals(o)) return false;
        Friends friends = (Friends) o;
        return Objects.equal(friendList, friends.friendList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), friendList);
    }
}
