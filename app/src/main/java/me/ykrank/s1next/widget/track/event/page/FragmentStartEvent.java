package me.ykrank.s1next.widget.track.event.page;

import android.support.v4.app.Fragment;

import me.ykrank.s1next.widget.track.event.TrackEvent;

public class FragmentStartEvent extends TrackEvent {
    private Fragment fragment;

    public FragmentStartEvent(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
