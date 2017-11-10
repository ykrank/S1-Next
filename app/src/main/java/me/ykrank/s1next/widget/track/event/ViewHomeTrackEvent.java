package me.ykrank.s1next.widget.track.event;

import com.github.ykrank.androidtools.widget.track.event.TrackEvent;

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
