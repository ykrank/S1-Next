package me.ykrank.s1next.widget.glide;

import android.content.Context;

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
    public void applyOptions(Context context, GlideBuilder builder) {
        // set max size of the disk cache for images
        builder.setDiskCache(new InternalCacheDiskCacheFactory(
                context, App.getAppComponent().getDownloadPreferencesManager()
                .getTotalImageCacheSize()));

        ViewTarget.setTagId(R.id.tag_glide);

        //从默认的RGB_565改为ARGB_8888显示
        builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888));
    }

    @Override
    public void registerComponents(Context context, Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new AppHttpUrlLoader.Factory(
                App.getAppComponent().getImageOkHttpClient()));
    }
}
