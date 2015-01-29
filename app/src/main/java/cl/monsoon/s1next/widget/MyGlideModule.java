package cl.monsoon.s1next.widget;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.singleton.MyOkHttpClient;

/**
 * Lazily configures Glide.
 */
public final class MyGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // set max size of the disk cache for images
        builder.setDiskCache(
                new InternalCacheDiskCacheFactory(context, Config.GLIDE_DISK_CACHE_SIZE));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register the OkHttp for Glide
        glide.register(
                GlideUrl.class,
                InputStream.class,
                new OkHttpUrlLoader.Factory(MyOkHttpClient.get()));
    }
}
