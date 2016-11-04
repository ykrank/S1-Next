package me.ykrank.s1next.util;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Single;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public final class RxJavaUtil {

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
     * @param workAction 工作线程
     * @param uiAction 主线程
     * @return 订单
     */
    public static Subscription workWithUiThread(Action0 workAction, Action0 uiAction) {
        return workWithUiThread(workAction, uiAction, Throwable::printStackTrace);
    }

    /**
     * 快速发送工作线程和UI回调
     *
     * @param workAction 工作线程
     * @param uiAction   主线程
     * @param error      错误回调
     * @return 订单
     */
    public static Subscription workWithUiThread(Action0 workAction, Action0 uiAction, Action1<Throwable> error) {
        return Observable.just(null)
                .doOnNext(i -> workAction.call())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> uiAction.call(), error::call);
    }

    /**
     * 快速发送工作线程和有返回值的UI回调
     *
     * @param workAction 工作线程
     * @param uiAction   主线程
     * @param <R>        返回值的类型
     * @return 订单
     */
    public static <R> Subscription workWithUiResult(Func0<R> workAction, Action1<R> uiAction) {
        return workWithUiResult(workAction, uiAction, Throwable::printStackTrace);
    }

    /**
     * 快速发送工作线程和有返回值的UI回调
     *
     * @param workAction 工作线程
     * @param uiAction   主线程
     * @param error      错误回调
     * @param <R>        返回值的类型
     * @return 订单
     */
    public static <R> Subscription workWithUiResult(Func0<R> workAction, Action1<R> uiAction, Action1<Throwable> error) {
        return Observable.just(null)
                .map(n -> workAction.call())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uiAction::call, error::call);
    }

    public static <T> Observable.Transformer<T, T> iOTransformer() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Single.Transformer<T, T> iOSingleTransformer() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable.Transformer<T, T> clickThrottleTransformer() {
        return observable -> observable.throttleFirst(1, TimeUnit.SECONDS);
    }
}
