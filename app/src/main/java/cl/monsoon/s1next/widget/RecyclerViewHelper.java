package cl.monsoon.s1next.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Forwards {@link android.support.v7.widget.RecyclerView.OnItemTouchListener}
 * to {@link cl.monsoon.s1next.widget.RecyclerViewHelper.OnItemClickListener}.
 * <p>
 * Forked from https://stackoverflow.com/questions/24471109/recyclerview-onclick
 */
public final class RecyclerViewHelper implements RecyclerView.OnItemTouchListener {

    private final OnItemClickListener mListener;
    private final GestureDetector mGestureDetector;

    public RecyclerViewHelper(Context context, RecyclerView recyclerView, OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;

        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                        if (childView != null) {
                            mListener.onItemLongClick(childView,
                                    recyclerView.getChildLayoutPosition(childView));
                        }
                    }
                });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, rv.getChildLayoutPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
}
