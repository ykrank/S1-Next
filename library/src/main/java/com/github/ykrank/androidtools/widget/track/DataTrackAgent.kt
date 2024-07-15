package com.github.ykrank.androidtools.widget.track

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.text.TextUtils
import com.github.ykrank.androidtools.data.TrackUser
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.track.event.TrackEvent
import com.github.ykrank.androidtools.widget.track.event.page.ActivityEndEvent
import com.github.ykrank.androidtools.widget.track.event.page.ActivityStartEvent
import com.github.ykrank.androidtools.widget.track.event.page.FragmentEndEvent
import com.github.ykrank.androidtools.widget.track.event.page.FragmentStartEvent
import com.github.ykrank.androidtools.widget.track.event.page.LocalFragmentEndEvent
import com.github.ykrank.androidtools.widget.track.event.page.LocalFragmentStartEvent
import com.github.ykrank.androidtools.widget.track.event.page.PageEndEvent
import com.github.ykrank.androidtools.widget.track.event.page.PageStartEvent
import com.github.ykrank.androidtools.widget.track.talkingdata.TalkingDataAgent
import com.github.ykrank.androidtools.widget.track.trackhandler.DefaultTrackHandler
import com.github.ykrank.androidtools.widget.track.trackhandler.TrackHandler
import com.github.ykrank.androidtools.widget.track.trackhandler.page.ActivityEndTrackHandler
import com.github.ykrank.androidtools.widget.track.trackhandler.page.ActivityStartTrackHandler
import com.github.ykrank.androidtools.widget.track.trackhandler.page.FragmentEndTrackHandler
import com.github.ykrank.androidtools.widget.track.trackhandler.page.FragmentStartTrackHandler
import com.github.ykrank.androidtools.widget.track.trackhandler.page.LocalFragmentEndTrackHandler
import com.github.ykrank.androidtools.widget.track.trackhandler.page.LocalFragmentStartTrackHandler
import com.github.ykrank.androidtools.widget.track.trackhandler.page.PageEndTrackHandler
import com.github.ykrank.androidtools.widget.track.trackhandler.page.PageStartTrackHandler
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by ykrank on 2016/12/27.
 */

class DataTrackAgent {

    private val mThread = HandlerThread(THREAD_NAME)
    private var looper: Looper? = null
    private var mHandler: Handler? = null
    private val handlerMapper = ConcurrentHashMap<Class<out TrackEvent>, TrackHandler<out TrackEvent>>()
    private var defaultHandler: TrackHandler<TrackEvent>? = null
    private var mUser: TrackUser? = null
    private lateinit var agent: TrackAgent

    fun init(context: Context) {
        agent = TalkingDataAgent()
        mThread.start()
        looper = mThread.looper
        mHandler = Handler(mThread.looper)

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
        val threadHandler = mHandler
        if (threadHandler == null) {
            L.report(IllegalStateException("TrackEvent no threadHandler:$event"))
            return
        }
        val handler = handlerMapper[event.eventType]
        if (handler != null) {
            handler as TrackHandler<TrackEvent>
            handler.track(threadHandler, event)
        } else if (defaultHandler != null) {
            defaultHandler?.track(threadHandler, event)
        } else {
            L.report(IllegalStateException("TrackEvent not handler:$event"))
        }
    }

    companion object {
        private val TAG = "DataTrackAgent"
        private val THREAD_NAME = "EventTrackThread"
    }
}
