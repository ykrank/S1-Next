/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cl.monsoon.s1next.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.util.ResourceUtil;

/**
 * Lightweight implementation of ViewPager tabs. This looks similar to traditional actionBar tabs,
 * but allows for the view containing the tabs to be placed anywhere on screen. Text-related
 * attributes can also be assigned in XML - these will get propogated to the child TextViews
 * automatically.
 * <p>
 * Forked from https://android.googlesource.com/platform/packages/apps/ContactsCommon/+/master/src/com/android/contacts/common/list/ViewPagerTabs.java
 */
public final class ViewPagerTabs extends HorizontalScrollView implements ViewPager.OnPageChangeListener {

    private static final int TAB_SIDE_PADDING_IN_DPS = 16;

    private static final int[] ATTRS = new int[]{
            android.R.attr.textSize,
            android.R.attr.textStyle,
            android.R.attr.textColor,
            android.R.attr.textAllCaps
    };

    private ViewPager mViewPager;
    /**
     * LinearLayout that will contain the TextViews serving as tabs. This is the only child
     * of the parent HorizontalScrollView.
     */
    private LinearLayout mTabsHeader;

    private int mSidePadding;

    private int mTextSize;
    private int mTextStyle;
    private ColorStateList mTextColor;
    private boolean mTextAllCaps;

    private int mPrevSelected = -1;

    public ViewPagerTabs(Context context) {
        this(context, null);
    }

    public ViewPagerTabs(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerTabs(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewPagerTabs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setFillViewport(true);

        mSidePadding = (int) getResources().getDisplayMetrics().density * TAB_SIDE_PADDING_IN_DPS;

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, ATTRS);
        mTextSize = typedArray.getDimensionPixelSize(0, 0);
        mTextStyle = typedArray.getInt(1, 0);
        mTextColor = typedArray.getColorStateList(2);
        mTextAllCaps = typedArray.getBoolean(3, false);
        typedArray.recycle();

        mTabsHeader = new LinearLayout(context);
        addView(mTabsHeader, new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
    }

    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        addTabs(this.mViewPager.getAdapter());
    }

    private void addTabs(PagerAdapter pagerAdapter) {
        mTabsHeader.removeAllViews();

        final int count = pagerAdapter.getCount();
        for (int i = 0; i < count; i++) {
            addTab(pagerAdapter.getPageTitle(i), i);
        }
    }

    private void addTab(CharSequence tabTitle, final int position) {
        final TextView textView = new TextView(getContext());
        textView.setText(tabTitle);
        textView.setBackgroundResource(ResourceUtil.getResourceId(getContext().getTheme(),
                R.attr.selectableItemBackground));
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(v -> mViewPager.setCurrentItem(getRtlPosition(position)));

        textView.setOnLongClickListener(new OnTabLongClickListener(position));

        // Assign various text appearance related attributes to child views.
        if (mTextSize > 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        }
        if (mTextStyle > 0) {
            textView.setTypeface(textView.getTypeface(), mTextStyle);
        }
        if (mTextColor != null) {
            textView.setTextColor(mTextColor);
        }
        textView.setAllCaps(mTextAllCaps);
        textView.setPadding(mSidePadding, 0, mSidePadding, 0);

        mTabsHeader.addView(textView, new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1));

        // default to the first child being selected
        if (position == 0) {
            mPrevSelected = 0;
            textView.setSelected(true);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        position = getRtlPosition(position);
        if (mPrevSelected >= 0) {
            mTabsHeader.getChildAt(mPrevSelected).setSelected(false);
        }
        final View selectedChild = mTabsHeader.getChildAt(position);
        selectedChild.setSelected(true);

        // update scroll position
        final int scrollPos = selectedChild.getLeft() - (getWidth() - selectedChild.getWidth()) / 2;
        smoothScrollTo(scrollPos, 0);
        mPrevSelected = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private int getRtlPosition(int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                return mTabsHeader.getChildCount() - 1 - position;
            }
        }
        return position;
    }

    /**
     * Simulates actionbar tab behavior by showing a toast with the tab title when long clicked.
     */
    private class OnTabLongClickListener implements OnLongClickListener {

        final int mPosition;

        public OnTabLongClickListener(int position) {
            this.mPosition = position;
        }

        @Override
        public boolean onLongClick(View v) {
            final int[] location = new int[2];
            getLocationOnScreen(location);

            final int width = getWidth();
            final int height = getHeight();
            final int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;

            Toast toast = Toast.makeText(getContext(),
                    mViewPager.getAdapter().getPageTitle(mPosition), Toast.LENGTH_SHORT);
            // show the toast under the tab
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                    location[0] + width / 2 - screenWidth / 2, location[1] + height);
            toast.show();

            return true;
        }
    }
}
