package me.ykrank.s1next.widget.glide;

import com.bumptech.glide.Priority;
import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.util.ContentLengthInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.widget.glide.model.AvatarUrl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.cache.CacheStrategy;

/**
 * Fetches an {@link java.io.InputStream} from {@link AvatarUrl} using the OkHttp library.
 * <p>
 * Forked from {@link OkHttpStreamFetcher}
 */

public class AvatarStreamFetcher implements DataFetcher<InputStream> {
    private final AvatarUrl url;
    private final Call.Factory client;
    private InputStream stream;
    private ResponseBody responseBody;
    private volatile Call call;

    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;
    @Inject
    AvatarUrlsCache avatarUrlsCache;

    public AvatarStreamFetcher(Call.Factory client, AvatarUrl url) {
        this.url = url;
        this.client = client;
        App.getAppComponent().inject(this);
    }

    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
        if (!mDownloadPreferencesManager.isAvatarsDownload()) {
            callback.onDataReady(null);
            return;
        }
        String url_string = url.toStringUrl();
        //whether cached error url
        final Key avatarKey = OriginalKey.obtainAvatarKey(mDownloadPreferencesManager, url_string);
        if (avatarUrlsCache.has(avatarKey)) {
            // already have cached this not success avatar url
            callback.onDataReady(null);
            return;
        }

        Request.Builder requestBuilder = new Request.Builder().url(url_string);

        for (Map.Entry<String, String> headerEntry : url.getHeaders().entrySet()) {
            String key = headerEntry.getKey();
            requestBuilder.addHeader(key, headerEntry.getValue());
        }

        Request request = requestBuilder.build();
        call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@Nonnull Call call, @Nullable IOException e) {
                if (avatarKey != null) {
                    avatarUrlsCache.put(avatarKey);
                }
                callback.onLoadFailed(e);
            }

            @Override
            public void onResponse(@Nonnull Call call, @Nonnull Response response) throws IOException {
                responseBody = response.body();
                if (!response.isSuccessful()) {
                    // if (this this a avatar URL) && (this URL is cacheable)
                    if (avatarKey != null && CacheStrategy.isCacheable(response, request)) {
                        avatarUrlsCache.put(avatarKey);
                        callback.onDataReady(null);
                        return;
                    }
                    callback.onLoadFailed(new IOException("Request failed with code: " + response.code()));
                } else {
                    // if download success, and (this this a avatar URL) && (this URL is cacheable)
                    // remove from cache list
                    if (avatarKey != null && avatarUrlsCache.has(avatarKey)) {
                        avatarUrlsCache.remove(avatarKey);
                    }
                    if (responseBody != null) {
                        long contentLength = responseBody.contentLength();
                        stream = ContentLengthInputStream.obtain(responseBody.byteStream(), contentLength);
                    }

                    callback.onDataReady(stream);
                }
            }
        });
    }

    @Override
    public void cleanup() {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // Ignored
        }
        if (responseBody != null) {
            responseBody.close();
        }
    }

    @Override
    public void cancel() {
        Call local = call;
        if (local != null) {
            local.cancel();
        }
    }

    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
