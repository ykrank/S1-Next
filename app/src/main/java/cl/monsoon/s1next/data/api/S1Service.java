package cl.monsoon.s1next.data.api;

import cl.monsoon.s1next.data.api.model.wrapper.FavouritesWrapper;
import cl.monsoon.s1next.data.api.model.wrapper.ForumGroupsWrapper;
import cl.monsoon.s1next.data.api.model.wrapper.PostsWrapper;
import cl.monsoon.s1next.data.api.model.wrapper.ResultWrapper;
import cl.monsoon.s1next.data.api.model.wrapper.ThreadsWrapper;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
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
}
