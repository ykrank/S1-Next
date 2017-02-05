package me.ykrank.s1next.widget.track.event;

/**
 * Created by ykrank on 2016/12/29.
 */

public class ViewUserReplyTrackEvent extends TrackEvent {

    public ViewUserReplyTrackEvent(String id, String name) {
        setGroup("浏览个人回复列表");
        addData("id", id);
        addData("name", name);
    }
}
