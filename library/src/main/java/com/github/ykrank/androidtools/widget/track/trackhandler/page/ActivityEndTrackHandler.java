package com.github.ykrank.androidtools.widget.track.trackhandler.page;

import androidx.annotation.NonNull;

import com.github.ykrank.androidtools.widget.track.TrackAgent;
import com.github.ykrank.androidtools.widget.track.event.page.ActivityEndEvent;
import com.github.ykrank.androidtools.widget.track.trackhandler.ContextTrackHandlerImp;

/**
 * Created by ykrank on 2016/12/29.
 */

public class ActivityEndTrackHandler extends ContextTrackHandlerImp<ActivityEndEvent> {

    public ActivityEndTrackHandler(@NonNull TrackAgent agent) {
        super(agent);
    }

    @Override
    public boolean trackEvent(ActivityEndEvent event) {
        agent.onPause(event.getActivity());
        return true;
    }
}
