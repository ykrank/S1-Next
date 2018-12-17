package me.ykrank.s1next.widget.track.event;

import com.github.ykrank.androidtools.widget.track.event.TrackEvent;

/**
 * Created by ykrank on 2016/12/28.
 */

public class BlackListTrackEvent extends TrackEvent {

    public BlackListTrackEvent(boolean isAdd, String authorId, String authorName) {
        setGroup("黑名单");
        if (isAdd) {
            setName("添加黑名单");
        } else {
            setName("删除黑名单");
        }
        addData("id", authorId);
        addData("name", authorName);
    }
}
