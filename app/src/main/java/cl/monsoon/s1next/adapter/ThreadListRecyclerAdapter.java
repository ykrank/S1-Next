package cl.monsoon.s1next.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cl.monsoon.s1next.MyApplication;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Thread;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.singleton.MyAccount;
import cl.monsoon.s1next.util.ColorUtil;
import cl.monsoon.s1next.util.ViewHelper;

public final class ThreadListRecyclerAdapter extends RecyclerAdapter<Thread, ThreadListRecyclerAdapter.ViewHolder> {

    private final int mSecondaryTextColor;

    private static final String THREAD_PERMISSION_HINT_PREFIX =
            MyApplication.getContext().getString(R.string.thread_activity_thread_permission);

    public ThreadListRecyclerAdapter() {
        setHasStableIds(true);

        mSecondaryTextColor = ColorUtil.a(Config.getColorAccent(), Config.getSecondaryTextAlpha());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.multi_line_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Thread thread = mList.get(position);

        TextView textView = holder.mTextView;
        textView.setText(thread.getTitle());
        int start = textView.length();

        if (thread.getPermission() != 0) {
            // add thread's permission hint
            ViewHelper.concatWithTwoSpaces(
                    textView,
                    "[" + THREAD_PERMISSION_HINT_PREFIX + thread.getPermission() + "]");
        }
        // disable TextView if user has no permission to access this thread
        holder.setTextViewEnabled(MyAccount.getPermission() >= thread.getPermission());

        // add thread's replies count to each thread
        ViewHelper.concatWithTwoSpaces(textView, thread.getReplies());
        ViewHelper.setForegroundColor(textView, mSecondaryTextColor, start, textView.length());
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mList.get(position).getId());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;

        private final int mDefaultTextViewColor;
        private final int mDisabledTextViewColor;

        public ViewHolder(View itemView) {
            super(itemView);

            mTextView = (TextView) itemView;
            ViewHelper.updateTextSize(mTextView);
            ViewHelper.updateTextColorWhenS1Theme(mTextView);

            mDefaultTextViewColor = mTextView.getCurrentTextColor();
            mDisabledTextViewColor =
                    ColorUtil.a(mDefaultTextViewColor, Config.getDisabledOrHintTextAlpha());
        }

        public void setTextViewEnabled(Boolean enabled) {
            if (enabled) {
                mTextView.setTextColor(mDefaultTextViewColor);
            } else {
                mTextView.setTextColor(mDisabledTextViewColor);
            }
            mTextView.setEnabled(enabled);
        }
    }
}
