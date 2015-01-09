package cl.monsoon.s1next.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cl.monsoon.s1next.MyApplication;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Thread;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.ColorUtil;
import cl.monsoon.s1next.util.StringHelper;
import cl.monsoon.s1next.util.ViewHelper;

/**
 * Similar to {@see cl.monsoon.s1next.adapter.ForumListRecyclerAdapter}.
 */
public final class ThreadListRecyclerAdapter
        extends RecyclerAdapter<Thread, ThreadListRecyclerAdapter.ViewHolder> {

    private final int mSecondaryTextColor;

    private static final CharSequence THREAD_PERMISSION_HINT_PREFIX =
            MyApplication.getContext().getText(R.string.thread_activity_thread_permission);

    public ThreadListRecyclerAdapter() {
        setHasStableIds(true);

        mSecondaryTextColor =
                ColorUtil.a(Config.getColorAccent(), Config.getSecondaryTextAlpha());
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
        TextView textView = holder.mTextView;
        Thread thread = mList.get(position);

        textView.setText(thread.getTitle());
        int start = textView.getText().length();

        if (thread.getPermission() != 0) {
            // add thread's permission hint
            textView.append(
                    StringHelper.Util.TWO_SPACES
                            + "[" + THREAD_PERMISSION_HINT_PREFIX + thread.getPermission() + "]");
        }
        // disable TextView if user has not permission to access this thread
        holder.setTextViewEnabled(User.getPermission() >= thread.getPermission());

        // add thread's replies count to each thread
        textView.append(StringHelper.Util.TWO_SPACES + thread.getReplies());

        Spannable spannable = (Spannable) textView.getText();
        spannable.setSpan(
                new ForegroundColorSpan(mSecondaryTextColor),
                start,
                textView.getText().length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(spannable);
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
            ViewHelper.updateTextSize(new TextView[]{mTextView});
            ViewHelper.updateTextColorWhenS1Theme(new TextView[]{mTextView});

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
