package me.ykrank.s1next.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import me.ykrank.s1next.R;
import me.ykrank.s1next.util.TransformationUtil;
import me.ykrank.s1next.widget.PhotoView;
import me.ykrank.s1next.widget.glide.viewtarget.GlideDrawablePhotoViewTarget;

public final class PhotoViewBindingAdapter {

    private PhotoViewBindingAdapter() {
    }

    @BindingAdapter({"url", "thumbUrl"})
    public static void loadImage(PhotoView photoView, String url, @Nullable String thumbUrl) {
        Context context = photoView.getContext();
        photoView.setMaxInitialScale(1);
        photoView.enableImageTransforms(true);

        DrawableRequestBuilder<String> builder = Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .transform(new TransformationUtil.GlMaxTextureSizeBitmapTransformation(context))
                .error(R.mipmap.error_symbol)
                .fitCenter()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (thumbUrl != null) {
                            loadImage(photoView, thumbUrl, null);
                        } else {
                            target.onLoadFailed(e, ContextCompat.getDrawable(context, R.mipmap.error_symbol));
                        }
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                });

        if (!TextUtils.isEmpty(thumbUrl)) {
            DrawableRequestBuilder<String> thumbnailRequest = Glide
                    .with(context)
                    .load(thumbUrl)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE);
            builder = builder.thumbnail(thumbnailRequest)
                    .placeholder(R.drawable.loading);
        } else {
            DrawableTypeRequest<Integer> loadingRequest = Glide.with(context).load(R.drawable.loading);
            builder = builder.thumbnail(loadingRequest);
        }

        builder.into(new GlideDrawablePhotoViewTarget(photoView));
    }
}
