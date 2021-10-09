package com.github.ykrank.androidtools.widget.track.trackhandler

import android.content.Context
import com.github.ykrank.androidtools.widget.track.TrackAgent
import com.github.ykrank.androidtools.widget.track.event.TrackEvent

/**
 * Created by ykrank on 2016/12/27.
 */

class DefaultTrackHandler(private val context: Context, agent: TrackAgent) : TrackHandlerImp<TrackEvent>(agent) {

    override fun trackEvent(event: TrackEvent?): Boolean {
        if (event != null) {
            var name = event.group
            var label = event.name
            if (name == null) {
                if (label != null) {
                    name = label
                    label = ""
                } else {
                    return false
                }
            }
            if (label == null) {
                label = ""
            }
            agent.onEvent(context, name, label, event.getData())
            return true
        }
        return false
    }
}
