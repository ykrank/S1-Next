package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.view.adapter.delegate.FooterProgressAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.PmGroupsAdapterDelegate;

public final class PmGroupsRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    public PmGroupsRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new PmGroupsAdapterDelegate(activity));
        addAdapterDelegate(new FooterProgressAdapterDelegate(activity));

        setHasStableIds(true);
    }
}
