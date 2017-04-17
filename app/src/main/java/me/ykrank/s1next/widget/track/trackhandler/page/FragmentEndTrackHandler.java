package me.ykrank.s1next.widget.track.trackhandler.page;

import android.support.annotation.NonNull;

import me.ykrank.s1next.util.ContextUtils;
import me.ykrank.s1next.widget.track.TrackAgent;
import me.ykrank.s1next.widget.track.event.page.FragmentEndEvent;
import me.ykrank.s1next.widget.track.trackhandler.ContextTrackHandlerImp;

/**
 * Created by ykrank on 2016/12/29.
 */

public class FragmentEndTrackHandler extends ContextTrackHandlerImp<FragmentEndEvent> {

    public FragmentEndTrackHandler(@NonNull TrackAgent agent) {
        super(agent);
    }

    @Override
    public boolean trackEvent(FragmentEndEvent event) {
        agent.onPageEnd(event.getFragment().getContext(), ContextUtils.getLocalClassName(event.getFragment()));
        return true;
    }
}
