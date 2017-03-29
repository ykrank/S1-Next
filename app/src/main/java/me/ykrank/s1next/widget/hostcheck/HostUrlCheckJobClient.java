package me.ykrank.s1next.widget.hostcheck;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.util.L;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

import static me.ykrank.s1next.widget.hostcheck.HostUrlCheckJobClient.ApiCheckService.URL_CHECK_API;

/**
 * Created by ykrank on 2017/3/29.
 */

public class HostUrlCheckJobClient {
    private OkHttpClient okHttpClient;
    private ApiCheckService apiCheckService;

    public Single<List<HostUrlCheckResult>> startJob(@NonNull Iterable<String> baseUrls) {
        okHttpClient = initOkHttpClient();
        apiCheckService = initApiCheckService();
        return checkBaseUrl(baseUrls);
    }

    public void stopJob() {
        okHttpClient = null;
        apiCheckService = null;
    }

    private OkHttpClient initOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(17, TimeUnit.SECONDS)
                .writeTimeout(17, TimeUnit.SECONDS)
                .readTimeout(34, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .cache(null)
                .build();
    }

    @NonNull
    private ApiCheckService initApiCheckService() {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiCheckService.class);
    }

    /**
     * a sorted HostUrlCheckResult
     *
     * @see #getRealBaseUrlAndResult(String)
     */
    private Single<List<HostUrlCheckResult>> checkBaseUrl(Iterable<String> baseUrls) {
        return Observable.fromIterable(baseUrls)
                .map(this::getRealBaseUrlAndResult)
                .onErrorReturnItem(new HostUrlCheckResult(null, false, 0))
                .filter(HostUrlCheckResult::isSuccess)
                .toSortedList((r1, r2) -> r1.getTime() - r2.getTime());
    }

    /**
     * get real base url and response
     *
     * @param baseUrl base url
     * @return HostUrlCheckResult
     */
    private HostUrlCheckResult getRealBaseUrlAndResult(String baseUrl) {
        String realBaseUrl = null;
        ErrorModel errorModel = null;
        int time = 0;
        try {
            Response<ErrorModel> response = apiCheckService.checkResult(baseUrl + URL_CHECK_API).execute();
            okhttp3.Response rawResponse = response.raw();
            if (!rawResponse.isRedirect()) {
                realBaseUrl = baseUrl;
                errorModel = response.body();
                time = (int) (rawResponse.receivedResponseAtMillis() - rawResponse.sentRequestAtMillis());
            } else {
                String realUrl = rawResponse.request().url().toString();
                HttpUrl realHttpUrl = HttpUrl.parse(realUrl);
                realBaseUrl = Api.parseBaseUrl(realHttpUrl);
                Response<ErrorModel> realResponse = apiCheckService.checkResult(realBaseUrl + URL_CHECK_API).execute();
                okhttp3.Response realRawResponse = realResponse.raw();
                errorModel = realResponse.body();
                time = (int) (realRawResponse.receivedResponseAtMillis() - realRawResponse.sentRequestAtMillis());
            }
        } catch (Throwable tr) {
            L.leaveMsg(tr);
        }
        boolean isSuccess = errorModel != null && !TextUtils.isEmpty(errorModel.error);
        return new HostUrlCheckResult(realBaseUrl, isSuccess, time);
    }

    interface ApiCheckService {
        String URL_CHECK_API = "api/mobile/index.php?module=";

        @GET()
        Call<ErrorModel> checkResult(@Url String url);
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
