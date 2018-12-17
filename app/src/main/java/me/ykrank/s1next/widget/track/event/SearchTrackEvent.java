package me.ykrank.s1next.widget.track.event;

import com.github.ykrank.androidtools.widget.track.event.TrackEvent;

/**
 * Created by ykrank on 2016/12/28.
 */

public class SearchTrackEvent extends TrackEvent {

    public SearchTrackEvent(String query) {
        setGroup("搜索");
        addData("query", query);
    }
}
