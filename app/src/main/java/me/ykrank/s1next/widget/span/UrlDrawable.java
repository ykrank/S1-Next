package me.ykrank.s1next.widget.span;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.ImageSpan;

/**
 * Implements {@link Drawable.Callback} in order to show animated GIFs in the TextView.
 * <p>
 * Used in {@link GlideImageGetter}.
 */
final class UrlDrawable extends Drawable implements Drawable.Callback {
    @Nullable
    private final Rect initRect;
    private Drawable mDrawable;
    @Nullable
    private ImageSpan imageSpan;

    private String url;

    public UrlDrawable(String url, @Nullable Rect rect) {
        this.initRect = rect;
        this.url = url;

        if (rect != null) {
            setBounds(rect);
        }
    }

    @Nullable
    public Rect getInitRect() {
        return initRect;
    }

    public String getUrl() {
        return url;
    }

    @Nullable
    public ImageSpan getImageSpan() {
        return imageSpan;
    }

    public void setImageSpan(@Nullable ImageSpan imageSpan) {
        this.imageSpan = imageSpan;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mDrawable != null) {
            mDrawable.draw(canvas);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (mDrawable != null) {
            mDrawable.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (mDrawable != null) {
            mDrawable.setColorFilter(cf);
        }
    }

    @Override
    public int getOpacity() {
        if (mDrawable != null) {
            return mDrawable.getOpacity();
        }
        return PixelFormat.UNKNOWN;
    }

    public void setDrawable(@Nullable Drawable drawable) {
        if (this.mDrawable != null) {
            this.mDrawable.setCallback(null);
        }
        if (drawable != null) {
            drawable.setCallback(this);
        }
        this.mDrawable = drawable;
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        if (getCallback() != null) {
            getCallback().invalidateDrawable(who);
        }
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
        if (getCallback() != null) {
            getCallback().scheduleDrawable(who, what, when);
        }
    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
        if (getCallback() != null) {
            getCallback().unscheduleDrawable(who, what);
        }
    }
}
