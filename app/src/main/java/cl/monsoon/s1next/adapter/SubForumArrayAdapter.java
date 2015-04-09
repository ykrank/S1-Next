package cl.monsoon.s1next.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cl.monsoon.s1next.model.Forum;
import cl.monsoon.s1next.singleton.Settings;
import cl.monsoon.s1next.util.ViewUtil;

public final class SubForumArrayAdapter extends ArrayAdapter<Forum> {

    @LayoutRes
    private final int mResource;

    private final int mSecondaryTextColor;

    public SubForumArrayAdapter(Context context, @LayoutRes int resource, List<Forum> objects) {
        super(context, resource, objects);

        this.mResource = resource;
        mSecondaryTextColor = Settings.Theme.getSecondaryTextColor();
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mResource, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Forum forum = getItem(position);

        TextView textView = viewHolder.textView;
        textView.setText(forum.getName());
        // add today's posts count to each forum
        if (forum.getTodayPosts() != 0) {
            int start = textView.length();
            ViewUtil.concatWithTwoSpaces(textView, forum.getTodayPosts());
            ViewUtil.setForegroundColor(textView, mSecondaryTextColor, start, textView.length());
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(getItem(position).getId());
    }

    private static class ViewHolder {

        private TextView textView;
    }
}
