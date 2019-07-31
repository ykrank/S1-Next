package me.ykrank.s1next.viewmodel;

import androidx.databinding.ObservableField;
import androidx.fragment.app.FragmentActivity;
import android.view.View;

import me.ykrank.s1next.data.api.model.Friend;
import me.ykrank.s1next.view.activity.UserHomeActivity;


public final class FriendViewModel {

    public final ObservableField<Friend> friend = new ObservableField<>();

    public final void onClick(View v) {
        UserHomeActivity.Companion.start((FragmentActivity) v.getContext(), friend.get().getUid(), friend.get().getUsername(), v);
    }
}
