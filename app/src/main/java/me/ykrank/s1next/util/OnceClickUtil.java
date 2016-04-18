package me.ykrank.s1next.util;

import android.view.View;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.MainThreadSubscription;
import rx.android.schedulers.AndroidSchedulers;

import static rx.android.MainThreadSubscription.verifyMainThread;

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
    public static Subscription setOnceClickLister(final View view, final View.OnClickListener clickListener) {
        return setOnceClickLister(view, clickListener, DEFAULT_CLICK_THROTTLE);
    }

    /**
     * 设置有抖动的点击事件
     *
     * @param view
     * @param millDuration 抖动毫秒
     * @return
     */
    public static Subscription setOnceClickLister(final View view, final View.OnClickListener clickListener,
                                                  final int millDuration) {
        return Observable.create(new ViewClickOnSubscribe(view))
                .throttleFirst(millDuration, TimeUnit.MILLISECONDS)
                .subscribe(vo->{
                    clickListener.onClick(view);
                });
    }

    /**
     * 有抖动的点击事件订阅
     * @param view
     * @param millDuration
     * @return
     */
    public static Observable<Void> onceClickObservable(final View view, final int millDuration){
        return Observable.create(new ViewClickOnSubscribe(view))
                .throttleFirst(millDuration, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 点击事件的订阅
     */
    private static final class ViewClickOnSubscribe implements Observable.OnSubscribe<Void> {
        final View view;

        ViewClickOnSubscribe(View view) {
            this.view = view;
        }

        @Override
        public void call(final Subscriber<? super Void> subscriber) {
            verifyMainThread();

            view.setOnClickListener(v -> {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(null);
                }
            });

            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    view.setOnClickListener(null);
                }
            });
        }
    }
}
