package cl.monsoon.s1next.singleton;

import com.squareup.okhttp.HttpMethodWhitelistRetryPolicy;
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
    private final CookieManager cookieManager;

    private MyOkHttpClient() {
        okHttpClient = new OkHttpClient();

        okHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        // see https://github.com/square/okhttp/pull/1185
        okHttpClient.setRetryPolicy(HttpMethodWhitelistRetryPolicy.forIdempotentOnly());

        cookieManager = new CookieManager(
                new PersistentHttpCookieStore(
                        MyApplication.getContext()), CookiePolicy.ACCEPT_ALL);

        okHttpClient.setCookieHandler(cookieManager);
    }

    public static OkHttpClient get() {
        return INSTANCE.okHttpClient;
    }

    public static void clearCookie() {
        INSTANCE.cookieManager.getCookieStore().removeAll();
    }
}
