package me.ykrank.s1next.widget.hostcheck;

import android.accounts.NetworkErrorException;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.GeneralPreferencesManager;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import okhttp3.HttpUrl;

/**
 * Created by ykrank on 2017/3/29.
 */

public enum HostUrlCheckTask {
    INSTANCE;

    private GeneralPreferencesManager prefManager;
    private HttpUrl baseHttpUrl;
    private List<String> toBeCheckedBaseUrls;

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

    public List<String> getToBeCheckedBaseUrls() {
        ArrayList<String> baseUrls = new ArrayList<>();
        for (String baseUrl : Api.HOST_LIST) {
            baseUrls.add("http://" + baseUrl + "/");
            baseUrls.add("https://" + baseUrl + "/");
            baseUrls.add("http://" + baseUrl + "/2b/");
            baseUrls.add("https://" + baseUrl + "/2b/");
        }
        return baseUrls;
    }

    public Single<List<HostUrlCheckResult>> forceCheckHost() {
        HostUrlCheckJobClient jobClient = new HostUrlCheckJobClient();
        return jobClient.startJob(getToBeCheckedBaseUrls())
                .compose(RxJavaUtil.newThreadSingleTransformer())
                .doOnSuccess(results -> {
                    if (results.size() > 0) {
                        L.leaveMsg("Host check result" + results.toString());
                        setBaseHttpUrl(HttpUrl.parse(results.get(0).getBaseUrl()));
                    } else {
                        L.report(new NetworkErrorException("Host check result is null"));
                    }
                });
    }

    public void stargCheckHostJobScheduler() {

    }
}
