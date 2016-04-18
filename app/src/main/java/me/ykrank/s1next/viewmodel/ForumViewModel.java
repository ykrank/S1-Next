package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import me.ykrank.s1next.data.api.model.Forum;
import me.ykrank.s1next.view.activity.ThreadListActivity;


public final class ForumViewModel {

    public final ObservableField<Forum> forum = new ObservableField<>();

    public View.OnClickListener goToThisForum() {
        return v -> ThreadListActivity.startThreadListActivity(v.getContext(), forum.get());
    }
}
