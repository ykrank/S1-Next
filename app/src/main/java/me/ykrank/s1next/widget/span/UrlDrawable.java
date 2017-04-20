package me.ykrank.s1next.widget.span;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.ykrank.s1next.util.L;

/**
 * Implements {@link Drawable.Callback} in order to show animated GIFs in the TextView.
 * <p>
 * Used in {@link GlideImageGetter}.
 */
final class UrlDrawable extends Drawable implements Drawable.Callback {
    @Nullable
    private Drawable initDrawable;
    private Drawable mDrawable;

    private String url;

    public UrlDrawable(String url, @Nullable Drawable initDrawable) {
        this.url = url;
        setInitDrawable(initDrawable);
    }

    public void setInitDrawable(@Nullable Drawable initDrawable) {
        this.initDrawable = initDrawable;
    }

    @NonNull
    public Rect getInitRect() {
        if (initDrawable != null) {
            return initDrawable.getBounds();
        }
        return new Rect();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        boolean drawn = false;
        if (mDrawable != null) {
            try {
                mDrawable.draw(canvas);
                drawn = true;
            } catch (Exception e) {
                L.report("UrlDrawable##url:" + url + ",GlideDrawable:" + mDrawable, e);
            }
        }
        if (!drawn && initDrawable != null) {
            initDrawable.draw(canvas);
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
