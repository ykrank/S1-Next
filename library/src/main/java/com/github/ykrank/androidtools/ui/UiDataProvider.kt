package com.github.ykrank.androidtools.ui

import com.github.ykrank.androidtools.widget.net.WifiActivityLifecycleCallbacks
import com.github.ykrank.androidtools.widget.track.DataTrackAgent

/**
 * Created by ykrank on 2017/11/6.
 */
interface UiDataProvider {
    val actLifeCallback: WifiActivityLifecycleCallbacks?
    val trackAgent: DataTrackAgent?
}