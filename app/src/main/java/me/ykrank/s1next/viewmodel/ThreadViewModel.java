package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.view.activity.PostListActivity;

public final class ThreadViewModel {

    public final ObservableField<Thread> thread = new ObservableField<>();

    public View.OnClickListener onClick(Thread thread) {
        return v -> PostListActivity.clickStartPostListActivity(v, thread);
    }

    public View.OnLongClickListener goToThisThreadLastPage() {
        return v -> {
            PostListActivity.startPostListActivity(v.getContext(), thread.get(), true);
            return true;
        };
    }

}
