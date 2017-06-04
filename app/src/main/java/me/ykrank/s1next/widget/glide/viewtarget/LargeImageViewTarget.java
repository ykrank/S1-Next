package me.ykrank.s1next.widget.glide.viewtarget;

/**
 * Created by ykrank on 2017/1/6.
 * <p>
 * fork from {@link com.bumptech.glide.request.target.ImageViewTarget}
 */

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.bumptech.glide.request.target.ViewTarget;
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
public class LargeImageViewTarget extends ViewTarget<LargeImageView, File> {

    public LargeImageViewTarget(LargeImageView view) {
        super(view);
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        view.setImageDrawable(placeholder);
    }


    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        view.setImageDrawable(errorDrawable);
    }

    @Override
    public void onResourceReady(File resource, Transition<? super File> transition) {
        view.setImage(new FileBitmapDecoderFactory(resource));
    }

    @Override
    public void onLoadCleared(Drawable placeholder) {
        view.setImageDrawable(placeholder);
    }

}
