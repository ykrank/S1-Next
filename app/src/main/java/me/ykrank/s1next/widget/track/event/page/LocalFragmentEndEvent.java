package me.ykrank.s1next.widget.track.event.page;

import android.app.Fragment;

import me.ykrank.s1next.widget.track.event.TrackEvent;

public class LocalFragmentEndEvent extends TrackEvent {
    private Fragment fragment;

    public LocalFragmentEndEvent(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
