package cl.monsoon.s1next.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

/**
 * An ImageView that supports multi-touch (zoom in/out and move).
 */
public final class TouchImageView extends ImageView implements View.OnTouchListener {

    private static final float MAX_SCALE = 5;
    private static final float MIN_SCALE = .1f;

    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    private final Matrix mMatrix = new Matrix();

    private boolean mIsFirst = true;

    public TouchImageView(Context context) {
        super(context);

        init();
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @SuppressWarnings("UnusedDeclaration")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TouchImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    private void init() {
        // scale
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        // move
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());

        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }

        int srcWidth = getDrawable().getIntrinsicWidth();
        int srcHeight = getDrawable().getIntrinsicHeight();
        if (srcWidth == -1 || srcHeight == -1) {
            return;
        }

        if (mIsFirst) {
            mIsFirst = false;

            int dx = getWidth() - srcWidth;
            int dy = getHeight() - srcHeight;
            postTranslate(dx / 2, dy / 2);
        }

        inspectTranslateBounds(mMatrix);
        canvas.save();
        // don't use canvas.setMatrix(mMatrix)
        // because it won't offset the Status Bar.
        canvas.concat(mMatrix);
        getDrawable().draw(canvas);
        canvas.restore();
    }

    void postTranslate(float distanceX, float distanceY) {
        mMatrix.postTranslate(distanceX, distanceY);

        invalidate();
    }

    void postScale(float scale, float focusX, float focusY) {
        float srcScale = MatrixHelper.getScale(mMatrix);

        // don't let the drawable get too small or too large
        if (scale > 1) {
            scale = Math.min(scale, MAX_SCALE / srcScale);
        } else {
            scale = Math.max(scale, MIN_SCALE / srcScale);
        }

        mMatrix.postScale(scale, scale, focusX, focusY);

        invalidate();
    }

    private void inspectTranslateBounds(Matrix matrix) {
        // We need to set this TouchImageView's layout to
        // match_parent to get its container's width or height.
        // Or just use TouchImageView#getParent() to get these.
        int containerWidth = getWidth();
        int containerHeight = getHeight();

        RectF bounds =
                MatrixHelper.getBounds(
                        matrix,
                        getDrawable().getIntrinsicWidth(),
                        getDrawable().getIntrinsicHeight());

        float dx = containerWidth - bounds.right;
        float dy = containerHeight - bounds.bottom;

        // don't let the drawable move out of screen
        if (bounds.width() > containerWidth) {
            if (bounds.left > 0) {
                matrix.postTranslate(-bounds.left, 0);
            } else if (dx > 0) {
                matrix.postTranslate(dx, 0);
            }
        } else {
            if (bounds.left < 0) {
                matrix.postTranslate(-bounds.left, 0);
            } else if (dx < 0) {
                matrix.postTranslate(dx, 0);
            }
        }
        if (bounds.height() > containerHeight) {
            if (bounds.top > 0) {
                matrix.postTranslate(0, -bounds.top);
            } else if (dy > 0) {
                matrix.postTranslate(0, dy);
            }
        } else {
            if (bounds.top < 0) {
                matrix.postTranslate(0, -bounds.top);
            } else if (dy < 0) {
                matrix.postTranslate(0, dy);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (getDrawable() == null) {
            return false;
        }

        // let the ScaleGestureDetector and GestureDetector inspect all events
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            postScale(scaleFactor, detector.getFocusX(), detector.getFocusY());

            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mScaleGestureDetector != null && mScaleGestureDetector.isInProgress()) {
                return false;
            }
            postTranslate(-distanceX, -distanceY);

            return true;
        }
    }

    private static class MatrixHelper extends RectF {

        private static final float[] VALUES = new float[9];

        /**
         * Get drawable's scale.
         */
        public static float getScale(Matrix matrix) {
            matrix.getValues(VALUES);

            return VALUES[Matrix.MSCALE_X];
        }

        /**
         * Get drawable's bounds.
         */
        public static RectF getBounds(Matrix matrix, int srcWidth, int srcHeight) {
            matrix.getValues(VALUES);

            RectF rectF = new RectF();
            rectF.left = VALUES[Matrix.MTRANS_X];
            rectF.top = VALUES[Matrix.MTRANS_Y];
            rectF.right = rectF.left + srcWidth * VALUES[Matrix.MSCALE_X];
            rectF.bottom = rectF.top + srcHeight * VALUES[Matrix.MSCALE_X];

            return rectF;
        }
    }
}
