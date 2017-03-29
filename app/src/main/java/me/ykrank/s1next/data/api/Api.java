package me.ykrank.s1next.data.api;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.URLUtil;

import java.util.List;

import okhttp3.HttpUrl;

public final class Api {

    public static final String BASE_URL = "http://bbs.saraba1st.com/2b/";
    public static final String BASE_API_URL = "http://bbs.saraba1st.com/2b/api/mobile/";
    public static final String BASE_STATIC_URL = "http://static.saraba1st.com/";
    public static final String[] HOST_LIST = new String[]{
            "bbs.saraba1st.com", "www.saraba1st.com", "stage1st.com", "www.stage1st.com"
    };
    
    static final String RANDOM_IMAGE_URL = "http://ac.stage3rd.com/S1_ACG_randpic.asp";
    static final String BASE_API_PREFIX = "index.php?module=";

    public static final String API_VERSION_DEFAULT = "1";
    public static final int THREADS_PER_PAGE = 50;
    public static final int POSTS_PER_PAGE = 30;

    public static final int REPLY_NOTIFICATION_MAX_LENGTH = 100;
    public static final String URL_EMOTICON_IMAGE_PREFIX = "static/image/smiley/";
    public static final String URL_EMOTICON_IMAGE_PREFIX_STATIC = "image/smiley/";
    /**
     * Opens the browser via {@link android.content.Intent}.
     */
    public static final String URL_BROWSER_REGISTER = prepend("member.php?mod=register");

    static final String URL_QUOTE_POST_REDIRECT = BASE_URL + "forum.php?mod=redirect&goto=findpost";

    /**
     * A URL to get the quoted user identification and processed quoted
     * content (with some HTML tags and its origin redirect hyperlink).
     */
    static final String URL_QUOTE_HELPER = BASE_URL + "forum.php?mod=post&action=reply&inajax=yes";
    /**
     * 发布新帖前获取必要前置信息
     */
    static final String URL_NEW_THREAD_HELPER = BASE_URL + "forum.php?mod=post&action=newthread";
    static final String URL_SEARCH_FORUM = BASE_URL + "search.php?mod=forum";
    private static final String URL_USER_AVATAR_PREFIX = BASE_URL + "uc_server/avatar.php?uid=";
    private static final String URL_USER_AVATAR_SMALL = URL_USER_AVATAR_PREFIX + "%s&size=small";
    private static final String URL_USER_AVATAR_MEDIUM = URL_USER_AVATAR_PREFIX + "%s&size=middle";
    private static final String URL_USER_AVATAR_BIG = URL_USER_AVATAR_PREFIX + "%s&size=big";
    private static final String URL_BROWSER_FAVOURITES = prepend("home.php?mod=space&do=favorite");
    private static final String URL_BROWSER_THREAD_LIST = prepend("forum-%s-%d.html");
    private static final String URL_BROWSER_POST_LIST = prepend("thread-%s-%d-1.html");

    private Api() {
    }

    private static String prepend(String suffix) {
        return BASE_URL + suffix;
    }

    public static String getAvatarSmallUrl(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        return String.format(URL_USER_AVATAR_SMALL, userId);
    }

    public static String getAvatarMediumUrl(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        return String.format(URL_USER_AVATAR_MEDIUM, userId);
    }

    public static String getAvatarBigUrl(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return null;
        }
        return String.format(URL_USER_AVATAR_BIG, userId);
    }

    public static boolean isAvatarUrl(String url) {
        return url != null && url.startsWith(URL_USER_AVATAR_PREFIX);
    }

    public static String getFavouritesListUrlForBrowser(int pageNum) {
        return Uri.parse(URL_BROWSER_FAVOURITES).buildUpon()
                .appendQueryParameter("page", String.valueOf(pageNum))
                .toString();
    }

    public static String getThreadListUrlForBrowser(String forumId, int pageNum) {
        return String.format(URL_BROWSER_THREAD_LIST, forumId, pageNum);
    }

    public static String getPostListUrlForBrowser(String threadId, int pageNum) {
        return String.format(URL_BROWSER_POST_LIST, threadId, pageNum);
    }

    public static String randomImage() {
        return RANDOM_IMAGE_URL + "?" + System.currentTimeMillis();
    }

    /**
     * get emoticon from url
     *
     * @return emoticon name if exist. null if not
     */
    @Nullable
    public static String parseEmoticonName(String url) {
        // url has no domain if it comes from BASE_URL server.
        if (!URLUtil.isNetworkUrl(url)) {
            if (url.startsWith(Api.URL_EMOTICON_IMAGE_PREFIX)) {
                return url.substring(Api.URL_EMOTICON_IMAGE_PREFIX.length());
            }
        } else if (url.startsWith(Api.BASE_URL + Api.URL_EMOTICON_IMAGE_PREFIX)) {
            return url.substring((Api.BASE_URL + Api.URL_EMOTICON_IMAGE_PREFIX).length());
        } else if (url.startsWith(Api.BASE_STATIC_URL + Api.URL_EMOTICON_IMAGE_PREFIX_STATIC)) {
            return url.substring((Api.BASE_STATIC_URL + Api.URL_EMOTICON_IMAGE_PREFIX_STATIC).length());
        }
        return null;
    }

    public static boolean isEmoticonName(String url) {
        if (URLUtil.isNetworkUrl(url)) {
            return url.startsWith(Api.BASE_URL + Api.URL_EMOTICON_IMAGE_PREFIX) ||
                    url.startsWith(Api.BASE_STATIC_URL + Api.URL_EMOTICON_IMAGE_PREFIX_STATIC);
        } else {
            return url.startsWith(Api.URL_EMOTICON_IMAGE_PREFIX);
        }
    }

    public static String parseBaseUrl(HttpUrl httpUrl) {
        List<String> pathSegments = httpUrl.pathSegments();
        //if like [host]/2b/
        if (pathSegments.size() > 0 && "2b".equals(pathSegments.get(0))) {
            return httpUrl.scheme() + "://" + httpUrl.host() + "/2b/";
        } else {
            return httpUrl.scheme() + "://" + httpUrl.host() + "/";
        }
    }
}
