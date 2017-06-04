package me.ykrank.s1next.widget.glide.viewtarget;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * fork from {@linkplain com.bumptech.glide.request.target.DrawableImageViewTarget}
 * <p>
 * A {@link com.bumptech.glide.request.target.Target} that can display an {@link android.graphics.drawable.Drawable} in
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
