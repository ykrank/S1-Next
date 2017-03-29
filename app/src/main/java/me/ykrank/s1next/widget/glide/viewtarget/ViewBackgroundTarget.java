package me.ykrank.s1next.widget.glide.viewtarget;

/**
 * Created by ykrank on 2017/1/6.
 * <p>
 * fork from {@link com.bumptech.glide.request.target.ImageViewTarget}
 */

import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

/**
 * fork from {@linkplain com.bumptech.glide.request.target.ImageViewTarget}
 * <p>
 * A base {@link com.bumptech.glide.request.target.Target} for displaying resources in
 * {@link View}s.
 *
 * @param <Z> The type of resource that this target will display in the wrapped {@link View}.
 */
public abstract class ViewBackgroundTarget<Z> extends ViewTarget<View, Z> implements GlideAnimation.ViewAdapter {

    public ViewBackgroundTarget(View view) {
        super(view);
    }

    /**
     * Returns the current {@link Drawable} being displayed in the view using
     */
    @Override
    public Drawable getCurrentDrawable() {
        return view.getBackground();
    }

    /**
     * Sets the given {@link Drawable} on the view using
     *
     * @param drawable {@inheritDoc}
     */
    @Override
    public void setDrawable(Drawable drawable) {
        ViewCompat.setBackground(view, drawable);
    }

    /**
     * Sets the given {@link Drawable} on the view using
     *
     * @param placeholder {@inheritDoc}
     */
    @Override
    public void onLoadStarted(Drawable placeholder) {
        setDrawable(placeholder);
    }

    /**
     * Sets the given {@link Drawable} on the view using
     *
     * @param errorDrawable {@inheritDoc}
     */
    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        setDrawable(errorDrawable);
    }

    /**
     * Sets the given {@link Drawable} on the view using
     *
     * @param placeholder {@inheritDoc}
     */
    @Override
    public void onLoadCleared(Drawable placeholder) {
        setDrawable(placeholder);
    }

    @Override
    public void onResourceReady(Z resource, GlideAnimation<? super Z> glideAnimation) {
        if (glideAnimation == null || !glideAnimation.animate(resource, this)) {
            setResource(resource);
        }
    }

    protected abstract void setResource(Z resource);

}
