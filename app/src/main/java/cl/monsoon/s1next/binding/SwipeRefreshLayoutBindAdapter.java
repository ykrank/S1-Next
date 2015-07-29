package cl.monsoon.s1next.binding;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

public final class SwipeRefreshLayoutBindAdapter {

    @BindingAdapter("colorSchemeResources")
    public static void setColorSchemeResources(SwipeRefreshLayout swipeRefreshLayout, int[] colors) {
        swipeRefreshLayout.setColorSchemeColors(colors);
    }

    @BindingAdapter("enable")
    public static void setEnabled(SwipeRefreshLayout swipeRefreshLayout, boolean enabled) {
        swipeRefreshLayout.setEnabled(enabled);
    }
}
