package me.ykrank.s1next.widget.hostcheck;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.Preconditions;

import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.GeneralPreferencesManager;
import me.ykrank.s1next.util.L;
import okhttp3.HttpUrl;

/**
 * Created by ykrank on 2017/3/29.
 */

public enum HostUrlCheckTask {
    INSTANCE;

    private GeneralPreferencesManager prefManager;
    private HttpUrl baseHttpUrl;

    public static void init(GeneralPreferencesManager prefManager) {
        INSTANCE.prefManager = prefManager;
        String url = prefManager.getBaseUrl();
        if (TextUtils.isEmpty(url)) {
            url = Api.BASE_URL;
        }
        Preconditions.checkArgument(url.endsWith("/"));
        INSTANCE.baseHttpUrl = HttpUrl.parse(url);
    }

    public GeneralPreferencesManager getPrefManager() {
        return prefManager;
    }

    public boolean isInit() {
        return prefManager != null;
    }

    /**
     * always end with / if not null
     *
     * @return base HttpUrl
     */
    @Nullable
    public HttpUrl getBaseHttpUrl() {
        return baseHttpUrl;
    }

    public void setBaseHttpUrl(HttpUrl baseHttpUrl) {
        if (baseHttpUrl == null) {
            return;
        }
        this.baseHttpUrl = baseHttpUrl;
        if (!isInit()) {
            L.report(new IllegalStateException("HostUrlCheckTask not init when set base url"));
            return;
        }
        prefManager.invalidateBaseUrl(baseHttpUrl.toString());
    }

    private void checkHost() {

    }
}
