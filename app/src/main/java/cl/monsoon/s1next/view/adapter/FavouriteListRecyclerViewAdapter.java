package cl.monsoon.s1next.view.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Favourite;
import cl.monsoon.s1next.databinding.ItemFavouriteBinding;
import cl.monsoon.s1next.viewmodel.FavouriteViewModel;

public final class FavouriteListRecyclerViewAdapter extends BaseRecyclerViewAdapter<Favourite, FavouriteListRecyclerViewAdapter.BindingViewHolder> {

    private final LayoutInflater mLayoutInflater;

    public FavouriteListRecyclerViewAdapter(Activity activity) {
        mLayoutInflater = activity.getLayoutInflater();

        setHasStableIds(true);
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFavouriteBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_favourite, parent, false);

        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        holder.itemFavouriteBinding.setFavouriteViewModel(new FavouriteViewModel(getItem(position)));
        holder.itemFavouriteBinding.executePendingBindings();
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(getItem(position).getId());
    }

    public static class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemFavouriteBinding itemFavouriteBinding;

        public BindingViewHolder(ItemFavouriteBinding itemFavouriteBinding) {
            super(itemFavouriteBinding.getRoot());

            this.itemFavouriteBinding = itemFavouriteBinding;
        }
    }
}
