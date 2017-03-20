package me.ykrank.s1next.util;

import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.ykrank.s1next.widget.ViewClickObservable;

/**
 * Created by AdminYkrank on 2016/4/17.
 */
public class OnceClickUtil {
    private static final int DEFAULT_CLICK_THROTTLE = 500;

    /**
     * 设置有抖动的点击事件
     *
     * @param view
     * @return
     */
    public static Disposable setOnceClickLister(final View view, final View.OnClickListener clickListener) {
        return setOnceClickLister(view, clickListener, DEFAULT_CLICK_THROTTLE);
    }

    /**
     * 设置有抖动的点击事件
     *
     * @param view
     * @param millDuration 抖动毫秒
     * @return
     */
    public static Disposable setOnceClickLister(final View view, final View.OnClickListener clickListener,
                                                final int millDuration) {
        return new ViewClickObservable(view)
                .throttleFirst(millDuration, TimeUnit.MILLISECONDS)
                .subscribe(vo -> clickListener.onClick(view), L::report);
    }

    /**
     * 有抖动的点击事件订阅
     *
     * @param view
     * @param millDuration
     * @return
     */
    public static Observable<Object> onceClickObservable(final View view, final int millDuration) {
        return new ViewClickObservable(view)
                .throttleFirst(millDuration, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread());
    }
}
