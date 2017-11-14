package me.ykrank.s1next.widget.glide;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.InputStream;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;

/**
 * Created by ykrank on 2017/3/21.
 */

public class AppHttpStreamFetcher extends MultiThreadHttpStreamFetcher {
    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;

    public AppHttpStreamFetcher(GlideUrl url) {
        super(url);
        App.Companion.getAppComponent().inject(this);
    }

    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
        if (!mDownloadPreferencesManager.isImagesDownload()) {
            callback.onDataReady(null);
            return;
        }
        super.loadData(priority, callback);
    }
}
