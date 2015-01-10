package cl.monsoon.s1next.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
import cl.monsoon.s1next.util.StringHelper;

/**
 * This Adapter behaves different from each other.
 * The item view title is always the forum title.
 * And its dropdown view is always the sub-forums item.
 * The item view never changes when we select dropdown item.
 * <p>
 * We need to reselect the first dropdown item if dropdown selection changes
 * in order to let us get the correct position of sub-forums later.
 */
public class SubForumBaseAdapter extends BaseAdapter {

    private static final long HEADER_ID = Integer.MIN_VALUE;

    private final LayoutInflater mLayoutInflater;

    private CharSequence mItemTitle;
    private final CharSequence mDropdownHeaderTitle;
    private final List<Forum> mSubForms;

    private final int mSecondaryTextColor;

    private TextView mSpinnerItemView;

    public SubForumBaseAdapter(Context context, CharSequence itemTitle, List<Forum> subForms) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.mItemTitle = itemTitle;
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
        return mSubForms.size() + 1;
    }

    @Override
    public Forum getItem(int position) {
        if (isHeader(position)) {
            throw new IllegalStateException("Position can't be " + position + ".");
        }

        return mSubForms.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        if (isHeader(position)) {
            return HEADER_ID;
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
        mSpinnerItemView.setText(mItemTitle);

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
            viewHolder.mItemView = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TextView headerView = viewHolder.mHeaderView;
        TextView itemView = viewHolder.mItemView;
        if (isHeader(position)) {
            headerView.setVisibility(View.VISIBLE);
            itemView.setVisibility(View.GONE);

            headerView.setText(mDropdownHeaderTitle);
            headerView.setTextColor(
                    ColorUtil.a(headerView.getCurrentTextColor(), Config.getSecondaryTextAlpha()));
        } else {
            headerView.setVisibility(View.GONE);
            itemView.setVisibility(View.VISIBLE);

            Forum forum = getItem(position);

            itemView.setText(forum.getName());
            // add today's posts count to each forum
            if (forum.getTodayPosts() != 0) {
                int start = itemView.getText().length();

                itemView.append(StringHelper.Util.TWO_SPACES + forum.getTodayPosts());
                Spannable spannable = (Spannable) itemView.getText();
                spannable.setSpan(
                        new ForegroundColorSpan(mSecondaryTextColor),
                        start,
                        itemView.getText().length(),
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                itemView.setText(spannable);
            }
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return !isHeader(position);
    }

    private boolean isHeader(int position) {
        return position == 0;
    }

    public void setItemTitle(CharSequence itemTitle) {
        this.mItemTitle = itemTitle;

        if (mSpinnerItemView != null) {
            mSpinnerItemView.setText(mItemTitle);
        }
    }

    private static class ViewHolder {

        private TextView mHeaderView;
        private TextView mItemView;
    }
}
