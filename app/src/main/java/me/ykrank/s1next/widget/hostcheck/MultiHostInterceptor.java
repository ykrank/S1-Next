package me.ykrank.s1next.widget.hostcheck;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

import me.ykrank.s1next.data.api.Api;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Self-adaption multi host
 * Created by ykrank on 2017/3/29.
 */

public class MultiHostInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        HttpUrl originHttpUrl = originRequest.url();

        HttpUrl newHttpUrl = mergeHttpUrl(originHttpUrl, HostUrlCheckTask.INSTANCE.getBaseHttpUrl());

        Request newRequest = originRequest;
        if (originHttpUrl != newHttpUrl) {
            Request.Builder builder = originRequest.newBuilder();
            builder.url(newHttpUrl);
            builder.header("host", newHttpUrl.host());
            newRequest = builder.build();
        }

        return chain.proceed(newRequest);
    }

    /**
     * merge complete url with base url
     */
    private static HttpUrl mergeHttpUrl(HttpUrl originHttpUrl, HttpUrl baseHttpUrl) {
        if (baseHttpUrl == null || originHttpUrl == null) {
            return originHttpUrl;
        }
        // s1 site
        if (ArrayUtils.contains(Api.HOST_LIST, originHttpUrl.host())) {
            String originUrl = originHttpUrl.toString();
            String originReplacedUrl = Api.parseBaseUrl(originHttpUrl);
            return HttpUrl.parse(originUrl.replace(originReplacedUrl, baseHttpUrl.toString()));
        }
        return originHttpUrl;
    }
}
