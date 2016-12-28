package me.ykrank.s1next.widget.track.trackhandler;

import com.tendcloud.tenddata.TCAgent;

import me.ykrank.s1next.App;
import me.ykrank.s1next.widget.track.event.TrackEvent;

/**
 * Created by ykrank on 2016/12/27.
 */

public class DefaultTrackHandler extends TrackHandlerImp<TrackEvent> {

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
            TCAgent.onEvent(App.get(), name, label, event.getData());
            return true;
        }
        return false;
    }
}
