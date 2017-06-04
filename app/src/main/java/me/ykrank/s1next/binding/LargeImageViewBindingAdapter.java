package me.ykrank.s1next.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.shizhefei.view.largeimage.LargeImageView;

import java.io.File;

import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.widget.glide.model.ForcePassUrl;
import me.ykrank.s1next.widget.glide.viewtarget.LargeImageViewTarget;

public final class LargeImageViewBindingAdapter {

    private LargeImageViewBindingAdapter() {
    }

    @BindingAdapter({"url", "thumbUrl", "manager"})
    public static void loadImage(LargeImageView largeImageView, String url, @Nullable String thumbUrl, DownloadPreferencesManager manager) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Context context = largeImageView.getContext();
        RequestBuilder<File> builder = Glide.with(context)
                .download(new ForcePassUrl(url));
        //avatar signature
        if (manager != null && Api.isAvatarUrl(url)) {
            builder = builder.apply(new RequestOptions()
                    .signature(manager.getAvatarCacheInvalidationIntervalSignature()));
        }

        builder.into(new LargeImageViewTarget(largeImageView) {
            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                if (thumbUrl != null) {
                    loadImage(largeImageView, thumbUrl, null, manager);
                } else {
                    super.onLoadFailed(errorDrawable);
                }
            }
        });
    }
}
