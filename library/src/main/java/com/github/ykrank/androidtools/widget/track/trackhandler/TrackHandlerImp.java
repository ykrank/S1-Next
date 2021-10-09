package com.github.ykrank.androidtools.widget.track.trackhandler;

import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.github.ykrank.androidtools.util.L;
import com.github.ykrank.androidtools.widget.track.TrackAgent;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by ykrank on 2016/12/27.
 */

public abstract class TrackHandlerImp<T> implements TrackHandler<T> {
    protected TrackAgent agent;

    TrackHandlerImp(@NonNull TrackAgent agent) {
        this.agent = agent;
    }

    @Override
    public final void track(Looper looper, T event) {
        Single.just(event)
                .map(this::trackEvent)
                .subscribeOn(AndroidSchedulers.from(looper))
                .subscribe(b -> L.d("track:" + b), L::report);
    }

    /**
     * event handle action. run on work thread.
     *
     * @param event Event
     * @return does action success
     */
    @WorkerThread
    public abstract boolean trackEvent(T event);
}
