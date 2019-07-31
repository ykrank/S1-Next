package me.ykrank.s1next.data.api.app;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;

import me.ykrank.s1next.data.User;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Add app token to app request header
 * Created by ykrank on 2017/3/28.
 */

public class AppTokenInterceptor implements Interceptor {
    private final User user;

    public AppTokenInterceptor(User user) {
        this.user = user;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        if (!TextUtils.isEmpty(user.getAppSecureToken())) {
            request = request.newBuilder().addHeader("authorization", user.getAppSecureToken()).build();
        }
        return chain.proceed(request);
    }
}
