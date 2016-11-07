package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.view.adapter.delegate.FavouriteAdapterDelegate;

public final class FavouriteRecyclerViewAdapter extends BaseRecyclerViewAdapter {
    
    public FavouriteRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new FavouriteAdapterDelegate(activity));

        setHasStableIds(true);
    }
}
