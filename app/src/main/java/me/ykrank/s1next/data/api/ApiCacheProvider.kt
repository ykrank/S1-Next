package me.ykrank.s1next.data.api

import com.github.ykrank.androidtools.data.CacheParam
import com.github.ykrank.androidtools.data.Resource
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import me.ykrank.s1next.data.api.model.wrapper.ForumGroupsWrapper
import me.ykrank.s1next.data.api.model.wrapper.ThreadsWrapper

/**
 * RxCache provide cache for retrofit
 * Created by ykrank on 2017/4/22.
 */
interface ApiCacheProvider {
    suspend fun getForumGroupsWrapper(
        param: CacheParam? = null
    ): Flow<Resource<ForumGroupsWrapper>>

    suspend fun getThreadsWrapper(
        forumId: String?,
        typeId: String?,
        page: Int,
        param: CacheParam? = null
    ): Flow<Resource<ThreadsWrapper>>

    fun getPostsWrapper(
        oWrapper: Single<String>,
        param: CacheParam? = null
    ): Single<Resource<String>>

    fun getPostsWrapperNew(
        oWrapper: Single<String>,
        param: CacheParam? = null
    ): Single<Resource<String>>
}
