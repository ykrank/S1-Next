package me.ykrank.s1next.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.widget.PhotoView;
import me.ykrank.s1next.widget.glide.model.ForcePassUrl;
import me.ykrank.s1next.widget.glide.transformations.GlMaxTextureSizeBitmapTransformation;
import me.ykrank.s1next.widget.glide.viewtarget.GlideDrawablePhotoViewTarget;

public final class PhotoViewBindingAdapter {

    private PhotoViewBindingAdapter() {
    }

    @BindingAdapter({"url", "thumbUrl", "manager"})
    public static void loadImage(PhotoView photoView, String url, @Nullable String thumbUrl, DownloadPreferencesManager manager) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Context context = photoView.getContext();
        photoView.setMaxInitialScale(1);
        photoView.enableImageTransforms(true);

        DrawableRequestBuilder<ForcePassUrl> builder = Glide.with(context)
                .load(new ForcePassUrl(url))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .transform(new GlMaxTextureSizeBitmapTransformation(context))
                .error(R.mipmap.error_symbol)
                .fitCenter()
                .priority(Priority.HIGH)
                .listener(new RequestListener<ForcePassUrl, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, ForcePassUrl model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (thumbUrl != null) {
                            loadImage(photoView, thumbUrl, null, manager);
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, ForcePassUrl model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
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
        //avatar signature
        if (manager != null && Api.isAvatarUrl(url)) {
            builder = builder.signature(manager.getAvatarCacheInvalidationIntervalSignature());
        }

        builder.into(new GlideDrawablePhotoViewTarget(photoView));
    }
}
