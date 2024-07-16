package me.ykrank.s1next.data.api.empty

import io.reactivex.Single
import me.ykrank.s1next.data.api.ApiCacheProvider
import me.ykrank.s1next.data.cache.CacheParam
import me.ykrank.s1next.data.cache.Resource
import me.ykrank.s1next.data.cache.Source

class EmptyApiCacheProvider : ApiCacheProvider {
    override fun getForumGroupsWrapper(
        oWrapper: Single<String>,
        param: CacheParam?
    ): Single<String> {
        return oWrapper
    }

    override fun getThreadsWrapper(
        oWrapper: Single<String>,
        param: CacheParam?
    ): Single<String> {
        return oWrapper
    }

    override fun getPostsWrapper(
        oWrapper: Single<String>,
        param: CacheParam?
    ): Single<Resource<String>> {
        return oWrapper.map {
            Resource.Success(Source.CLOUD, it)
        }
    }

    override fun getPostsWrapperNew(
        oWrapper: Single<String>,
        param: CacheParam?
    ): Single<Resource<String>> {
        return oWrapper.map {
            Resource.Success(Source.CLOUD, it)
        }
    }
}