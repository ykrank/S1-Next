/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cl.monsoon.s1next.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import cl.monsoon.s1next.R;

/**
 * An {@link android.widget.ImageView} that draws its contents inside a mask and draws a border
 * drawable on top. This is useful for applying a beveled look to image contents, but is also
 * flexible enough for use with other desired aesthetics.
 * <p>
 * Forked from https://github.com/google/iosched/blob/0a90bf8e6b90e9226f8c15b34eb7b1e4bf6d632e/android/src/main/java/com/google/samples/apps/iosched/ui/widget/BezelImageView.java
 * SHA: 36d88985ff6813fa9035530cd426393720a6f7b4
 */
public final class BezelImageView extends ImageView {

    private Paint mBlackPaint;
    private Paint mMaskedPaint;

    private Rect mBounds;
    private RectF mBoundsF;

    private Drawable mBorderDrawable;
    private Drawable mMaskDrawable;

    private ColorMatrixColorFilter mDesaturateColorFilter;
    private boolean mDesaturateOnPress = false;

    private boolean mCacheValid = false;
    private Bitmap mCacheBitmap;
    private int mCachedWidth;
    private int mCachedHeight;

    public BezelImageView(Context context) {
        this(context, null);
    }

    public BezelImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezelImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BezelImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        // attribute initialization
        final TypedArray a =
                context.obtainStyledAttributes(
                        attrs,
                        R.styleable.BezelImageView,
                        defStyleAttr,
                        defStyleRes);

        mMaskDrawable = a.getDrawable(R.styleable.BezelImageView_maskDrawable);
        if (mMaskDrawable != null) {
            mMaskDrawable.setCallback(this);
        }

        mBorderDrawable = a.getDrawable(R.styleable.BezelImageView_borderDrawable);
        if (mBorderDrawable != null) {
            mBorderDrawable.setCallback(this);
        }

        mDesaturateOnPress = a.getBoolean(R.styleable.BezelImageView_desaturateOnPress,
                mDesaturateOnPress);

        a.recycle();

        // other initialization
        mBlackPaint = new Paint();
        mBlackPaint.setColor(0xff000000);

        mMaskedPaint = new Paint();
        mMaskedPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // always want a cache allocated
        mCacheBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        if (mDesaturateOnPress) {
            // create a desaturate color filter for pressed state
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            mDesaturateColorFilter = new ColorMatrixColorFilter(cm);
        }
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        final boolean changed = super.setFrame(l, t, r, b);
        mBounds = new Rect(0, 0, r - l, b - t);
        mBoundsF = new RectF(mBounds);

        if (mBorderDrawable != null) {
            mBorderDrawable.setBounds(mBounds);
        }
        if (mMaskDrawable != null) {
            mMaskDrawable.setBounds(mBounds);
        }

        if (changed) {
            mCacheValid = false;
        }

        return changed;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (mBounds == null) {
            return;
        }

        int width = mBounds.width();
        int height = mBounds.height();

        if (width == 0 || height == 0) {
            return;
        }

        if (!mCacheValid || width != mCachedWidth || height != mCachedHeight) {
            // need to redraw the cache
            if (width == mCachedWidth && height == mCachedHeight) {
                // have a correct-sized bitmap cache already allocated
                // just erase it
                mCacheBitmap.eraseColor(0);
            } else {
                // allocate a new bitmap with the correct dimensions
                mCacheBitmap.recycle();
                //noinspection AndroidLintDrawAllocation
                mCacheBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                mCachedWidth = width;
                mCachedHeight = height;
            }

            Canvas cacheCanvas = new Canvas(mCacheBitmap);
            if (mMaskDrawable != null) {
                int sc = cacheCanvas.save();
                mMaskDrawable.draw(cacheCanvas);
                mMaskedPaint.setColorFilter((mDesaturateOnPress && isPressed())
                        ? mDesaturateColorFilter : null);
                cacheCanvas.saveLayer(mBoundsF, mMaskedPaint,
                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG);
                super.onDraw(cacheCanvas);
                cacheCanvas.restoreToCount(sc);
            } else if (mDesaturateOnPress && isPressed()) {
                int sc = cacheCanvas.save();
                cacheCanvas.drawRect(0, 0, mCachedWidth, mCachedHeight, mBlackPaint);
                mMaskedPaint.setColorFilter(mDesaturateColorFilter);
                cacheCanvas.saveLayer(mBoundsF, mMaskedPaint,
                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG);
                super.onDraw(cacheCanvas);
                cacheCanvas.restoreToCount(sc);
            } else {
                super.onDraw(cacheCanvas);
            }

            if (mBorderDrawable != null) {
                mBorderDrawable.draw(cacheCanvas);
            }
        }

        // draw from cache
        canvas.drawBitmap(mCacheBitmap, mBounds.left, mBounds.top, null);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mBorderDrawable != null && mBorderDrawable.isStateful()) {
            mBorderDrawable.setState(getDrawableState());
        }
        if (mMaskDrawable != null && mMaskDrawable.isStateful()) {
            mMaskDrawable.setState(getDrawableState());
        }
        if (isDuplicateParentStateEnabled()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        if (who == mBorderDrawable || who == mMaskDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(who);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mBorderDrawable || who == mMaskDrawable || super.verifyDrawable(who);
    }
}
