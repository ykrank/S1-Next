package me.ykrank.s1next.widget.glide.viewtarget;

/**
 * Created by ykrank on 2017/1/6.
 * <p>
 * fork from {@link com.bumptech.glide.request.target.ImageViewTarget}
 */

import android.graphics.drawable.Drawable;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import me.ykrank.s1next.widget.PhotoView;

/**
 * fork from {@linkplain com.bumptech.glide.request.target.ImageViewTarget}
 *
 * A base {@link com.bumptech.glide.request.target.Target} for displaying resources in
 * {@link me.ykrank.s1next.widget.PhotoView}s.
 *
 * @param <Z> The type of resource that this target will display in the wrapped {@link me.ykrank.s1next.widget.PhotoView}.
 */
public abstract class PhotoViewTarget<Z> extends ViewTarget<PhotoView, Z> implements GlideAnimation.ViewAdapter {

    public PhotoViewTarget(PhotoView view) {
        super(view);
    }

    /**
     * Returns the current {@link android.graphics.drawable.Drawable} being displayed in the view using
     */
    @Override
    public Drawable getCurrentDrawable() {
        return view.getDrawable();
    }

    /**
     * Sets the given {@link android.graphics.drawable.Drawable} on the view using
     *
     * @param drawable {@inheritDoc}
     */
    @Override
    public void setDrawable(Drawable drawable) {
        view.bindDrawable(drawable);
    }

    /**
     * Sets the given {@link android.graphics.drawable.Drawable} on the view using
     *
     * @param placeholder {@inheritDoc}
     */
    @Override
    public void onLoadStarted(Drawable placeholder) {
        view.bindDrawable(placeholder);
    }

    /**
     * Sets the given {@link android.graphics.drawable.Drawable} on the view using
     *
     * @param errorDrawable {@inheritDoc}
     */
    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        view.bindDrawable(errorDrawable);
    }

    /**
     * Sets the given {@link android.graphics.drawable.Drawable} on the view using
     *
     * @param placeholder {@inheritDoc}
     */
    @Override
    public void onLoadCleared(Drawable placeholder) {
        view.bindDrawable(placeholder);
    }

    @Override
    public void onResourceReady(Z resource, GlideAnimation<? super Z> glideAnimation) {
        if (glideAnimation == null || !glideAnimation.animate(resource, this)) {
            setResource(resource);
        }
    }

    protected abstract void setResource(Z resource);

}
