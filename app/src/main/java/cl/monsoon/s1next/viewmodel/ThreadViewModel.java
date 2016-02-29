package cl.monsoon.s1next.viewmodel;

import android.app.Activity;
import android.databinding.ObservableField;
import android.view.View;

import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.view.activity.PostListActivity;

public final class ThreadViewModel {

    public final ObservableField<Thread> thread = new ObservableField<>();

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
        if (view.getContext() instanceof Activity){
            PostListActivity.startPostListActivityForResult((Activity) view.getContext(), thread.get(), shouldGoToLastPage);
        }else {
            PostListActivity.startPostListActivity(view.getContext(), thread.get(), shouldGoToLastPage);
        }
    }
}
