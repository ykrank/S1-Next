package com.github.ykrank.androidtools.widget;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.github.ykrank.androidtools.util.LooperUtil;

import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.subjects.PublishSubject;

/**
 * See https://code.google.com/p/guava-libraries/wiki/EventBusExplained
 * <p>
 * Forked from https://github.com/wangjiegulu/RxAndroidEventsSample/blob/master/sample/src/main/java/com/wangjie/rxandroideventssample/rxbus/RxBus.java
 */
public final class RxBus {
    private static final String DEFAULT_TAG = "default_tag";

    private ConcurrentHashMap<Object, PublishSubject<Object>> subjectMapper = new ConcurrentHashMap<>();

    @MainThread
    public void post(@NonNull Object o) {
        LooperUtil.enforceOnMainThread();
        post(DEFAULT_TAG, o);
    }

    @AnyThread
    public void post(@NonNull Object tag, @NonNull Object o) {
        PublishSubject<Object> eventBus = subjectMapper.get(tag);
        if (eventBus != null) {
            try {
                eventBus.onNext(o);
            } catch (Exception e) {
                eventBus.onError(e);
            }
        }
    }

    @MainThread
    @NonNull
    public PublishSubject<Object> get() {
        LooperUtil.enforceOnMainThread();
        return get(DEFAULT_TAG);
    }

    @AnyThread
    @NonNull
    public PublishSubject<Object> get(@NonNull Object tag) {
        PublishSubject<Object> subject = subjectMapper.get(tag);
        if (subject == null) {
            subject = PublishSubject.create();
            subjectMapper.put(tag, subject);
        }
        return subject;
    }

}
