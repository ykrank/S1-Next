package com.github.ykrank.androidtools.widget.glide.transformations;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

public final class TransformationUtil {

    private TransformationUtil() {
    }

    /**
     * Applies a multiplier to the {@code toTransform}'s size.
     * <p>
     * Forked from {@link TransformationUtils#fitCenter(BitmapPool, Bitmap, int, int)} .
     *
     * @param sizeMultiplier The multiplier to apply to the {@code toTransform}'s dimensions.
     */
    public static Bitmap sizeMultiplier(BitmapPool pool, Bitmap toTransform, float sizeMultiplier) {
        final int targetWidth = Math.round(sizeMultiplier * toTransform.getWidth());
        final int targetHeight = Math.round(sizeMultiplier * toTransform.getHeight());

        Bitmap.Config config = getSafeConfig(toTransform);
        Bitmap toReuse = pool.get(targetWidth, targetHeight, config);
        // We don't add or remove alpha, so keep the alpha setting of the Bitmap we were given.
        TransformationUtils.setAlpha(toTransform, toReuse);

        Canvas canvas = new Canvas(toReuse);
        Matrix matrix = new Matrix();
        matrix.setScale(sizeMultiplier, sizeMultiplier);
        Paint paint = new Paint(TransformationUtils.PAINT_FLAGS);
        canvas.drawBitmap(toTransform, matrix, paint);

        return toReuse;
    }

    /**
     * Copied from {@link TransformationUtils#getSafeConfig(Bitmap)}.
     */
    private static Bitmap.Config getSafeConfig(Bitmap bitmap) {
        return bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
    }

}
