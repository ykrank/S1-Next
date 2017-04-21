package me.ykrank.s1next.widget.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * A {@link com.bumptech.glide.load.Transformation} for transforming {@link android.graphics.Bitmap}'s
 * size not to exceed the OpenGl texture size limit.
 */
public final class GlMaxTextureSizeBitmapTransformation extends BitmapTransformation {

    public GlMaxTextureSizeBitmapTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        final int resWidth = toTransform.getWidth();
        final int resHeight = toTransform.getHeight();
        float maxTextureSize = GlMaxTextureCalculator.getInstance().getGlMaxTextureSize();

        float sizeMultiplier = Math.min(maxTextureSize / resWidth, maxTextureSize / resHeight);
        if (sizeMultiplier < 1) {
            return TransformationUtil.sizeMultiplier(pool, toTransform, sizeMultiplier);
        } else {
            return toTransform;
        }
    }

    @Override
    public String getId() {
        return "TransformationUtil.GlMaxTextureSizeBitmapTransformation";
    }

}
