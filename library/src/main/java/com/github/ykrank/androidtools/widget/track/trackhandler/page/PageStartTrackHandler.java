package com.github.ykrank.androidtools.widget.track.trackhandler.page;

import androidx.annotation.NonNull;

import com.github.ykrank.androidtools.widget.track.TrackAgent;
import com.github.ykrank.androidtools.widget.track.event.page.PageStartEvent;
import com.github.ykrank.androidtools.widget.track.trackhandler.ContextTrackHandlerImp;

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
