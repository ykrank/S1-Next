package com.github.ykrank.androidtools.widget.track

import android.app.Activity
import android.content.Context

import com.github.ykrank.androidtools.data.TrackUser

/**
 * Created by ykrank on 2016/12/29.
 */

interface TrackAgent {
    fun init(context: Context)

    fun setUser(user: TrackUser)

    fun onResume(activity: Activity)

    fun onPause(activity: Activity)

    fun onPageStart(context: Context, string: String)

    fun onPageEnd(context: Context, string: String)

    fun onEvent(context: Context, name: String, label: String, data: Map<String, String?>)
}
