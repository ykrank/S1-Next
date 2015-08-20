package cl.monsoon.s1next.view.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import javax.inject.Inject;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.databinding.ItemThreadBinding;
import cl.monsoon.s1next.viewmodel.ThreadViewModel;
import cl.monsoon.s1next.viewmodel.UserViewModel;

public final class ThreadListRecyclerViewAdapter extends BaseRecyclerViewAdapter<Thread, ThreadListRecyclerViewAdapter.BindingViewHolder> {

    @Inject
    UserViewModel mUserViewModel;

    @Inject
    ThemeManager mThemeManager;

    private final LayoutInflater mLayoutInflater;

    public ThreadListRecyclerViewAdapter(Activity activity) {
        App.getAppComponent(activity).inject(this);
        mLayoutInflater = activity.getLayoutInflater();

        setHasStableIds(true);
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemThreadBinding binding = DataBindingUtil.inflate(mLayoutInflater, R.layout.item_thread,
                parent, false);
        BindingViewHolder holder = new BindingViewHolder(binding);
        // we do not use view model for ThemeManager
        // because theme changes only when Activity recreated
        holder.itemThreadBinding.setUserViewModel(mUserViewModel);
        holder.itemThreadBinding.setThemeManager(mThemeManager);
        holder.itemThreadBinding.setThreadViewModel(new ThreadViewModel());

        return holder;
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        holder.itemThreadBinding.getThreadViewModel().thread.set(getItem(position));
        holder.itemThreadBinding.executePendingBindings();
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(getItem(position).getId());
    }

    public final static class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemThreadBinding itemThreadBinding;

        public BindingViewHolder(ItemThreadBinding itemThreadBinding) {
            super(itemThreadBinding.getRoot());

            this.itemThreadBinding = itemThreadBinding;
        }
    }
}
