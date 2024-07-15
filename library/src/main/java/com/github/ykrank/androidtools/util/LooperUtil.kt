package com.github.ykrank.androidtools.util;

import android.os.Looper;

import com.google.common.base.Preconditions;

public final class LooperUtil {

    private LooperUtil() {
    }

    /**
     * Enforces the method caller on main thread.
     */
    public static void enforceOnMainThread() {
        Preconditions.checkState(isOnMainThread(),
                "Must be called on the main thread.");
    }

    public static void enforceOnWorkThread() {
        Preconditions.checkState(!isOnMainThread(),
                "Must be called on the work thread.");
    }

    /**
     * Returns {@code true} if called on the main thread, {@code false} otherwise.
     */
    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
