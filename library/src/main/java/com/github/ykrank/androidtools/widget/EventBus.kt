package com.github.ykrank.androidtools.widget

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import com.github.ykrank.androidtools.util.LooperUtil.enforceOnMainThread
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.ConcurrentHashMap

/**
 * See https://code.google.com/p/guava-libraries/wiki/EventBusExplained
 *
 *
 * Forked from https://github.com/wangjiegulu/RxAndroidEventsSample/blob/master/sample/src/main/java/com/wangjie/rxandroideventssample/rxbus/RxBus.java
 */
class EventBus {
    private val subjectMapper = ConcurrentHashMap<Any, PublishSubject<Any>>()

    @MainThread
    fun post(o: Any) {
        enforceOnMainThread()
        post(DEFAULT_TAG, o)
    }

    @AnyThread
    fun post(tag: Any, o: Any) {
        val eventBus = subjectMapper[tag]
        if (eventBus != null) {
            try {
                eventBus.onNext(o)
            } catch (e: Exception) {
                eventBus.onError(e)
            }
        }
    }

    @MainThread
    fun get(): PublishSubject<Any> {
        enforceOnMainThread()
        return get(DEFAULT_TAG)
    }

    @AnyThread
    fun get(tag: Any): PublishSubject<Any> {
        var subject = subjectMapper[tag]
        if (subject == null) {
            subject = PublishSubject.create()
            subjectMapper[tag] = subject
        }
        return subject
    }

    companion object {
        private const val DEFAULT_TAG = "default_tag"
    }
}
