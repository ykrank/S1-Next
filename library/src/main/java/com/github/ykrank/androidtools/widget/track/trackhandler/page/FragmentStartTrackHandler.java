package com.github.ykrank.androidtools.widget.track.trackhandler.page;

import androidx.annotation.NonNull;

import com.github.ykrank.androidtools.util.ContextUtils;
import com.github.ykrank.androidtools.widget.track.TrackAgent;
import com.github.ykrank.androidtools.widget.track.event.page.FragmentStartEvent;
import com.github.ykrank.androidtools.widget.track.trackhandler.ContextTrackHandlerImp;

/**
 * Created by ykrank on 2016/12/29.
 */

public class FragmentStartTrackHandler extends ContextTrackHandlerImp<FragmentStartEvent> {

    public FragmentStartTrackHandler(@NonNull TrackAgent agent) {
        super(agent);
    }

    @Override
    public boolean trackEvent(FragmentStartEvent event) {
        agent.onPageStart(event.getFragment().getContext(), ContextUtils.getLocalClassName(event.getFragment()));
        return true;
    }
}
