package me.ykrank.s1next.widget.track.event;

/**
 * Created by ykrank on 2016/12/29.
 */

public class ViewUserFriendsTrackEvent extends TrackEvent {

    public ViewUserFriendsTrackEvent(String id, String name) {
        setGroup("浏览个人好友列表");
        addData("id", id);
        addData("name", name);
    }
}
