package cl.monsoon.s1next.widget;

import android.support.annotation.UiThread;

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

    @UiThread
    public void post(Object o) {
        eventBus.onNext(o);
    }

    @UiThread
    public Observable<Object> get() {
        return eventBus;
    }
}
