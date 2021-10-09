package com.github.ykrank.androidtools.widget.glide.viewtarget;

import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import android.view.View;

/**
 * fork from {@linkplain com.bumptech.glide.request.target.DrawableImageViewTarget}
 * <p>
 * A {@link com.bumptech.glide.request.target.Target} that can display an {@link Drawable} in
 * an {@link View}.
 */
public class DrawableViewBackgroundTarget extends ViewBackgroundTarget<Drawable> {
    public DrawableViewBackgroundTarget(View view) {
        super(view);
    }

    @Override
    protected void setResource(@Nullable Drawable resource) {
        ViewCompat.setBackground(view, resource);
    }
}
