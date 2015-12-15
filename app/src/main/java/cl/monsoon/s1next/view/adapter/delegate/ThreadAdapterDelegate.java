package cl.monsoon.s1next.view.adapter.delegate;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;

import java.util.List;

import javax.inject.Inject;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.databinding.ItemThreadBinding;
import cl.monsoon.s1next.viewmodel.ThreadViewModel;
import cl.monsoon.s1next.viewmodel.UserViewModel;

public final class ThreadAdapterDelegate extends AbsAdapterDelegate<List<Object>> {

    @Inject
    UserViewModel mUserViewModel;

    @Inject
    ThemeManager mThemeManager;

    private final LayoutInflater mLayoutInflater;

    public ThreadAdapterDelegate(Activity activity, int viewType) {
        super(viewType);

        App.getAppComponent(activity).inject(this);
        mLayoutInflater = activity.getLayoutInflater();
    }

    @Override
    public boolean isForViewType(@NonNull List<Object> items, int position) {
        return items.get(position) instanceof Thread;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemThreadBinding binding = DataBindingUtil.inflate(mLayoutInflater, R.layout.item_thread,
                parent, false);
        // we do not use view model for ThemeManager
        // because theme changes only when Activity recreated
        binding.setUserViewModel(mUserViewModel);
        binding.setThemeManager(mThemeManager);
        binding.setThreadViewModel(new ThreadViewModel());

        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull List<Object> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        ItemThreadBinding binding = ((BindingViewHolder) holder).itemThreadBinding;
        binding.getThreadViewModel().thread.set((Thread) items.get(position));
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
