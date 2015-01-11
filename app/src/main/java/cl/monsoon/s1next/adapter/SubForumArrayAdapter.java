package cl.monsoon.s1next.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cl.monsoon.s1next.model.Forum;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.util.ColorUtil;
import cl.monsoon.s1next.util.TextViewHelper;

public final class SubForumArrayAdapter extends ArrayAdapter<Forum> {

    private final int mResource;

    private final int mSecondaryTextColor;

    public SubForumArrayAdapter(Context context, int resource, List<Forum> objects) {
        super(context, resource, objects);

        this.mResource = resource;

        mSecondaryTextColor = ColorUtil.a(Config.getColorAccent(), Config.getSecondaryTextAlpha());
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(getItem(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mResource, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Forum forum = getItem(position);

        TextView textView = viewHolder.mTextView;
        textView.setText(forum.getName());
        // add today's posts count to each forum
        if (forum.getTodayPosts() != 0) {
            int start = textView.length();
            TextViewHelper.appendWithTwoSpaces(textView, forum.getTodayPosts());
            TextViewHelper.setForegroundColor(
                    textView, mSecondaryTextColor, start, textView.length());
        }

        return convertView;
    }

    private static class ViewHolder {

        private TextView mTextView;
    }
}
