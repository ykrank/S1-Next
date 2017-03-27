package me.ykrank.s1next.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.shizhefei.view.largeimage.LargeImageView;

import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.widget.glide.model.ForcePassUrl;
import me.ykrank.s1next.widget.glide.viewtarget.GlideDrawableLargeImageViewTarget;

public final class LargeImageViewBindingAdapter {

    private LargeImageViewBindingAdapter() {
    }

    @BindingAdapter({"url", "thumbUrl", "manager"})
    public static void loadImage(LargeImageView largeImageView, String url, @Nullable String thumbUrl, DownloadPreferencesManager manager) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Context context = largeImageView.getContext();
        DrawableTypeRequest<ForcePassUrl> builder = Glide.with(context)
                .load(new ForcePassUrl(url));
        //avatar signature
        if (manager != null && Api.isAvatarUrl(url)) {
            builder = (DrawableTypeRequest<ForcePassUrl>) builder.signature(manager.getAvatarCacheInvalidationIntervalSignature());
        }

        builder.downloadOnly(new GlideDrawableLargeImageViewTarget(largeImageView) {
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                if (thumbUrl != null) {
                    loadImage(largeImageView, thumbUrl, null, manager);
                } else {
                    super.onLoadFailed(e, errorDrawable);
                }
            }
        });
    }
}
