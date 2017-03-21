package me.ykrank.s1next.widget.glide;

import android.content.Context;

import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import java.io.InputStream;

import me.ykrank.s1next.widget.glide.model.AvatarUrl;
import okhttp3.OkHttpClient;

/**
 * A simple model loader for fetching media over HTTP/HTTPS using OkHttp.
 * <p>
 * Forked from {@link OkHttpUrlLoader}
 */
final class AvatarUrlLoader implements StreamModelLoader<AvatarUrl> {

    private final OkHttpClient mOkHttpClient;

    private AvatarUrlLoader(OkHttpClient okHttpClient) {
        this.mOkHttpClient = okHttpClient;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(AvatarUrl model, int width, int height) {
        return new AvatarStreamFetcher(mOkHttpClient, model);
    }

    /**
     * The default factory for {@link AvatarUrlLoader}.
     */
    public static final class Factory implements ModelLoaderFactory<AvatarUrl, InputStream> {

        private final OkHttpClient mOkHttpClient;

        /**
         * Constructor for a new Factory that runs requests using given client.
         */
        public Factory(OkHttpClient okHttpClient) {
            this.mOkHttpClient = okHttpClient;
        }

        @Override
        public ModelLoader<AvatarUrl, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new AvatarUrlLoader(mOkHttpClient);
        }

        @Override
        public void teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }
}
