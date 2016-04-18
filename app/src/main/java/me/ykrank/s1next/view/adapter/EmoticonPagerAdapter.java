package me.ykrank.s1next.view.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.List;

import me.ykrank.s1next.R;
import me.ykrank.s1next.util.ViewTreeObserverUtil;
import me.ykrank.s1next.widget.EmoticonFactory;

public final class EmoticonPagerAdapter extends PagerAdapter {

    private final Activity mActivity;

    private final float mEmoticonWidth;
    private final int mEmoticonGridPadding;

    private final EmoticonFactory mEmoticonFactory;
    private final List<String> mEmoticonTypeTitles;

    public EmoticonPagerAdapter(Activity activity) {
        this.mActivity = activity;

        Resources resources = activity.getResources();
        mEmoticonWidth = resources.getDimension(R.dimen.minimum_touch_target_size);
        mEmoticonGridPadding = resources.getDimensionPixelSize(R.dimen.emoticon_padding);

        mEmoticonFactory = new EmoticonFactory(activity);
        mEmoticonTypeTitles = mEmoticonFactory.getEmotionTypeTitles();
    }

    @Override
    public int getCount() {
        return mEmoticonTypeTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mEmoticonTypeTitles.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RecyclerView recyclerView = new RecyclerView(mActivity);
        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(false);
        recyclerView.setPadding(0, mEmoticonGridPadding, 0, mEmoticonGridPadding);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        RecyclerView.Adapter<EmoticonGridRecyclerAdapter.BindingViewHolder> recyclerAdapter =
                new EmoticonGridRecyclerAdapter(mActivity,
                        mEmoticonFactory.getEmoticonsByIndex(position));
        recyclerView.setAdapter(recyclerAdapter);

        // auto fit grid
        // forked from http://stackoverflow.com/a/27000759
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        ViewTreeObserverUtil.removeOnGlobalLayoutListener(
                                recyclerView.getViewTreeObserver(), this);
                        int measuredWidth = recyclerView.getMeasuredWidth();
                        int spanCount = (int) Math.floor(measuredWidth / mEmoticonWidth);
                        gridLayoutManager.setSpanCount(spanCount);
                        gridLayoutManager.requestLayout();
                    }
                });

        container.addView(recyclerView);

        return recyclerView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
