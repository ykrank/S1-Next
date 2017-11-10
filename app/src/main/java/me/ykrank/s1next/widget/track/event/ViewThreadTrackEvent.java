package me.ykrank.s1next.widget.track.event;

import com.github.ykrank.androidtools.widget.track.event.TrackEvent;

/**
 * Created by ykrank on 2016/12/29.
 */

public class ViewThreadTrackEvent extends TrackEvent {

    public ViewThreadTrackEvent(String title, String threadId) {
        setGroup("浏览帖子");
        addData("title", title);
        addData("ThreadId", threadId);
    }
}
