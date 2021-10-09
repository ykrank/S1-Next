package com.github.ykrank.androidtools.widget.uglyfix;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.github.ykrank.androidtools.util.L;

/**
 * Created by ykrank on 2017/9/23.
 */

public class FixTextView extends AppCompatTextView {
    public FixTextView(Context context) {
        super(context);
    }

    public FixTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performLongClick() {
        try {
            return super.performLongClick();
        } catch (Exception e) {
            L.leaveMsg("FixTextView:" + this.getText());
            return false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (IndexOutOfBoundsException e) {
            //java.lang.IndexOutOfBoundsException in meizu
            L.leaveMsg("FixTextView onDraw IndexOutOfBoundsException");
        }
    }
}
