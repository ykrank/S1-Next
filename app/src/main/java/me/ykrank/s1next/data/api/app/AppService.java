package me.ykrank.s1next.data.api.app;

import java.util.List;

import io.reactivex.Observable;
import me.ykrank.s1next.data.api.app.model.AppDataWrapper;
import me.ykrank.s1next.data.api.app.model.AppLoginResult;
import me.ykrank.s1next.data.api.app.model.AppResult;
import me.ykrank.s1next.data.api.app.model.AppThread;
import me.ykrank.s1next.data.api.app.model.AppUserInfo;
import me.ykrank.s1next.data.api.app.model.AppVote;
import me.ykrank.s1next.data.api.model.Vote;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AppService {

    @GET("user")
    Observable<AppDataWrapper<AppUserInfo>> getUserInfo(@Query("uid") String uid);

    @FormUrlEncoded
    @POST("user/login")
    Observable<AppDataWrapper<AppLoginResult>> login(@Field("username") String username, @Field("password") String password,
                                                     @Field("questionid") int questionId, @Field("answer") String answer);

    @FormUrlEncoded
    @POST("user/sign")
    Observable<AppResult> sign(@Field("uid") String uid, @Field("sid") String security);

    @FormUrlEncoded
    @POST("thread/page")
    Observable<String> getPostsWrapper(@Field("sid") String security, @Field("tid") String threadId, @Field("pageNo") int page);

    @FormUrlEncoded
    @POST("thread")
    Observable<AppDataWrapper<AppThread>> getThreadInfo(@Field("sid") String security, @Field("tid") String threadId);

    @FormUrlEncoded
    @POST("poll/poll")
    Observable<AppDataWrapper<AppVote>> getPollInfo(@Field("sid") String security, @Field("tid") String threadId);

    @FormUrlEncoded
    @POST("poll/options")
    Observable<AppDataWrapper<List<Vote.VoteOption>>> getPollOptions(@Field("sid") String security, @Field("tid") String threadId);

    @FormUrlEncoded
    @POST("poll/vote")
    Observable<AppDataWrapper<List<Vote.VoteOption>>> vote(@Field("sid") String security, @Field("tid") String threadId, @Field("options") String optionId);
}
