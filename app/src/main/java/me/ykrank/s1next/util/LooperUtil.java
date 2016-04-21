package me.ykrank.s1next.util;

import android.os.Looper;

import com.google.common.base.Preconditions;

public final class LooperUtil {

    private LooperUtil() {}

    /**
     * Enforces the method caller on main thread.
     */
    public static void enforceOnMainThread() {
        Preconditions.checkState(Looper.myLooper() == Looper.getMainLooper(),
                "Must be called on the main thread.");
    }

    public static void enforceOnWorkThread() {
        Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper(),
                "Must be called on the work thread.");
    }
}
