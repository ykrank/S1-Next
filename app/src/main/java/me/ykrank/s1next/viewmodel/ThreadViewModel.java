package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.view.activity.PostListActivity;
import rx.Subscription;
import rx.functions.Func1;

public final class ThreadViewModel {

    public final ObservableField<Thread> thread = new ObservableField<>();

    public final ObservableField<Func1<View, Subscription>> subscription = new ObservableField<>();

    public void setSubscription() {
        subscription.set(v -> PostListActivity.clickStartPostListActivity(v, thread.get()));
    }

    public View.OnLongClickListener goToThisThreadLastPage() {
        return v -> {
            PostListActivity.startPostListActivity(v.getContext(), thread.get(), true);
            return true;
        };
    }

}
