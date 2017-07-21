package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Favourite;
import me.ykrank.s1next.databinding.ItemFavouriteBinding;
import me.ykrank.s1next.viewmodel.FavouriteViewModel;
import me.ykrank.s1next.widget.RxBus;

public final class FavouriteAdapterDelegate extends BaseAdapterDelegate<Favourite, FavouriteAdapterDelegate.BindingViewHolder> {

    @Inject
    RxBus mRxBus;

    public FavouriteAdapterDelegate(Context context) {
        super(context);
        App.getAppComponent().inject(this);
    }

    @NonNull
    @Override
    protected Class<Favourite> getTClass() {
        return Favourite.class;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemFavouriteBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_favourite, parent, false);
        binding.setModel(new FavouriteViewModel());
        binding.setEventBus(mRxBus);
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolderData(Favourite favourite, int position, @NonNull BindingViewHolder holder, @NonNull List<Object> payloads) {
        ItemFavouriteBinding binding = holder.itemFavouriteBinding;
        binding.getModel().favourite.set(favourite);
        binding.executePendingBindings();
    }

    static final class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemFavouriteBinding itemFavouriteBinding;

        public BindingViewHolder(ItemFavouriteBinding itemFavouriteBinding) {
            super(itemFavouriteBinding.getRoot());

            this.itemFavouriteBinding = itemFavouriteBinding;
        }
    }
}
