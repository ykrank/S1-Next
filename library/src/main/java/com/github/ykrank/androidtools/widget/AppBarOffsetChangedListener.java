package com.github.ykrank.androidtools.widget;

import com.google.android.material.appbar.AppBarLayout;

/**
 * Created by ykrank on 2017/1/9.
 */

public abstract class AppBarOffsetChangedListener implements AppBarLayout.OnOffsetChangedListener {
    private int verticalOffsetTemp;

    public abstract void onStateChanged(AppBarLayout appBarLayout, int oldVerticalOffset, int verticalOffset);

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        onStateChanged(appBarLayout, verticalOffsetTemp, verticalOffset);
        verticalOffsetTemp = verticalOffset;
    }
}
