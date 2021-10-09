package com.github.ykrank.androidtools.ui.internal;

import androidx.viewpager.widget.PagerAdapter;

public interface PagerCallback {

    /**
     * A callback to set actual total pages
     * which used for {@link PagerAdapter}。
     */
    void setTotalPages(int totalPages);
}
