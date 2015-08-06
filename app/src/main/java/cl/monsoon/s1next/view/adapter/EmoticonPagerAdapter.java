package cl.monsoon.s1next.view.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.List;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.widget.EmoticonFactory;

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
        RecyclerView.Adapter recyclerAdapter = new EmoticonGridRecyclerAdapter(mActivity,
                mEmoticonFactory.getEmoticonsByIndex(position));
        recyclerView.setAdapter(recyclerAdapter);

        // auto fit grid
        // forked from https://stackoverflow.com/questions/26666143/recyclerview-gridlayoutmanager-how-to-auto-detect-span-count#answer-27000759
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    @SuppressWarnings("deprecation")
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
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
