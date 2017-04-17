package me.ykrank.s1next.widget.hostcheck;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.NetworkPreferencesManager;
import okhttp3.HttpUrl;

/**
 * Base url and host ip
 */

public class BaseHostUrl {
    @Nullable
    private String baseUrl;
    @Nullable
    private HttpUrl baseHttpUrl;
    @Nullable
    private String hostIp;

    private final NetworkPreferencesManager prefManager;

    public BaseHostUrl(NetworkPreferencesManager prefManager) {
        this.prefManager = prefManager;
        refreshBaseHostUrl();
        refreshForceHostIp();
    }

    @Nullable
    public String getBaseUrl() {
        return baseUrl;
    }

    @Nullable
    public HttpUrl getBaseHttpUrl() {
        return baseHttpUrl;
    }

    @Nullable
    public String getHostIp() {
        return hostIp;
    }

    /**
     * check whether base url is a well-formed HTTP or HTTPS URL and end with /
     *
     * @param baseUrl url eg:http://bbs.saraba1st.com/2b/
     * @return parsed HttpUrl
     */
    @Nullable
    public static HttpUrl checkBaseHostUrl(@Nullable String baseUrl) {
        if (!TextUtils.isEmpty(baseUrl) && baseUrl.endsWith("/")) {
            return HttpUrl.parse(baseUrl);
        }
        return null;
    }

    public void refreshBaseHostUrl() {
        String url;
        if (prefManager.isForceBaseUrlEnable()) {
            url = prefManager.getForceBaseUrl();
        } else {
            url = Api.BASE_URL;
        }

        if (!TextUtils.equals(baseUrl, url)) {
            baseHttpUrl = checkBaseHostUrl(url);
        }
    }

    public void refreshForceHostIp() {
        if (prefManager.isForceHostIpEnable()) {
            hostIp = prefManager.getForceHostIp();
        } else {
            hostIp = null;
        }
    }
}
