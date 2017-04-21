package me.ykrank.s1next.data.api;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKeyGroup;
import io.rx_cache2.LifeCache;
import me.ykrank.s1next.data.api.model.wrapper.PostsWrapper;

/**
 * RxCache provide cache for retrofit
 * Created by ykrank on 2017/4/22.
 */

public interface ApiCacheProvider {

    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<PostsWrapper> getPostsWrapper(Observable<PostsWrapper> oPostWrapper, DynamicKeyGroup page, EvictDynamicKeyGroup evictDynamicKey);

    @LifeCache(duration = 3, timeUnit = TimeUnit.SECONDS)
    Observable<PostsWrapper> getPostsWrapperNewer(Observable<PostsWrapper> oPostWrapper, DynamicKeyGroup page, EvictDynamicKeyGroup evictDynamicKey);
}
