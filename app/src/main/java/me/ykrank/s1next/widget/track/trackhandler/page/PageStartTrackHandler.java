package me.ykrank.s1next.widget.track.trackhandler.page;

import android.support.annotation.NonNull;

import me.ykrank.s1next.widget.track.TrackAgent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;
import me.ykrank.s1next.widget.track.trackhandler.ContextTrackHandlerImp;

/**
 * Created by ykrank on 2016/12/28.
 */

public class PageStartTrackHandler extends ContextTrackHandlerImp<PageStartEvent> {

    public PageStartTrackHandler(@NonNull TrackAgent agent) {
        super(agent);
    }

    @Override
    public boolean trackEvent(PageStartEvent event) {
        agent.onPageStart(event.getContext(), event.getPageName());
        return true;
    }
}
