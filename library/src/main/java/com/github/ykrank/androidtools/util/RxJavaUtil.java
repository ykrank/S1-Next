package com.github.ykrank.androidtools.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public final class RxJavaUtil {
    public static final Object NULL = "";

    private RxJavaUtil() {
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


    public static <T> SingleTransformer<T, T> iOSingleTransformer() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static <T> SingleTransformer<T, T> computationSingleTransformer() {
        return observable -> observable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
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
