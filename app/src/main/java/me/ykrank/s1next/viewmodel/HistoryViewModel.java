package me.ykrank.s1next.viewmodel;

import android.view.View;

import androidx.databinding.ObservableField;

import com.google.common.base.Supplier;

import io.reactivex.functions.Consumer;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.db.dbmodel.History;
import me.ykrank.s1next.view.page.post.PostListActivity;

public final class HistoryViewModel {

    public final ObservableField<History> history = new ObservableField<>();

    private final Supplier<Thread> threadSupplier = () -> new Thread(history.get());

    public Consumer<View> onBind() {
        return v -> PostListActivity.Companion.bindClickStartForView(v, threadSupplier);
    }
}
