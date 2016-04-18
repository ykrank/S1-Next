package me.ykrank.s1next.widget;

import android.content.Context;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * A simple model loader for fetching media over HTTP/HTTPS using OkHttp.
 * <p>
 * Forked from https://github.com/bumptech/glide/blob/master/integration/okhttp/src/main/java/com/bumptech/glide/integration/okhttp/OkHttpUrlLoader.java
 */
final class OkHttpUrlLoader implements ModelLoader<GlideUrl, InputStream> {

    private final OkHttpClient mOkHttpClient;

    private OkHttpUrlLoader(OkHttpClient okHttpClient) {
        this.mOkHttpClient = okHttpClient;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(GlideUrl model, int width, int height) {
        return new OkHttpStreamFetcher(mOkHttpClient, model);
    }

    /**
     * The default factory for {@link OkHttpUrlLoader}.
     */
    public static final class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {

        private final OkHttpClient mOkHttpClient;

        /**
         * Constructor for a new Factory that runs requests using given client.
         */
        public Factory(OkHttpClient okHttpClient) {
            this.mOkHttpClient = okHttpClient;
        }

        @Override
        public ModelLoader<GlideUrl, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new OkHttpUrlLoader(mOkHttpClient);
        }

        @Override
        public void teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }
}
