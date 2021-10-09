package com.github.ykrank.androidtools.widget.track.trackhandler.page;

import androidx.annotation.NonNull;

import com.github.ykrank.androidtools.util.ContextUtils;
import com.github.ykrank.androidtools.widget.track.TrackAgent;
import com.github.ykrank.androidtools.widget.track.event.page.FragmentEndEvent;
import com.github.ykrank.androidtools.widget.track.trackhandler.ContextTrackHandlerImp;

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
