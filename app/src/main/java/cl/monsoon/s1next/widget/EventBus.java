package cl.monsoon.s1next.widget;

import android.support.annotation.MainThread;

import cl.monsoon.s1next.util.LooperUtil;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * See https://code.google.com/p/guava-libraries/wiki/EventBusExplained
 * <p>
 * Forked from http://nerds.weddingpartyapp.com/tech/2014/12/24/implementing-an-event-bus-with-rxjava-rxbus/
 */
public final class EventBus {

    private final Subject<Object, Object> eventBus = PublishSubject.create();

    @MainThread
    public void post(Object o) {
        LooperUtil.enforceOnMainThread();
        eventBus.onNext(o);
    }

    @MainThread
    public Observable<Object> get() {
        LooperUtil.enforceOnMainThread();
        return eventBus;
    }
}
