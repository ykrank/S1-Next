package me.ykrank.s1next.data.api

import io.reactivex.Single
import me.ykrank.s1next.data.cache.CacheParam
import me.ykrank.s1next.data.cache.Resource

/**
 * RxCache provide cache for retrofit
 * Created by ykrank on 2017/4/22.
 */
interface ApiCacheProvider {
    fun getForumGroupsWrapper(
        oWrapper: Single<String>,
        param: CacheParam? = null
    ): Single<String>

    fun getThreadsWrapper(
        oWrapper: Single<String>,
        param: CacheParam? = null
    ): Single<String>

    fun getPostsWrapper(
        oWrapper: Single<String>,
        param: CacheParam? = null
    ): Single<Resource<String>>

    fun getPostsWrapperNew(
        oWrapper: Single<String>,
        param: CacheParam? = null
    ): Single<Resource<String>>
}
