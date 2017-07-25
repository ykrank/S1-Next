package me.ykrank.s1next.data.api;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.EvictDynamicKeyGroup;
import io.rx_cache2.LifeCache;
import io.rx_cache2.Migration;
import io.rx_cache2.Reply;
import io.rx_cache2.SchemeMigration;

/**
 * RxCache provide cache for retrofit
 * Created by ykrank on 2017/4/22.
 */
@SchemeMigration({
        @Migration(version = 1, evictClasses = {String.class})
})
public interface ApiCacheProvider {

    @LifeCache(duration = 1, timeUnit = TimeUnit.MINUTES)
    Observable<String> getForumGroupsWrapper(Observable<String> oWrapper, DynamicKey user, EvictDynamicKey evictDynamicKey);

    @LifeCache(duration = 1, timeUnit = TimeUnit.MINUTES)
    Observable<String> getThreadsWrapper(Observable<String> oWrapper, DynamicKeyGroup user, EvictDynamicKeyGroup evictDynamicKey);

    @LifeCache(duration = 30, timeUnit = TimeUnit.MINUTES)
    Observable<Reply<String>> getPostsWrapper(Observable<String> oWrapper, DynamicKeyGroup page, EvictDynamicKeyGroup evictDynamicKey);
}
