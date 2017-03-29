package me.ykrank.s1next.widget.hostcheck;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.util.RxJavaUtil;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

/**
 * Created by ykrank on 2017/3/29.
 */

public class HostUrlCheckJobClient {
    public static final String URL_CHECK_API = "api/mobile/index.php?module=";

    private Disposable mDisposable;
    private OkHttpClient okHttpClient;
    private ApiCheckService apiCheckService;

    public Single<Pair<String, ErrorModel>> startJob(@NonNull String baseUrl) {
        okHttpClient = initOkHttpClient();
        apiCheckService = initApiCheckService(baseUrl);
        return checkBaseUrl(baseUrl);
    }

    public void stopJob() {
        RxJavaUtil.disposeIfNotNull(mDisposable);
        mDisposable = null;
        okHttpClient = null;
        apiCheckService = null;
    }

    private OkHttpClient initOkHttpClient() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
    }

    @NonNull
    private ApiCheckService initApiCheckService(@NonNull String baseUrl) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiCheckService.class);
    }

    /**
     * Check whether base url valid
     *
     * @param baseUrl base url
     * @return {realBaseUrl, realResult}
     */
    private Single<Pair<String, ErrorModel>> checkBaseUrl(String baseUrl) {
        return Single.just(baseUrl)
                .map(url -> {
                    String realUrl = apiCheckService.getRealHost().raw().request().url().toString();
                    if (!TextUtils.equals(url + URL_CHECK_API, realUrl)) {
                        HttpUrl realHttpUrl = HttpUrl.parse(realUrl);
                        String realBaseUrl = Api.parseBaseUrl(realHttpUrl);
                        apiCheckService = initApiCheckService(realBaseUrl);
                        return realBaseUrl;
                    }
                    return url;
                }).map(url -> new Pair<>(url, apiCheckService.checkResult()));

    }

    /**
     * get real base url if redirect
     */
    private String getRealBaseUrl(String baseUrl) {
        String realUrl = apiCheckService.getRealHost().raw().request().url().toString();
        HttpUrl realHttpUrl = HttpUrl.parse(realUrl);
        return Api.parseBaseUrl(realHttpUrl);
    }

    interface ApiCheckService {
        @GET(URL_CHECK_API)
        Response<Void> getRealHost();

        @GET(URL_CHECK_API)
        ErrorModel checkResult();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorModel {
        @Nullable
        @JsonProperty("error")
        private String error;

        @Nullable
        public String getError() {
            return error;
        }

        public void setError(@Nullable String error) {
            this.error = error;
        }
    }
}
