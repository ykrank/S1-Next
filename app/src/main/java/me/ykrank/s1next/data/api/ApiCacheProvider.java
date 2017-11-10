package me.ykrank.s1next.data.api;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.EvictDynamicKeyGroup;
import io.rx_cache2.LifeCache;
import io.rx_cache2.ProviderKey;
import io.rx_cache2.Reply;

/**
 * RxCache provide cache for retrofit
 * Created by ykrank on 2017/4/22.
 */
public interface ApiCacheProvider {

    @ProviderKey("forum_groups_wrapper")
    @LifeCache(duration = 1, timeUnit = TimeUnit.MINUTES)
    Single<String> getForumGroupsWrapper(Single<String> oWrapper, DynamicKey user, EvictDynamicKey evictDynamicKey);

    @ProviderKey("threads_wrapper")
    @LifeCache(duration = 1, timeUnit = TimeUnit.MINUTES)
    Single<String> getThreadsWrapper(Single<String> oWrapper, DynamicKeyGroup user, EvictDynamicKeyGroup evictDynamicKey);


    @ProviderKey("posts_wrapper")
    @LifeCache(duration = 30, timeUnit = TimeUnit.MINUTES)
    Single<Reply<String>> getPostsWrapper(Single<String> oWrapper, DynamicKeyGroup page, EvictDynamicKeyGroup evictDynamicKey);
}
