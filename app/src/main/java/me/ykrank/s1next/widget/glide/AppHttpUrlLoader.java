package me.ykrank.s1next.widget.glide;

import androidx.annotation.Nullable;

import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.github.ykrank.androidtools.widget.glide.model.ForcePassUrl;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.widget.glide.model.AvatarUrl;
import okhttp3.OkHttpClient;

/**
 * A simple model loader for fetching media over HTTP/HTTPS using OkHttp.
 * <p>
 * Forked from {@link OkHttpUrlLoader}
 */
final public class AppHttpUrlLoader implements ModelLoader<GlideUrl, InputStream> {

    private final OkHttpClient mOkHttpClient;

    private AppHttpUrlLoader(OkHttpClient okHttpClient) {
        this.mOkHttpClient = okHttpClient;
    }

    @Override
    public LoadData<InputStream> buildLoadData(@NotNull GlideUrl glideUrl, int width, int height, @NotNull Options options) {
        return new LoadData<>(glideUrl, buildResourceFetcher(glideUrl, width, height, options));
    }

    @Override
    public boolean handles(@NotNull GlideUrl glideUrl) {
        return true;
    }

    private DataFetcher<InputStream> buildResourceFetcher(GlideUrl model, int width, int height, Options options) {
        if (model instanceof AvatarUrl) {
            return new AvatarStreamFetcher(mOkHttpClient, (AvatarUrl) model);
        } else if (model instanceof ForcePassUrl) {
            return new MultiThreadHttpStreamFetcher(mOkHttpClient, model);
        }
        return new AppHttpStreamFetcher(mOkHttpClient, model);
    }

    /**
     * The default factory for {@link AppHttpUrlLoader}.
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
        public ModelLoader<GlideUrl, InputStream> build(@NotNull MultiModelLoaderFactory multiFactory) {
            return new AppHttpUrlLoader(mOkHttpClient);
        }

        @Override
        public void teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }
}
