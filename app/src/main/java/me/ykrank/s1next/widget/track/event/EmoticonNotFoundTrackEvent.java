package me.ykrank.s1next.widget.track.event;

import com.github.ykrank.androidtools.widget.track.event.TrackEvent;

public class EmoticonNotFoundTrackEvent extends TrackEvent {

    public EmoticonNotFoundTrackEvent(String uri) {
        setGroup("未知表情");
        setName(uri);
    }
}
