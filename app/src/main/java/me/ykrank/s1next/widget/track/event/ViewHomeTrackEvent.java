package me.ykrank.s1next.widget.track.event;

/**
 * Created by ykrank on 2016/12/29.
 */

public class ViewHomeTrackEvent extends TrackEvent {

    public ViewHomeTrackEvent(String id, String name) {
        setGroup("浏览个人主页");
        addData("id", id);
        addData("name", name);
    }
}
