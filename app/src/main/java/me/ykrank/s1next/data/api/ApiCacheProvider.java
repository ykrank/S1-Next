package me.ykrank.s1next.data.api;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.EvictDynamicKeyGroup;
import io.rx_cache2.LifeCache;
import me.ykrank.s1next.data.api.model.wrapper.ForumGroupsWrapper;
import me.ykrank.s1next.data.api.model.wrapper.PostsWrapper;
import me.ykrank.s1next.data.api.model.wrapper.ThreadsWrapper;

/**
 * RxCache provide cache for retrofit
 * Created by ykrank on 2017/4/22.
 */

public interface ApiCacheProvider {

    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<PostsWrapper> getPostsWrapper(Observable<PostsWrapper> oWrapper, DynamicKeyGroup page, EvictDynamicKeyGroup evictDynamicKey);

    @LifeCache(duration = 3, timeUnit = TimeUnit.SECONDS)
    Observable<PostsWrapper> getPostsWrapperNewer(Observable<PostsWrapper> oWrapper, DynamicKeyGroup page, EvictDynamicKeyGroup evictDynamicKey);

    @LifeCache(duration = 10, timeUnit = TimeUnit.MINUTES)
    Observable<ForumGroupsWrapper> getForumGroupsWrapper(Observable<ForumGroupsWrapper> oWrapper, DynamicKey user, EvictDynamicKey evictDynamicKey);

    @LifeCache(duration = 10, timeUnit = TimeUnit.MINUTES)
    Observable<ThreadsWrapper> getThreadsWrapper(Observable<ThreadsWrapper> oWrapper, DynamicKey user, EvictDynamicKey evictDynamicKey);
}
