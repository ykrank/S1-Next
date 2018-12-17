package me.ykrank.s1next.widget.track.event;

import com.github.ykrank.androidtools.widget.track.event.TrackEvent;

/**
 * Created by ykrank on 2016/12/28.
 */

public class ViewImageTrackEvent extends TrackEvent {

    public ViewImageTrackEvent(String url, boolean fromAvatar) {
        setGroup("图片浏览");
        if (fromAvatar) {
            setName("头像");
        } else {
            setName("帖子中图片");
        }
        addData("url", url);
    }
}
