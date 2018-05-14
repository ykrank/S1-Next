package me.ykrank.s1next.widget.glide;

import android.support.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.InputStream;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import okhttp3.Call;

/**
 * Created by ykrank on 2017/3/21.
 */

public class AppHttpStreamFetcher extends OkHttpStreamFetcher {
    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;

    public AppHttpStreamFetcher(Call.Factory client, GlideUrl url) {
        super(client, url);
        App.Companion.getAppComponent().inject(this);
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        if (!mDownloadPreferencesManager.isImagesDownload()) {
            callback.onDataReady(null);
            return;
        }
        super.loadData(priority, callback);
    }
}
