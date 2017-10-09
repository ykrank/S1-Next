package me.ykrank.s1next.data.api;

import java.util.List;

import io.reactivex.Observable;
import me.ykrank.s1next.data.api.model.Profile;
import me.ykrank.s1next.data.api.model.collection.Favourites;
import me.ykrank.s1next.data.api.model.collection.Friends;
import me.ykrank.s1next.data.api.model.collection.Notes;
import me.ykrank.s1next.data.api.model.collection.PmGroups;
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper;
import me.ykrank.s1next.data.api.model.wrapper.BaseDataWrapper;
import me.ykrank.s1next.data.api.model.wrapper.BaseResultWrapper;
import me.ykrank.s1next.data.api.model.wrapper.PmsWrapper;
import me.ykrank.s1next.data.api.model.wrapper.PostsWrapper;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface S1Service {

    @GET(ApiForum.URL_FORUM)
    Observable<String> getForumGroupsWrapper();

    @GET(ApiHome.URL_FAVOURITES)
    Observable<BaseResultWrapper<Favourites>> getFavouritesWrapper(@Query("page") int page);

    @GET(ApiForum.URL_THREAD_LIST)
    Observable<String> getThreadsWrapper(@Query("fid") String forumId, @Query("page") int page);

    @GET(ApiForum.URL_POST_LIST)
    Observable<PostsWrapper> getPostsWrapper(@Query("tid") String threadId, @Query("page") int page);

    @GET(ApiForum.URL_TRADE_POST_INFO)
    Observable<String> getTradePostInfo(@Query("tid") String threadId, @Query("pid") int pid);

    @GET(ApiForum.URL_QUOTE_POST_REDIRECT)
    Observable<Response<Void>> getQuotePostResponseBody(@Query("ptid") String threadId, @Query("pid") String quotePostId);

    @FormUrlEncoded
    @POST(ApiMember.URL_LOGIN)
    Observable<AccountResultWrapper> login(@Field("username") String username, @Field("password") String password);

    @GET(ApiForum.URL_AUTHENTICITY_TOKEN_HELPER)
    Observable<AccountResultWrapper> refreshAuthenticityToken();

    //region Favourites
    @FormUrlEncoded
    @POST(ApiHome.URL_THREAD_FAVOURITES_ADD)
    Observable<AccountResultWrapper> addThreadFavorite(@Field("formhash") String authenticityToken, @Field("id") String threadId, @Field("description") String remark);

    @FormUrlEncoded
    @POST(ApiHome.URL_THREAD_FAVOURITES_REMOVE)
    Observable<AccountResultWrapper> removeThreadFavorite(@Field("formhash") String authenticityToken, @Field("favid") String favId);
    //endregion

    //region Reply
    @FormUrlEncoded
    @POST(ApiForum.URL_REPLY)
    Observable<AccountResultWrapper> reply(@Field("formhash") String authenticityToken, @Field("tid") String threadId, @Field("message") String reply);

    @GET(ApiForum.URL_QUOTE_HELPER)
    Observable<String> getQuoteInfo(@Query("tid") String threadId, @Query("repquote") String quotePostId);

    @FormUrlEncoded
    @POST(ApiForum.URL_REPLY)
    Observable<AccountResultWrapper> replyQuote(@Field("formhash") String authenticityToken, @Field("tid") String threadId, @Field("message") String reply,
                                                @Field("noticeauthor") String encodedUserId, @Field("noticetrimstr") String quoteMessage, @Field("noticeauthormsg") String replyNotification);
    //endregion

    //region PM
    @GET(ApiHome.URL_PM_LIST)
    Observable<BaseDataWrapper<PmGroups>> getPmGroups(@Query("page") int page);

    @GET(ApiHome.URL_PM_VIEW_LIST)
    Observable<PmsWrapper> getPmList(@Query("touid") String toUid, @Query("page") int page);

    @FormUrlEncoded
    @POST(ApiHome.URL_PM_POST)
    Observable<AccountResultWrapper> postPm(@Field("formhash") String authenticityToken, @Field("touid") String toUid, @Field("message") String msg);
    //endregion

    //region New thread
    @GET(ApiForum.URL_NEW_THREAD_HELPER)
    Observable<String> getNewThreadInfo(@Query("fid") int fid);

    @FormUrlEncoded
    @POST(ApiForum.URL_NEW_THREAD)
    Observable<AccountResultWrapper> newThread(@Query("fid") int fid, @Field("formhash") String authenticityToken, @Field("posttime") long postTime,
                                               @Field("typeid") String typeId, @Field("subject") String subject, @Field("message") String message,
                                               @Field("allownoticeauthor") int allowNoticeAuthor, @Field("usesig") int useSign,
                                               @Field("save") Integer saveAsDraft);
    //endregion

    @GET(ApiForum.URL_EDIT_POST_HELPER)
    Observable<String> getEditPostInfo(@Query("fid") int fid, @Query("tid") int tid, @Query("pid") int pid);
    
    @FormUrlEncoded
    @POST(ApiForum.URL_EDIT_POST)
    Observable<String> editPost(@Field("fid") int fid, @Field("tid") int tid, @Field("pid") int pid,
                                @Field("formhash") String authenticityToken, @Field("posttime") long postTime,
                                @Field("typeid") String typeId, @Field("subject") String subject,
                                @Field("message") String message, @Field("allownoticeauthor") int allowNoticeAuthor,
                                @Field("usesig") int useSign, @Field("save") Integer saveAsDraft);

    @FormUrlEncoded
    @POST(ApiForum.URL_SEARCH_FORUM)
    Observable<String> searchForum(@Field("formhash") String authenticityToken, @Field("srchtxt") String text);

    @FormUrlEncoded
    @POST(ApiForum.URL_SEARCH_USER)
    Observable<String> searchUser(@Field("formhash") String authenticityToken, @Field("srchtxt") String text);

    //region User home
    @GET(ApiHome.URL_MY_NOTE_LIST)
    Observable<BaseDataWrapper<Notes>> getMyNotes(@Query("page") int page);

    @GET(ApiHome.URL_PROFILE)
    Observable<BaseDataWrapper<Profile>> getProfile(@Query("uid") String uid);

    @GET(ApiHome.URL_FRIENDS)
    Observable<BaseDataWrapper<Friends>> getFriends(@Query("uid") String uid);

    @GET(ApiHome.URL_THREADS)
    Observable<String> getHomeThreads(@Query("uid") String uid, @Query("page") int page);

    @GET(ApiHome.URL_REPLIES)
    Observable<String> getHomeReplies(@Query("uid") String uid, @Query("page") int page);

    //endregion

    @GET(ApiHome.URL_RATE_PRE)
    Observable<String> getRatePreInfo(@Query("tid") String threadId, @Query("pid") String postId, @Query("t") long timestamp);

    @FormUrlEncoded
    @POST(ApiHome.URL_RATE)
    Observable<String> rate(@Field("formhash") String authenticityToken, @Field("tid") String threadId, @Field("pid") String postId
            , @Field("referer") String refer, @Field("handlekey") String handleKey, @Field("score1") String score, @Field("reason") String reason);

    @GET(ApiMember.URL_AUTO_SIGN)
    Observable<String> autoSign(@Query("formhash") String authenticityToken);

    @FormUrlEncoded
    @POST(ApiForum.URL_VOTE)
    Observable<String> vote(@Query("tid") String threadId, @Field("formhash") String authenticityToken
            , @Field("pollanswers[]") List<Integer> answers);
}
