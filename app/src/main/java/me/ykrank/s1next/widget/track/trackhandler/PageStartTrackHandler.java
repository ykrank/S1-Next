package me.ykrank.s1next.widget.track.trackhandler;

import com.tendcloud.tenddata.TCAgent;

import me.ykrank.s1next.App;
import me.ykrank.s1next.widget.track.event.PageStartEvent;

/**
 * Created by ykrank on 2016/12/28.
 */

public class PageStartTrackHandler extends TrackHandlerImp<PageStartEvent> {

    @Override
    public boolean trackEvent(PageStartEvent event) {
        TCAgent.onPageStart(App.get(), event.getPageName());
        return true;
    }
}
