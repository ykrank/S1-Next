package me.ykrank.s1next.binding;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.ykrank.androidtools.util.L;
import com.github.ykrank.androidtools.util.RxJavaUtil;
import com.github.ykrank.androidtools.widget.glide.transformations.BlurTransformation;
import com.github.ykrank.androidtools.widget.glide.viewtarget.DrawableViewBackgroundTarget;
import com.github.ykrank.androidtools.widget.glide.viewtarget.ViewBackgroundTarget;

import io.reactivex.functions.Consumer;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;

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

    /**
     * action when view init
     *
     * @param view       view
     * @param onViewBind action when view init
     */
    @BindingAdapter("bindEvent")
    public static void setOnViewBind(View view, Consumer<View> onViewBind) {
        try {
            onViewBind.accept(view);
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
        BlurTransformation blurTransformation = new BlurTransformation(context, 20);
        blurTransformation.setTargetSize(50);
        if (TextUtils.isEmpty(newBlurUrl)) {
            Glide.with(view)
                    .load(R.drawable.ic_avatar_placeholder)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .transform(blurTransformation)
                    )
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(new DrawableViewBackgroundTarget(view));
            return;
        }
        if (!TextUtils.equals(oldBlurUrl, newBlurUrl)) {
            Glide.with(view)
                    .asBitmap()
                    .load(newBlurUrl)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .signature(newManager.getAvatarCacheInvalidationIntervalSignature())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .transform(blurTransformation)
                    )
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            RxJavaUtil.workInMainThreadWithView(view, () -> setBlurBackground(view, oldManager, null, newManager, null));
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
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
