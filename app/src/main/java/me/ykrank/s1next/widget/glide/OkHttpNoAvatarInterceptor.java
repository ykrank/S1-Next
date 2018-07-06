package me.ykrank.s1next.widget.glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.data.api.Api;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;

/**
 * Created by ykrank on 2017/3/6.
 */

public class OkHttpNoAvatarInterceptor implements Interceptor {
    private static final String NO_AVATAR_SMALL_PREFIX = "images/noavatar_small.gif";
    private static final String NO_AVATAR_MIDDLE_PREFIX = "images/noavatar_middle.gif";
    private static final String NO_AVATAR_BIG_PREFIX = "images/noavatar_big.gif";
    private static final String AVATAR_HOST = "avatar.saraba1st.com";
    private static final String[] NO_AVATAR_PREFIXES = new String[]{
            NO_AVATAR_SMALL_PREFIX, NO_AVATAR_MIDDLE_PREFIX, NO_AVATAR_BIG_PREFIX
    };
    private static final List<String> NO_AVATAR_URLS = initNoAvatarUrls();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        if (NO_AVATAR_URLS.contains(url)) {
            return new Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(504)
                    .message("Empty avatar image")
                    .body(Util.EMPTY_RESPONSE)
                    .sentRequestAtMillis(-1L)
                    .receivedResponseAtMillis(System.currentTimeMillis())
                    .build();
        }
        return chain.proceed(request);
    }

    private static List<String> initNoAvatarUrls() {
        List<String> list = new ArrayList<>();
        for (String prefix : NO_AVATAR_PREFIXES) {
            //eg http://avatar.saraba1st.com/images/noavatar_small.gif
            list.add(String.format("http://%s/%s", AVATAR_HOST, prefix));
            if (Api.SUPPORT_HTTPS) {
                list.add(String.format("https://%s/%s", AVATAR_HOST, prefix));
            }
            for (String host : Api.HOST_LIST) {
                //eg http://bbs.saraba1st.com/uc_server/images/noavatar_small.gif
                list.add(String.format("http://%s/uc_server/%s", host, prefix));
                //eg http://bbs.saraba1st.com/2b/uc_server/images/noavatar_small.gif
                list.add(String.format("http://%s/2b/uc_server/%s", host, prefix));
                if (Api.SUPPORT_HTTPS) {
                    //eg https://bbs.saraba1st.com/uc_server/images/noavatar_small.gif
                    list.add(String.format("https://%s/uc_server/%s", host, prefix));
                    //eg https://bbs.saraba1st.com/2b/uc_server/images/noavatar_small.gif
                    list.add(String.format("https://%s/2b/uc_server/%s", host, prefix));
                }
            }
        }
        return list;
    }
}
