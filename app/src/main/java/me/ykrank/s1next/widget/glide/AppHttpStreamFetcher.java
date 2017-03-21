package me.ykrank.s1next.widget.glide;

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
        App.getPrefComponent().inject(this);
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        if (!mDownloadPreferencesManager.isImagesDownload()) {
            return null;
        }
        return super.loadData(priority);
    }
}
