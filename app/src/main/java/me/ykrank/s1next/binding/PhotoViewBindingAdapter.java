package me.ykrank.s1next.binding;

import android.databinding.BindingAdapter;
import android.support.annotation.Nullable;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import me.ykrank.s1next.R;
import me.ykrank.s1next.util.TransformationUtil;
import me.ykrank.s1next.widget.PhotoView;

public final class PhotoViewBindingAdapter {

    private PhotoViewBindingAdapter() {}

    @BindingAdapter({"url", "thumbUrl"})
    public static void loadImage(PhotoView photoView, String url, @Nullable String thumbUrl) {
        photoView.setMaxInitialScale(1);
        photoView.enableImageTransforms(true);

        DrawableRequestBuilder<String> builder = Glide.with(photoView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .transform(new TransformationUtil.GlMaxTextureSizeBitmapTransformation(
                        photoView.getContext()));
        if (thumbUrl != null) {
            DrawableRequestBuilder<String> thumbnailRequest = Glide
                    .with(photoView.getContext())
                    .load(thumbUrl)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE);
            builder = builder.thumbnail(thumbnailRequest);
        } else {
            builder = builder.placeholder(android.R.color.white)
                    .error(R.drawable.ic_avatar_placeholder);
        }

        builder.into(new SimpleTarget<GlideDrawable>() {

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
