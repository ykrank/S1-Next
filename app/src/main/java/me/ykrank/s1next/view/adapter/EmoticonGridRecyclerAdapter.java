package me.ykrank.s1next.view.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Emoticon;
import me.ykrank.s1next.databinding.ItemEmoticonBinding;
import me.ykrank.s1next.viewmodel.EmoticonViewModel;
import me.ykrank.s1next.widget.RxBus;

public final class EmoticonGridRecyclerAdapter
        extends RecyclerView.Adapter<EmoticonGridRecyclerAdapter.BindingViewHolder> {

    private final LayoutInflater mLayoutInflater;

    private final List<Emoticon> mEmoticons;
    private final RequestManager mEmoticonRequestBuilder;

    private final RxBus mRxBus;

    public EmoticonGridRecyclerAdapter(Activity activity, List<Emoticon> emoticons) {
        mLayoutInflater = activity.getLayoutInflater();
        this.mEmoticons = emoticons;
        mEmoticonRequestBuilder = Glide.with(activity);
        mRxBus = App.getAppComponent().getEventBus();

        setHasStableIds(true);
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemEmoticonBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_emoticon, parent, false);
        binding.setEventBus(mRxBus);
        binding.setRequestManager(mEmoticonRequestBuilder);
        binding.setEmoticonViewModel(new EmoticonViewModel());

        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        holder.itemEmoticonBinding.getEmoticonViewModel().emoticon.set(mEmoticons.get(position));
        holder.itemEmoticonBinding.executePendingBindings();
    }

    @Override
    public void onViewRecycled(BindingViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mEmoticons.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static final class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemEmoticonBinding itemEmoticonBinding;

        public BindingViewHolder(ItemEmoticonBinding itemEmoticonBinding) {
            super(itemEmoticonBinding.getRoot());

            this.itemEmoticonBinding = itemEmoticonBinding;
        }
    }
}
