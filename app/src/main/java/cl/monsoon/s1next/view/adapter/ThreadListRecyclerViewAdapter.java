package cl.monsoon.s1next.view.adapter;

import android.app.Activity;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.databinding.ItemThreadBinding;
import cl.monsoon.s1next.util.ViewUtil;
import cl.monsoon.s1next.viewmodel.ThreadViewModel;
import cl.monsoon.s1next.viewmodel.UserViewModel;

public final class ThreadListRecyclerViewAdapter extends BaseRecyclerViewAdapter<Thread, ThreadListRecyclerViewAdapter.BindingViewHolder> {

    @Inject
    ThemeManager mThemeManager;

    @Inject
    UserViewModel mUserViewModel;

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
        holder.itemThreadBinding.setThemeManager(mThemeManager);
        holder.itemThreadBinding.setUserViewModel(mUserViewModel);

        return holder;
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        holder.itemThreadBinding.setThreadViewModel(new ThreadViewModel(getItem(position)));
        holder.itemThreadBinding.executePendingBindings();
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(getItem(position).getId());
    }

    @BindingAdapter({"themeManager", "thread", "user"})
    public static void setText(TextView textView, ThemeManager themeManager, Thread thread, User user) {
        textView.setText(thread.getTitle());
        if (thread.getPermission() != 0) {
            // add thread's permission hint
            ViewUtil.concatWithTwoSpacesForRtlSupport(textView,
                    "[" + textView.getContext().getString(R.string.thread_permission_hint)
                            + thread.getPermission() + "]");
        }
        // disable TextView if user has no permission to access this thread
        boolean hasPermission = user.getPermission() >= thread.getPermission();
        textView.setEnabled(hasPermission);

        // add thread's replies count to each thread
        ViewUtil.concatWithTwoSpacesForRtlSupport(textView, String.valueOf(thread.getReplies()),
                hasPermission ? themeManager.getGentleAccentColor()
                        : themeManager.getHintOrDisabledGentleAccentColor());
    }

    public static class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemThreadBinding itemThreadBinding;

        public BindingViewHolder(ItemThreadBinding itemThreadBinding) {
            super(itemThreadBinding.getRoot());

            this.itemThreadBinding = itemThreadBinding;
        }
    }
}
