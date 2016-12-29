package me.ykrank.s1next.widget.track.event;

/**
 * Created by ykrank on 2016/12/29.
 */

public class AddFavoriteTrackEvent extends TrackEvent {

    public AddFavoriteTrackEvent(String threadId) {
        setGroup("添加收藏");
        addData("ThreadId", threadId);
    }
}
