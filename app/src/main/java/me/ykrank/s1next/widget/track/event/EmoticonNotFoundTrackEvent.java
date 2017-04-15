package me.ykrank.s1next.widget.track.event;

public class EmoticonNotFoundTrackEvent extends TrackEvent {

    public EmoticonNotFoundTrackEvent(String uri) {
        setGroup("未知表情");
        setName(uri);
    }
}
