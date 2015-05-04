package cl.monsoon.s1next.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import cl.monsoon.s1next.singleton.GL;

public final class TransformationUtil {

    private TransformationUtil() {

    }

    /**
     * Forked from {@link com.bumptech.glide.load.resource.bitmap.TransformationUtils#fitCenter(Bitmap, BitmapPool, int, int)}.
     */
    private static Bitmap sizeMultiplier(BitmapPool pool, Bitmap toTransform, float sizeMultiplier) {
        final int targetWidth = (int) (sizeMultiplier * toTransform.getWidth());
        final int targetHeight = (int) (sizeMultiplier * toTransform.getHeight());

        Bitmap.Config config = getSafeConfig(toTransform);
        Bitmap toReuse = pool.get(targetWidth, targetHeight, config);
        if (toReuse == null) {
            toReuse = Bitmap.createBitmap(targetWidth, targetHeight, config);
        }
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
     * Copied from {@link com.bumptech.glide.load.resource.bitmap.TransformationUtils#getSafeConfig(Bitmap)}.
     */
    private static Bitmap.Config getSafeConfig(Bitmap bitmap) {
        return bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
    }

    public static class SizeMultiplierBitmapTransformation extends BitmapTransformation {

        private final float mSizeMultiplier;

        public SizeMultiplierBitmapTransformation(Context context, float sizeMultiplier) {
            super(context);

            this.mSizeMultiplier = sizeMultiplier;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return sizeMultiplier(pool, toTransform, mSizeMultiplier);
        }

        @Override
        public String getId() {
            return SizeMultiplierBitmapTransformation.class.getName();
        }
    }

    public static class GlMaxTextureSizeBitmapTransformation extends BitmapTransformation {

        public GlMaxTextureSizeBitmapTransformation(Context context) {
            super(context);
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            final int resWidth = toTransform.getWidth();
            final int resHeight = toTransform.getHeight();
            float maxTextureSize = GL.getGlMaxTextureSize();

            float sizeMultiplier = Math.min(maxTextureSize / resWidth, maxTextureSize / resHeight);
            if (sizeMultiplier < 1) {
                return sizeMultiplier(pool, toTransform, sizeMultiplier);
            } else {
                return toTransform;
            }
        }

        @Override
        public String getId() {
            return GlMaxTextureSizeBitmapTransformation.class.getName();
        }
    }
}
