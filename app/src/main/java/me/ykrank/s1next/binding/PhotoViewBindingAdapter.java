package me.ykrank.s1next.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
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

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.widget.glide.model.ForcePassUrl;
import me.ykrank.s1next.widget.glide.transformations.GlMaxTextureSizeDownSampleStrategy;

public final class PhotoViewBindingAdapter {

    private PhotoViewBindingAdapter() {
    }

    @BindingAdapter({"url", "thumbUrl", "manager"})
    public static void loadImage(PhotoView photoView, String url, @Nullable String thumbUrl, DownloadPreferencesManager manager) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Context context = photoView.getContext();

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .downsample(new GlMaxTextureSizeDownSampleStrategy())
                .error(R.mipmap.error_symbol)
                .fitCenter()
                .priority(Priority.HIGH);

        RequestBuilder<Drawable> thumbnailRequest;

        if (!TextUtils.isEmpty(thumbUrl)) {
            thumbnailRequest = Glide
                    .with(context)
                    .load(thumbUrl)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.DATA));
            requestOptions = requestOptions.placeholder(R.drawable.loading);
        } else {
            thumbnailRequest = Glide.with(context).load(R.drawable.loading);
        }

        //avatar signature
        if (manager != null && Api.isAvatarUrl(url)) {
            requestOptions = requestOptions.signature(manager.getAvatarCacheInvalidationIntervalSignature());
        }

        RequestBuilder<Drawable> builder = Glide.with(context)
                .load(new ForcePassUrl(url))
                .apply(requestOptions)
                .thumbnail(thumbnailRequest)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (thumbUrl != null) {
                            loadImage(photoView, thumbUrl, null, manager);
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                });

        builder.into(photoView);
    }
}
