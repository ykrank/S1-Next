package me.ykrank.s1next.data.api.app;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AppService {
    
    @GET(AppApi.URL_USER_INFO)
    Observable<String> getUserInfo(@Query("uid") String uid);

    @FormUrlEncoded
    @POST(AppApi.URL_SIGN)
    Observable<String> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST(AppApi.URL_SIGN)
    Observable<String> sign(@Field("uid") String uid, @Field("sid") String security);
}
