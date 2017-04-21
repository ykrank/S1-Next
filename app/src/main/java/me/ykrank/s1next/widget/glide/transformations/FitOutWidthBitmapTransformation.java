package me.ykrank.s1next.widget.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * A {@link com.bumptech.glide.load.Transformation} for transforming {@link Bitmap}'s
 * size match parent width.
 */
public final class FitOutWidthBitmapTransformation extends BitmapTransformation {

    public FitOutWidthBitmapTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        final int resWidth = toTransform.getWidth();
        final int resHeight = toTransform.getHeight();

        float maxTextureSize = GlMaxTextureCalculator.getInstance().getGlMaxTextureSize();
        float textureSizeMultiplier = Math.min(maxTextureSize / resWidth, maxTextureSize / resHeight);

        float fitOutSizeMultiplier = outWidth / resWidth;
        float sizeMultiplier = Math.min(textureSizeMultiplier, fitOutSizeMultiplier);

        if (sizeMultiplier < 1) {
            return TransformationUtil.sizeMultiplier(pool, toTransform, sizeMultiplier);
        } else {
            return toTransform;
        }
    }

    @Override
    public String getId() {
        return "TransformationUtil.FitOutWidthBitmapTransformation";
    }
}
