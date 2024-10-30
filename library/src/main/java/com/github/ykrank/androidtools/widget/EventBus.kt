package com.github.ykrank.androidtools.widget

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import com.github.ykrank.androidtools.util.LooperUtil.enforceOnMainThread
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * See https://code.google.com/p/guava-libraries/wiki/EventBusExplained
 *
 *
 * Forked from https://github.com/wangjiegulu/RxAndroidEventsSample/blob/master/sample/src/main/java/com/wangjie/rxandroideventssample/rxbus/RxBus.java
 */
class EventBus {
    private val subjectMapper = ConcurrentHashMap<Any, PublishSubject<Any>>()
    private val eventFlow = ConcurrentHashMap<Any, MutableSharedFlow<Any>>()

    @AnyThread
    fun postDefault(o: Any) {
        post(DEFAULT_TAG, o)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @AnyThread
    fun post(tag: Any, event: Any) {
        subjectMapper[tag]?.apply {
            try {
                this.onNext(event)
            } catch (e: Exception) {
                this.onError(e)
            }
        }

        eventFlow[tag]?.apply {
            GlobalScope.launch {
                emit(event)
            }
        }
    }


    @MainThread
    fun get(): PublishSubject<Any> {
        enforceOnMainThread()
        return get(DEFAULT_TAG)
    }

    @MainThread
    fun get(tag: Any): PublishSubject<Any> {
        enforceOnMainThread()
        var subject = subjectMapper[tag]
        if (subject == null) {
            subject = PublishSubject.create()
            subjectMapper[tag] = subject
        }
        return subject
    }

    @AnyThread
    fun getFlow(tag: Any = DEFAULT_TAG): SharedFlow<Any> {
        val flow = eventFlow[tag]
            ?: synchronized(eventFlow) {
                val flow1 = eventFlow[tag]
                if (flow1 == null) {
                    val nFlow = MutableSharedFlow<Any>()
                    eventFlow[tag] = nFlow
                    return nFlow
                }
                return flow1
            }
        return flow
    }

    @AnyThread
    inline fun <reified T> getClsFlow(tag: Any = DEFAULT_TAG): Flow<T> {
        return getFlow(tag).filterIsInstance()
    }

    companion object {
        const val DEFAULT_TAG = "default_tag"
    }
}
