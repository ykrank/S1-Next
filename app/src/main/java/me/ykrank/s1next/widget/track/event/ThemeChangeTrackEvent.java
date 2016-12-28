package me.ykrank.s1next.widget.track.event;

/**
 * Created by ykrank on 2016/12/28.
 */

public class ThemeChangeTrackEvent extends TrackEvent {

    /**
     * 主题切换事件
     *
     * @param fromAvatar 是否是通过点击头像
     */
    public ThemeChangeTrackEvent(boolean fromAvatar) {
        setGroup("切换主题");
        if (fromAvatar) {
            setName("点击头像");
        } else {
            setName("设置中切换");
        }
    }
}
