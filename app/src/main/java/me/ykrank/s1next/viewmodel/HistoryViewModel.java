package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.db.dbmodel.History;
import me.ykrank.s1next.view.activity.PostListActivity;

public final class HistoryViewModel {

    public final ObservableField<History> history = new ObservableField<>();

    public View.OnClickListener onClick(History history) {
        return v -> PostListActivity.clickStartPostListActivity(v, new Thread(history));
    }

}
