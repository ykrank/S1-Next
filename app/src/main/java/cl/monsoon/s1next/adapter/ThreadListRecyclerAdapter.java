package cl.monsoon.s1next.adapter;

import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Thread;
import cl.monsoon.s1next.singleton.Settings;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.ViewUtil;

public final class ThreadListRecyclerAdapter extends RecyclerAdapter<Thread, ThreadListRecyclerAdapter.ViewHolder> {

    private final int mSecondaryTextColor;

    private static final String THREAD_PERMISSION_HINT_PREFIX =
            App.getContext().getString(R.string.thread_activity_thread_permission);

    public ThreadListRecyclerAdapter() {
        setHasStableIds(true);

        mSecondaryTextColor = Settings.Theme.getSecondaryTextColor();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_line_list_item,
                parent, false);

        return new ViewHolder(view);
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
        holder.setTextViewEnabled(User.getPermission() >= thread.getPermission());

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

        public ViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView;

            mDefaultTextViewColor = textView.getCurrentTextColor();
            mDisabledTextViewColor = ColorUtils.setAlphaComponent(mDefaultTextViewColor,
                    Settings.Theme.getDisabledOrHintTextAlpha());
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
