package me.ykrank.s1next.binding;

import androidx.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.ykrank.androidtools.widget.glide.model.ForcePassUrl;
import com.github.ykrank.androidtools.widget.glide.transformations.GlMaxTextureSizeBitmapTransformation;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;

public final class PhotoViewBindingAdapter {

    private PhotoViewBindingAdapter() {
    }

    @BindingAdapter({"url", "thumbUrl", "manager"})
    public static void loadImage(PhotoView photoView, String url, @Nullable String thumbUrl, DownloadPreferencesManager manager) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .transform(new GlMaxTextureSizeBitmapTransformation())
                .error(R.mipmap.error_symbol)
                .fitCenter()
                .priority(Priority.HIGH);

        RequestBuilder<Drawable> thumbnailRequest;

        if (!TextUtils.isEmpty(thumbUrl)) {
            RequestOptions thumbRequestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.DATA);
            if (manager != null && Api.isAvatarUrl(thumbUrl)) {
                thumbRequestOptions = thumbRequestOptions.signature(manager.getAvatarCacheInvalidationIntervalSignature());
            }
            thumbnailRequest = Glide
                    .with(photoView)
                    .load(thumbUrl)
                    .apply(thumbRequestOptions);
        } else {
            thumbnailRequest = Glide.with(photoView).load(R.drawable.loading);
        }

        //avatar signature
        if (manager != null && Api.isAvatarUrl(url)) {
            requestOptions = requestOptions.signature(manager.getAvatarCacheInvalidationIntervalSignature());
        }

        RequestBuilder<Drawable> builder = Glide.with(photoView)
                .load(new ForcePassUrl(url))
                .apply(requestOptions)
                .thumbnail(thumbnailRequest)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //stop thumnal animatable like gif
                        target.onStop();
                        target.onLoadFailed(ContextCompat.getDrawable(photoView.getContext(), R.mipmap.error_symbol));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                });

        builder.into(photoView);
    }
}
