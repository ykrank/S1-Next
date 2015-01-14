package cl.monsoon.s1next.widget;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

public final class StateListDrawableWithTint extends StateListDrawable {

    private final ColorStateList mColorStateList;
    private final PorterDuff.Mode mMode;

    public StateListDrawableWithTint(@DrawableRes Drawable drawable, @NonNull ColorStateList colorStateList, @NonNull PorterDuff.Mode mode) {
        this.mColorStateList = colorStateList;
        this.mMode = mode;

        addState(new int[]{}, drawable);
    }

    @Override
    protected boolean onStateChange(int[] stateSet) {
        // mColorStateList is null when constructor invokes super() implicitly
        //noinspection ConstantConditions
        if (mColorStateList != null) {
            int color = mColorStateList.getColorForState(stateSet, Color.TRANSPARENT);
            setColorFilter(color, mMode);
        }

        return super.onStateChange(stateSet);
    }
}
