package me.ykrank.s1next.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.widget.glide.GlideDrawableViewBackgroundTarget;
import me.ykrank.s1next.widget.glide.transformations.BlurTransformation;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by AdminYkrank on 2016/4/17.
 */
public final class ViewBindingAdapter {
    private ViewBindingAdapter() {
    }

    @BindingAdapter("onceClickSubscription")
    public static void setOnceClickListener(View view, Func1<View, Subscription> onceClickSubscription) {
        onceClickSubscription.call(view);
    }

    @BindingAdapter({"downloadPreferencesManager", "blurUid"})
    public static void setUserBlurBackground(View view, DownloadPreferencesManager oldManager, String oldBlurUid,
                                             DownloadPreferencesManager newManager, String newBlurUid) {
        if (TextUtils.isEmpty(newBlurUid)) {
            setBlurBackground(view, oldManager, null, newManager, null);
            return;
        }
        if (!TextUtils.equals(oldBlurUid, newBlurUid)) {
            String oldAvatarUrl, newAvatarUrl;
            if (newManager.isHighResolutionAvatarsDownload()) {
                oldAvatarUrl = Api.getAvatarMediumUrl(oldBlurUid);
                newAvatarUrl = Api.getAvatarMediumUrl(newBlurUid);
            } else {
                oldAvatarUrl = Api.getAvatarSmallUrl(oldBlurUid);
                newAvatarUrl = Api.getAvatarSmallUrl(newBlurUid);
            }
            setBlurBackground(view, oldManager, oldAvatarUrl, newManager, newAvatarUrl);
        }
    }

    @BindingAdapter({"downloadPreferencesManager", "blurUrl"})
    public static void setBlurBackground(View view, DownloadPreferencesManager oldManager, String oldBlurUrl,
                                         DownloadPreferencesManager newManager, String newBlurUrl) {
        Context context = view.getContext();
        if (TextUtils.isEmpty(newBlurUrl)) {
            Glide.with(context)
                    .load(R.drawable.ic_avatar_placeholder)
                    .bitmapTransform(new BlurTransformation(context, 30), new CenterCrop(context))
                    .crossFade()
                    .into(new GlideDrawableViewBackgroundTarget(view));
            return;
        }
        if (!TextUtils.equals(oldBlurUrl, newBlurUrl)) {
            Glide.with(context)
                    .load(newBlurUrl)
                    .signature(newManager.getAvatarCacheInvalidationIntervalSignature())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .bitmapTransform(new BlurTransformation(context, 15), new CenterCrop(context))
                    .crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            setBlurBackground(view, oldManager, null, newManager, null);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(new GlideDrawableViewBackgroundTarget(view));
        }
    }
}
