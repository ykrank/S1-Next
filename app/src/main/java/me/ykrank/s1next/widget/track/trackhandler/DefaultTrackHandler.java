package me.ykrank.s1next.widget.track.trackhandler;

import android.support.annotation.NonNull;

import me.ykrank.s1next.App;
import me.ykrank.s1next.widget.track.TrackAgent;
import me.ykrank.s1next.widget.track.event.TrackEvent;

/**
 * Created by ykrank on 2016/12/27.
 */

public class DefaultTrackHandler extends TrackHandlerImp<TrackEvent> {

    public DefaultTrackHandler(@NonNull TrackAgent agent) {
        super(agent);
    }

    @Override
    public boolean trackEvent(TrackEvent event) {
        if (event != null) {
            String name = event.getGroup();
            String label = event.getName();
            if (name == null) {
                if (label != null) {
                    name = label;
                    label = "";
                } else {
                    return false;
                }
            }
            if (label == null) {
                label = "";
            }
            agent.onEvent(App.get(), name, label, event.getData());
            return true;
        }
        return false;
    }
}
