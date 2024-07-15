package com.github.ykrank.androidtools.widget.track.trackhandler

import android.os.Handler

/**
 * Created by ykrank on 2016/12/27.
 */
interface TrackHandler<T> {
    fun track(handler: Handler, eventType: T)
}
