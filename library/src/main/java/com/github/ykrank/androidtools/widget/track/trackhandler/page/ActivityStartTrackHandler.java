package com.github.ykrank.androidtools.widget.track.trackhandler.page;

import androidx.annotation.NonNull;

import com.github.ykrank.androidtools.widget.track.TrackAgent;
import com.github.ykrank.androidtools.widget.track.event.page.ActivityStartEvent;
import com.github.ykrank.androidtools.widget.track.trackhandler.ContextTrackHandlerImp;

/**
 * Created by ykrank on 2016/12/29.
 */

public class ActivityStartTrackHandler extends ContextTrackHandlerImp<ActivityStartEvent> {

    public ActivityStartTrackHandler(@NonNull TrackAgent agent) {
        super(agent);
    }

    @Override
    public boolean trackEvent(ActivityStartEvent event) {
        agent.onResume(event.getActivity());
        return true;
    }
}
