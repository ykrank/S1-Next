package me.ykrank.s1next.widget.track.trackhandler;

import android.support.annotation.NonNull;

import me.ykrank.s1next.widget.track.TrackAgent;
import me.ykrank.s1next.widget.track.event.page.ActivityStartEvent;

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
