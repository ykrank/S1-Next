package me.ykrank.s1next.widget.track.event

import com.github.ykrank.androidtools.widget.track.event.TrackEvent

/**
 * Created by ykrank on 2016/12/29.
 */

class LargeImageTrackEvent(image: String, thumb: String?) : TrackEvent() {

    init {
        group = "切换显示大图"
        addData("Image", image)
        addData("Thumb", thumb)
    }
}
