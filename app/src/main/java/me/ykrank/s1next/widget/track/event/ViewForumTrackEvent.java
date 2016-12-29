package me.ykrank.s1next.widget.track.event;

/**
 * Created by ykrank on 2016/12/29.
 */

public class ViewForumTrackEvent extends TrackEvent {

    public ViewForumTrackEvent(String id, String name) {
        setGroup("浏览版块");
        addData("id", id);
        addData("name", name);
    }
}
