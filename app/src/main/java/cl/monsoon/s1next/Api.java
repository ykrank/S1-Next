package cl.monsoon.s1next;

import android.net.Uri;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.singleton.User;

public final class Api {

    public static final String URL_S1 = "http://bbs.saraba1st.com/2b/";

    public static final String URL_LOGIN = prefix("api/mobile/index.php?module=login&loginsubmit=yes&loginfield=username");

    // public static final String URL_HOT_FORUM = prefix("api/mobile/index.php?module=hotforum");
    public static final String URL_FORUM = prefix("api/mobile/index.php?module=forumindex");
    // open in browser
    public static final String URL_REGISTER = prefix("member.php?mod=register");

    private static final String URL_THREAD_LIST = prefix("api/mobile/index.php?module=forumdisplay");
    private static final String URL_POST_LIST = prefix("api/mobile/index.php?module=viewthread");

    // The authenticity token (formhash) we have gotten is not fresh
    // when we have logged in without browsing any other contents.
    // So we need get the correct token when we want to reply after login.
    public static final String URL_REPLY_HELPER = prefix("api/mobile/index.php?module=toplist");
    private static final String URL_REPLY = prefix("api/mobile/index.php?module=sendreply&replysubmit=yes");

    // private static final String URL_USER_AVATAR_SMALL = prefix("uc_server/avatar.php?uid=%s&size=small");
    // private static final String URL_USER_AVATAR_MEDIUM = prefix("uc_server/avatar.php?uid=%s&size=middle");
    private static final String URL_USER_AVATAR_SMALL = prefix("uc_server/data/avatar/%s_avatar_small.jpg");
    private static final String URL_USER_AVATAR_MEDIUM = prefix("uc_server/data/avatar/%s_avatar_middle.jpg");
    private static final String URL_BROWSER_THREAD_LIST = prefix("forum-%s-%d.html");
    private static final String URL_BROWSER_POST_LIST = prefix("thread-%s-%d-1.html");

    private static String prefix(String suffix) {
        return URL_S1 + suffix;
    }

    public static RequestBody getLoginPostBuilder(CharSequence username, CharSequence password) {
        return
                new FormEncodingBuilder()
                        .add("username", username.toString())
                        .add("password", password.toString())
                        .add("cookietime", "2592000")
                        .build();
    }

    public static RequestBody getReplyPostBuilder(String reply) {
        if (User.getAuthenticityToken() == null) {
            throw new IllegalStateException("AuthenticityToken must not be null.");
        }

        return new FormEncodingBuilder()
                //.add("mobiletype", "1")
                .add("message", reply)
                .add("formhash", User.getAuthenticityToken())
                .build();
    }

    public static String getUrlThreadList(CharSequence forumId, int pageNum) {
        return
                Uri.parse(URL_THREAD_LIST).buildUpon()
                        .appendQueryParameter("fid", forumId.toString())
                        .appendQueryParameter("page", String.valueOf(pageNum))
                        .appendQueryParameter("tpp", String.valueOf(Config.THREADS_PER_PAGE))
                        .toString();
    }

    public static String getUrlPostList(CharSequence threadId, int pageNum) {
        return
                Uri.parse(URL_POST_LIST).buildUpon()
                        .appendQueryParameter("tid", threadId.toString())
                        .appendQueryParameter("page", String.valueOf(pageNum))
                        .appendQueryParameter("ppp", String.valueOf(Config.POSTS_PER_PAGE))
                        .toString();
    }

    public static String getPostRely(CharSequence threadId) {
        return
                Uri.parse(URL_REPLY).buildUpon()
                        .appendQueryParameter("tid", threadId.toString())
                        .toString();
    }

    public static String getUrlAvatarSmall(String userId) {
        return formatUrlAvatar(URL_USER_AVATAR_SMALL, userId);
    }

    public static String getUrlAvatarMedium(String userId) {
        return formatUrlAvatar(URL_USER_AVATAR_MEDIUM, userId);
    }

    public static String getUrlBrowserThreadList(CharSequence forumId, int pageNum) {
        return String.format(URL_BROWSER_THREAD_LIST, forumId, pageNum);
    }

    public static String getUrlBrowserPostList(CharSequence threadId, int pageNum) {
        return String.format(URL_BROWSER_POST_LIST, threadId, pageNum);
    }

    private static String formatUrlAvatar(String url, String userId) {
        String s = String.format("%09d", Integer.parseInt(userId));

        return
                String.format(
                        url,
                        s.substring(0, 3)
                                + "/" + s.substring(3, 5)
                                + "/" + s.substring(5, 7)
                                + "/" + s.substring(7));
    }
}
