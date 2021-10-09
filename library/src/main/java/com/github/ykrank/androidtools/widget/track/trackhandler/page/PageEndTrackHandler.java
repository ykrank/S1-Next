package com.github.ykrank.androidtools.widget.track.trackhandler.page;

import androidx.annotation.NonNull;

import com.github.ykrank.androidtools.widget.track.TrackAgent;
import com.github.ykrank.androidtools.widget.track.event.page.PageEndEvent;
import com.github.ykrank.androidtools.widget.track.trackhandler.ContextTrackHandlerImp;

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
