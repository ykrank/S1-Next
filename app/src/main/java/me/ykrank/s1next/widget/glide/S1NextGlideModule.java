package me.ykrank.s1next.widget.glide;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;

import java.io.InputStream;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;

/**
 * Lazily configures Glide.
 */
@GlideModule
public final class S1NextGlideModule extends AppGlideModule {

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(@NonNull Context context, GlideBuilder builder) {
        // set max size of the disk cache for images
        builder.setDiskCache(new InternalCacheDiskCacheFactory(
                context, App.Companion.getPreAppComponent().getDownloadPreferencesManager()
                .getTotalImageCacheSize()));

        ViewTarget.setTagId(R.id.tag_glide);
        builder.setLogLevel(Log.ERROR);

        RequestOptions requestOptions = new RequestOptions();

        //Change default RGB_565 to ARGB_8888, show image with transparent
        requestOptions = requestOptions.format(DecodeFormat.PREFER_ARGB_8888);

        //shared element transition crash in version O, fix in O MR1
        //https://muyangmin.github.io/glide-docs-cn/doc/hardwarebitmaps.html
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            requestOptions = requestOptions.disallowHardwareConfig();
    }

        builder.setDefaultRequestOptions(requestOptions);
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.replace(GlideUrl.class, InputStream.class, new AppHttpUrlLoader.Factory(
                App.Companion.getAppComponent().getImageOkHttpClient()));
    }
}
