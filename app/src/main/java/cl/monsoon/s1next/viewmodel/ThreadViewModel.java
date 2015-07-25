package cl.monsoon.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.view.activity.PostListActivity;

public final class ThreadViewModel {

    public ObservableField<Thread> thread = new ObservableField<>();

    public ThreadViewModel(Thread thread) {
        this.thread.set(thread);
    }

    public View.OnClickListener goToThisThread() {
        return v -> goToThisThread(v, false);
    }

    public View.OnLongClickListener goToThisThreadLastPage() {
        return v -> {
            goToThisThread(v, true);

            return true;
        };
    }

    private void goToThisThread(View view, boolean shouldGoToLastPage) {
        PostListActivity.startPostListActivity(view.getContext(), thread.get(), shouldGoToLastPage);
    }
}
