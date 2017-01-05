package me.ykrank.s1next.data.api;

import me.ykrank.s1next.data.api.model.wrapper.FavouritesWrapper;
import me.ykrank.s1next.data.api.model.wrapper.ForumGroupsWrapper;
import me.ykrank.s1next.data.api.model.wrapper.NotesWrapper;
import me.ykrank.s1next.data.api.model.wrapper.PmGroupsWrapper;
import me.ykrank.s1next.data.api.model.wrapper.PmsWrapper;
import me.ykrank.s1next.data.api.model.wrapper.PostsWrapper;
import me.ykrank.s1next.data.api.model.wrapper.ResultWrapper;
import me.ykrank.s1next.data.api.model.wrapper.ThreadsWrapper;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface S1Service {

    @GET(ApiForum.URL_FORUM)
    Observable<ForumGroupsWrapper> getForumGroupsWrapper();

    @GET(ApiHome.URL_FAVOURITES)
    Observable<FavouritesWrapper> getFavouritesWrapper(@Query("page") int page);

    @GET(ApiForum.URL_THREAD_LIST)
    Observable<ThreadsWrapper> getThreadsWrapper(@Query("fid") String forumId, @Query("page") int page);

    @GET(ApiForum.URL_POST_LIST)
    Observable<PostsWrapper> getPostsWrapper(@Query("tid") String threadId, @Query("page") int page);

    @GET(Api.URL_QUOTE_POST_REDIRECT)
    Observable<Response<Void>> getQuotePostResponseBody(@Query("ptid") String threadId, @Query("pid") String quotePostId);

    @FormUrlEncoded
    @POST(ApiMember.URL_LOGIN)
    Observable<ResultWrapper> login(@Field("username") String username, @Field("password") String password);

    @GET(ApiForum.URL_AUTHENTICITY_TOKEN_HELPER)
    Observable<ResultWrapper> refreshAuthenticityToken();

    //region Favourites
    @FormUrlEncoded
    @POST(ApiHome.URL_THREAD_FAVOURITES_ADD)
    Observable<ResultWrapper> addThreadFavorite(@Field("formhash") String authenticityToken, @Field("id") String threadId, @Field("description") String remark);

    @FormUrlEncoded
    @POST(ApiHome.URL_THREAD_FAVOURITES_REMOVE)
    Observable<ResultWrapper> removeThreadFavorite(@Field("formhash") String authenticityToken, @Field("favid") String favId);
    //endregion

    //region Reply
    @FormUrlEncoded
    @POST(ApiForum.URL_REPLY)
    Observable<ResultWrapper> reply(@Field("formhash") String authenticityToken, @Field("tid") String threadId, @Field("message") String reply);

    @GET(Api.URL_QUOTE_HELPER)
    Observable<String> getQuoteInfo(@Query("tid") String threadId, @Query("repquote") String quotePostId);

    @FormUrlEncoded
    @POST(ApiForum.URL_REPLY)
    Observable<ResultWrapper> replyQuote(@Field("formhash") String authenticityToken, @Field("tid") String threadId, @Field("message") String reply,
                                         @Field("noticeauthor") String encodedUserId, @Field("noticetrimstr") String quoteMessage, @Field("noticeauthormsg") String replyNotification);
    //endregion

    //<editor-fold desc="PM">
    @GET(ApiHome.URL_PM_LIST)
    Observable<PmGroupsWrapper> getPmGroups(@Query("page") int page);

    @GET(ApiHome.URL_PM_VIEW_LIST)
    Observable<PmsWrapper> getPmList(@Query("touid") String toUid, @Query("page") int page);

    @FormUrlEncoded
    @POST(ApiHome.URL_PM_POST)
    Observable<ResultWrapper> postPm(@Field("formhash") String authenticityToken, @Field("touid") String toUid, @Field("message") String msg);
    //</editor-fold>

    //region New thread
    @GET(Api.URL_NEW_THREAD_HELPER)
    Observable<String> getNewThreadInfo(@Query("fid") int fid);

    @FormUrlEncoded
    @POST(ApiForum.URL_NEW_THREAD)
    Observable<ResultWrapper> newThread(@Query("fid") int fid, @Field("formhash") String authenticityToken, @Field("posttime") long postTime, @Field("typeid") String typeId,
                                        @Field("subject") String subject, @Field("message") String message, @Field("allownoticeauthor") int allowNoticeAuthor,
                                        @Field("usesig") int useSign, @Field("save") Integer saveAsDraft);
    //endregion

    @FormUrlEncoded
    @POST(Api.URL_SEARCH_FORUM)
    Observable<String> searchForum(@Field("formhash") String authenticityToken, @Field("searchsubmit") String searchSubmit, @Field("srchtxt") String text);

    @GET(ApiHome.URL_MY_NOTE_LIST)
    Observable<NotesWrapper> getMyNotes(@Query("page") int page);
}
