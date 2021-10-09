package com.github.ykrank.androidtools.util

import android.content.Context
import android.os.Build
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import java.net.CookieStore


/**
 * Created by ykrank on 2016/10/23 0023.
 */

object WebViewUtils {

    @Suppress("DEPRECATION")
    fun clearWebViewCookies(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.removeSessionCookies(null)
            cookieManager.flush()
        } else {
            val cookieSyncMngr = CookieSyncManager.createInstance(context)
            cookieSyncMngr.startSync()
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookie()
            cookieManager.removeSessionCookie()
            cookieSyncMngr.stopSync()
            cookieSyncMngr.sync()
        }
    }

    @Suppress("DEPRECATION")
    fun syncWebViewCookies(context: Context, cookieStore: CookieStore) {
        CookieSyncManager.createInstance(context)
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.removeSessionCookie()
        cookieManager.removeAllCookie()

        val urls = cookieStore.urIs
        for (url in urls) {
            val cookies = cookieStore.get(url)
            for (cookie in cookies) {
                cookieManager.setCookie(url.toString(), cookie.toString())
            }

            cookieManager.getCookie(url.toString())?.let {
                L.w("Cookies", it)
            }
        }

        CookieSyncManager.getInstance().sync()
    }
}
