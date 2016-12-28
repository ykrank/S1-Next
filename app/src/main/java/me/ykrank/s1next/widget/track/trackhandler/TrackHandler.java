package me.ykrank.s1next.widget.track.trackhandler;

import android.os.Looper;

/**
 * Created by ykrank on 2016/12/27.
 */

public interface TrackHandler<T> {

    void track(Looper looper, T eventType);
}
