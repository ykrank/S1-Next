package cl.monsoon.s1next.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cl.monsoon.s1next.util.ResourceUtil;

public final class VerticalDividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private final Drawable mDivider;

    private final boolean mRtl;
    private int mStartBound;

    public VerticalDividerItemDecoration(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(ATTRS);
        mDivider = typedArray.getDrawable(0);
        typedArray.recycle();

        mRtl = ResourceUtil.isRTL(context.getResources());
    }

    public VerticalDividerItemDecoration(Context context, @DimenRes int startBoundResId) {
        this(context);

        mStartBound = context.getResources().getDimensionPixelSize(startBoundResId);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        // do not draw for the first divider
        for (int i = 0; i < childCount; i++) {
            final View childView = parent.getChildAt(i);
            if (isDecorated(childView, parent)) {
                final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)
                        childView.getLayoutParams();
                final int top = childView.getBottom() + layoutParams.bottomMargin +
                        (int) childView.getTranslationY();
                final int bottom = top + mDivider.getIntrinsicHeight();

                if (mRtl) {
                    mDivider.setBounds(left, top, right - mStartBound, bottom);
                } else {
                    mDivider.setBounds(left + mStartBound, top, right, bottom);
                }
                mDivider.draw(c);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (isDecorated(view, parent)) {
            outRect.bottom = mDivider.getIntrinsicHeight();
        }
    }

    private boolean isDecorated(View view, RecyclerView parent) {
        return !(parent.getChildViewHolder(view) instanceof Undecorated);
    }

    /**
     * A marker interface that any {@link android.support.v7.widget.RecyclerView.ViewHolder}
     * implements this interface would not decorated with divider.
     */
    public interface Undecorated {}
}
