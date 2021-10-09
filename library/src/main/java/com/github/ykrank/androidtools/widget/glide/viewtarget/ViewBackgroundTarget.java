package com.github.ykrank.androidtools.widget.glide.viewtarget;

/**
 * Created by ykrank on 2017/1/6.
 * <p>
 * fork from {@link com.bumptech.glide.request.target.ImageViewTarget}
 */

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import android.view.View;

import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * fork from {@linkplain com.bumptech.glide.request.target.ImageViewTarget}
 * <p>
 * A base {@link com.bumptech.glide.request.target.Target} for displaying resources in
 * {@link View}s.
 *
 * @param <Z> The type of resource that this target will display in the wrapped {@link View}.
 */
public abstract class ViewBackgroundTarget<Z> extends ViewTarget<View, Z>
        implements Transition.ViewAdapter {

    @Nullable
    private Animatable animatable;

    public ViewBackgroundTarget(View view) {
        super(view);
    }

    /**
     * Returns the current {@link Drawable} being displayed in the view
     * using {@link android.widget.ImageView#getDrawable()}.
     */
    @Override
    @Nullable
    public Drawable getCurrentDrawable() {
        return view.getBackground();
    }

    /**
     * Sets the given {@link Drawable} on the view using {@link
     * android.widget.ImageView#setImageDrawable(Drawable)}.
     *
     * @param drawable {@inheritDoc}
     */
    @Override
    public void setDrawable(Drawable drawable) {
        ViewCompat.setBackground(view, drawable);
    }

    /**
     * Sets the given {@link Drawable} on the view using {@link
     * android.widget.ImageView#setImageDrawable(Drawable)}.
     *
     * @param placeholder {@inheritDoc}
     */
    @Override
    public void onLoadStarted(@Nullable Drawable placeholder) {
        super.onLoadStarted(placeholder);
        setResourceInternal(null);
        setDrawable(placeholder);
    }

    /**
     * Sets the given {@link Drawable} on the view using {@link
     * android.widget.ImageView#setImageDrawable(Drawable)}.
     *
     * @param errorDrawable {@inheritDoc}
     */
    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        super.onLoadFailed(errorDrawable);
        setResourceInternal(null);
        setDrawable(errorDrawable);
    }

    /**
     * Sets the given {@link Drawable} on the view using {@link
     * android.widget.ImageView#setImageDrawable(Drawable)}.
     *
     * @param placeholder {@inheritDoc}
     */
    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {
        super.onLoadCleared(placeholder);
        setResourceInternal(null);
        setDrawable(placeholder);
    }

    @Override
    public void onResourceReady(Z resource, @Nullable Transition<? super Z> transition) {
        if (transition == null || !transition.transition(resource, this)) {
            setResourceInternal(resource);
        } else {
            maybeUpdateAnimatable(resource);
        }
    }

    @Override
    public void onStart() {
        if (animatable != null) {
            animatable.start();
        }
    }

    @Override
    public void onStop() {
        if (animatable != null) {
            animatable.stop();
        }
    }

    private void setResourceInternal(@Nullable Z resource) {
        maybeUpdateAnimatable(resource);
        setResource(resource);
    }

    private void maybeUpdateAnimatable(@Nullable Z resource) {
        if (resource instanceof Animatable) {
            animatable = (Animatable) resource;
            animatable.start();
        } else {
            animatable = null;
        }
    }

    protected abstract void setResource(@Nullable Z resource);
}
