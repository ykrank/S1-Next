package cl.monsoon.s1next.singleton;

import com.squareup.okhttp.OkHttpClient;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.Config;
import cl.monsoon.s1next.widget.PersistentHttpCookieStore;

/**
 * OkHttpClient singleton.
 */
public enum OkHttpClientProvider {
    INSTANCE;

    private final OkHttpClient okHttpClient;

    /**
     * Used for HTTP POST requests in order to avoid retrying requests.
     */
    private final OkHttpClient okHttpClientForNonIdempotent;

    private final CookieManager mCookieManager;

    OkHttpClientProvider() {
        okHttpClient = new OkHttpClient();

        okHttpClient.setConnectTimeout(Config.OKHTTP_CLIENT_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(Config.OKHTTP_CLIENT_WRITE_TIMEOUT, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(Config.OKHTTP_CLIENT_READ_TIMEOUT, TimeUnit.SECONDS);

        mCookieManager = new CookieManager(
                new PersistentHttpCookieStore(App.getContext()),
                CookiePolicy.ACCEPT_ALL);

        okHttpClient.setCookieHandler(mCookieManager);

        // this is a shallow copy (including CookieManager reference)
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
        INSTANCE.mCookieManager.getCookieStore().removeAll();
    }
}
