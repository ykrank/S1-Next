package com.github.ykrank.androidtools.widget.track.trackhandler

import android.os.Handler
import androidx.annotation.WorkerThread
import com.github.ykrank.androidtools.widget.track.TrackAgent

/**
 * Created by ykrank on 2016/12/27.
 */
abstract class TrackHandlerImp<T> internal constructor(protected var agent: TrackAgent) :
    TrackHandler<T> {
    override fun track(handler: Handler, eventType: T) {
        handler.post {
            trackEvent(eventType)
        }
    }

    /**
     * event handle action. run on work thread.
     *
     * @param event Event
     * @return does action success
     */
    @WorkerThread
    abstract fun trackEvent(event: T?): Boolean
}
