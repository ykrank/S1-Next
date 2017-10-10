package me.ykrank.s1next.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.github.ykrank.androidautodispose.AndroidRxDispose;
import com.github.ykrank.androidlifecycle.event.ViewEvent;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.ykrank.s1next.App;

public final class RxJavaUtil {
    public static final Object NULL = "";

    private RxJavaUtil() {
    }

    public static <T> T createNull() {
        return (T) NULL;
    }

    /**
     * @see Disposable#dispose()
     */
    public static void disposeIfNotNull(Disposable disposable) {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    /**
     * push work to RxJava io thread {@link AndroidSchedulers#mainThread()}, auto dispose with view destroy event
     */
    public static Disposable workInMainThreadWithView(View view, Action workAction) {
        return Single.just(NULL)
                .subscribeOn(AndroidSchedulers.mainThread())
                .to(AndroidRxDispose.withSingle(view, ViewEvent.DESTROY))
                .subscribe(o -> workAction.run(),
                        L::report);
    }

    /**
     * push work to RxJava io thread {@link AndroidSchedulers#mainThread()}
     */
    public static Disposable workInMainThread(Action workAction) {
        return workInMainThread(NULL, o -> workAction.run());
    }

    /**
     * push work to RxJava io thread {@link AndroidSchedulers#mainThread()}
     */
    public static <D> Disposable workInMainThread(D data, Consumer<D> workAction) {
        return Single.just(data)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(workAction,
                        L::report);
    }

    /**
     * push work to RxJava io thread {@link Schedulers#io()}
     *
     * @param workAction
     */
    public static Disposable workInRxIoThread(Action workAction) {
        return Single.just(NULL)
                .subscribeOn(Schedulers.io())
                .subscribe(o -> workAction.run(),
                        L::report);
    }

    /**
     * push work to RxJava computation thread {@link Schedulers#computation()}
     *
     * @param workAction
     */
    public static Disposable workInRxComputationThread(Action workAction) {
        return Single.just(NULL)
                .subscribeOn(Schedulers.computation())
                .subscribe(o -> workAction.run(), L::report);
    }

    /**
     * 快速发送工作线程和UI回调
     *
     * @param workAction 工作线程
     * @param uiAction   主线程
     * @return 订单
     */
    public static Disposable workWithUiThread(Action workAction, Action uiAction) {
        return workWithUiThread(workAction, uiAction, L::report);
    }

    /**
     * 快速发送工作线程和UI回调
     *
     * @param workAction 工作线程
     * @param uiAction   主线程
     * @param error      错误回调
     * @return 订单
     */
    public static Disposable workWithUiThread(@NonNull Action workAction, @NonNull Action uiAction, Consumer<Throwable> error) {
        return Observable.just(NULL)
                .doOnNext(i -> workAction.run())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> uiAction.run(), error);
    }

    /**
     * 快速发送工作线程和有返回值的UI回调
     *
     * @param workAction 工作线程
     * @param uiAction   主线程
     * @param <R>        返回值的类型
     * @return 订单
     */
    public static <R> Disposable workWithUiResult(Supplier<R> workAction, Consumer<R> uiAction) {
        return workWithUiResult(workAction, uiAction, L::report);
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
    public static <R> Disposable workWithUiResult(Supplier<R> workAction, Consumer<R> uiAction, Consumer<Throwable> error) {
        return Single.just(NULL)
                .map(o -> workAction.get())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uiAction, error);
    }

    /**
     * wrap nullable source in Single flatMap
     *
     * @param source
     * @param <T>
     * @return
     */
    @NonNull
    public static <T> Single<T> neverNull(@Nullable T source) {
        return source == null ? Single.never() : Single.just(source);
    }

    /**
     * wrap nullable source in Observable flatMap
     *
     * @param source
     * @param <T>
     * @return
     */
    @NonNull
    public static <T> Observable<T> neverNullObservable(@Nullable T source) {
        return source == null ? Observable.never() : Observable.just(source);
    }

    public static <T> ObservableTransformer<T, T> iOTransformer() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> iOSingleTransformer() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> computationTransformer() {
        return observable -> observable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> computationSingleTransformer() {
        return observable -> observable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> newThreadTransformer() {
        return observable -> observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> newThreadSingleTransformer() {
        return observable -> observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> clickThrottleTransformer() {
        return observable -> observable.throttleFirst(1, TimeUnit.SECONDS);
    }

    public static <D> ObservableTransformer<String, D> jsonTransformer(Class<D> dClass) {
        return observable -> observable.map(s -> App.getAppComponent().getJsonMapper().readValue(s, dClass));
    }

    public static <D> ObservableTransformer<String, D> jsonTransformer(JavaType javaType) {
        return observable -> observable.map(s -> App.getAppComponent().getJsonMapper().readValue(s, javaType));
    }

    public static <D> ObservableTransformer<String, D> jsonTransformer(TypeReference<D> typeReference) {
        return observable -> observable.map(s -> App.getAppComponent().getJsonMapper().readValue(s, typeReference));
    }

    public interface Supplier<T> {
        /**
         * Retrieves an instance of the appropriate type. The returned object may or
         * may not be a new instance, depending on the implementation.
         *
         * @return an instance of the appropriate type
         */
        @NonNull
        T get() throws Exception;
    }
}
