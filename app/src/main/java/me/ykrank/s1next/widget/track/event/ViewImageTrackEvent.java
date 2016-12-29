package me.ykrank.s1next.widget.track.event;

import java.util.HashMap;
import java.util.Map;

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
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        setData(data);
    }
}
