package com.github.ykrank.androidtools.ui.adapter.delegate.item

import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import java.text.DecimalFormat

class ProgressItem :StableIdModel{
    val max: Long
    val current: Long
    val finished: Boolean

    constructor() : this(0, 0, false)

    constructor(max: Long, current: Long, finished: Boolean) {
        this.max = max
        this.current = current
        this.finished = finished
    }

    override val stableId: Long
        get() = StableIdModel.ID_PROGRESS

    val kbStr: String
        get() = "${kb(current)}K / ${kb(max)}K"

    companion object {
        val df = DecimalFormat("###.0")

        fun kb(byte: Long): String? {
            return df.format((byte / 1024.0))
        }
    }
}
