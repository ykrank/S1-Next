package com.github.ykrank.androidtools.binding;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.ykrank.androidtools.GlobalData;
import com.github.ykrank.androidtools.R;
import com.github.ykrank.androidtools.util.L;
import com.github.ykrank.androidtools.widget.glide.downsamplestrategy.GlMaxTextureSizeDownSampleStrategy;

public final class LibImageViewBindingAdapter {

    private LibImageViewBindingAdapter() {
    }

    @BindingAdapter("imageDrawable")
    public static void setImageDrawable(ImageView imageView, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            @SuppressLint("PrivateResource") @ColorInt int rippleColor = ContextCompat.getColor(
                    imageView.getContext(), R.color.ripple_material_dark);
            // add ripple effect if API >= 21
            RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(rippleColor),
                    drawable, null);
            imageView.setImageDrawable(rippleDrawable);
        } else {
            imageView.setImageDrawable(drawable);
        }
    }


    @BindingAdapter("roundAvatar")
    public static void setRoundAvatarmageDrawable(ImageView imageView, String oUri, String uri) {
        if (TextUtils.equals(oUri, uri)) {
            return;
        }
        if (TextUtils.isEmpty(uri)) {
            Glide.with(imageView)
                    .load(R.drawable.ic_avatar_placeholder)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        } else {
            Glide.with(imageView)
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .thumbnail(Glide.with(imageView)
                            .load(R.drawable.ic_avatar_placeholder)
                            .apply(RequestOptions.circleCropTransform()))
                    .into(imageView);
        }
    }

    @BindingAdapter({"url"})
    public static void loadImage(ImageView imageView, String url) {
        loadImage(imageView, url, null);
    }

    @BindingAdapter({"url", "thumbUrl"})
    public static void loadImage(ImageView imageView, String url, String thumbUrl) {
        loadImage(imageView, url, thumbUrl, GlobalData.INSTANCE.getRecycleViewLoadingId(), GlobalData.INSTANCE.getRecycleViewErrorId());
    }

    @BindingAdapter({"url", "thumbUrl", "loading", "error"})
    public static void loadImage(ImageView imageView, String url, @Nullable String thumbUrl, @DrawableRes int loading, @DrawableRes int error) {
        if (TextUtils.isEmpty(url)) {
            imageView.setImageResource(error);
            return;
        }

        RequestManager requestManager = Glide.with(imageView);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .downsample(new GlMaxTextureSizeDownSampleStrategy())
                .error(error)
                .fitCenter()
                .priority(Priority.HIGH);

        RequestBuilder<Drawable> thumbnailRequest;

        if (!TextUtils.isEmpty(thumbUrl)) {
            RequestOptions thumbRequestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.DATA);
            thumbnailRequest = requestManager
                    .load(thumbUrl)
                    .apply(thumbRequestOptions);
        } else {
            thumbnailRequest = requestManager.load(loading);
        }

        RequestBuilder<Drawable> builder = requestManager
                .load(url)
                .apply(requestOptions)
                .thumbnail(thumbnailRequest)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //stop thumnal animatable like gif
                        L.e(e);
                        target.onStop();
                        target.onLoadFailed(ContextCompat.getDrawable(imageView.getContext(), error));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                });

        builder.into(imageView);
    }
}
