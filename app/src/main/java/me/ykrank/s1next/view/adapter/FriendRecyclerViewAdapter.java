package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.view.adapter.delegate.FriendAdapterDelegate;

public final class FriendRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    public FriendRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new FriendAdapterDelegate(activity));
    }
}
