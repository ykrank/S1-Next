package cl.monsoon.s1next.widget;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Backport of {@link android.view.View#setBackgroundTintList(android.content.res.ColorStateList)}
 * and {@link android.view.View#setBackgroundTintMode(android.graphics.PorterDuff.Mode)} to API 20 and below.
 */
public final class StateListDrawableWithTint extends StateListDrawable {

    private final ColorStateList mColorStateList;
    private final PorterDuff.Mode mMode;

    public StateListDrawableWithTint(Drawable drawable, ColorStateList colorStateList, PorterDuff.Mode mode) {
        this.mColorStateList = colorStateList;
        this.mMode = mode;

        addState(new int[]{}, drawable);
    }

    @Override
    protected boolean onStateChange(int[] stateSet) {
        // mColorStateList hasn't been initialized when
        // superclass's constructor (which would call
        // in our constructor's first line implicitly)
        // invokes this method
        //noinspection ConstantConditions
        if (mColorStateList != null) {
            int color = mColorStateList.getColorForState(stateSet, Color.TRANSPARENT);
            setColorFilter(color, mMode);
        }

        return super.onStateChange(stateSet);
    }
}
