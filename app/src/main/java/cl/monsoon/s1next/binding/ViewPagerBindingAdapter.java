package cl.monsoon.s1next.binding;

import android.databinding.BindingAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public final class ViewPagerBindingAdapter {

    private ViewPagerBindingAdapter() {

    }

    @BindingAdapter("totalPage")
    public static void setTotalPage(ViewPager viewPager, int totalPage) {
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter != null) {
            pagerAdapter.notifyDataSetChanged();
        }
    }
}
