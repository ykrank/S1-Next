package me.ykrank.s1next.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.widget.glide.transformations.BlurTransformation;
import me.ykrank.s1next.widget.glide.viewtarget.GlideDrawableViewBackgroundTarget;
import me.ykrank.s1next.widget.glide.viewtarget.ViewBackgroundTarget;

/**
 * Created by AdminYkrank on 2016/4/17.
 */
public final class ViewBindingAdapter {
    private ViewBindingAdapter() {
    }

    @BindingAdapter("marginEnd")
    public static void setMarginEnd(View view, float margin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginEnd((int) margin);
        } else {
            layoutParams.rightMargin = (int) margin;
        }
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("onceClickDisposable")
    public static void setOnceClickListener(View view, Function<View, Disposable> onceClickDisposable) {
        try {
            onceClickDisposable.apply(view);
        } catch (Exception e) {
            L.report(e);
        }
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
                    .bitmapTransform(new BlurTransformation(context, 25), new CenterCrop(context))
                    .crossFade()
                    .into(new GlideDrawableViewBackgroundTarget(view));
            return;
        }
        if (!TextUtils.equals(oldBlurUrl, newBlurUrl)) {
            int radius = 10;
            if (newManager.isHighResolutionAvatarsDownload()) {
                radius = 20;
            }
            Glide.with(context)
                    .load(newBlurUrl)
                    .asBitmap()
                    .signature(newManager.getAvatarCacheInvalidationIntervalSignature())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .transform(new BlurTransformation(context, radius), new CenterCrop(context))
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            setBlurBackground(view, oldManager, null, newManager, null);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(new ViewBackgroundTarget<Bitmap>(view) {

                        @Override
                        protected void setResource(Bitmap resource) {
                            setDrawable(new BitmapDrawable(getView().getResources(), resource));
                        }
                    });
        }
    }
}
