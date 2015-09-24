package cl.monsoon.s1next.view.adapter.delegate;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;

import java.util.List;

import javax.inject.Inject;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.data.pref.DownloadPreferencesManager;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.databinding.ItemThreadBinding;
import cl.monsoon.s1next.viewmodel.ThreadViewModel;
import cl.monsoon.s1next.viewmodel.UserViewModel;

public final class ThreadAdapterDelegate extends AbsAdapterDelegate<List<Object>> {

    @Inject
    UserViewModel mUserViewModel;

    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;

    @Inject
    ThemeManager mThemeManager;

    private final LayoutInflater mLayoutInflater;

    private final DrawableRequestBuilder<String> mAvatarRequestBuilder;

    public ThreadAdapterDelegate(Activity activity, int viewType) {
        super(viewType);

        App.getAppComponent(activity).inject(this);
        mLayoutInflater = activity.getLayoutInflater();
        mAvatarRequestBuilder = Glide.with(activity)
                .from(String.class)
                .error(R.drawable.ic_avatar_placeholder)
                .priority(Priority.HIGH)
                .transform(new CenterCrop(Glide.get(activity).getBitmapPool()));
    }

    @Override
    public boolean isForViewType(@NonNull List<Object> objectList, int i) {
        return objectList.get(i) instanceof Thread;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        ItemThreadBinding binding = DataBindingUtil.inflate(mLayoutInflater, R.layout.item_thread,
                viewGroup, false);
        // we do not use view model for ThemeManager
        // because theme changes only when Activity recreated
        binding.setUserViewModel(mUserViewModel);
        binding.setDownloadPreferencesManager(mDownloadPreferencesManager);
        binding.setDrawableRequestBuilder(mAvatarRequestBuilder);
        binding.setThemeManager(mThemeManager);
        binding.setThreadViewModel(new ThreadViewModel());

        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull List<Object> objectList, int i, @NonNull RecyclerView.ViewHolder viewHolder) {
        ItemThreadBinding binding = ((BindingViewHolder) viewHolder).itemThreadBinding;
        binding.getThreadViewModel().thread.set((Thread) objectList.get(i));
        binding.executePendingBindings();
    }

    private static final class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemThreadBinding itemThreadBinding;

        public BindingViewHolder(ItemThreadBinding itemThreadBinding) {
            super(itemThreadBinding.getRoot());

            this.itemThreadBinding = itemThreadBinding;
        }
    }
}
