package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.view.adapter.delegate.PmLeftAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.PmRightAdapterDelegate;

public final class PmRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    public PmRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new PmLeftAdapterDelegate(activity));
        addAdapterDelegate(new PmRightAdapterDelegate(activity));
    }
}
