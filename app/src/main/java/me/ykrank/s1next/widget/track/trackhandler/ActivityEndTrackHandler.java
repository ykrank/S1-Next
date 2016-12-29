package me.ykrank.s1next.widget.track.trackhandler;

import android.support.annotation.NonNull;

import me.ykrank.s1next.widget.track.TrackAgent;
import me.ykrank.s1next.widget.track.event.page.ActivityEndEvent;

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
