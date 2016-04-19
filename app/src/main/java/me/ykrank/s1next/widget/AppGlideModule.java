package me.ykrank.s1next.widget;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.target.ViewTarget;

import java.io.InputStream;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;

/**
 * Lazily configures Glide.
 */
public final class AppGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // set max size of the disk cache for images
        builder.setDiskCache(new InternalCacheDiskCacheFactory(
                context, App.getAppComponent(context).getDownloadPreferencesManager()
                .getTotalDownloadCacheSize()));

        ViewTarget.setTagId(R.id.tag_glide);

        //从默认的RGB_565改为ARGB_8888显示
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register the OkHttp for Glide
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(
                App.getAppComponent(context).getOkHttpClient()));
    }
}
