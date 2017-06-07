package me.ykrank.s1next.data.api;

import me.ykrank.s1next.data.User;

/**
 * Created by ykrank on 2017/1/4.
 */

public class ApiForum {

    static final String URL_FORUM = "index.php?module=forumindex";

    static final String URL_THREAD_LIST = "index.php?module=forumdisplay&version=4&tpp=" + Api.THREADS_PER_PAGE;
    static final String URL_POST_LIST = "index.php?module=viewthread&ppp=" + Api.POSTS_PER_PAGE;
    static final String URL_TRADE_POST_INFO = Api.BASE_URL + "forum.php?mod=viewthread&do=tradeinfo&inajax=1";

    /**
     * A URL used to get the correct authenticity token after login.
     * <p>
     * The {@link User#authenticityToken}
     * is not fresh if we have only logged in and haven't browsed
     * any new contents (which means requesting HTTP GET successfully).
     */
    static final String URL_AUTHENTICITY_TOKEN_HELPER = "index.php?module=toplist";
    static final String URL_REPLY = "index.php?module=sendreply&replysubmit=yes";

    /**
     * 发布新帖前获取必要前置信息
     */
    static final String URL_NEW_THREAD_HELPER = Api.BASE_URL + "forum.php?mod=post&action=newthread";
    static final String URL_NEW_THREAD = "index.php?module=newthread&extra=&topicsubmit=yes";

    static final String URL_EDIT_POST_HELPER = Api.BASE_URL + "forum.php?mod=post&action=edit";
    static final String URL_EDIT_POST = Api.BASE_URL + "forum.php?mod=post&action=edit&editsubmit=yes&inajax=yes&wysiwyg=1&delete=0";
    static final String URL_QUOTE_POST_REDIRECT = Api.BASE_URL + "forum.php?mod=redirect&goto=findpost";
    /**
     * A URL to get the quoted user identification and processed quoted
     * content (with some HTML tags and its origin redirect hyperlink).
     */
    static final String URL_QUOTE_HELPER = Api.BASE_URL + "forum.php?mod=post&action=reply&inajax=yes";

    static final String URL_SEARCH_FORUM = Api.BASE_URL + "search.php?searchsubmit=yes&mod=forum";
    static final String URL_SEARCH_USER = Api.BASE_URL + "search.php?searchsubmit=yes&mod=user";
}
