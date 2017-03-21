package me.ykrank.s1next.widget.glide;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

import me.ykrank.s1next.data.api.Api;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ykrank on 2017/3/6.
 */

public class OkHttpNoAvatarInterceptor implements Interceptor {
    private static final String NO_AVATAR_SMALL = "http://avatar.saraba1st.com/images/noavatar_small.gif";
    private static final String NO_AVATAR_MIDDLE = "http://avatar.saraba1st.com/images/noavatar_middle.gif";
    private static final String NO_AVATAR_BIG = "http://avatar.saraba1st.com/images/noavatar_big.gif";
    private static final String[] NO_AVATAR_URLS = new String[]{
            NO_AVATAR_SMALL, NO_AVATAR_MIDDLE, NO_AVATAR_BIG,
            Api.BASE_URL + "uc_server/images/noavatar_small.gif",
            Api.BASE_URL + "uc_server/images/noavatar_middle.gif",
            Api.BASE_URL + "uc_server/images/noavatar_big.gif"
    };

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        if (ArrayUtils.contains(NO_AVATAR_URLS, url)) {
            throw new NoAvatarException(url);
        }
        return chain.proceed(request);
    }
}
