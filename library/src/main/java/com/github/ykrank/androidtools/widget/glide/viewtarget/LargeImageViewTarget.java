package com.github.ykrank.androidtools.widget.glide.viewtarget;

/**
 * Created by ykrank on 2017/1/6.
 * <p>
 * fork from {@link com.bumptech.glide.request.target.ImageViewTarget}
 */

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.transition.Transition;
import com.shizhefei.view.largeimage.LargeImageView;
import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory;

import java.io.File;

/**
 * fork from {@linkplain com.bumptech.glide.request.target.ImageViewTarget}
 * <p>
 * A base {@link com.bumptech.glide.request.target.Target} for displaying resources in
 * {@link LargeImageView}s.
 */
public class LargeImageViewTarget extends CustomViewTarget<LargeImageView, File> {

    public LargeImageViewTarget(LargeImageView view) {
        super(view);
    }

    @Override
    public void onResourceLoading(Drawable placeholder) {
        view.setImageDrawable(placeholder);
    }


    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        view.setImageDrawable(errorDrawable);
    }

    @Override
    public void onResourceReady(@NonNull File resource, Transition<? super File> transition) {
        view.setImage(new FileBitmapDecoderFactory(resource));
    }

    @Override
    public void onResourceCleared(Drawable placeholder) {
        view.setImageDrawable(placeholder);
    }

}
