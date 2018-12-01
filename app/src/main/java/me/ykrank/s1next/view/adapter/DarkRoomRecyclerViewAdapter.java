package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.view.adapter.delegate.DarkRoomAdapterDelegate;

public final class DarkRoomRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    public DarkRoomRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new DarkRoomAdapterDelegate(activity));
    }
}
