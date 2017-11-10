package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import com.github.ykrank.androidtools.ui.adapter.delegate.FooterProgressAdapterDelegate;

import me.ykrank.s1next.view.adapter.delegate.FriendAdapterDelegate;

public final class FriendRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    public FriendRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new FriendAdapterDelegate(activity));
        addAdapterDelegate(new FooterProgressAdapterDelegate(activity));
    }
}
