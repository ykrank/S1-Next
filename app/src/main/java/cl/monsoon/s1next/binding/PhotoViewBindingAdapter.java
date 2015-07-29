package cl.monsoon.s1next.binding;

import android.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import cl.monsoon.s1next.util.TransformationUtil;
import cl.monsoon.s1next.widget.PhotoView;

public final class PhotoViewBindingAdapter {

    @BindingAdapter("url")
    public static void loadImage(PhotoView photoView, String url) {
        photoView.setMaxInitialScaleFactor(1);
        photoView.enableImageTransforms(true);

        Glide.with(photoView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .transform(new TransformationUtil.GlMaxTextureSizeBitmapTransformation(
                        photoView.getContext()))
                .into(new SimpleTarget<GlideDrawable>() {

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        photoView.bindDrawable(resource);
                        // start animation if this image is a GIF
                        if (resource.isAnimated()) {
                            resource.setLoopCount(GlideDrawable.LOOP_FOREVER);
                            resource.start();
                        }
                    }
                });
    }
}
