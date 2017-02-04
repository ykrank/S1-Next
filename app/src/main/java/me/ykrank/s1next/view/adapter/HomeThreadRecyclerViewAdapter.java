package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.view.adapter.delegate.FooterProgressAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.HomeThreadAdapterDelegate;

public final class HomeThreadRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    public HomeThreadRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new HomeThreadAdapterDelegate(activity));
        addAdapterDelegate(new FooterProgressAdapterDelegate(activity));
    }
}
