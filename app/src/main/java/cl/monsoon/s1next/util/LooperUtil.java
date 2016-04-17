package cl.monsoon.s1next.util;

import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import com.google.common.base.Preconditions;

public final class LooperUtil {

    private LooperUtil() {}

    /**
     * Enforces the method caller on main thread.
     */
    @MainThread
    public static void enforceOnMainThread() {
        Preconditions.checkState(Looper.myLooper() == Looper.getMainLooper(),
                "Must be called on the main thread.");
    }

    @WorkerThread
    public static void enforceOnWorkThread() {
        Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper(),
                "Must be called on the work thread.");
    }
}
