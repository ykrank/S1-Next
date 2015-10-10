package cl.monsoon.s1next.data.api;

import android.net.Uri;

import org.apache.commons.lang3.StringUtils;

import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.api.model.ThreadLink;

public final class Api {

    public static final String BASE_URL = "http://bbs.saraba1st.com/2b/";
    public static final String BASE_API_URL = "http://bbs.saraba1st.com/2b/api/mobile/";

    public static final int THREADS_PER_PAGE = 50;
    public static final int POSTS_PER_PAGE = 30;

    public static final int REPLY_NOTIFICATION_MAX_LENGTH = 100;

    static final String URL_FORUM = "index.php?module=forumindex";
    static final String URL_FAVOURITES = "index.php?module=myfavthread";
    static final String URL_THREAD_LIST = "index.php?module=forumdisplay&tpp=" + THREADS_PER_PAGE;
    static final String URL_POST_LIST = "index.php?module=viewthread&ppp=" + POSTS_PER_PAGE;

    static final String URL_QUOTE_POST_REDIRECT = "/2b/forum.php?mod=redirect&goto=findpost";

    static final String URL_LOGIN = "index.php?module=login&loginsubmit=yes&loginfield=auto&cookietime=2592000";
    /**
     * A URL used to get the correct authenticity token after login.
     * <p>
     * The {@link User#authenticityToken}
     * is not fresh if we have only logged in and haven't browsed
     * any new contents (which means requesting HTTP GET successfully).
     */
    static final String URL_AUTHENTICITY_TOKEN_HELPER = "index.php?module=toplist";
    static final String URL_THREAD_FAVOURITES_ADD = "index.php?module=favthread&favoritesubmit=yes";
    static final String URL_REPLY = "index.php?module=sendreply&replysubmit=yes";
    /**
     * A URL to get the quoted user identification and processed quoted
     * content (with some HTML tags and its origin redirect hyperlink).
     */
    static final String URL_QUOTE_HELPER = BASE_URL + "forum.php?mod=post&action=reply&inajax=yes";

    public static final String URL_EMOTICON_IMAGE_PREFIX = "static/image/smiley/";

    private static final String URL_USER_AVATAR_PREFIX = prepend("uc_server/data/avatar/");
    private static final String URL_USER_AVATAR_SMALL = URL_USER_AVATAR_PREFIX + "%s_avatar_small.jpg";
    private static final String URL_USER_AVATAR_MEDIUM = URL_USER_AVATAR_PREFIX + "%s_avatar_middle.jpg";

    /**
     * Opens the browser via {@link android.content.Intent}.
     */
    public static final String URL_BROWSER_REGISTER = prepend("member.php?mod=register");
    private static final String URL_BROWSER_FAVOURITES = prepend("home.php?mod=space&do=favorite");
    private static final String URL_BROWSER_THREAD_LIST = prepend("forum-%s-%d.html");
    private static final String URL_BROWSER_POST_LIST = prepend("thread-%s-%d-1.html");

    private Api() {}

    private static String prepend(String suffix) {
        return BASE_URL + suffix;
    }

    public static String getAvatarSmallUrl(String userId) {
        return appendAvatarUrlWithUserId(URL_USER_AVATAR_SMALL, userId);
    }

    public static String getAvatarMediumUrl(String userId) {
        return appendAvatarUrlWithUserId(URL_USER_AVATAR_MEDIUM, userId);
    }

    /**
     * See https://github.com/Discuz-X/DiscuzX/blob/35db41f75b102708033f3bd501eace6dbe11b7e2/uc_server/avatar.php#L47-L56
     * <p>
     * Example:
     * URL: http://bbs.saraba1st.com/2b/uc_server/data/avatar/%s_avatar_middle.jpg
     * User ID: 123456 -> 000123456 -> 000/12/34/56 -> http://bbs.saraba1st.com/2b/uc_server/data/avatar/000/12/34/56_avatar_middle.jpg
     */
    private static String appendAvatarUrlWithUserId(String url, String userId) {
        String s = StringUtils.leftPad(userId, 9, '0');

        return String.format(url, s.substring(0, 3)
                + "/" + s.substring(3, 5)
                + "/" + s.substring(5, 7)
                + "/" + s.substring(7));
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
}
