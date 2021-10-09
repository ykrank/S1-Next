package com.github.ykrank.androidtools.widget.track.trackhandler.page;

import androidx.annotation.NonNull;

import com.github.ykrank.androidtools.util.ContextUtils;
import com.github.ykrank.androidtools.widget.track.TrackAgent;
import com.github.ykrank.androidtools.widget.track.event.page.LocalFragmentEndEvent;
import com.github.ykrank.androidtools.widget.track.trackhandler.ContextTrackHandlerImp;

/**
 * Created by ykrank on 2016/12/29.
 */

public class LocalFragmentEndTrackHandler extends ContextTrackHandlerImp<LocalFragmentEndEvent> {

    public LocalFragmentEndTrackHandler(@NonNull TrackAgent agent) {
        super(agent);
    }

    @Override
    public boolean trackEvent(LocalFragmentEndEvent event) {
        agent.onPageEnd(event.getFragment().getActivity(), ContextUtils.getLocalClassName(event.getFragment()));
        return true;
    }
}
