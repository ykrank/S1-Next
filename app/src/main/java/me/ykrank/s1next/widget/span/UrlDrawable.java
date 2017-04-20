package me.ykrank.s1next.widget.span;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import me.ykrank.s1next.R;
import me.ykrank.s1next.util.L;

/**
 * Implements {@link Drawable.Callback} in order to show animated GIFs in the TextView.
 * <p>
 * Used in {@link GlideImageGetter}.
 */
final class UrlDrawable extends Drawable implements Drawable.Callback {
    @NonNull
    private Drawable unknownDrawable;
    private Drawable mDrawable;

    private String url;

    public UrlDrawable(@NonNull Context context, String url) {
        this.url = url;
        initUnknownDrawable(context);
    }
    
    private void initUnknownDrawable(Context context) {
        unknownDrawable = ContextCompat.getDrawable(context, R.mipmap.unknown_image);
        setBounds(new Rect(0, 0, unknownDrawable.getIntrinsicWidth(), unknownDrawable.getIntrinsicHeight()));
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mDrawable != null) {
            try {
                mDrawable.draw(canvas);
            } catch (Exception e) {
                L.report("UrlDrawable##url:" + url + ",GlideDrawable:" + mDrawable, e);
            }
        } else {
            unknownDrawable.draw(canvas);
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
