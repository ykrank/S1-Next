package me.ykrank.s1next.data.api;

import me.ykrank.s1next.data.api.model.wrapper.FavouritesWrapper;
import me.ykrank.s1next.data.api.model.wrapper.ForumGroupsWrapper;
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

    @GET(Api.URL_FORUM)
    Observable<ForumGroupsWrapper> getForumGroupsWrapper();

    @GET(Api.URL_FAVOURITES)
    Observable<FavouritesWrapper> getFavouritesWrapper(@Query("page") int page);

    @GET(Api.URL_THREAD_LIST)
    Observable<ThreadsWrapper> getThreadsWrapper(@Query("fid") String forumId, @Query("page") int page);

    @GET(Api.URL_POST_LIST)
    Observable<PostsWrapper> getPostsWrapper(@Query("tid") String threadId, @Query("page") int page);

    @GET(Api.URL_QUOTE_POST_REDIRECT)
    Observable<Response<Void>> getQuotePostResponseBody(@Query("ptid") String threadId, @Query("pid") String quotePostId);

    @FormUrlEncoded
    @POST(Api.URL_LOGIN)
    Observable<ResultWrapper> login(@Field("username") String username, @Field("password") String password);

    @GET(Api.URL_AUTHENTICITY_TOKEN_HELPER)
    Observable<ResultWrapper> refreshAuthenticityToken();

    @FormUrlEncoded
    @POST(Api.URL_THREAD_FAVOURITES_ADD)
    Observable<ResultWrapper> addThreadFavorite(@Field("formhash") String authenticityToken, @Field("id") String threadId, @Field("description") String remark);

    @FormUrlEncoded
    @POST(Api.URL_REPLY)
    Observable<ResultWrapper> reply(@Field("formhash") String authenticityToken, @Field("tid") String threadId, @Field("message") String reply);

    @GET(Api.URL_QUOTE_HELPER)
    Observable<String> getQuoteInfo(@Query("tid") String threadId, @Query("repquote") String quotePostId);

    @FormUrlEncoded
    @POST(Api.URL_REPLY)
    Observable<ResultWrapper> replyQuote(@Field("formhash") String authenticityToken, @Field("tid") String threadId, @Field("message") String reply,
                                         @Field("noticeauthor") String encodedUserId, @Field("noticetrimstr") String quoteMessage, @Field("noticeauthormsg") String replyNotification);

    @GET(Api.URL_PM_LIST)
    Observable<String> getPmList(@Query("page") int page);

    @GET(Api.URL_NEW_THREAD_HELPER)
    Observable<String> getNewThreadInfo(@Query("fid") int fid);

    @FormUrlEncoded
    @POST(Api.URL_NEW_THREAD)
    Observable<ResultWrapper> newThread(@Query("fid") int fid, @Field("formhash") String authenticityToken, @Field("posttime") long postTime, @Field("typeid") String typeId,
                                        @Field("subject") String subject, @Field("message") String message, @Field("allownoticeauthor") int allowNoticeAuthor,
                                        @Field("usesig") int useSign, @Field("save") Integer saveAsDraft);

    @FormUrlEncoded
    @POST(Api.URL_SEARCH_FORUM)
    Observable<String> searchForum(@Field("formhash") String authenticityToken, @Field("searchsubmit") String searchSubmit, @Field("srchtxt") String text);
}
