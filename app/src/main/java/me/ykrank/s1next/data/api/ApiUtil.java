package me.ykrank.s1next.data.api;

import android.support.annotation.NonNull;

/**
 * Created by ykrank on 2017/6/8.
 */

public class ApiUtil {

    @NonNull
    public static String replaceAjaxHeader(@NonNull String html) {
        return html.replace("<root><![CDATA[", "").replace("]]></root>", "");
    }
}
