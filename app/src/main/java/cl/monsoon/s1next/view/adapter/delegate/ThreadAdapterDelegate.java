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
    public boolean isForViewType(@NonNull List<Object> objectList, int i) {
        return objectList.get(i) instanceof Thread;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        ItemThreadBinding binding = DataBindingUtil.inflate(mLayoutInflater, R.layout.item_thread,
                viewGroup, false);
        BindingViewHolder holder = new BindingViewHolder(binding);
        // we do not use view model for ThemeManager
        // because theme changes only when Activity recreated
        holder.itemThreadBinding.setUserViewModel(mUserViewModel);
        holder.itemThreadBinding.setThemeManager(mThemeManager);
        holder.itemThreadBinding.setThreadViewModel(new ThreadViewModel());

        return holder;
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
