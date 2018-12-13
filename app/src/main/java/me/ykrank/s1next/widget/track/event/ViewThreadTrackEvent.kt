package me.ykrank.s1next.widget.track.event

import com.github.ykrank.androidtools.widget.track.event.TrackEvent

/**
 * Created by ykrank on 2016/12/29.
 */

class ViewThreadTrackEvent(title: String?, threadId: String, extras: Map<String, String>? = null) : TrackEvent() {

    init {
        group = "浏览帖子"
        addData("title", title)
        addData("ThreadId", threadId)
        extras?.forEach { addData(it.key, it.value) }
    }
}
