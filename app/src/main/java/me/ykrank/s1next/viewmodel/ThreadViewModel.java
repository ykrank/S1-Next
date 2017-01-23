package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.view.activity.PostListActivity;

public final class ThreadViewModel {

    public final ObservableField<Thread> thread = new ObservableField<>();

    public final ObservableField<Function<View, Disposable>> subscription = new ObservableField<>();

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
