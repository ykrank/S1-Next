package cl.monsoon.s1next.viewmodel;

import android.app.Activity;
import android.databinding.ObservableField;
import android.view.View;

import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.view.activity.PostListActivity;
import rx.Subscription;
import rx.functions.Func1;

public final class ThreadViewModel {

    public final ObservableField<Thread> thread = new ObservableField<>();

    public Func1<View, Subscription> goToThisThread() {
        return v -> PostListActivity.clickStartPostListActivity(v, thread.get());
    }

    public View.OnLongClickListener goToThisThreadLastPage() {
        return v -> {
            PostListActivity.startPostListActivity(v.getContext(), thread.get(), true);
            return true;
        };
    }
    
}
