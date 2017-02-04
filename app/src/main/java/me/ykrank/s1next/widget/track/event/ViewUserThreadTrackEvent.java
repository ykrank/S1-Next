package me.ykrank.s1next.widget.track.event;

/**
 * Created by ykrank on 2016/12/29.
 */

public class ViewUserThreadTrackEvent extends TrackEvent {

    public ViewUserThreadTrackEvent(String id, String name) {
        setGroup("浏览个人发帖列表");
        addData("id", id);
        addData("name", name);
    }
}
