package com.github.ykrank.androidtools.widget.glide.transformations;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * A {@link com.bumptech.glide.load.Transformation} for transforming {@link Bitmap}'s
 * size with a multiplier.
 */
public final class SizeMultiplierBitmapTransformation extends BitmapTransformation {

    private final float mSizeMultiplier;

    public SizeMultiplierBitmapTransformation(float sizeMultiplier) {

        this.mSizeMultiplier = sizeMultiplier;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return TransformationUtil.sizeMultiplier(pool, toTransform, mSizeMultiplier);
    }

    public String getId() {
        return "TransformationUtil.SizeMultiplierBitmapTransformation";
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(getId().getBytes());
    }
}
