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
    public boolean isForViewType(@NonNull List<Object> objectList, int i) {
        return objectList.get(i) instanceof Favourite;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        ItemFavouriteBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_favourite, viewGroup, false);
        binding.setFavouriteViewModel(new FavouriteViewModel());

        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull List<Object> objectList, int i, @NonNull RecyclerView.ViewHolder viewHolder) {
        ItemFavouriteBinding binding = ((BindingViewHolder) viewHolder).itemFavouriteBinding;
        binding.getFavouriteViewModel().favourite.set((Favourite) objectList.get(i));
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
