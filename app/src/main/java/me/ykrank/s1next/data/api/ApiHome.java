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
    static final String url_my_note_list = Api.BASE_API_PREFIX + "mynotelist&version=3";
}
