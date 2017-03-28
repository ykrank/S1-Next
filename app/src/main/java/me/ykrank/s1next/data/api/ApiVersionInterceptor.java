package me.ykrank.s1next.data.api;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Add version to api
 * Created by ykrank on 2017/3/28.
 */

public class ApiVersionInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url();
        if (TextUtils.isEmpty(url.queryParameter("version"))) {
            url = url.newBuilder().addQueryParameter("version", Api.API_VERSION_DEFAULT).build();
            request = request.newBuilder().url(url).build();
        }
        return chain.proceed(request);
    }
}
