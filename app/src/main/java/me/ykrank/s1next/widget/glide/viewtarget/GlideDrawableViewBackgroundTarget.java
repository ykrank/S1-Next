package me.ykrank.s1next.widget.glide.viewtarget;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SquaringDrawable;

/**
 * fork from {@linkplain com.bumptech.glide.request.target.GlideDrawableImageViewTarget}
 * <p>
 * A {@link com.bumptech.glide.request.target.Target} that can display an {@link android.graphics.drawable.Drawable} in
 * an {@link View}.
 */
public class GlideDrawableViewBackgroundTarget extends ViewBackgroundTarget<GlideDrawable> {
    private static final float SQUARE_RATIO_MARGIN = 0.05f;
    private int maxLoopCount;
    private GlideDrawable resource;

    /**
     * Constructor for an {@link com.bumptech.glide.request.target.Target} that can display an
     * {@link GlideDrawable} in an {@link ImageView}.
     *
     * @param view The view to display the drawable in.
     */
    public GlideDrawableViewBackgroundTarget(View view) {
        this(view, GlideDrawable.LOOP_FOREVER);
    }

    /**
     * Constructor for an {@link com.bumptech.glide.request.target.Target} that can display an
     * {@link GlideDrawable} in an {@link ImageView}.
     *
     * @param view         The view to display the drawable in.
     * @param maxLoopCount A value to pass to to {@link GlideDrawable}s
     *                     indicating how many times they should repeat their animation (if they have one). See
     *                     {@link GlideDrawable#setLoopCount(int)}.
     */
    public GlideDrawableViewBackgroundTarget(View view, int maxLoopCount) {
        super(view);
        this.maxLoopCount = maxLoopCount;
    }

    /**
     * {@inheritDoc}
     * If no {@link GlideAnimation} is given or if the animation does not set the
     * {@link android.graphics.drawable.Drawable} on the view
     *
     * @param resource  {@inheritDoc}
     * @param animation {@inheritDoc}
     */
    @Override
    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
        if (!resource.isAnimated()) {
            //TODO: Try to generalize this to other sizes/shapes.
            // This is a dirty hack that tries to make loading square thumbnails and then square full images less costly
            // by forcing both the smaller thumb and the larger version to have exactly the same intrinsic dimensions.
            // If a drawable is replaced in an ImageView by another drawable with different intrinsic dimensions,
            // the ImageView requests a layout. Scrolling rapidly while replacing thumbs with larger images triggers
            // lots of these calls and causes significant amounts of jank.
            float viewRatio = view.getWidth() / (float) view.getHeight();
            float drawableRatio = resource.getIntrinsicWidth() / (float) resource.getIntrinsicHeight();
            if (Math.abs(viewRatio - 1f) <= SQUARE_RATIO_MARGIN
                    && Math.abs(drawableRatio - 1f) <= SQUARE_RATIO_MARGIN) {
                resource = new SquaringDrawable(resource, view.getWidth());
            }
        }
        super.onResourceReady(resource, animation);
        this.resource = resource;
        resource.setLoopCount(maxLoopCount);
        resource.start();
    }

    /**
     * Sets the drawable on the view
     *
     * @param resource The {@link android.graphics.drawable.Drawable} to display in the view.
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void setResource(GlideDrawable resource) {
        view.setBackgroundDrawable(resource);
    }

    @Override
    public void onStart() {
        if (resource != null) {
            resource.start();
        }
    }

    @Override
    public void onStop() {
        if (resource != null) {
            resource.stop();
        }
    }
}
