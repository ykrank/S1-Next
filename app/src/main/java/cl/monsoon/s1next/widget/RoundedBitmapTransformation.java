package cl.monsoon.s1next.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public final class RoundedBitmapTransformation extends BitmapTransformation {

    public RoundedBitmapTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        int size = Math.min(toTransform.getWidth(), toTransform.getHeight());
        int x = (toTransform.getWidth() - size) / 2;
        int y = (toTransform.getHeight() - size) / 2;

        Bitmap bitmap = Bitmap.createBitmap(toTransform, x, y, size, size);
        // do not need to call toTransform.recycle()
        // this is done by Glide automatically

        Bitmap resultBitmap = Bitmap.createBitmap(size, size, toTransform.getConfig());
        Canvas canvas = new Canvas(resultBitmap);

        Paint paint = new Paint();
        BitmapShader bitmapShader =
                new BitmapShader(bitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(bitmapShader);
        paint.setAntiAlias(true);

        float c = size / 2f;
        canvas.drawCircle(c, c, c, paint);
        bitmap.recycle();

        return resultBitmap;
    }

    @Override
    public String getId() {
        return "rounded_transform";
    }
}
