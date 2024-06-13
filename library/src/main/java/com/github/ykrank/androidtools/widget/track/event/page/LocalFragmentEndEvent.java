package com.github.ykrank.androidtools.widget.track.event.page;


import androidx.fragment.app.Fragment;

import com.github.ykrank.androidtools.widget.track.event.TrackEvent;

public class LocalFragmentEndEvent extends TrackEvent {
    private Fragment fragment;

    public LocalFragmentEndEvent(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
