package cl.monsoon.s1next.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Forum;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.util.ColorUtil;
import cl.monsoon.s1next.util.TextViewHelper;

/**
 * This Adapter behaves different from each other.
 * The item view is always the current forum.
 * And its dropdown view is always the sub-forums list.
 * The item view never changes when we select dropdown item.
 * <p>
 * We need to reselect the first dropdown item if dropdown selection changes
 * in order to let us get the correct position of sub-forums later.
 */
public class SubForumBaseAdapter extends BaseAdapter {

    private static final long CURRENT_FORUM_ID = Integer.MIN_VALUE;
    private static final long SUB_FORUMS_HEADER_ID = Integer.MAX_VALUE;

    private final LayoutInflater mLayoutInflater;

    private CharSequence mCurrentForumTitle;
    private final CharSequence mDropdownHeaderTitle;
    private final List<Forum> mSubForms;

    private final int mSecondaryTextColor;

    private TextView mSpinnerItemView;

    public SubForumBaseAdapter(Context context, CharSequence currentForumTitle, List<Forum> subForms) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.mCurrentForumTitle = currentForumTitle;
        this.mSubForms = subForms;

        mDropdownHeaderTitle =
                context.getText(R.string.toolbar_spinner_drop_down_sub_forums_header_title);
        mSecondaryTextColor =
                ColorUtil.a(Config.getColorAccent(), Config.getSecondaryTextAlpha());
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        // one for the Forum title
        // another for the sub-forums header
        return mSubForms.size() + 2;
    }

    @Override
    public Forum getItem(int position) {
        if (isCurrentForum(position) || isSubForumHeader(position)) {
            throw new IllegalStateException("Position can't be " + position + ".");
        }

        return mSubForms.get(position - 2);
    }

    @Override
    public long getItemId(int position) {
        if (isCurrentForum(position)) {
            return CURRENT_FORUM_ID;
        } else if (isSubForumHeader(position)) {
            return SUB_FORUMS_HEADER_ID;
        }

        return Long.parseLong(getItem(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mSpinnerItemView == null) {
            mSpinnerItemView =
                    (TextView)
                            mLayoutInflater.inflate(R.layout.toolbar_spinner_item, parent, false);
        }
        mSpinnerItemView.setText(mCurrentForumTitle);

        return mSpinnerItemView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView =
                    mLayoutInflater.inflate(
                            R.layout.toolbar_spinner_dropdown_item_with_header, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mHeaderView = (TextView) convertView.findViewById(R.id.header);
            viewHolder.mDropdownItemView = (TextView) convertView.findViewById(android.R.id.text1);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TextView headerView = viewHolder.mHeaderView;
        TextView dropdownItemView = viewHolder.mDropdownItemView;
        if (isCurrentForum(position)) {
            headerView.setVisibility(View.GONE);
            dropdownItemView.setVisibility(View.VISIBLE);

            dropdownItemView.setText(mCurrentForumTitle);
        } else if (isSubForumHeader(position)) {
            headerView.setVisibility(View.VISIBLE);
            dropdownItemView.setVisibility(View.GONE);

            headerView.setText(mDropdownHeaderTitle);
        } else {
            headerView.setVisibility(View.GONE);
            dropdownItemView.setVisibility(View.VISIBLE);

            Forum forum = getItem(position);

            dropdownItemView.setText(forum.getName());
            // add today's posts count to each forum
            if (forum.getTodayPosts() != 0) {
                int start = dropdownItemView.length();
                TextViewHelper.appendWithTwoSpaces(dropdownItemView, forum.getTodayPosts());
                TextViewHelper.setForegroundColor(
                        dropdownItemView, mSecondaryTextColor, start, dropdownItemView.length());
            }
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return !isSubForumHeader(position);
    }

    private boolean isCurrentForum(int position) {
        return position == 0;
    }

    private boolean isSubForumHeader(int position) {
        return position == 1;
    }

    public void setItemTitle(CharSequence currentForumTitle) {
        this.mCurrentForumTitle = currentForumTitle;

        if (mSpinnerItemView != null) {
            mSpinnerItemView.setText(mCurrentForumTitle);
        }
    }

    private static class ViewHolder {

        private TextView mHeaderView;
        private TextView mDropdownItemView;
    }
}
