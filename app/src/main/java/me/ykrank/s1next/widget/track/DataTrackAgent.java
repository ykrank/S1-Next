package me.ykrank.s1next.widget.track;

import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.concurrent.ConcurrentHashMap;

import me.ykrank.s1next.data.User;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.widget.track.event.TrackEvent;
import me.ykrank.s1next.widget.track.event.page.ActivityEndEvent;
import me.ykrank.s1next.widget.track.event.page.ActivityStartEvent;
import me.ykrank.s1next.widget.track.event.page.FragmentEndEvent;
import me.ykrank.s1next.widget.track.event.page.FragmentStartEvent;
import me.ykrank.s1next.widget.track.event.page.LocalFragmentEndEvent;
import me.ykrank.s1next.widget.track.event.page.LocalFragmentStartEvent;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;
import me.ykrank.s1next.widget.track.talkingdata.TalkingDataAgent;
import me.ykrank.s1next.widget.track.trackhandler.DefaultTrackHandler;
import me.ykrank.s1next.widget.track.trackhandler.TrackHandler;
import me.ykrank.s1next.widget.track.trackhandler.page.ActivityEndTrackHandler;
import me.ykrank.s1next.widget.track.trackhandler.page.ActivityStartTrackHandler;
import me.ykrank.s1next.widget.track.trackhandler.page.FragmentEndTrackHandler;
import me.ykrank.s1next.widget.track.trackhandler.page.FragmentStartTrackHandler;
import me.ykrank.s1next.widget.track.trackhandler.page.LocalFragmentEndTrackHandler;
import me.ykrank.s1next.widget.track.trackhandler.page.LocalFragmentStartTrackHandler;
import me.ykrank.s1next.widget.track.trackhandler.page.PageEndTrackHandler;
import me.ykrank.s1next.widget.track.trackhandler.page.PageStartTrackHandler;

/**
 * Created by ykrank on 2016/12/27.
 */

public class DataTrackAgent {
    private static final String TAG = "DataTrackAgent";
    private static final String THREAD_NAME = "EventTrackThread";

    private HandlerThread mThread = new HandlerThread(THREAD_NAME);
    private Looper looper;
    private ConcurrentHashMap<Class, TrackHandler> handlerMapper = new ConcurrentHashMap<>();
    private TrackHandler<TrackEvent> defaultHandler;
    private User mUser;
    private TrackAgent agent;

    public void init() {
        agent = new TalkingDataAgent();
        mThread.start();
        looper = mThread.getLooper();

        agent.init();
        setDefaultHandler(new DefaultTrackHandler(agent));
        regHandler(PageStartEvent.class, new PageStartTrackHandler(agent));
        regHandler(PageEndEvent.class, new PageEndTrackHandler(agent));
        regHandler(ActivityStartEvent.class, new ActivityStartTrackHandler(agent));
        regHandler(ActivityEndEvent.class, new ActivityEndTrackHandler(agent));
        regHandler(FragmentStartEvent.class, new FragmentStartTrackHandler(agent));
        regHandler(FragmentEndEvent.class, new FragmentEndTrackHandler(agent));
        regHandler(LocalFragmentStartEvent.class, new LocalFragmentStartTrackHandler(agent));
        regHandler(LocalFragmentEndEvent.class, new LocalFragmentEndTrackHandler(agent));
    }

    public void setUser(@NonNull User user) {
        if (mUser == null || !TextUtils.equals(mUser.getUid(), user.getUid())) {
            mUser = user;
            agent.setUser(user);
        }
    }

    public <T> void regHandler(Class<T> eventType, TrackHandler<T> handler) {
        handlerMapper.put(eventType, handler);
    }

    public void setDefaultHandler(@Nullable TrackHandler<TrackEvent> handler) {
        if (handler != defaultHandler) {
            defaultHandler = handler;
        }
    }

    @SuppressWarnings("unchecked")
    public void post(@NonNull TrackEvent event) {
        TrackHandler handler = handlerMapper.get(event.getEventType());
        if (handler != null) {
            handler.track(looper, event);
        } else if (defaultHandler != null) {
            defaultHandler.track(looper, event);
        } else {
            L.report(new IllegalStateException("TrackEvent not handler:" + event));
        }
    }
}
