package com.github.ykrank.androidtools.util

import android.os.Handler
import android.os.Looper

object LooperUtil {
    private val handler = Handler(Looper.getMainLooper())

    /**
     * Enforces the method caller on main thread.
     */
    @JvmStatic
    fun enforceOnMainThread() {
        if (!isOnMainThread) {
            throw IllegalArgumentException("You must call this method on a main thread")
        }
    }

    @JvmStatic
    fun enforceOnWorkThread() {
        if (isOnMainThread) {
            throw IllegalArgumentException("You must call this method on a background thread")
        }
    }

    val isOnMainThread: Boolean
        /**
         * Returns `true` if called on the main thread, `false` otherwise.
         */
        get() = Looper.myLooper() == Looper.getMainLooper()

    fun workInMainThread(action: () -> Unit) {
        if (isOnMainThread) {
            action()
        } else {
            handler.post(action)
        }
    }

    fun postToMainThread(action: () -> Unit) {
        handler.post(action)
    }

    fun <T> workInMainThread(data: T, action: (d: T) -> Unit) {
        handler.post {
            action(data)
        }
    }
}
