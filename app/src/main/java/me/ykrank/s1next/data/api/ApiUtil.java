package me.ykrank.s1next.data.api;

import android.util.Base64;

import androidx.annotation.NonNull;

/**
 * Created by ykrank on 2017/6/8.
 */

public class ApiUtil {

    @NonNull
    public static String replaceAjaxHeader(@NonNull String html) {
        return html.replace("<root><![CDATA[", "").replace("]]></root>", "");
    }

    public static String getUrlId(String url) {
        return Base64.encodeToString(url.getBytes(), Base64.URL_SAFE);
    }
}
