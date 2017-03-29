package me.ykrank.s1next.widget.glide.viewtarget;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.shizhefei.view.largeimage.LargeImageView;
import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory;

import java.io.File;

/**
 * fork from {@linkplain com.bumptech.glide.request.target.GlideDrawableImageViewTarget}
 * <p>
 * A {@link com.bumptech.glide.request.target.Target} that can display an {@link android.graphics.drawable.Drawable} in
 * an {@link LargeImageView}.
 */
public class GlideDrawableLargeImageViewTarget extends LargeImageViewTarget {

    /**
     * Constructor for an {@link com.bumptech.glide.request.target.Target} that can display an
     * {@link GlideDrawable} in an {@link ImageView}.
     *
     * @param view The view to display the drawable in.
     */
    public GlideDrawableLargeImageViewTarget(LargeImageView view) {
        super(view);
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
    public void onResourceReady(File resource, GlideAnimation<? super File> animation) {
        view.setImage(new FileBitmapDecoderFactory(resource));
    }
}
