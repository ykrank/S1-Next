package com.github.ykrank.androidtools.widget.track

import android.content.Context
import android.os.HandlerThread
import android.os.Looper
import android.text.TextUtils
import com.github.ykrank.androidtools.data.TrackUser
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.track.event.TrackEvent
import com.github.ykrank.androidtools.widget.track.event.page.*
import com.github.ykrank.androidtools.widget.track.talkingdata.TalkingDataAgent
import com.github.ykrank.androidtools.widget.track.trackhandler.DefaultTrackHandler
import com.github.ykrank.androidtools.widget.track.trackhandler.TrackHandler
import com.github.ykrank.androidtools.widget.track.trackhandler.page.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by ykrank on 2016/12/27.
 */

class DataTrackAgent {

    private val mThread = HandlerThread(THREAD_NAME)
    private var looper: Looper? = null
    private val handlerMapper = ConcurrentHashMap<Class<out TrackEvent>, TrackHandler<out TrackEvent>>()
    private var defaultHandler: TrackHandler<TrackEvent>? = null
    private var mUser: TrackUser? = null
    private lateinit var agent: TrackAgent

    fun init(context: Context) {
        agent = TalkingDataAgent()
        mThread.start()
        looper = mThread.looper

        agent.init(context.applicationContext)
        setDefaultHandler(DefaultTrackHandler(context.applicationContext, agent))
        regHandler(PageStartEvent::class.java, PageStartTrackHandler(agent))
        regHandler(PageEndEvent::class.java, PageEndTrackHandler(agent))
        regHandler(ActivityStartEvent::class.java, ActivityStartTrackHandler(agent))
        regHandler(ActivityEndEvent::class.java, ActivityEndTrackHandler(agent))
        regHandler(FragmentStartEvent::class.java, FragmentStartTrackHandler(agent))
        regHandler(FragmentEndEvent::class.java, FragmentEndTrackHandler(agent))
        regHandler(LocalFragmentStartEvent::class.java, LocalFragmentStartTrackHandler(agent))
        regHandler(LocalFragmentEndEvent::class.java, LocalFragmentEndTrackHandler(agent))
    }

    fun setUser(user: TrackUser) {
        if (mUser == null || !TextUtils.equals(mUser?.uid, user.uid)) {
            mUser = user
            agent.setUser(user)
        }
    }

    fun <T : TrackEvent> regHandler(eventType: Class<T>, handler: TrackHandler<T>) {
        handlerMapper.put(eventType, handler)
    }

    fun setDefaultHandler(handler: TrackHandler<TrackEvent>?) {
        if (handler !== defaultHandler) {
            defaultHandler = handler
        }
    }

    @SuppressWarnings("unchecked")
    fun post(event: TrackEvent) {
        val handler = handlerMapper[event.eventType]
        if (handler != null) {
            handler as TrackHandler<TrackEvent>
            handler.track(looper, event)
        } else if (defaultHandler != null) {
            defaultHandler?.track(looper, event)
        } else {
            L.report(IllegalStateException("TrackEvent not handler:" + event))
        }
    }

    companion object {
        private val TAG = "DataTrackAgent"
        private val THREAD_NAME = "EventTrackThread"
    }
}
