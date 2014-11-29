package cl.monsoon.s1next.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Implement RecyclerView's OnItemTouchListener.
 * <p>
 * Forked from https://stackoverflow.com/questions/24471109/recyclerview-onclick
 */
public final class RecyclerViewOnItemTouchListener implements RecyclerView.OnItemTouchListener {

    private final OnItemClickListener mListener;
    private final GestureDetector mGestureDetector;

    public RecyclerViewOnItemTouchListener(Context context, OnItemClickListener listener) {
        this.mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        View childView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(motionEvent)) {
            mListener.onItemClick(recyclerView.getChildPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

    }

    public static interface OnItemClickListener {

        public void onItemClick(int position);
    }
}
