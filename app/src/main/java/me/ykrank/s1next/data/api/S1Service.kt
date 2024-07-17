package me.ykrank.s1next.data.api

import io.reactivex.Single
import me.ykrank.s1next.data.api.model.Profile
import me.ykrank.s1next.data.api.model.collection.Favourites
import me.ykrank.s1next.data.api.model.collection.Friends
import me.ykrank.s1next.data.api.model.collection.Notes
import me.ykrank.s1next.data.api.model.collection.PmGroups
import me.ykrank.s1next.data.api.model.darkroom.DarkRoomWrapper
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper
import me.ykrank.s1next.data.api.model.wrapper.BaseDataWrapper
import me.ykrank.s1next.data.api.model.wrapper.BaseResultWrapper
import me.ykrank.s1next.data.api.model.wrapper.ForumGroupsWrapper
import me.ykrank.s1next.data.api.model.wrapper.PmsWrapper
import me.ykrank.s1next.data.api.model.wrapper.ThreadsWrapper
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface S1Service {
    @GET(ApiForum.URL_FORUM)
    suspend fun getForumGroupsWrapper(): ForumGroupsWrapper

    @GET(ApiHome.URL_FAVOURITES)
    fun getFavouritesWrapper(@Query("page") page: Int): Single<BaseResultWrapper<Favourites>>

    @GET(ApiForum.URL_THREAD_LIST)
    suspend fun getThreadsWrapper(
        @Query("fid") forumId: String?,
        @Query("typeid") typeId: String?,
        @Query("page") page: Int
    ): ThreadsWrapper

    @GET(ApiForum.URL_POST_LIST)
    fun getPostsWrapper(
        @Query("tid") threadId: String?,
        @Query("page") page: Int,
        @Query("authorid") authorId: String?
    ): Single<String>

    @GET(ApiForum.URL_POST_LIST_NEW)
    fun getPostsWrapperNew(
        @Query("tid") threadId: String?,
        @Query("page") page: Int,
        @Query("authorid") authorId: String?
    ): Single<String>

    @GET(ApiForum.URL_TRADE_POST_INFO)
    fun getTradePostInfo(@Query("tid") threadId: String?, @Query("pid") pid: Int): Single<String>

    @GET(ApiForum.URL_QUOTE_POST_REDIRECT)
    fun getQuotePostResponseBody(
        @Query("ptid") threadId: String?,
        @Query("pid") quotePostId: String?
    ): Single<Response<Void>>

    @FormUrlEncoded
    @POST(ApiMember.URL_LOGIN)
    fun login(
        @Field("username") username: String?,
        @Field("password") password: String?,
        @Field("questionid") questionId: Int?,
        @Field("answer") answer: String?
    ): Single<AccountResultWrapper>

    @GET(ApiForum.URL_AUTHENTICITY_TOKEN_HELPER)
    fun refreshAuthenticityToken(): Single<AccountResultWrapper>

    //region Favourites
    @FormUrlEncoded
    @POST(ApiHome.URL_THREAD_FAVOURITES_ADD)
    fun addThreadFavorite(
        @Field("formhash") authenticityToken: String?,
        @Field("id") threadId: String?,
        @Field("description") remark: String?
    ): Single<AccountResultWrapper>

    @FormUrlEncoded
    @POST(ApiHome.URL_THREAD_FAVOURITES_REMOVE)
    fun removeThreadFavorite(
        @Field("formhash") authenticityToken: String?,
        @Field("favid") favId: String?
    ): Single<AccountResultWrapper>

    //endregion
    //region Reply
    @FormUrlEncoded
    @POST(ApiForum.URL_REPLY)
    fun reply(
        @Field("formhash") authenticityToken: String?,
        @Field("tid") threadId: String?,
        @Field("message") reply: String?
    ): Single<AccountResultWrapper>

    @GET(ApiForum.URL_QUOTE_HELPER)
    fun getQuoteInfo(
        @Query("tid") threadId: String?,
        @Query("repquote") quotePostId: String?
    ): Single<String>

    @FormUrlEncoded
    @POST(ApiForum.URL_REPLY)
    fun replyQuote(
        @Field("formhash") authenticityToken: String?,
        @Field("tid") threadId: String?,
        @Field("message") reply: String?,
        @Field("noticeauthor") encodedUserId: String?,
        @Field("noticetrimstr") quoteMessage: String?,
        @Field("noticeauthormsg") replyNotification: String?
    ): Single<AccountResultWrapper>

    //endregion
    //region PM
    @GET(ApiHome.URL_PM_LIST)
    fun getPmGroups(@Query("page") page: Int): Single<BaseDataWrapper<PmGroups>>

    @GET(ApiHome.URL_PM_VIEW_LIST)
    fun getPmList(@Query("touid") toUid: String?, @Query("page") page: Int): Single<PmsWrapper>

    @FormUrlEncoded
    @POST(ApiHome.URL_PM_POST)
    fun postPm(
        @Field("formhash") authenticityToken: String?,
        @Field("touid") toUid: String?,
        @Field("message") msg: String?
    ): Single<AccountResultWrapper>

    //endregion
    //region New thread
    @GET(ApiForum.URL_NEW_THREAD_HELPER)
    fun getNewThreadInfo(@Query("fid") fid: Int): Single<String>

    @FormUrlEncoded
    @POST(ApiForum.URL_NEW_THREAD)
    fun newThread(
        @Query("fid") fid: Int,
        @Field("formhash") authenticityToken: String?,
        @Field("posttime") postTime: Long,
        @Field("typeid") typeId: String?,
        @Field("subject") subject: String?,
        @Field("message") message: String?,
        @Field("allownoticeauthor") allowNoticeAuthor: Int,
        @Field("usesig") useSign: Int,
        @Field("save") saveAsDraft: Int?
    ): Single<AccountResultWrapper>

    //endregion
    @GET(ApiForum.URL_EDIT_POST_HELPER)
    fun getEditPostInfo(
        @Query("fid") fid: Int,
        @Query("tid") tid: Int,
        @Query("pid") pid: Int
    ): Single<String>

    @FormUrlEncoded
    @POST(ApiForum.URL_EDIT_POST)
    fun editPost(
        @Field("fid") fid: Int, @Field("tid") tid: Int, @Field("pid") pid: Int,
        @Field("formhash") authenticityToken: String?, @Field("posttime") postTime: Long,
        @Field("typeid") typeId: String?, @Field("subject") subject: String?,
        @Field("message") message: String?, @Field("allownoticeauthor") allowNoticeAuthor: Int,
        @Field("usesig") useSign: Int, @Field("save") saveAsDraft: Int?,
        @Field("readperm") readPerm: String?
    ): Single<String>

    @FormUrlEncoded
    @POST(ApiForum.URL_SEARCH_FORUM)
    fun searchForum(
        @Field("formhash") authenticityToken: String?,
        @Field("srchtxt") text: String?
    ): Single<String>

    @FormUrlEncoded
    @POST(ApiForum.URL_SEARCH_USER)
    fun searchUser(
        @Field("formhash") authenticityToken: String?,
        @Field("srchtxt") text: String?
    ): Single<String>

    //region User home
    @GET(ApiHome.URL_MY_NOTE_LIST)
    fun getMyNotes(@Query("page") page: Int): Single<BaseDataWrapper<Notes>>

    @GET(ApiHome.URL_MY_NOTE_LIST_SYSTEM)
    fun getMyNotesSystem(@Query("page") page: Int): Single<String>

    @GET(ApiHome.URL_PROFILE)
    fun getProfile(@Query("uid") uid: String?): Single<BaseDataWrapper<Profile>>

    @GET(ApiHome.URL_PROFILE_WEB)
    fun getProfileWeb(
        @Header("Referer") referer: String?,
        @Query("uid") uid: String?
    ): Single<String>

    @GET(ApiHome.URL_FRIENDS)
    fun getFriends(@Query("uid") uid: String?): Single<BaseDataWrapper<Friends>>

    @GET(ApiHome.URL_THREADS)
    fun getHomeThreads(@Query("uid") uid: String?, @Query("page") page: Int): Single<String>

    @GET(ApiHome.URL_REPLIES)
    fun getHomeReplies(@Query("uid") uid: String?, @Query("page") page: Int): Single<String>

    //endregion
    @GET(ApiHome.URL_RATE_PRE)
    fun getRatePreInfo(
        @Query("tid") threadId: String?,
        @Query("pid") postId: String?,
        @Query("t") timestamp: Long
    ): Single<String>

    @FormUrlEncoded
    @POST(ApiHome.URL_RATE)
    fun rate(
        @Field("formhash") authenticityToken: String?,
        @Field("tid") threadId: String?,
        @Field("pid") postId: String?,
        @Field("referer") refer: String?,
        @Field("handlekey") handleKey: String?,
        @Field("score1") score: String?,
        @Field("reason") reason: String?
    ): Single<String>

    @GET(ApiHome.URL_REPORT_PRE)
    fun getReportPreInfo(
        @Query("tid") threadId: String?,
        @Query("rid") postId: String?,
        @Query("t") timestamp: Long
    ): Single<String>

    @FormUrlEncoded
    @POST(ApiHome.URL_REPORT)
    suspend fun report(
        @FieldMap fields: Map<String, String>,
        @Field("report_select") report: String?,
        @Field("message") msg: String?
    ): String

    @GET(ApiMember.URL_AUTO_SIGN)
    fun autoSign(@Query("formhash") authenticityToken: String?): Single<String>

    @FormUrlEncoded
    @POST(ApiForum.URL_VOTE)
    fun vote(
        @Query("tid") threadId: String?,
        @Field("formhash") authenticityToken: String?,
        @Field("pollanswers[]") answers: List<Int>
    ): Single<String>

    @GET(ApiForum.URL_RATE_LIST)
    suspend fun getRates(@Query("tid") threadId: String?, @Query("pid") postId: String?): String

    @GET(Api.URL_DARK_ROOM)
    fun getDarkRoom(@Query("cid") cid: String?): Single<DarkRoomWrapper>

    @GET(Api.URL_WEB_BLACK_LIST)
    fun getWebBlackList(@Query("uid") uid: String?, @Query("page") page: Int): Single<String>
}
