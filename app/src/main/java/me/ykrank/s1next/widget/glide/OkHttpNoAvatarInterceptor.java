package me.ykrank.s1next.widget.glide;

import java.io.IOException;

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

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        switch (url) {
            case NO_AVATAR_SMALL:
            case NO_AVATAR_MIDDLE:
            case NO_AVATAR_BIG:
                throw new NoAvatarException(url);
            default:
        }
        return chain.proceed(request);
    }
}
