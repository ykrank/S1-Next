package me.ykrank.s1next.widget.track.trackhandler;

import com.tendcloud.tenddata.TCAgent;

import me.ykrank.s1next.App;
import me.ykrank.s1next.widget.track.event.PageEndEvent;

/**
 * Created by ykrank on 2016/12/28.
 */

public class PageEndTrackHandler extends TrackHandlerImp<PageEndEvent> {

    @Override
    public boolean trackEvent(PageEndEvent event) {
        TCAgent.onPageEnd(App.get(), event.getPageName());
        return true;
    }
}
