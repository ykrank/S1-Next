package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.view.adapter.delegate.FooterProgressAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.HomeReplyItemAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.HomeReplyTitleAdapterDelegate;

public final class HomeReplyRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    public HomeReplyRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new HomeReplyTitleAdapterDelegate(activity));
        addAdapterDelegate(new HomeReplyItemAdapterDelegate(activity));
        addAdapterDelegate(new FooterProgressAdapterDelegate(activity));
    }
}
