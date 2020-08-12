package me.ykrank.s1next.viewmodel;

import android.view.View;

import androidx.databinding.ObservableField;

import com.google.common.base.Supplier;

import io.reactivex.functions.Consumer;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.view.page.post.PostListActivity;

public final class ThreadViewModel {

    public final ObservableField<Thread> thread = new ObservableField<>();

    private final Supplier<Thread> threadSupplier = thread::get;

    public Consumer<View> onBind() {
        return v -> PostListActivity.Companion.bindClickStartForView(v, threadSupplier);
    }

    public View.OnLongClickListener goToThisThreadLastPage() {
        return v -> {
            PostListActivity.Companion.start(v.getContext(), thread.get(), true);
            return true;
        };
    }

}
