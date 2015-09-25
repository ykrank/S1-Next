package cl.monsoon.s1next.util;

import rx.Subscription;

public final class RxJavaUtil {

    private RxJavaUtil() {}

    /**
     * @see Subscription#unsubscribe()
     */
    public static void unsubscribeIfNotNull(Subscription subscription) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
