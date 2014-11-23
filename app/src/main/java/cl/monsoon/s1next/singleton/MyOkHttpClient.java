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
    private final CookieManager cookieManager;

    private MyOkHttpClient() {
        okHttpClient = new OkHttpClient();

        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);

        cookieManager = new CookieManager(
                new PersistentHttpCookieStore(
                        MyApplication.getContext()), CookiePolicy.ACCEPT_ALL);

        okHttpClient.setCookieHandler(cookieManager);
    }

    public static OkHttpClient get() {
        return INSTANCE.okHttpClient;
    }

    public static boolean clearCookie() {
        return INSTANCE.cookieManager.getCookieStore().removeAll();
    }
}
