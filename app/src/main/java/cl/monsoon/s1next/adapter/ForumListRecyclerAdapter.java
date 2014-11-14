package cl.monsoon.s1next.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cl.monsoon.s1next.Config;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Forum;

public final class ForumListRecyclerAdapter extends RecyclerAdapter<Forum, ForumListRecyclerAdapter.ViewHolder> {

    public ForumListRecyclerAdapter() {
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =
                LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.single_line_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        TextView textView = viewHolder.mTextView;
        Forum forum = mList.get(i);

        viewHolder.mTextView.setText(forum.getName());
        // add today's posts count to each forum
        if (forum.getTodayPosts() != 0) {
            int start = textView.getText().length();

            textView.append("  " + forum.getTodayPosts());
            Spannable spannable = (Spannable) textView.getText();
            spannable.setSpan(
                    new ForegroundColorSpan(Config.getColorAccent()),
                    start,
                    textView.getText().length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            textView.setText(spannable);
        }
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mList.get(position).getId());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }
    }
}
