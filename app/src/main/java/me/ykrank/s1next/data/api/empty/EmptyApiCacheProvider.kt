package me.ykrank.s1next.data.api.empty

import io.reactivex.Single
import io.rx_cache2.DynamicKey
import io.rx_cache2.DynamicKeyGroup
import io.rx_cache2.EvictDynamicKey
import io.rx_cache2.EvictDynamicKeyGroup
import io.rx_cache2.Reply
import io.rx_cache2.Source
import me.ykrank.s1next.data.api.ApiCacheProvider

class EmptyApiCacheProvider : ApiCacheProvider {
    override fun getForumGroupsWrapper(
        oWrapper: Single<String>,
        user: DynamicKey?,
        evictDynamicKey: EvictDynamicKey?
    ): Single<String> {
        return oWrapper
    }

    override fun getThreadsWrapper(
        oWrapper: Single<String>,
        user: DynamicKeyGroup?,
        evictDynamicKey: EvictDynamicKeyGroup?
    ): Single<String> {
        return oWrapper
    }

    override fun getPostsWrapper(
        oWrapper: Single<String>,
        page: DynamicKeyGroup?,
        evictDynamicKey: EvictDynamicKeyGroup?
    ): Single<Reply<String>> {
        return oWrapper.map {
            Reply(it, Source.CLOUD, false)
        }
    }

    override fun getPostsWrapperNew(
        oWrapper: Single<String>,
        page: DynamicKeyGroup?,
        evictDynamicKey: EvictDynamicKeyGroup?
    ): Single<Reply<String>> {
        return oWrapper.map {
            Reply(it, Source.CLOUD, false)
        }
    }
}