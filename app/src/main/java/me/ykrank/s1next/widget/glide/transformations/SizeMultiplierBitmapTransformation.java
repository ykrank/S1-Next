package me.ykrank.s1next.widget.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * A {@link com.bumptech.glide.load.Transformation} for transforming {@link android.graphics.Bitmap}'s
 * size with a multiplier.
 */
public final class SizeMultiplierBitmapTransformation extends BitmapTransformation {

    private final float mSizeMultiplier;

    public SizeMultiplierBitmapTransformation(Context context, float sizeMultiplier) {
        super(context);

        this.mSizeMultiplier = sizeMultiplier;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return TransformationUtil.sizeMultiplier(pool, toTransform, mSizeMultiplier);
    }

    @Override
    public String getId() {
        return "TransformationUtil.SizeMultiplierBitmapTransformation";
    }
}
