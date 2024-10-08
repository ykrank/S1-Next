package me.ykrank.s1next.data.api.app

import io.reactivex.Single
import me.ykrank.s1next.data.api.app.model.AppDataWrapper
import me.ykrank.s1next.data.api.app.model.AppLoginResult
import me.ykrank.s1next.data.api.app.model.AppResult
import me.ykrank.s1next.data.api.app.model.AppThread
import me.ykrank.s1next.data.api.app.model.AppUserInfo
import me.ykrank.s1next.data.api.app.model.AppVote
import me.ykrank.s1next.data.api.model.Vote.VoteOption
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AppService {
    @GET("user")
    fun getUserInfo(@Query("uid") uid: String?): Single<AppDataWrapper<AppUserInfo>>

    @FormUrlEncoded
    @POST("user/login")
    fun login(
        @Field("username") username: String?, @Field("password") password: String?,
        @Field("questionid") questionId: Int?, @Field("answer") answer: String?
    ): Single<AppDataWrapper<AppLoginResult>>

    @FormUrlEncoded
    @POST("user/sign")
    fun sign(@Field("uid") uid: String?, @Field("sid") security: String?): Single<AppResult>

    @FormUrlEncoded
    @POST("thread/page")
    fun getPostsWrapper(
        @Field("sid") security: String?,
        @Field("tid") threadId: String?,
        @Field("pageNo") page: Int
    ): Single<String>

    @FormUrlEncoded
    @POST("thread")
    fun getThreadInfo(
        @Field("sid") security: String?,
        @Field("tid") threadId: String?
    ): Single<AppDataWrapper<AppThread>>

    @FormUrlEncoded
    @POST("poll/poll")
    suspend fun getPollInfo(
        @Field("sid") security: String?,
        @Field("tid") threadId: String?
    ): AppDataWrapper<AppVote>

    @FormUrlEncoded
    @POST("poll/options")
    suspend fun getPollOptions(
        @Field("sid") security: String?,
        @Field("tid") threadId: String?
    ): AppDataWrapper<List<VoteOption>>

    @FormUrlEncoded
    @POST("poll/vote")
    fun vote(
        @Field("sid") security: String?,
        @Field("tid") threadId: String?,
        @Field("options") optionId: String?
    ): Single<AppDataWrapper<List<VoteOption>>>?
}
