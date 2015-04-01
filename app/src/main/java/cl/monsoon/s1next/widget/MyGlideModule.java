package cl.monsoon.s1next.widget;

import android.content.Context;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

import cl.monsoon.s1next.singleton.OkHttpClientManager;
import cl.monsoon.s1next.singleton.Setting;

/**
 * Lazily configures Glide.
 */
public final class MyGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // set max size of the disk cache for images
        builder.setDiskCache(new InternalCacheDiskCacheFactory(
                context,
                Setting.Download.getCacheSize(
                        PreferenceManager.getDefaultSharedPreferences(context))));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register the OkHttp for Glide
        glide.register(GlideUrl.class,
                InputStream.class,
                new OkHttpUrlLoader.Factory(OkHttpClientManager.get()));
    }
}
