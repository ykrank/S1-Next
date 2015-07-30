package cl.monsoon.s1next.data.api;

import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.api.model.VoidElement;
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

    String BASE_URL = "http://bbs.saraba1st.com/2b/api/mobile/";

    //    int COOKIES_MAX_AGE = Ints.checkedCast(TimeUnit.DAYS.toSeconds(30));
    int COOKIES_MAX_AGE = 2592000;

    int THREADS_PER_PAGE = 50;
    int POSTS_PER_PAGE = 30;

    @GET("index.php?module=forumindex")
    Observable<ForumGroupsWrapper> getForumGroupsWrapper();

    @GET("index.php?module=myfavthread")
    Observable<FavouritesWrapper> getFavouritesWrapper(@Query("page") int page);

    @GET("index.php?module=forumdisplay&tpp=" + THREADS_PER_PAGE)
    Observable<ThreadsWrapper> getThreadsWrapper(@Query("fid") String forumId, @Query("page") int page);

    @GET("index.php?module=viewthread&ppp=" + POSTS_PER_PAGE)
    Observable<PostsWrapper> getPostsWrapper(@Query("tid") String threadId, @Query("page") int page);

    @FormUrlEncoded
    @POST("index.php?module=login&loginsubmit=yes&loginfield=username&cookietime=" + COOKIES_MAX_AGE)
    Observable<ResultWrapper> login(@Field("username") String username, @Field("password") String password);

    /**
     * Refreshes the correct authenticity token after login.
     * <p>
     * The {@link User#authenticityToken}
     * is not fresh if we have only logged in and haven't browsed
     * any new contents (which means requesting HTTP GET successfully).
     */
    @GET("index.php?module=toplist")
    Observable<VoidElement> refreshAuthenticityToken();

    @FormUrlEncoded
    @POST("index.php?module=favthread&favoritesubmit=yes")
    Observable<ResultWrapper> addThreadFavorite(@Field("formhash") String authenticityToken, @Field("id") String threadId, @Field("description") String remark);
}
