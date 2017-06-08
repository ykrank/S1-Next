package me.ykrank.s1next.util;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;


/**
 * Created by ykrank on 2016/10/23 0023.
 */

public class WebViewUtils {

    @SuppressWarnings("deprecation")
    public static void clearWebViewCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookies(null);
            cookieManager.removeSessionCookies(null);
            cookieManager.flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public static void syncWebViewCookies(@NonNull Context context, @NonNull CookieStore cookieStore) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();

        List<URI> urls = cookieStore.getURIs();
        for (URI url : urls) {
            List<HttpCookie> cookies = cookieStore.get(url);
            for (HttpCookie cookie : cookies) {
                cookieManager.setCookie(url.toString(), cookie.toString());
            }

            L.w("Cookies", cookieManager.getCookie(url.toString()));
        }

        CookieSyncManager.getInstance().sync();
    }
}
