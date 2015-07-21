package cl.monsoon.s1next.data.api;

import cl.monsoon.s1next.data.api.model.wrapper.FavouritesWrapper;
import cl.monsoon.s1next.data.api.model.wrapper.ForumGroupsWrapper;
import cl.monsoon.s1next.data.api.model.wrapper.PostsWrapper;
import cl.monsoon.s1next.data.api.model.wrapper.ThreadsWrapper;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface S1Service {

    String BASE_URL = "http://bbs.saraba1st.com/2b/api/mobile/";

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
}
