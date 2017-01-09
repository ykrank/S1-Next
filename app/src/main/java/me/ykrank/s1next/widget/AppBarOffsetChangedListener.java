package me.ykrank.s1next.widget;

import android.support.annotation.IntDef;
import android.support.design.widget.AppBarLayout;

/**
 * Created by ykrank on 2017/1/9.
 */

public abstract class AppBarOffsetChangedListener implements AppBarLayout.OnOffsetChangedListener {

    private int verticalOffsetTemp;


    public static final int UP = 0;
    public static final int DOWN = 1;

    @IntDef({UP, DOWN})
    public @interface Direction {
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, @Direction int direction, int verticalOffset);

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int direction = verticalOffset > verticalOffsetTemp ? DOWN : UP;
        verticalOffsetTemp = verticalOffset;
        onStateChanged(appBarLayout, direction, verticalOffset);
    }
}
