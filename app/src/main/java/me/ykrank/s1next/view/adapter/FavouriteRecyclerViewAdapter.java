package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.data.api.model.Favourite;
import me.ykrank.s1next.view.adapter.delegate.FavouriteAdapterDelegate;

public final class FavouriteRecyclerViewAdapter extends BaseRecyclerViewAdapter<Favourite> {

    private static final int VIEW_TYPE_FAVOURITE = 1;

    public FavouriteRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new FavouriteAdapterDelegate(activity, VIEW_TYPE_FAVOURITE));

        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (getItemViewTypeFromDelegatesManager(position) == VIEW_TYPE_FAVOURITE) {
            return Long.parseLong(((Favourite) getItem(position)).getId());
        } else {
            return super.getItemId(position);
        }
    }
}
