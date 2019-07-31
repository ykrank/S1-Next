package me.ykrank.s1next.binding;

import android.content.res.ColorStateList;
import androidx.databinding.BindingAdapter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.core.graphics.drawable.DrawableCompat;
import android.widget.ProgressBar;

/**
 * Created by ykrank on 2017/9/28.
 */

public class ProgressBarBindingAdapter {

    @BindingAdapter("progressTint")
    public static void setProgressBarTint(ProgressBar view, @ColorInt int oldTintColor, @ColorInt int tintColor) {
        if (oldTintColor != tintColor) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setProgressTintList(ColorStateList.valueOf(tintColor));
            } else {
                Drawable drawable = view.getProgressDrawable().mutate();
                DrawableCompat.setTint(drawable, tintColor);
                view.setProgressDrawable(drawable);
                if (drawable instanceof LayerDrawable) {
                    LayerDrawable stars = (LayerDrawable) drawable;
                    stars.getDrawable(1).setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP); // PARTIAL OR FULL?
                    stars.getDrawable(2).setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP); // PARTIAL OR FULL?
                }
            }
        }
    }
}
