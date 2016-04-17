package cl.monsoon.s1next.util;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public final class RxJavaUtil {
    private static final int RUN_ON_THREAD = 11;

    private RxJavaUtil() {
    }

    /**
     * @see Subscription#unsubscribe()
     */
    public static void unsubscribeIfNotNull(Subscription subscription) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    /**
     * 快速发送工作线程和UI回调
     *
     * @param workAction
     * @param uiAction
     * @return
     */
    public static Subscription workWithUiThread(Action0 workAction, Action0 uiAction) {
        return Observable.just(RUN_ON_THREAD)
                .doOnNext(i -> workAction.call())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> uiAction.call());
    }


}
