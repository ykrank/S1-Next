package cl.monsoon.s1next.singleton;

import com.squareup.okhttp.OkHttpClient;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import cl.monsoon.s1next.MyApplication;
import cl.monsoon.s1next.widget.PersistentHttpCookieStore;

/**
 * OkHttpClient singleton.
 */
public enum MyOkHttpClient {
    INSTANCE;

    private final OkHttpClient okHttpClient;
    private final OkHttpClient okHttpClientForNonIdempotent;

    private final CookieManager cookieManager;

    private MyOkHttpClient() {
        okHttpClient = new OkHttpClient();

        okHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);

        cookieManager = new CookieManager(
                new PersistentHttpCookieStore(
                        MyApplication.getContext()), CookiePolicy.ACCEPT_ALL);

        okHttpClient.setCookieHandler(cookieManager);

        okHttpClientForNonIdempotent = okHttpClient.clone();
        okHttpClientForNonIdempotent.setRetryOnConnectionFailure(false);
    }

    public static OkHttpClient get() {
        return INSTANCE.okHttpClient;
    }

    public static OkHttpClient getForNonIdempotent() {
        return INSTANCE.okHttpClientForNonIdempotent;
    }

    public static void clearCookie() {
        INSTANCE.cookieManager.getCookieStore().removeAll();
    }
}
