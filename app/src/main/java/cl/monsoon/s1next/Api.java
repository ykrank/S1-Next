package cl.monsoon.s1next;

import android.net.Uri;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import cl.monsoon.s1next.model.Quote;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.StringHelper;

public final class Api {

    public static final String URL_S1 = "http://bbs.saraba1st.com/2b/";

    public static final String URL_LOGIN = prefix("api/mobile/index.php?module=login&loginsubmit=yes&loginfield=username");

    // public static final String URL_HOT_FORUM = prefix("api/mobile/index.php?module=hotforum");
    public static final String URL_FORUM = prefix("api/mobile/index.php?module=forumindex");

    private static final String URL_THREAD_LIST = prefix("api/mobile/index.php?module=forumdisplay");
    private static final String URL_POST_LIST = prefix("api/mobile/index.php?module=viewthread");

    /**
     * The authenticity token (formhash) we have gotten is not fresh
     * when we have logged in without browsing any other contents.
     * So we need get the correct token when we want to reply after login.
     */
    public static final String URL_AUTHENTICITY_TOKEN_HELPER = prefix("api/mobile/index.php?module=toplist");
    private static final String URL_REPLY = prefix("api/mobile/index.php?module=sendreply&replysubmit=yes");

    public static final String URL_THREAD_FAVOURITES_ADD = prefix("api/mobile/index.php?module=favthread&favoritesubmit=yes");

    /**
     * We need to use this to get extra information for quote.
     * <b>noticeauthor</b> the user ID which was encoded in server, without this we can't notify the original author.
     * <b>noticetrimstr</b> the quote string though we could build it by ourselves.
     */
    private static final String URL_QUOTE_HELPER = prefix("forum.php?mod=post&action=reply&inajax=yes");

    // private static final String URL_USER_AVATAR_SMALL = prefix("uc_server/avatar.php?uid=%s&size=small");
    // private static final String URL_USER_AVATAR_MEDIUM = prefix("uc_server/avatar.php?uid=%s&size=middle");
    private static final String URL_USER_AVATAR_SMALL = prefix("uc_server/data/avatar/%s_avatar_small.jpg");
    private static final String URL_USER_AVATAR_MEDIUM = prefix("uc_server/data/avatar/%s_avatar_middle.jpg");

    /**
     * open in browser
     */
    public static final String URL_BROWSER_REGISTER = prefix("member.php?mod=register");
    private static final String URL_BROWSER_THREAD_LIST = prefix("forum-%s-%d.html");
    private static final String URL_BROWSER_POST_LIST = prefix("thread-%s-%d-1.html");

    private Api() {

    }

    private static String prefix(String suffix) {
        return URL_S1 + suffix;
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

    public static String getQuoteHelper(CharSequence threadId, CharSequence quotePostId) {
        return
                Uri.parse(URL_QUOTE_HELPER).buildUpon()
                        .appendQueryParameter("tid", threadId.toString())
                        .appendQueryParameter("repquote", quotePostId.toString())
                        .toString();
    }

    public static String getUrlAvatarSmall(String userId) {
        return formatUrlAvatar(URL_USER_AVATAR_SMALL, userId);
    }

    public static String getUrlAvatarMedium(String userId) {
        return formatUrlAvatar(URL_USER_AVATAR_MEDIUM, userId);
    }

    /**
     * Example:
     * URL: http://bbs.saraba1st.com/2b/uc_server/data/avatar/%s_avatar_middle.jpg
     * User id: 123456 -> 000123456 -> 000/12/34/56 -> http://bbs.saraba1st.com/2b/uc_server/data/avatar/000/12/34/56_avatar_middle.jpg
     */
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

    public static String getUrlBrowserThreadList(CharSequence forumId, int pageNum) {
        return String.format(URL_BROWSER_THREAD_LIST, forumId, pageNum);
    }

    public static String getUrlBrowserPostList(CharSequence threadId, int pageNum) {
        return String.format(URL_BROWSER_POST_LIST, threadId, pageNum);
    }

    public static RequestBody getLoginPostBuilder(CharSequence username, CharSequence password) {
        return
                new FormEncodingBuilder()
                        .add("username", username.toString())
                        .add("password", password.toString())
                        .add("cookietime", "2592000")
                        .build();
    }

    public static RequestBody getThreadFavouritesAddBuilder(CharSequence threadId, CharSequence remark) {
        return
                MyFormEncodingBuilder.newInstance()
                        .add("id", threadId.toString())
                        .add("description", remark.toString())
                        .build();
    }

    public static RequestBody getReplyPostBuilder(String reply) {
        return
                MyFormEncodingBuilder.newInstance()
                        .add("message", reply)
                        .build();
    }

    public static RequestBody getQuotePostBuilder(Quote quote, String reply) {
        return
                MyFormEncodingBuilder.newInstance()
                        .add("noticeauthor", quote.getEncodedUserId())
                        .add("noticetrimstr", quote.getQuoteMessage())
                        .add("noticeauthormsg", StringHelper.Util.ellipsize(reply, 100))
                        .add("message", reply)
                        .build();
    }

    private static class MyFormEncodingBuilder {

        private MyFormEncodingBuilder() {

        }

        private static FormEncodingBuilder newInstance() {
            return
                    new FormEncodingBuilder()
                            .add("formhash", getAuthenticityToken());
        }


        private static String getAuthenticityToken() {
            if (User.getAuthenticityToken() == null) {
                throw new IllegalStateException("AuthenticityToken must not be null.");
            }

            return User.getAuthenticityToken();
        }
    }
}
