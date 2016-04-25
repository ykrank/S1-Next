package me.ykrank.s1next.widget;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.ykrank.s1next.util.LooperUtil;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * See https://code.google.com/p/guava-libraries/wiki/EventBusExplained
 * <p>
 * Forked from https://github.com/wangjiegulu/RxAndroidEventsSample/blob/master/sample/src/main/java/com/wangjie/rxandroideventssample/rxbus/RxBus.java
 */
public final class EventBus {
    private static final String DEFAULT_TAG = "default_tag";

    private ConcurrentHashMap<Object, PublishSubject<Object>> subjectMapper = new ConcurrentHashMap<>();

    @MainThread
    public void post(@NonNull Object o) {
        post(DEFAULT_TAG, o);
    }

    @MainThread
    public void post(@NonNull Object tag, @NonNull Object o) {
        LooperUtil.enforceOnMainThread();
        PublishSubject<Object> eventBus = subjectMapper.get(tag);
        if (eventBus != null)
            eventBus.onNext(o);
    }

    @MainThread
    @NonNull
    public PublishSubject<Object> get() {
        return get(DEFAULT_TAG);
    }

    @MainThread
    @NonNull
    public PublishSubject<Object> get(@NonNull Object tag) {
        LooperUtil.enforceOnMainThread();
        PublishSubject<Object> subject = subjectMapper.get(tag);
        if (subject == null) {
            subject = PublishSubject.create();
            subjectMapper.put(tag, subject);
        }
        return subject;
    }

}
