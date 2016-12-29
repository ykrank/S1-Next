package me.ykrank.s1next.widget.track.trackhandler;

import android.support.annotation.NonNull;

import me.ykrank.s1next.widget.track.TrackAgent;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;

/**
 * Created by ykrank on 2016/12/28.
 */

public class PageEndTrackHandler extends ContextTrackHandlerImp<PageEndEvent> {

    public PageEndTrackHandler(@NonNull TrackAgent agent) {
        super(agent);
    }

    @Override
    public boolean trackEvent(PageEndEvent event) {
        agent.onPageEnd(event.getContext(), event.getPageName());
        return true;
    }
}
