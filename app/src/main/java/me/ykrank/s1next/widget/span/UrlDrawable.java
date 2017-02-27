package me.ykrank.s1next.widget.span;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import me.ykrank.s1next.util.L;

/**
 * Implements {@link Drawable.Callback} in order to show animated GIFs in the TextView.
 * <p>
 * Used in {@link GlideImageGetter}.
 */
final class UrlDrawable extends Drawable implements Drawable.Callback {

    private Drawable mDrawable;

    private String url;

    public UrlDrawable(String url) {
        this.url = url;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mDrawable != null) {
            try {
                mDrawable.draw(canvas);
            } catch (Exception e) {
                L.report("UrlDrawable##url:" + url + ",GlideDrawable:" + mDrawable, e);
            }
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

    public void setDrawable(Drawable drawable) {
        if (this.mDrawable != null) {
            this.mDrawable.setCallback(null);
        }
        drawable.setCallback(this);
        this.mDrawable = drawable;
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        if (getCallback() != null) {
            getCallback().invalidateDrawable(who);
        }
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (getCallback() != null) {
            getCallback().scheduleDrawable(who, what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (getCallback() != null) {
            getCallback().unscheduleDrawable(who, what);
        }
    }
}
