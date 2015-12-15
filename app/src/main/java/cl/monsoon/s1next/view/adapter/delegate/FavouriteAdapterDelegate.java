package cl.monsoon.s1next.view.adapter.delegate;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;

import java.util.List;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Favourite;
import cl.monsoon.s1next.databinding.ItemFavouriteBinding;
import cl.monsoon.s1next.viewmodel.FavouriteViewModel;

public final class FavouriteAdapterDelegate extends AbsAdapterDelegate<List<Object>> {

    private final LayoutInflater mLayoutInflater;

    public FavouriteAdapterDelegate(Activity activity, int viewType) {
        super(viewType);

        mLayoutInflater = activity.getLayoutInflater();
    }

    @Override
    public boolean isForViewType(@NonNull List<Object> items, int position) {
        return items.get(position) instanceof Favourite;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemFavouriteBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_favourite, parent, false);
        binding.setFavouriteViewModel(new FavouriteViewModel());

        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull List<Object> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        ItemFavouriteBinding binding = ((BindingViewHolder) holder).itemFavouriteBinding;
        binding.getFavouriteViewModel().favourite.set((Favourite) items.get(position));
        binding.executePendingBindings();
    }

    private static final class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemFavouriteBinding itemFavouriteBinding;

        public BindingViewHolder(ItemFavouriteBinding itemFavouriteBinding) {
            super(itemFavouriteBinding.getRoot());

            this.itemFavouriteBinding = itemFavouriteBinding;
        }
    }
}
