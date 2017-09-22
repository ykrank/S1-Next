package me.ykrank.s1next.widget.uglyfix;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import me.ykrank.s1next.util.L;

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
            L.report(e);
            return false;
        }
    }
}
