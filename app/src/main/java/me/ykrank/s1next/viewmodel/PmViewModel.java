package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import me.ykrank.s1next.data.api.model.Pm;
import me.ykrank.s1next.view.activity.UserHomeActivity;


public final class PmViewModel {

    public final ObservableField<Pm> pm = new ObservableField<>();

    public final void onClick(View v) {
        UserHomeActivity.Companion.start(v.getContext(), pm.get().getAuthorId(), pm.get().getAuthor(), v);
    }
}
