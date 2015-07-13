package cl.monsoon.s1next.widget;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.target.ViewTarget;

import java.io.InputStream;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.singleton.OkHttpClientProvider;

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

        ViewTarget.setTagId(R.id.glide_tag);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register the OkHttp for Glide
        glide.register(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(OkHttpClientProvider.get()));
    }
}
