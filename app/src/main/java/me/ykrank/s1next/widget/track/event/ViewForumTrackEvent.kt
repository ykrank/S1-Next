package me.ykrank.s1next.widget.track.event

import com.github.ykrank.androidtools.widget.track.event.TrackEvent

/**
 * Created by ykrank on 2016/12/29.
 */

class ViewForumTrackEvent(id: String, name: String) : TrackEvent() {

    init {
        group = "浏览版块"
        addData("id", id)
        addData("name", name)
    }
}
