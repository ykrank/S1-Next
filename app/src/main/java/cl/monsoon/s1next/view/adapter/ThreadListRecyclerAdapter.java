package cl.monsoon.s1next.view.adapter;

import android.content.Context;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.util.ViewUtil;

public final class ThreadListRecyclerAdapter extends RecyclerAdapter<Thread, ThreadListRecyclerAdapter.ViewHolder> {

    private static final String THREAD_PERMISSION_HINT_PREFIX =
            App.get().getString(R.string.thread_activity_thread_permission);

    @Inject
    ThemeManager mThemeManager;

    @Inject
    User mUser;

    private final int mSecondaryTextColor;

    public ThreadListRecyclerAdapter(Context context) {
        setHasStableIds(true);

        App.getAppComponent(context).inject(this);
        mSecondaryTextColor = mThemeManager.getSecondaryTextColor();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multi_line_text,
                parent, false);

        return new ViewHolder(view, mThemeManager);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Thread thread = mList.get(position);

        TextView textView = holder.textView;
        textView.setText(thread.getTitle());
        if (thread.getPermission() != 0) {
            // add thread's permission hint
            ViewUtil.concatWithTwoSpacesForRtlSupport(textView,
                    "[" + THREAD_PERMISSION_HINT_PREFIX + thread.getPermission() + "]");
        }
        // disable TextView if user has no permission to access this thread
        holder.setTextViewEnabled(mUser.getPermission() >= thread.getPermission());

        // add thread's replies count to each thread
        ViewUtil.concatWithTwoSpacesForRtlSupport(textView, String.valueOf(thread.getReplies()),
                mSecondaryTextColor);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mList.get(position).getId());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        private final int mDefaultTextViewColor;
        private final int mDisabledTextViewColor;

        public ViewHolder(View itemView, ThemeManager themeManager) {
            super(itemView);

            textView = (TextView) itemView;

            mDefaultTextViewColor = textView.getCurrentTextColor();
            mDisabledTextViewColor = ColorUtils.setAlphaComponent(mDefaultTextViewColor,
                    themeManager.getDisabledOrHintTextAlpha());
        }

        public void setTextViewEnabled(Boolean enabled) {
            if (enabled) {
                textView.setTextColor(mDefaultTextViewColor);
            } else {
                textView.setTextColor(mDisabledTextViewColor);
            }
            textView.setEnabled(enabled);
        }
    }
}
