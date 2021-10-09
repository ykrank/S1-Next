package com.github.ykrank.androidtools.widget.track.talkingdata

import android.app.Activity
import android.content.Context

import com.github.ykrank.androidtools.data.TrackUser
import com.github.ykrank.androidtools.widget.track.TrackAgent
import com.tendcloud.tenddata.TCAgent

/**
 * Created by ykrank on 2016/12/28.
 * Agent for talking data proxy
 */

class TalkingDataAgent : TrackAgent {

    override fun init(context: Context) {
        TCAgent.LOG_ON = false
        TCAgent.init(context)
        TCAgent.setReportUncaughtExceptions(false)
    }

    override fun setUser(user: TrackUser) {
        TCAgent.setGlobalKV("UserName", user.name)
        TCAgent.setGlobalKV("Uid", user.uid)
        TCAgent.setGlobalKV("Permission", user.permission.toString())
        user.extras?.forEach { TCAgent.setGlobalKV(it.key, it.value) }
    }

    override fun onResume(activity: Activity) {
        TCAgent.onPageStart(activity, activity.localClassName)
    }

    override fun onPause(activity: Activity) {
        TCAgent.onPageEnd(activity, activity.localClassName)
    }

    override fun onPageStart(context: Context, string: String) {
        TCAgent.onPageStart(context, string)
    }

    override fun onPageEnd(context: Context, string: String) {
        TCAgent.onPageEnd(context, string)
    }

    override fun onEvent(context: Context, name: String, label: String, data: Map<String, String?>) {
        TCAgent.onEvent(context, name, label, data)
    }
}
