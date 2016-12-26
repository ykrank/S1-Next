package me.ykrank.s1next.binding;

import android.databinding.BindingAdapter;
import android.view.View;

import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by AdminYkrank on 2016/4/17.
 */
public final class ViewBindingAdapter {
    private ViewBindingAdapter() {
    }

    @BindingAdapter("onceClickSubscription")
    public static void setOnceClickListener(View view, Func1<View, Subscription> onceClickSubscription) {
        onceClickSubscription.call(view);
    }
}
