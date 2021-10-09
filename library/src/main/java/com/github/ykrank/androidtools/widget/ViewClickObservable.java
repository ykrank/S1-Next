package com.github.ykrank.androidtools.widget;

import android.view.View;

import com.github.ykrank.androidtools.util.RxJavaUtil;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

import static io.reactivex.android.MainThreadDisposable.verifyMainThread;

/**
 * Fork from https://github.com/JakeWharton/RxBinding/blob/master/rxbinding/src/main/java/com/jakewharton/rxbinding2/view/ViewClickObservable.java
 */

public final class ViewClickObservable extends Observable<Object> {
    private final View view;

    public ViewClickObservable(View view) {
        this.view = view;
    }

    @Override
    protected void subscribeActual(Observer<? super Object> observer) {
        verifyMainThread();
        Listener listener = new Listener(view, observer);
        observer.onSubscribe(listener);
        view.setOnClickListener(listener);
    }

    static final class Listener extends MainThreadDisposable implements View.OnClickListener {
        private final View view;
        private final Observer<? super Object> observer;

        Listener(View view, Observer<? super Object> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override
        public void onClick(View v) {
            if (!isDisposed()) {
                observer.onNext(RxJavaUtil.NULL);
            }
        }

        @Override
        protected void onDispose() {
            view.setOnClickListener(null);
        }
    }
}
