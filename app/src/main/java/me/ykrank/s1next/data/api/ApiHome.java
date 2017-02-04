package me.ykrank.s1next.data.api;

/**
 * Created by ykrank on 2017/1/4.
 */

public class ApiHome {

    static final String URL_FAVOURITES = Api.BASE_API_PREFIX + "myfavthread";

    //PM
    static final String URL_PM_LIST = Api.BASE_API_PREFIX + "mypm&filter=privatepm";
    static final String URL_ANNOUNCE_PM_LIST = Api.BASE_API_PREFIX + "mypm&filter=announcepm";
    static final String URL_PM_VIEW_LIST = Api.BASE_API_PREFIX + "mypm&subop=view";
    static final String URL_PM_POST = Api.BASE_API_PREFIX + "sendpm&pmsubmit=true";

    //Favourites
    static final String URL_THREAD_FAVOURITES_ADD = Api.BASE_API_PREFIX + "favthread&favoritesubmit=yes";
    static final String URL_THREAD_FAVOURITES_REMOVE = Api.BASE_API_PREFIX + "favthread&deletesubmit=true&op=delete";

    //Notes
    static final String URL_MY_NOTE_LIST = Api.BASE_API_PREFIX + "mynotelist&view=mypost&type=post&version=3";

    //Profile
    static final String URL_PROFILE = Api.BASE_API_PREFIX + "profile";

    //Friends
    static final String URL_FRIENDS = Api.BASE_API_PREFIX + "friend";

    //Threads
    static final String URL_THREADS = Api.BASE_URL + "home.php?mod=space&do=thread&from=space&type=thread";

    //Replies
    static final String URL_REPLIES = Api.BASE_URL + "home.php?mod=space&do=thread&from=space&type=reply";
}
