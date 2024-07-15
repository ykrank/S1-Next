package me.ykrank.s1next.binding;

import android.content.res.ColorStateList;
import android.widget.ProgressBar;

import androidx.annotation.ColorInt;
import androidx.databinding.BindingAdapter;

/**
 * Created by ykrank on 2017/9/28.
 */

public class ProgressBarBindingAdapter {

    @BindingAdapter("progressTint")
    public static void setProgressBarTint(ProgressBar view, @ColorInt int oldTintColor, @ColorInt int tintColor) {
        if (oldTintColor != tintColor) {
            view.setProgressTintList(ColorStateList.valueOf(tintColor));
        }
    }
}
