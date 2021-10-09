package com.github.ykrank.androidtools.widget.track.trackhandler.page;

import androidx.annotation.NonNull;

import com.github.ykrank.androidtools.util.ContextUtils;
import com.github.ykrank.androidtools.widget.track.TrackAgent;
import com.github.ykrank.androidtools.widget.track.event.page.LocalFragmentStartEvent;
import com.github.ykrank.androidtools.widget.track.trackhandler.ContextTrackHandlerImp;

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
