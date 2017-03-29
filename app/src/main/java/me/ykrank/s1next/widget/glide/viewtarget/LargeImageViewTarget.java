package me.ykrank.s1next.widget.glide.viewtarget;

/**
 * Created by ykrank on 2017/1/6.
 * <p>
 * fork from {@link com.bumptech.glide.request.target.ImageViewTarget}
 */

import android.graphics.drawable.Drawable;

import com.bumptech.glide.request.target.ViewTarget;
import com.shizhefei.view.largeimage.LargeImageView;

import java.io.File;

/**
 * fork from {@linkplain com.bumptech.glide.request.target.ImageViewTarget}
 * <p>
 * A base {@link com.bumptech.glide.request.target.Target} for displaying resources in
 * {@link LargeImageView}s.
 */
public abstract class LargeImageViewTarget extends ViewTarget<LargeImageView, File> {

    public LargeImageViewTarget(LargeImageView view) {
        super(view);
    }

    /**
     * Sets the given {@link Drawable} on the view using
     *
     * @param placeholder {@inheritDoc}
     */
    @Override
    public void onLoadStarted(Drawable placeholder) {
        view.setImageDrawable(placeholder);
    }

    /**
     * Sets the given {@link Drawable} on the view using
     *
     * @param errorDrawable {@inheritDoc}
     */
    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        view.setImageDrawable(errorDrawable);
    }

    /**
     * Sets the given {@link Drawable} on the view using
     *
     * @param placeholder {@inheritDoc}
     */
    @Override
    public void onLoadCleared(Drawable placeholder) {
        view.setImageDrawable(placeholder);
    }

}
