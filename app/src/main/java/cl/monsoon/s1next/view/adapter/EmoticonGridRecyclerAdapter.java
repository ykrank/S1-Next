package cl.monsoon.s1next.view.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;

import java.util.List;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Emoticon;
import cl.monsoon.s1next.databinding.ItemEmoticonBinding;
import cl.monsoon.s1next.viewmodel.EmoticonViewModel;
import cl.monsoon.s1next.widget.EventBus;

public final class EmoticonGridRecyclerAdapter extends RecyclerView.Adapter<EmoticonGridRecyclerAdapter.BindingViewHolder> {

    private final LayoutInflater mLayoutInflater;

    private final List<Emoticon> mEmoticons;
    private final DrawableRequestBuilder<Uri> mEmoticonRequestBuilder;

    private final EventBus mEventBus;

    public EmoticonGridRecyclerAdapter(Activity activity, List<Emoticon> emoticons) {
        mLayoutInflater = activity.getLayoutInflater();
        this.mEmoticons = emoticons;
        mEmoticonRequestBuilder = Glide.with(activity).from(Uri.class);
        mEventBus = App.getAppComponent(activity).getEventBus();

        setHasStableIds(true);
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemEmoticonBinding binding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_emoticon, parent, false);
        binding.setEventBus(mEventBus);
        binding.setDrawableRequestBuilder(mEmoticonRequestBuilder);
        binding.setEmoticonViewModel(new EmoticonViewModel());

        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        holder.itemEmoticonBinding.getEmoticonViewModel().emoticon.set(mEmoticons.get(position));
        holder.itemEmoticonBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mEmoticons.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemEmoticonBinding itemEmoticonBinding;

        public BindingViewHolder(ItemEmoticonBinding itemEmoticonBinding) {
            super(itemEmoticonBinding.getRoot());

            this.itemEmoticonBinding = itemEmoticonBinding;
        }
    }
}
