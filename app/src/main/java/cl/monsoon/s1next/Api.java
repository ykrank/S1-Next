package cl.monsoon.s1next;

import android.net.Uri;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.apache.commons.lang3.StringUtils;

import cl.monsoon.s1next.model.Quote;
import cl.monsoon.s1next.singleton.User;

public final class Api {

    public static final String URL_S1 = "http://bbs.saraba1st.com/2b/";

    public static final String URL_EMOTICON_IMAGE_PREFIX = "static/image/smiley/";

    public static final String URL_LOGIN = prepend("api/mobile/index.php?module=login&loginsubmit=yes&loginfield=username");

    public static final String URL_FORUM = prepend("api/mobile/index.php?module=forumindex");

    private static final String URL_THREAD_LIST = prepend("api/mobile/index.php?module=forumdisplay");
    private static final String URL_POST_LIST = prepend("api/mobile/index.php?module=viewthread");
    private static final String URL_QUOTE_POST_REDIRECT = prepend("forum.php?mod=redirect&goto=findpost");

    /**
     * A URL used to get the correct token when we want to request HTTP POST.
     * <p>
     * The {@link User#authenticityToken}
     * is not fresh if we have only logged in and haven't browsed
     * any new contents (which means requesting HTTP GET successfully).
     * Otherwise we needn't to use this.
     */
    public static final String URL_AUTHENTICITY_TOKEN_HELPER = prepend("api/mobile/index.php?module=toplist");
    private static final String URL_REPLY = prepend("api/mobile/index.php?module=sendreply&replysubmit=yes");

    public static final String URL_THREAD_FAVOURITES_ADD = prepend("api/mobile/index.php?module=favthread&favoritesubmit=yes");

    /**
     * A URL to get the quoted user identification and processed quoted
     * content (with some HTML tags and its origin redirect hyperlink).
     */
    private static final String URL_QUOTE_HELPER = prepend("forum.php?mod=post&action=reply&inajax=yes");

    private static final String URL_USER_AVATAR_PREFIX = prepend("uc_server/data/avatar/");
    private static final String URL_USER_AVATAR_SMALL = URL_USER_AVATAR_PREFIX + "%s_avatar_small.jpg";
    private static final String URL_USER_AVATAR_MEDIUM = URL_USER_AVATAR_PREFIX + "%s_avatar_middle.jpg";

    /**
     * Opens the browser via {@link android.content.Intent}.
     */
    public static final String URL_BROWSER_REGISTER = prepend("member.php?mod=register");
    private static final String URL_BROWSER_THREAD_LIST = prepend("forum-%s-%d.html");
    private static final String URL_BROWSER_POST_LIST = prepend("thread-%s-%d-1.html");

    private Api() {

    }

    private static String prepend(String suffix) {
        return URL_S1 + suffix;
    }

    public static String getThreadListUrl(String forumId, int pageNum) {
        return Uri.parse(URL_THREAD_LIST).buildUpon()
                .appendQueryParameter("fid", forumId)
                .appendQueryParameter("page", String.valueOf(pageNum))
                .appendQueryParameter("tpp", String.valueOf(Config.THREADS_PER_PAGE))
                .toString();
    }

    public static String getPostListUrl(String threadId, int pageNum) {
        return Uri.parse(URL_POST_LIST).buildUpon()
                .appendQueryParameter("tid", threadId)
                .appendQueryParameter("page", String.valueOf(pageNum))
                .appendQueryParameter("ppp", String.valueOf(Config.POSTS_PER_PAGE))
                .toString();
    }

    public static String getQuotePostRedirectUrl(String threadId, String quotePostId) {
        return Uri.parse(URL_QUOTE_POST_REDIRECT).buildUpon()
                .appendQueryParameter("ptid", threadId)
                .appendQueryParameter("pid", quotePostId)
                .toString();
    }

    public static String getPostRelyUrl(String threadId) {
        return Uri.parse(URL_REPLY).buildUpon()
                .appendQueryParameter("tid", threadId)
                .toString();
    }

    public static String getQuoteHelperUrl(String threadId, String quotePostId) {
        return Uri.parse(URL_QUOTE_HELPER).buildUpon()
                .appendQueryParameter("tid", threadId)
                .appendQueryParameter("repquote", quotePostId)
                .toString();
    }

    public static String getAvatarSmallUrl(String userId) {
        return formatAvatarUrl(URL_USER_AVATAR_SMALL, userId);
    }

    public static String getAvatarMediumUrl(String userId) {
        return formatAvatarUrl(URL_USER_AVATAR_MEDIUM, userId);
    }

    /**
     * See https://github.com/Discuz-X/DiscuzX/blob/35db41f75b102708033f3bd501eace6dbe11b7e2/uc_server/avatar.php#L47-L56
     * <p>
     * Example:
     * URL: http://bbs.saraba1st.com/2b/uc_server/data/avatar/%s_avatar_middle.jpg
     * User ID: 123456 -> 000123456 -> 000/12/34/56 -> http://bbs.saraba1st.com/2b/uc_server/data/avatar/000/12/34/56_avatar_middle.jpg
     */
    private static String formatAvatarUrl(String url, String userId) {
        String s = StringUtils.leftPad(userId, 9, '0');

        return String.format(
                url,
                s.substring(0, 3)
                        + "/" + s.substring(3, 5)
                        + "/" + s.substring(5, 7)
                        + "/" + s.substring(7));
    }

    public static boolean isAvatarUrl(String url) {
        return url != null && url.startsWith(URL_USER_AVATAR_PREFIX);
    }

    public static String getThreadListUrlForBrowser(String forumId, int pageNum) {
        return String.format(URL_BROWSER_THREAD_LIST, forumId, pageNum);
    }

    public static String getPostListUrlForBrowser(String threadId, int pageNum) {
        return String.format(URL_BROWSER_POST_LIST, threadId, pageNum);
    }

    public static RequestBody getLoginPostBuilder(String username, String password) {
        return new FormEncodingBuilder()
                .add("username", username)
                .add("password", password)
                .add("cookietime", String.valueOf(Config.COOKIES_MAX_AGE))
                .build();
    }

    public static RequestBody getThreadFavouritesAddBuilder(String threadId, String remark) {
        return FormWithAuthTokenEncodingBuilder.newInstance()
                .add("id", threadId)
                .add("description", remark)
                .build();
    }

    public static RequestBody getReplyPostBuilder(String reply) {
        return FormWithAuthTokenEncodingBuilder.newInstance()
                .add("message", reply)
                .build();
    }

    public static RequestBody getQuotePostBuilder(Quote quote, String reply) {
        return FormWithAuthTokenEncodingBuilder.newInstance()
                .add("noticeauthor", quote.getEncodedUserId())
                .add("noticetrimstr", quote.getQuoteMessage())
                .add("noticeauthormsg", StringUtils.abbreviate(reply, Config.REPLY_NOTIFICATION_MAX_LENGTH))
                .add("message", reply)
                .build();
    }

    private static class FormWithAuthTokenEncodingBuilder {

        private FormWithAuthTokenEncodingBuilder() {

        }

        private static FormEncodingBuilder newInstance() {
            String authenticityToken = User.getAuthenticityToken();
            if (authenticityToken == null) {
                throw new IllegalStateException("AuthenticityToken must not be null.");
            }

            return new FormEncodingBuilder().add("formhash", authenticityToken);
        }
    }
}
