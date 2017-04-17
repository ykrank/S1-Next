package me.ykrank.s1next.widget.track.trackhandler.page;

import android.support.annotation.NonNull;

import me.ykrank.s1next.util.ContextUtils;
import me.ykrank.s1next.widget.track.TrackAgent;
import me.ykrank.s1next.widget.track.event.page.LocalFragmentStartEvent;
import me.ykrank.s1next.widget.track.trackhandler.ContextTrackHandlerImp;

/**
 * Created by ykrank on 2016/12/29.
 */

public class LocalFragmentStartTrackHandler extends ContextTrackHandlerImp<LocalFragmentStartEvent> {

    public LocalFragmentStartTrackHandler(@NonNull TrackAgent agent) {
        super(agent);
    }

    @Override
    public boolean trackEvent(LocalFragmentStartEvent event) {
        agent.onPageStart(event.getFragment().getActivity(), ContextUtils.getLocalClassName(event.getFragment()));
        return true;
    }
}
